package com.example.bankcards.config;

import com.example.bankcards.security.RefreshTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.bankcards.security.ExceptionHandlerFilter;
import com.example.bankcards.security.jwt.JwtTokenFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final RefreshTokenFilter refreshTokenFilter;

    private final ExceptionHandlerFilter exceptionHandlerFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PATCH", "DELETE", "PUT"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(exceptionHandlerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtTokenFilter, ExceptionHandlerFilter.class)
//                .addFilterAfter(refreshTokenFilter, RefreshTokenFilter.class)
                .exceptionHandling(Customizer.withDefaults())
                .authorizeHttpRequests(c ->
                        c
                                .requestMatchers("/favicon.ico", "/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/swagger/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

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
                .csrf(csrf -> csrf.disable())
                .logout(c -> c.invalidateHttpSession(true).clearAuthentication(true))
                .sessionManagement(c -> c.maximumSessions(1))
                .build();
    }
}