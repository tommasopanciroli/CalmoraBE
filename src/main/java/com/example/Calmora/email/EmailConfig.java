package com.example.Calmora.email;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${EMAIL}")
    private String email;

    @Value("${MAIL_SECRET}")
    private String mailSecret;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // Stampa i valori delle variabili per verificare se vengono lette
        System.out.println("📩 EMAIL: " + System.getenv("EMAIL"));
        System.out.println("🔑 MAIL_SECRET: " + System.getenv("MAIL_SECRET"));

        mailSender.setUsername(email);
        mailSender.setPassword(mailSecret);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}