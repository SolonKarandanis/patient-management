package com.pm.authservice.config.hazelcast;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.HazelcastSessionSerializer;
import org.springframework.session.hazelcast.PrincipalNameExtractor;

import java.util.List;

@Configuration
public class HazelcastConfig {

    @Value("${hazelcast.cluster.name}")
    private String hazelcastClusterName;

    @Value("${hazelcast.cluster.members}")
    private String hazelcastClusterMembers;

    @Value("${hazelcast.instance.name}")
    private String hazelcastInstanceName;

    @Value("${hazelcast.instance.port}")
    private Integer hazelCastInstancePort;

    @Value("${hazelcast.session.management.enabled}")
    private boolean hazelcastSessionManagementEnabled;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public HazelcastInstance hazelcastInstance() {
        logger.debug("Creating HazelcastInstance instance");
        logger.debug("Hazelcast Cluster members: {}", hazelcastClusterMembers);
        logger.debug("Hazelcast Cluster Name: {}", hazelcastClusterName);
        logger.debug("Hazelcast Instance Name: {}", hazelcastInstanceName);
        logger.debug("Hazelcast Instance Port: {}", hazelCastInstancePort);
        Config config = new Config();
        config.setClusterName(hazelcastClusterName);
        NetworkConfig network = getNetworkConfig(config);
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig()
                .setEnabled(true)
                .setMembers(List.of(hazelcastClusterMembers.split(",")));

        if (hazelcastSessionManagementEnabled) {
            logger.debug("CAS enabled, configuring Hazelcast for session replication");
            AttributeConfig attributeConfig = new AttributeConfig().setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
                    .setExtractorClassName(PrincipalNameExtractor.class.getName());
            config.getMapConfig(HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME).addAttributeConfig(attributeConfig)
                    .addIndexConfig(new IndexConfig(IndexType.HASH, HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));
            SerializationConfig serializationConfig = config.getSerializationConfig();
            serializationConfig.addSerializerConfig(getSerializerConfig());
        }

        return Hazelcast.newHazelcastInstance(config);
    }

    protected SerializerConfig getSerializerConfig() {
        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setImplementation(new HazelcastSessionSerializer()).setTypeClass(MapSession.class);
        return serializerConfig;
    }

    protected NetworkConfig getNetworkConfig(Config config) {
        NetworkConfig network = config.getNetworkConfig();
        network.setPort(hazelCastInstancePort);
        network.setPortAutoIncrement(false); // Prevent auto-increment if port is busy
        return network;
    }


}
