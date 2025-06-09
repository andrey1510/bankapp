package com.cashservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class CashserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashserviceApplication.class, args);
	}

}
