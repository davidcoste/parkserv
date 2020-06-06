package com.dcoste.parkserv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ParkservApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkservApplication.class, args);
    }
}
