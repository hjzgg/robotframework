package com.wmh.robotframework.service;

import com.wmh.robotframework.browser.BrowserDriverManager;
import com.wmh.robotframework.config.BrowserExportProperties;
import com.wmh.robotframework.execute.RobotFrameworkMojo;
import org.apache.commons.lang3.StringUtils;
import org.robotframework.javalib.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class TestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private BrowserExportProperties browserExportProperties;

    public void execute(TestProperties tp) {
        BrowserDriverManager browserDriverManager = BrowserDriverManager.getInstance(tp.getDriverManagerType());
        if (browserDriverManager.canUseMirror()) {
            browserDriverManager.useMirror();
        }
        if (StringUtils.isNoneBlank(tp.getDriverVersion())) {
            browserDriverManager.version(tp.getDriverVersion());
        }
        browserDriverManager.setup();
        String browserDriverPath = browserDriverManager.getBinaryPath();
        System.setProperty(browserExportProperties.resovleExportPath(tp.getDriverManagerType()), browserDriverPath);
        LOGGER.info("load browser driver path: " + browserDriverPath);

        RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
        LOGGER.error("TEST CASE PATH IS " + tp.getTestCasesDirectory());
        robotFrameworkMojo.setTestCasesDirectory(new File(tp.getTestCasesDirectory()));
        LOGGER.error("TEST REPORT PATH IS " + tp.getTestCasesDirectory());
        robotFrameworkMojo.setOutputDirectory(new File(tp.getOutputDirectory()));
        robotFrameworkMojo.execute();
    }
}
