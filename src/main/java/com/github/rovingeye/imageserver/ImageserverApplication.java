package com.github.rovingeye.imageserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ImageserverApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ImageserverApplication.class, args);
    }
}
