package com.splitmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SplitMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SplitMateApplication.class, args);
    }
}
