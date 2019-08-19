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
        return (caseId, message) -> {
			synchronized (this) {
				guiDemo.logTextArea.append(message);
				guiDemo.logTextArea.append(System.lineSeparator());
				guiDemo.logTextArea.paintImmediately(guiDemo.logTextArea.getBounds());
			}
        };
    }
}
