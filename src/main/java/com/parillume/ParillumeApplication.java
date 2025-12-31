package com.parillume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParillumeApplication {
    
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ParillumeApplication.class, args);    
    }
}
