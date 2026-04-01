package com.stockflow_backend.configuration;

import com.stockflow_backend.configuration.filter.JwtTokenValidator;
import com.stockflow_backend.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(crsf -> crsf.disable())
                .authorizeHttpRequests(auth -> {

                    // Categories
                    auth.requestMatchers(HttpMethod.GET,"/categories/**").authenticated();
                    auth.requestMatchers(HttpMethod.POST,"/categories/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE,"/categories/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT,"/categories/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH,"/categories/**").hasRole("ADMIN");

                    // Products
                    auth.requestMatchers(HttpMethod.GET,"/products/**").authenticated();
                    auth.requestMatchers(HttpMethod.POST,"/products/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE,"/products/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT,"/products/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH,"/products/**").hasRole("ADMIN");

                    // Sales
                    auth.requestMatchers(HttpMethod.GET,"/sales/**").authenticated();
                    auth.requestMatchers(HttpMethod.POST,"/sales/**").authenticated();
                    auth.requestMatchers(HttpMethod.DELETE,"/sales/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PATCH,"/sales/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT,"/sales/**").hasRole("ADMIN");

                    // Auth
                    auth.requestMatchers(HttpMethod.GET, "/auth/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/auth/**").permitAll();
                    auth.requestMatchers(HttpMethod.DELETE, "/auth/**").permitAll();
                    auth.requestMatchers(HttpMethod.PATCH, "/auth/**").permitAll();
                    auth.requestMatchers(HttpMethod.PUT, "/auth/**").permitAll();

                })
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class)


                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("*"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))

                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
