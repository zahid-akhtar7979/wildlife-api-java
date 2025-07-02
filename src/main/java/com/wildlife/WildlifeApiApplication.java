package com.wildlife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Wildlife Conservation Platform API
 * 
 * A comprehensive platform for wildlife conservation content management
 * with Keycloak authentication and role-based access control.
 * 
 * @author Wildlife Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class WildlifeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WildlifeApiApplication.class, args);
    }
} 