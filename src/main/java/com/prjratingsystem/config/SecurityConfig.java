package com.prjratingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/game-objects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/comments/sellers/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/comments/sellers").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/comments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/game-objects/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/game-objects/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/game-objects/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/game-objects/user/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").authenticated()
                        .requestMatchers("/swagger-ui/**").authenticated()
                        .requestMatchers("/v3/api-docs/**").authenticated()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .defaultSuccessUrl("/swagger-ui/index.html", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/api/login")
                        .permitAll()
                );

        return http.build();

//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorize -> authorize
//                        .anyRequest().permitAll()
//                );
//
//        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
