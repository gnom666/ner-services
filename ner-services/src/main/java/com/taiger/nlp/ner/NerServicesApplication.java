package com.taiger.nlp.ner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NerServicesApplication {
	
	public static String[] argsr;

	public static void main(String[] args) {
		
		argsr = args;
		
		SpringApplication.run(NerServicesApplication.class, args);

	}
}
