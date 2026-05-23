package com.pm.authservice.user.event;

import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserActivationEvent extends ApplicationEvent {
    private UserJpaEntity user;

    public UserActivationEvent(UserJpaEntity user) {
        super(user);
        this.user = user;
    }
}
