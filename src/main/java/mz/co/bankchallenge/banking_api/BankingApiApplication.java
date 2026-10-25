package mz.co.bankchallenge.banking_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/** Entry point for the Banking API Spring Boot application. */
public class BankingApiApplication {

	/** Starts the Spring application context. */
	public static void main(String[] args) {
		SpringApplication.run(BankingApiApplication.class, args);
	}

}
