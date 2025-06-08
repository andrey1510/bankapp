package com.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class AccountserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountserviceApplication.class, args);
    }

}
