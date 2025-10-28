package com.example.bankcards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Конфигурационный класс для создания HTTP-клиента.
 * <p>
 * Предоставляет бин {@link RestTemplate}, который является стандартным
 * синхронным клиентом Spring для выполнения HTTP-запросов.
 * </p>
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Создает бин {@link RestTemplate}.
     * <p>
     * Этот бин может быть внедрен в другие компоненты для взаимодействия
     * с внешними REST API.
     * </p>
     *
     * @return Новый экземпляр {@link RestTemplate}.
     */
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
