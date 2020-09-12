package com.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Crowd_Main_Pay8300 {
	public static void main(String[] args) {
		SpringApplication.run(Crowd_Main_Pay8300.class, args);
	}
}
