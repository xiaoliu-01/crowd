package com.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class Crowd_Main_Auth8003 {
	public static void main(String[] args) {
		SpringApplication.run(Crowd_Main_Auth8003.class, args);
	}
}
