package com.pm.authservice.event;

import com.pm.authservice.user.model.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationEvent extends ApplicationEvent {
    private UserEntity user;
    private String applicationUrl;

    public UserRegistrationEvent(UserEntity user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
