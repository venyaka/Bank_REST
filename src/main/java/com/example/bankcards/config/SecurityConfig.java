package com.example.bankcards.config;

import com.example.bankcards.security.ExceptionHandlerFilter;
import com.example.bankcards.security.RefreshTokenFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Конфигурационный класс для настройки безопасности веб-приложения.
 * Этот класс настраивает CORS, цепочку фильтров безопасности, включая обработку JWT,
 * и определяет правила авторизации для различных эндпоинтов.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final RefreshTokenFilter refreshTokenFilter;

    private final ExceptionHandlerFilter exceptionHandlerFilter;

    /**
     * Создает и настраивает бин CorsConfigurationSource для обработки CORS-запросов.
     * Разрешает определенные HTTP-методы и применяет стандартные значения для всех путей.
     * @return настроенный экземпляр CorsConfigurationSource.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PATCH", "DELETE", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Создает и настраивает цепочку фильтров безопасности.
     * Этот метод определяет правила доступа к различным эндпоинтам, настраивает
     * управление сессиями, добавляет кастомные фильтры для обработки исключений и JWT,
     * а также отключает CSRF-защиту.
     * @param httpSecurity объект HttpSecurity для настройки безопасности.
     * @return настроенный экземпляр SecurityFilterChain.
     * @throws Exception если при настройке возникает ошибка.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtTokenFilter, ExceptionHandlerFilter.class)
                .addFilterAfter(refreshTokenFilter, JwtTokenFilter.class)
                .exceptionHandling(Customizer.withDefaults())
                .authorizeHttpRequests(c ->
                        c
                                .requestMatchers("/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/swagger/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/docs/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()

                                .requestMatchers("/authorize/**").permitAll()

                                .requestMatchers("/users/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/cards/{id}").authenticated()
                                .requestMatchers("/cards/search", "/cards/page").authenticated()
                                .requestMatchers(HttpMethod.GET, "/cards").authenticated()
                                .requestMatchers("/cards/{id}/block-request").authenticated()
                                .requestMatchers("/cards/{id}/balance").authenticated()
                                .requestMatchers("/cards/block-requests").authenticated()
                                .requestMatchers("/cards/transfer").authenticated()

                                .requestMatchers("/cards", "/cards/{id}").hasAuthority("ADMIN")
                                .requestMatchers("/cards/{id}/activate").hasAuthority("ADMIN")
                                .requestMatchers("/cards/{id}/block").hasAuthority("ADMIN")
                                .requestMatchers("/cards/{id}", "/cards/{id}/delete").hasAuthority("ADMIN")
                                .requestMatchers("/cards/{id}/update").hasAuthority("ADMIN")

                                .requestMatchers("/cards/{id}/block-request/approve").hasAuthority("ADMIN")
                                .requestMatchers("/cards/{id}/block-request/reject").hasAuthority("ADMIN")
                                .requestMatchers("/admin/cards/block-requests/**").hasAuthority("ADMIN")
                                .requestMatchers("/admin/cards/{id}/test-balance").hasAuthority("ADMIN")
                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                .anyRequest().denyAll()
                )
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .logout(c -> c.invalidateHttpSession(true).clearAuthentication(true))
                .build();
    }
}