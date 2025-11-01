package com.pm.authservice.service;

import com.pm.authservice.exception.NotFoundException;
import com.pm.authservice.model.UserEventEntity;
import com.pm.authservice.repository.UserEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserEventServiceBean implements UserEventService {
    protected static final String USER_EVENT_NOT_FOUND="error.user.event.not.found";

    private final UserEventRepository userEventRepository;

    public UserEventServiceBean(UserEventRepository userEventRepository) {
        this.userEventRepository = userEventRepository;
    }

    @Override
    public UserEventEntity findById(Integer id) throws NotFoundException {
        return userEventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_EVENT_NOT_FOUND));
    }

    @Override
    public UserEventEntity findByPublicId(String publicId) throws NotFoundException {
        return userEventRepository.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new NotFoundException(USER_EVENT_NOT_FOUND));
    }

    @Override
    public List<UserEventEntity> findByUserId(Integer userId) {
        return userEventRepository.findByUserId(userId);
    }

    @Override
    public List<UserEventEntity> findByUserPublicId(String publicId) {
        return userEventRepository.findByUserPublicId(UUID.fromString(publicId));
    }

    @Override
    public List<UserEventEntity> findByUserName(String userName) {
        return userEventRepository.findByUserName(userName);
    }

    @Override
    public List<UserEventEntity> findByEmail(String email) {
        return userEventRepository.findByUserEmail(email);
    }

    @Override
    public UserEventEntity saveEvent(UserEventEntity user) {
        return userEventRepository.save(user);
    }
}
