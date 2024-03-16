package com.dori.SpringStory;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InitServer {

	public static void main(String[] args) {
		SpringApplication.run(InitServer.class, args);
	}

	@PreDestroy
	public void onExit() {
		// Adding Shutdown hook -
		Server.shutdown();
	}
}
