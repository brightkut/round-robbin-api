package com.coder.routingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RoutingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoutingApiApplication.class, args);
    }

}
