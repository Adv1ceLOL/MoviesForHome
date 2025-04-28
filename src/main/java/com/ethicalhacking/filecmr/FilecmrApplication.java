package com.ethicalhacking.filecmr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FilecmrApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
	System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
	System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
	System.setProperty("DB_NAME", dotenv.get("DB_DATABASE"));
	System.setProperty("DB_USERNAME", dotenv.get("DB_USER"));
	System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		SpringApplication.run(FilecmrApplication.class, args);
	}

}
