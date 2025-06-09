package com.exchangeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ExchangeserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeserviceApplication.class, args);
	}

}
