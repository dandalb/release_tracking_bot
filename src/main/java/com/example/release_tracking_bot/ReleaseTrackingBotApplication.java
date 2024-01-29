package com.example.release_tracking_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReleaseTrackingBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReleaseTrackingBotApplication.class, args);
    }

}
