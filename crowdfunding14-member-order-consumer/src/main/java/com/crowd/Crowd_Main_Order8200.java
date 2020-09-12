package com.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableFeignClients
public class Crowd_Main_Order8200 {
  public static void main(String[] args) {
	SpringApplication.run(Crowd_Main_Order8200.class, args);
}
}
