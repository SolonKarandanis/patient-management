package com.pm.authservice.user.event;

import com.pm.authservice.user.model.UserEntity;
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
