package com.goosage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.goosage.infra.repository")
@EntityScan(basePackages = "com.goosage")
public class GoosageApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoosageApiApplication.class, args);
    }
}
