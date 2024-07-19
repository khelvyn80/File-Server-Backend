package com.file.server.fileserver.project.service;

import com.file.server.fileserver.project.data.model.EmailRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(EmailRequest emailRequest){
    VerificationMessage verify = new VerificationMessage(emailRequest.getUrl());

    SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(emailRequest.getReciepient());
        mailMessage.setSubject(verify.getSubject());
        mailMessage.setText(verify.getMessage(emailRequest.getUrl()));
        mailMessage.setFrom("juliusadjeteysowah@gmail.com");
        mailSender.send(mailMessage);
}

public void sendResetTokenEmail(EmailRequest request){
    ResetTokenMessage tokenMessage = new ResetTokenMessage(request.getUrl());

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(request.getReciepient());
    mailMessage.setSubject(tokenMessage.getSubject());
    mailMessage.setText(tokenMessage.getResetTokenMessage());
    mailMessage.setFrom("juliusadjeteysowah@gmail.com");
    mailSender.send(mailMessage);
}

public void sendResetSuccessEmail(String email){
    final String SUBJECT = "PASSWORD RESET SUCCESSFUL FOR YOUR MICRO FOCUS INC ACCOUNT";
    final String emailBody = String.format(
            "This is to confirm that the password for your MICRO FOCUS INC account has been successfully reset.\n\n" +
                    "If you initiated this password reset, you can now log in to your account using your new password.\n\n" +
                    "If you did not request this password reset, please contact our support team immediately for assistance.\n\n" +
                    "If you have any questions or need further assistance, feel free to reach out to our support team.\n\n" +
                    "Thank you for choosing MICRO FOCUS INC.\n\n" +
                    "Best regards,\n" +
                    "The MICRO FOCUS Team"
    );

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(email);
    mailMessage.setSubject(SUBJECT);
    mailMessage.setText(emailBody);
    mailMessage.setFrom("juliusadjeteysowah@gmail.com");
    mailSender.send(mailMessage);
}

public void sendVerificationSuccessEmail(String email){

    final String SUBJECT = "WELCOME TO MICRO FOCUS INC! YOUR REGISTRATION IS SUCCESSFUL";
    final String EMAIL_BODY = String.format(
            "Thank you for registering with MICRO FOCUS INC! We're thrilled to have you on board.\n\n" +
                    "You can now access your account and explore our services.\n\n" +
                    "If you have any questions or need assistance, feel free to contact our support team.\n\n" +
                    "Welcome aboard!\n\n" +
                    "Best regards,\n" +
                    "The MICRO FOCUS Team"
    );

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(email);
    mailMessage.setSubject(SUBJECT);
    mailMessage.setText(EMAIL_BODY);
    mailMessage.setFrom("juliusadjeteysowah@gmail.com");
    mailSender.send(mailMessage);

}



@Getter
@ToString
@RequiredArgsConstructor
private class ResetTokenMessage{
    private String subject;
    private String resetTokenMessage;

    public ResetTokenMessage(String resetUrl){
        this.subject = "PASSWORD RESET REQUEST";
        this.resetTokenMessage = this.getEmailBodyString(resetUrl);
    }

    private String getEmailBodyString (String resetUrl){
        return String.format(
                "We received a request to reset the password for your MICRO FOCUS INC account. To proceed with resetting your password, please click the link below:\n\n" +
                        "%s\n\n" +
                        "For security reasons, this link will expire in 15 minutes. If you did not request a password reset, please ignore this email or contact our support team if you have any concerns.\n\n" +
                        "If you have any questions or need further assistance, feel free to contact our support team.\n\n" +
                        "Thank you,\n" +
                        "The MICRO FOCUS Team",
                resetUrl);
    }

}

@Getter
@ToString
private class VerificationMessage{
    private String subject;
    private String verificationEmailMessage;

    public VerificationMessage(String verificationUrl){
        this.subject ="EMAIL VERIFICATION";
        this.verificationEmailMessage = this.getMessage(verificationUrl);
    }

    private String getMessage(String verificationUrl){

        return String.format(
                "Thank you for signing up for MICRO FOCUS INC! We're excited to have you on board.\n\n" +
                        "To complete your registration and activate your account, please verify your email address by clicking the link below:\n\n " +
                        "%s "+

                        "This link will expire in 15 minutes for security reasons. If you did not create an account using this email address, please ignore this email.\n" +
                        "If you have any questions or need assistance, feel free to contact our support team.\n" +
                        "Thank you,\n" +
                        "The MICRO FOCUS Team", verificationUrl);
    }
}

}
