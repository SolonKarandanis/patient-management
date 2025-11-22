package com.pm.authservice.user.service;

import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.user.model.UserEventEntity;

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
