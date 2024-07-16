package com.file.server.fileserver.project.data.events.listeners;

import com.file.server.fileserver.project.data.events.hello.RegistrationCompleteEvent;
import com.file.server.fileserver.project.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class RegistrationEventCompleteListener {

    private final UsersRepository usersRepository;

    @EventListener
    public void publishEvent(RegistrationCompleteEvent event) {

        // 1. Create the user;
        var user = event.getUser();

        // 2. Create the token;
        String token = UUID.randomUUID().toString();

        // 3. Build the url;
        String url = event.getApplicationUrl()+"/verify?token="+token;

        // 4. Save url in the user repo
        user.setVerificationToken(token);
        user.setTokenExpiry(this.expiryToken());

        //5. Send/publish the url;
        log.info("Click on the ling to verify your account : {}",url);
        System.out.println("Click on the ling to verify your account : "+url);
    }

    private Date expiryToken(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 15);
        return ((Date) calendar.getTime());

    }
}
