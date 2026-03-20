package com.pm.streamprocessor.service;

import com.pm.streamprocessor.model.auth.AggregatedUser;
import com.pm.streamprocessor.model.auth.JsonSerde;
import com.pm.streamprocessor.model.auth.Role;
import com.pm.streamprocessor.model.auth.User;
import com.pm.streamprocessor.model.auth.UserRole;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorSupplier;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAggregationProcessor {

    private static final Logger log = LoggerFactory.getLogger(UserAggregationProcessor.class);

    @Value("${auth.processing.users.topic}")
    private String usersTopic;

    @Value("${auth.processing.roles.topic}")
    private String rolesTopic;

    @Value("${auth.processing.user_roles.topic}")
    private String userRolesTopic;

    @Value("${auth.processing.aggregated_users.topic}")
    private String aggregatedUsersTopic;

    @Autowired
    private StreamsBuilder streamsBuilder;

    @PostConstruct
    public void buildTopology() {
        // 1. Users Stream -> Table
        KStream<String, User> usersStream = streamsBuilder.stream(usersTopic, Consumed.with(Serdes.String(), new JsonSerde<>(User.class)))
                .peek((k, v) -> log.info("Received raw User change: key={}, value={}", k, v != null ? v.getUsername() : "null"));
        KTable<Long, User> usersTable = usersStream
                .filter((k, v) -> v != null && v.getId() != null)
                .map((k, v) -> KeyValue.pair(v.getId(), v))
                .toTable(Materialized.with(Serdes.Long(), new JsonSerde<>(User.class)));

        // 2. Roles Stream -> GlobalKTable
        String rolesRekeyedTopic = "roles-rekeyed";
        KStream<String, Role> rolesStream = streamsBuilder.stream(rolesTopic, Consumed.with(Serdes.String(), new JsonSerde<>(Role.class)));
        rolesStream
                .filter((k, v) -> v != null && v.getId() != null)
                .map((k, v) -> KeyValue.pair(v.getId(), v))
                .to(rolesRekeyedTopic, Produced.with(Serdes.Long(), new JsonSerde<>(Role.class)));

        streamsBuilder.globalTable(rolesRekeyedTopic, 
                Materialized.<Long, Role, org.apache.kafka.streams.state.KeyValueStore<org.apache.kafka.common.utils.Bytes, byte[]>>as("roles-store")
                .withKeySerde(Serdes.Long())
                .withValueSerde(new JsonSerde<>(Role.class)));

        // 3. User Roles Stream -> Grouped Table
        KStream<String, UserRole> userRolesStream = streamsBuilder.stream(userRolesTopic, Consumed.with(Serdes.String(), new JsonSerde<>(UserRole.class)))
                .peek((k, v) -> log.info("Received UserRole change: key={}, user_id={}, role_id={}", k, v != null ? v.getUserId() : "null", v != null ? v.getRoleId() : "null"));
        
        KTable<Long, List<Long>> userRolesAggTable = userRolesStream
                .filter((k, v) -> v != null && v.getUserId() != null)
                .map((k, v) -> KeyValue.pair(v.getUserId(), v.getRoleId()))
                .groupByKey(Grouped.with(Serdes.Long(), Serdes.Long()))
                .aggregate(
                        ArrayList::new,
                        (userId, roleId, roleList) -> {
                            log.info("Aggregating role {} for user {}", roleId, userId);
                            boolean contains = false;
                            for (Object existingRole : roleList) {
                                if (existingRole instanceof Number && ((Number) existingRole).longValue() == roleId.longValue()) {
                                    contains = true;
                                    break;
                                }
                            }
                            if (!contains) {
                                roleList.add(roleId);
                            }
                            return roleList;
                        },
                        Materialized.with(Serdes.Long(), new JsonSerde<>((Class<List<Long>>)(Class<?>)List.class))
                );

        // 4. Join Users with their Role IDs
        KTable<Long, AggregatedUser> intermediateUserTable = usersTable.leftJoin(
                userRolesAggTable,
                (user, roleIds) -> {
                    log.info("Joining User {} with Role IDs: {}", user.getUsername(), roleIds);
                    AggregatedUser aggregatedUser = new AggregatedUser();
                    aggregatedUser.setId(user.getId());
                    aggregatedUser.setPublicId(user.getPublicId());
                    aggregatedUser.setFirstName(user.getFirstName());
                    aggregatedUser.setLastName(user.getLastName());
                    aggregatedUser.setEmail(user.getEmail());
                    aggregatedUser.setUsername(user.getUsername());
                    aggregatedUser.setStatus(user.getStatus());
                    aggregatedUser.setIsEnabled(user.getIsEnabled());
                    aggregatedUser.setIsVerified(user.getIsVerified());
                    
                    if (roleIds != null) {
                        for (Object roleIdObj : roleIds) {
                            if (roleIdObj instanceof Number) {
                                aggregatedUser.getRoles().add(((Number) roleIdObj).toString());
                            } else {
                                aggregatedUser.getRoles().add(roleIdObj.toString());
                            }
                        }
                    }
                    return aggregatedUser;
                },
                Materialized.with(Serdes.Long(), new JsonSerde<>(AggregatedUser.class))
        );

        // 5. Expand Role IDs to Role Names using the GlobalKTable state store
        intermediateUserTable.toStream()
            .processValues(new FixedKeyProcessorSupplier<Long, AggregatedUser, AggregatedUser>() {
                @Override
                public FixedKeyProcessor<Long, AggregatedUser, AggregatedUser> get() {
                    return new FixedKeyProcessor<Long, AggregatedUser, AggregatedUser>() {
                        private FixedKeyProcessorContext<Long, AggregatedUser> context;
                        private ReadOnlyKeyValueStore<Long, Object> roleStore;

                        @Override
                        public void init(FixedKeyProcessorContext<Long, AggregatedUser> context) {
                            this.context = context;
                            this.roleStore = context.getStateStore("roles-store");
                        }

                        @Override
                        public void process(FixedKeyRecord<Long, AggregatedUser> record) {
                            AggregatedUser user = record.value();
                            if (user != null) {
                                List<String> roleNames = new ArrayList<>();
                                for (String roleIdStr : user.getRoles()) {
                                    try {
                                        Long roleId = Long.parseLong(roleIdStr);
                                        Object storeValue = roleStore.get(roleId);
                                        Role role = null;
                                        if (storeValue instanceof ValueAndTimestamp<?>) {
                                            role = (Role) ((ValueAndTimestamp) storeValue).value();
                                        } else if (storeValue instanceof Role) {
                                            role = (Role) storeValue;
                                        }
                                        if (role != null) {
                                            roleNames.add(role.getName());
                                        } else {
                                            roleNames.add("ROLE_ID_" + roleId);
                                        }
                                    } catch (NumberFormatException e) {
                                    }
                                }
                                user.setRoles(roleNames);
                            }
                            log.info("Aggregated User result for {}: {}", user != null ? user.getUsername() : "null", user);
                            context.forward(record.withValue(user));
                        }

                        @Override
                        public void close() {}
                    };
                }
            })
            .to(aggregatedUsersTopic, Produced.with(Serdes.Long(), new JsonSerde<>(AggregatedUser.class)));
    }
}