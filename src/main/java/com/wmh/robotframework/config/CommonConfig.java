package com.wmh.robotframework.config;

import com.wmh.robotframework.gui.GUIDemo;
import com.wmh.robotframework.log.ILogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
	@Autowired
	GUIDemo guiDemo;

	@Bean
	public ILogService logService() {

		return new ILogService() {
			@Override
			public void log(String caseId, String message) {
				System.out.println("------------" + (null == guiDemo));
				guiDemo.logTextArea.append(message);
				guiDemo.logTextArea.paintImmediately(guiDemo.logTextArea.getBounds());
			}
		};
	}
}
