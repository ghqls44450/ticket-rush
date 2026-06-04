package com.ticketrush.centralserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CentralServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentralServerApplication.class, args);
	}

}
