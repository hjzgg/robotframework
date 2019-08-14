package com.wmh.robotframework.test;

import com.wmh.robotframework.execute.RobotFrameworkMojo;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.File;

public class RobotFrameworkMojoTest {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        RobotFrameworkMojo robotFrameworkMojo = new RobotFrameworkMojo();
        robotFrameworkMojo.setTestCasesDirectory(new File("C:\\Users\\LCY\\Desktop\\接口文档\\robotframework\\src\\test\\robotframework\\acceptance"));
        robotFrameworkMojo.execute();
    }
}
