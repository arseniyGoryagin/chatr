package com.chatr.email;


import com.chatr.email.domain.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${mail.domain}")
    private String domain;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final JavaMailSender mailSender;


    public void sendEmail(Email email){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email.getAddress());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        message.setFrom(senderEmail +domain);
        mailSender.send(message);
    }


}
