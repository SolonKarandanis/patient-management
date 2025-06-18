package com.pm.authservice.event;

import com.pm.authservice.model.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationCompleteEvent extends ApplicationEvent {
    private UserEntity user;
    private String applicationUrl;

    public UserRegistrationCompleteEvent(UserEntity user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
