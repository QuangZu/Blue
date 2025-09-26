package com.techtack.blue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlueApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueApplication.class, args);
	}

}
