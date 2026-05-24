package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.exception.NotFoundException;
import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;

import java.util.List;

public interface UserEventService {

    UserEventEntity findById(Integer id) throws NotFoundException;
    UserEventEntity findByPublicId(String publicId)throws NotFoundException;
    List<UserEventEntity> findByUserId(Integer userId);
    List<UserEventEntity> findByUserPublicId(String publicId);
    List<UserEventEntity> findByUserName(String userName);
    List<UserEventEntity> findByEmail(String email);

    UserEventEntity saveEvent(UserEventEntity user);
}
