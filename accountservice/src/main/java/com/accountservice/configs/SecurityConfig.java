package com.accountservice.configs;

//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/auth/register").permitAll()
//                .anyRequest().authenticated()
//            )
//            .formLogin(form -> form
//                .loginProcessingUrl("/api/auth/login")
//                .permitAll()
//                .successHandler(loginSuccessHandler())
//            )
//            .logout(logout -> logout
//                .logoutUrl("/api/auth/logout")
//                .logoutSuccessHandler(logoutSuccessHandler())
//            )
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//            );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    private AuthenticationSuccessHandler loginSuccessHandler() {
//        return (request, response, authentication) -> {
//            response.setStatus(HttpStatus.OK.value());
//            response.getWriter().write("Успешный вход");
//        };
//    }
//
//    private LogoutSuccessHandler logoutSuccessHandler() {
//        return (request, response, authentication) -> {
//            response.setStatus(HttpStatus.OK.value());
//            response.getWriter().write("Успешный выход");
//        };
//    }
}