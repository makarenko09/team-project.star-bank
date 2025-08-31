package org.skypro.star;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(exclude = {LiquibaseAutoConfiguration.class})
public class StarApplication {
	public static void main(String[] args) {
		SpringApplication.run(StarApplication.class, args);
	}
}
