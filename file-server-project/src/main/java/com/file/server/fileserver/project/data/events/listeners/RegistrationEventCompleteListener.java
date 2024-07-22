package com.file.server.fileserver.project.data.events.listeners;

import com.file.server.fileserver.project.data.events.hello.RegistrationCompleteEvent;
import com.file.server.fileserver.project.data.model.EmailRequest;
import com.file.server.fileserver.project.repository.UsersRepository;
import com.file.server.fileserver.project.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Calendar;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationEventCompleteListener {

    private final UsersRepository usersRepository;
    private final EmailService emailService;

    @EventListener
    public void publishEvent(RegistrationCompleteEvent event) {

        // 1. Create the user;
        var user = event.getUser();

        // 2. Create the token;
        String token = UUID.randomUUID().toString();

        // 3. Build the url;
        String url = event.getApplicationUrl()+"verify?token="+token;

        // 4. Save url in the user repo
        user.setVerificationToken(token);
        user.setTokenExpiry(this.getTokenExpirationTime());
        this.usersRepository.save(user);


        //5. Send/publish the url;
        log.info("Click on the ling to verify your account : {}",url);
        EmailRequest emailRequest = new EmailRequest(user.getEmail(), url);
        emailService.sendVerificationEmail(emailRequest);
    }

    private static final int EXPIRATIONTIME = 15;

    /**
     * @return Date
     */
    // A method that handles the calculation of the expiration time.
    protected Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, EXPIRATIONTIME);
        return new Date(calendar.getTime().getTime());
    }




}
