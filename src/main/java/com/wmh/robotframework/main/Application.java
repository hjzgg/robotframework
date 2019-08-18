package com.wmh.robotframework.main;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import java.util.concurrent.CountDownLatch;

import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = "com.wmh.robotframework")
public class Application {

	public static void main(String[] args) {
		SimpleCommandLinePropertySource commandLineProperty = new SimpleCommandLinePropertySource(args);
		String activeProfile = commandLineProperty.getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
		if (StringUtils.isBlank(activeProfile)) {
			startGui(args);
		} else {
			if (activeProfile.equals(ProfileType.gui.name())) {
				startGui(args);
			} else if (activeProfile.equals(ProfileType.web.name())) {
				startWeb(args);
			} else {
				throw new IllegalStateException(String.format("illegal %s = %s", ACTIVE_PROFILES_PROPERTY_NAME, activeProfile));
			}
		}
	}

	private static void startGui(String[] args) {
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		Thread holderThread = new Thread(() -> {
			new SpringApplicationBuilder()
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

	private static void startWeb(String[] args) {
		new SpringApplicationBuilder()
				.web(WebApplicationType.SERVLET)
				.sources(Application.class)
				.run(args);
	}

	enum ProfileType {
		gui,
		web
	}
}
