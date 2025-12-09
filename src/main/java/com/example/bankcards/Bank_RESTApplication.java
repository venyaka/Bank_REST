package com.example.bankcards;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class Bank_RESTApplication {

    public static void main(String[] args) {
        Environment env = SpringApplication.run(Bank_RESTApplication.class, args).getEnvironment();

        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "/");

        log.info("=========================================================");
        log.info("  Приложение Bank REST успешно запущено!");
        log.info("  Локальный адрес:    http://localhost:{}{}", port, contextPath);
        log.info("  Swagger UI:         http://localhost:{}{}/swagger-ui.html", port, contextPath);
        log.info("  Профиль:            {}", String.join(", ", env.getActiveProfiles()));
        log.info("=========================================================");
    }

}
