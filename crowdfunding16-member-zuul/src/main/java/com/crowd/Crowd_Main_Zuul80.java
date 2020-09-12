package com.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableZuulProxy
@EnableRedisHttpSession
public class Crowd_Main_Zuul80 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(Crowd_Main_Zuul80.class, args);
	}

}
