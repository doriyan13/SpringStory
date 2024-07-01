package com.dori.SpringStory;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.dori.SpringStory.constants.ServerConstants.MAX_SHUTDOWN_TIME_IN_MIN;

@SpringBootApplication
@EnableScheduling
public class InitServer {

	@Value("${server.shutdown.buffer}")
	long shutdownBuffer;

	public static void main(String[] args) {
		SpringApplication.run(InitServer.class, args);
	}

	@PreDestroy
	public void onExit() {
		try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
			// Adding Shutdown hook -
			executorService.submit(Server::shutdown);
			executorService.shutdown();
			boolean finished = executorService.awaitTermination(MAX_SHUTDOWN_TIME_IN_MIN, TimeUnit.MINUTES);
			if (!finished) {
				System.err.println("Server shutdown timed out and did not finish properly!!!");
			}
			// There is a delay on shutdown after save, thus need to wait extra buffer before init destroy state on the server -
			Thread.sleep(shutdownBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
