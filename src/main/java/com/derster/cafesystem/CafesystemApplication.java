package com.derster.cafesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CafesystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CafesystemApplication.class, args);

		Runtime.Version version = Runtime.version();

		System.out.println("Version==="+version);
	}

}
