package com.pm.authservice.event;

import com.pm.authservice.model.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserUpdateEvent extends ApplicationEvent {
    private UserEntity user;

    public UserUpdateEvent(UserEntity user) {
        super(user);
        this.user = user;
    }
}
