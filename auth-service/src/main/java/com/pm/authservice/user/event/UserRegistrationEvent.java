package com.pm.authservice.user.event;

import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationEvent extends ApplicationEvent {
    private UserJpaEntity user;
    private String applicationUrl;

    public UserRegistrationEvent(UserJpaEntity user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
