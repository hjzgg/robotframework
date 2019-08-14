package com.wmh.robotframework.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		Thread holderThread = new Thread(() -> {
			ApplicationContext applicationContext = new SpringApplicationBuilder()
					.web(WebApplicationType.NONE)
					.sources(Application.class)
					.run(args);
			Logger logger = LoggerFactory.getLogger(Application.class);
			try {
				logger.info("holder thread started ...");
				countDownLatch.await();
			} catch (InterruptedException e) {
				logger.error("holder thread Interrupted", e);
			}
		});
		holderThread.setName("ApplicationThread");
		holderThread.start();
	}
}
