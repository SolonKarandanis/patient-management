package com.pm.authservice.event;

import com.pm.authservice.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private String applicationUrl;

    public UserRegistrationCompleteEvent(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
