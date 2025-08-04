package com.example.currencybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CurrencyBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyBotApplication.class, args);
    }
}
