package com.oingmaryho.business.hubservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HubServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubServiceApplication.class, args);
	}

}
