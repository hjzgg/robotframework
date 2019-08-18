package com.wmh.robotframework.service;

import com.wmh.robotframework.browser.BrowserDriverManager;
import com.wmh.robotframework.config.BrowserExportProperties;
import com.wmh.robotframework.execute.RobotFrameworkMojo;
import com.wmh.robotframework.log.LoggerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TestService implements LoggerAdapter {

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
        if (StringUtils.isBlank(tp.getDebugLogFile())) {
            String testCase = DigestUtils.md5DigestAsHex(tp.getTestCasesDirectory().getBytes()).toUpperCase();
            try {
                File tmpFile = Files.createTempFile(Files.createTempDirectory(testCase), "debug", ".log").toFile();
                tp.setDebugLogFile(tmpFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("DEBUG LOG FILE CREATE ERROR...", e);
            }
        }
        robotFrameworkMojo.setDebugFile(new File(tp.getDebugLogFile()));
        LOGGER.error("TEST DEBUG LOG PATH IS " + tp.getDebugLogFile());
        robotFrameworkMojo.execute();
    }
}
