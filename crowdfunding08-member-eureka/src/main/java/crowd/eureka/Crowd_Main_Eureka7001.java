package crowd.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Crowd_Main_Eureka7001 {
  
	public static void main(String[] args) {
		SpringApplication.run(Crowd_Main_Eureka7001.class, args);
	}
} 
