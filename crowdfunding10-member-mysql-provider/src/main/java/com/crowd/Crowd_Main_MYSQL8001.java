package com.crowd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@MapperScan(basePackages = "com.crowd.mapper")
@SpringBootApplication
@EnableEurekaClient
public class Crowd_Main_MYSQL8001 {
	public static void main(String[] args) {
		SpringApplication.run(Crowd_Main_MYSQL8001.class, args);
	}
}
