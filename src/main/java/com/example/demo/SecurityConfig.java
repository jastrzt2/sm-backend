package com.example.demo;
import com.example.demo.Session.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    @Autowired
    public SecurityConfig(@Lazy JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(antMatcher("/api/v1/users/create")).permitAll()
                        .requestMatchers(antMatcher("/api/auth/signin")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/users/details")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/users/getSavedPosts")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/users/update/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/getPosts")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/getPostsList*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/get/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/**")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/edit")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/delete")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/comments/create")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/comments/edit/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/comments/delete/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/comments/like/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/users/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/getComments/*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts?page=*")).permitAll()
                        .requestMatchers(antMatcher("/api/v1/posts/search")).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


