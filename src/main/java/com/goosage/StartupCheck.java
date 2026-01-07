package com.goosage;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;

@Component
public class StartupCheck implements CommandLineRunner {

    private final EntityManager em;

    public StartupCheck(EntityManager em) {
        this.em = em;
    }

    @Override
    public void run(String... args) {
        System.out.println("✅ EntityManager OK: " + em);
    }
}
