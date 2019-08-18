package com.wmh.robotframework.test;

import com.wmh.robotframework.browser.BrowserDriverManager;
import com.wmh.robotframework.execute.RobotFrameworkMojo;
import com.wmh.robotframework.main.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.wmh.robotframework.log.LogConstants.COLLECT_LOGGER_NAME;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class RobotFrameworkMojoTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(COLLECT_LOGGER_NAME);

    @Test
    public void testChrome() throws FileNotFoundException {
        BrowserDriverManager.chromedriver().useMirror().setup();
//        BrowserDriverManager.chromedriver().useMirror().version("77.0.3865.10").setup();
        String browserDriverPath = BrowserDriverManager.chromedriver().getBinaryPath();
        LOGGER.info("load browser driver path: " + browserDriverPath);
        System.setProperty("webdriver.chrome.driver", browserDriverPath);

        RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
        String path = ResourceUtils.getFile("classpath:").getPath();
        LOGGER.error("TEST REPORT PATH IS " + path);
        robotFrameworkMojo.setTestCasesDirectory(new File(path + "/robotframework/acceptance"));
        robotFrameworkMojo.setOutputDirectory(new File(path + "/report"));
        robotFrameworkMojo.setDebugFile(new File(path + "/report/hehe.txt"));
        robotFrameworkMojo.execute();
    }

    @Test
    public void testIE() throws FileNotFoundException {
        BrowserDriverManager.iedriver().useMirror().setup();
        String browserDriverPath = BrowserDriverManager.iedriver().getBinaryPath();
        LOGGER.info("load browser driver path: " + browserDriverPath);
        System.setProperty("webdriver.ie.driver", browserDriverPath);

        RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
        String path = ResourceUtils.getFile("classpath:").getPath();
        LOGGER.error("TEST REPORT PATH IS " + path);
        robotFrameworkMojo.setTestCasesDirectory(new File(path + "/robotframework/acceptance"));
        robotFrameworkMojo.setOutputDirectory(new File(path + "/report"));
        robotFrameworkMojo.execute();
    }

    @Test
    public void testMobileEmulation() {
        BrowserDriverManager.chromedriver().useMirror().setup();
        String browserDriverPath = BrowserDriverManager.chromedriver().getBinaryPath();
        LOGGER.info("load browser driver path: " + browserDriverPath);
        System.setProperty("webdriver.chrome.driver", browserDriverPath);

        //chrome浏览器设置成手机模式
        HashMap<String, String> mobileEmulation = new HashMap<>();
        String deviceName = "Galaxy S5";   //iPhone X/Galaxy S5
        mobileEmulation.put("deviceName", deviceName);
        Map<String, Object> chromeOptions = new HashMap<>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        //new一个chromedriver
        ChromeOptions co = new ChromeOptions();
        co.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        co.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        ChromeDriver driver = new ChromeDriver(co);
        WebDriver.Navigation navigation = driver.navigate();
        navigation.to("https://www.baidu.com");
        System.out.println(co);
    }

    @Test
    public void testMobileEmulation2() {
        BrowserDriverManager.chromedriver().useMirror().setup();
        String browserDriverPath = BrowserDriverManager.chromedriver().getBinaryPath();
        LOGGER.info("load browser driver path: " + browserDriverPath);
        System.setProperty("webdriver.chrome.driver", browserDriverPath);

        //chrome浏览器设置成手机模式
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", "Nexus 5");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("mobileEmulation", mobileEmulation);
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setExperimentalOption("prefs", prefs);
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        WebDriver driver = new ChromeDriver(chromeOptions);
        WebDriver.Navigation navigation = driver.navigate();
        navigation.to("https://www.baidu.com");
        System.out.println(chromeOptions.asMap());
    }
}
