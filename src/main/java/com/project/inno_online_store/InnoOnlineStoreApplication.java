package com.project.inno_online_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InnoOnlineStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(InnoOnlineStoreApplication.class, args);
	}

}
