package com.tpssoft.hham.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender(@Value("${com.tpssoft.hham.email.username}") String username,
                                            @Value("${com.tpssoft.hham.email.password}") String password) {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties pros = mailSender.getJavaMailProperties();
        pros.put("mail.transport.protocol", "smtp");
        pros.put("mail.smtp.auth", "true");
        pros.put("mail.smtp.starttls.enable", true);
        pros.put("mail.debug", "true");

        return mailSender;

    }
}

