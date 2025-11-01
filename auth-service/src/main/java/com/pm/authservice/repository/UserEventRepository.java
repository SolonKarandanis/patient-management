package com.pm.authservice.repository;

import com.pm.authservice.model.UserEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventRepository extends JpaRepository<UserEventEntity,Integer> {
}
