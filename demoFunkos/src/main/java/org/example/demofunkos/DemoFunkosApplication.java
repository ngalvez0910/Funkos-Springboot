package org.example.demofunkos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DemoFunkosApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoFunkosApplication.class, args);
    }

}
