package com.file.server.fileserver.project.data.events.hello;


import com.file.server.fileserver.project.data.model.Users;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private Users user;
    private String applicationUrl;

    public RegistrationCompleteEvent(Users user, String applicationUrl){
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
