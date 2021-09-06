package com.yologger.heart_to_heart_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HeartToHeartSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeartToHeartSpringbootApplication.class, args);
	}

}
