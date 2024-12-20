package com.coder.simpleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SimpleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleApiApplication.class, args);
    }

}
