package com.jjh.ggumu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.jjh.ggumu", "com.ggumu.server"})
public class GgumuApplication {

    public static void main(String[] args) {
        SpringApplication.run(GgumuApplication.class, args);
    }

}
