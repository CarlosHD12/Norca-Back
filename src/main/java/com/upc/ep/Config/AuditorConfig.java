package com.upc.ep.Config;

import com.upc.ep.security.entities.User;
import com.upc.ep.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditorConfig {
    @Autowired
    private UserRepository userRepos;

    @Bean
    public AuditorAware<User> auditorAware() {
        return () -> {
            return userRepos.findById(1L);
        };
    }
}