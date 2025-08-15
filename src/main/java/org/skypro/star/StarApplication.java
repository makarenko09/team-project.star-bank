package org.skypro.star;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching // включаем механизм кеширования
@SpringBootApplication
public class StarApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarApplication.class, args);
	}

}
