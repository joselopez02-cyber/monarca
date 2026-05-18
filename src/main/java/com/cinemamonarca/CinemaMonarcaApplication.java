package com.cinemamonarca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinemaMonarcaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaMonarcaApplication.class, args);
    }
}