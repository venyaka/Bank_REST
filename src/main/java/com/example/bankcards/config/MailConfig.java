package com.example.bankcards.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Конфигурационный класс для настройки отправки электронной почты.
 */
@Configuration
@Slf4j
public class MailConfig {

    @Value("${sender.mail}")
    private String fromMail;

    @Value("${sender.password}")
    private String password;

    @Value("${sender.smtp.host}")
    private String hostMail;

    @Value("${sender.smtp.port}")
    private Integer port;

    /**
     * Создает и настраивает бин JavaMailSender для отправки электронных писем.
     * @return настроенный экземпляр JavaMailSender.
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(hostMail);
        mailSender.setPort(port);
        mailSender.setUsername(fromMail);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.host", hostMail);
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port",port);

        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        return mailSender;
    }
}