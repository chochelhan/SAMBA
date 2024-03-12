package com.inysoft.samba.config;


import com.inysoft.samba.auth.JwtAuthenticateFilter;
import com.inysoft.samba.service.auth.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticateFilter jwtAuthenticateFilter;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                request -> request
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).permitAll()
                        //.requestMatchers(AntPathRequestMatcher.antMatcher("/login/**")).permitAll()
                        //.requestMatchers(AntPathRequestMatcher.antMatcher("/websocket/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/admin/controller/**")).authenticated()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/controller/**")).authenticated()


        );
        http.httpBasic(withDefaults());
        http.sessionManagement(
                session -> session.sessionCreationPolicy
                        (SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.addFilterBefore(jwtAuthenticateFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
