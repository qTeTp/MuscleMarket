package com.example.muscle_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MuscleMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MuscleMarketApplication.class, args);
	}

}
