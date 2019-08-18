package com.wmh.robotframework.config;

import com.wmh.robotframework.log.ILogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
    @Bean
    public ILogService logService() {
        return new ILogService() {
            @Override
            public void log(String caseId, String message) {
            }
        };
    }
}
