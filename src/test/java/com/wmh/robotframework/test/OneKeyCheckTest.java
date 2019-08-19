package com.wmh.robotframework.test;

import com.wmh.robotframework.browser.BrowserDriverManager;
import com.wmh.robotframework.execute.RobotFrameworkMojo;
import com.wmh.robotframework.main.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static com.wmh.robotframework.log.LogConstants.COLLECT_LOGGER_NAME;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class OneKeyCheckTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(COLLECT_LOGGER_NAME);

    @Test
    public void testChrome() throws FileNotFoundException {
        BrowserDriverManager.chromedriver().useMirror().setup();
        String browserDriverPath = BrowserDriverManager.chromedriver().getBinaryPath();
        LOGGER.info("load browser driver path: " + browserDriverPath);
        System.setProperty("webdriver.chrome.driver", browserDriverPath);

        RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
        String path = ResourceUtils.getFile("classpath:").getPath();
        LOGGER.error("TEST REPORT PATH IS " + path);
        robotFrameworkMojo.setTestCasesDirectory(new File(path + "/robotframework/oneKeyCheck"));
        robotFrameworkMojo.setOutputDirectory(new File(path + "/report"));
        robotFrameworkMojo.setDebugFile(new File(path + "/report/hehe.txt"));
        robotFrameworkMojo.execute();
    }

}
