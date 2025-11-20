package com.pm.authservice.event;

import com.pm.authservice.user.model.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserActivationEvent extends ApplicationEvent {
    private UserEntity user;

    public UserActivationEvent(UserEntity user) {
        super(user);
        this.user = user;
    }
}
