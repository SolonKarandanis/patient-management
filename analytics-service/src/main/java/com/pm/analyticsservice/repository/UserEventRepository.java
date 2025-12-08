package com.pm.analyticsservice.repository;

import com.pm.analyticsservice.model.UserEventModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserEventRepository extends CrudRepository<UserEventModel, UUID> {
}
