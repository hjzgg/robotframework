package com.wmh.robotframework.test;

import com.wmh.robotframework.execute.RobotFrameworkMojo;
import com.wmh.robotframework.main.Application;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class RobotFrameworkMojoTest {

    @Test
    public void test() {
//        WebDriverManager.chromedriver().setup();
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\LCY\\.m2\\repository\\webdriver\\chromedriver\\win32\\76.0.3809.68\\chromedriver.exe");
        RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
        robotFrameworkMojo.setTestCasesDirectory(new File("C:\\Users\\LCY\\Desktop\\接口文档\\robotframework\\src\\test\\robotframework\\acceptance"));
        robotFrameworkMojo.execute();
    }
}
