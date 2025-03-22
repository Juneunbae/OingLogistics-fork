package com.oringmaryho.business.slackservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class SlackServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(SlackServiceApplication.class, args);
  }

}
