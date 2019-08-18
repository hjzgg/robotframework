package com.wmh.robotframework.service;

import com.wmh.robotframework.browser.DriverManagerType;
import com.wmh.robotframework.main.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class TestServiceTest {

    @Autowired
    private TestService testService;

    @Test
    public void test() throws FileNotFoundException {
        TestProperties tp = new TestProperties();
        tp.setDriverManagerType(DriverManagerType.CHROME);
        String path = ResourceUtils.getFile("classpath:").getPath();
        tp.setTestCasesDirectory(path + "/robotframework/acceptance");
        tp.setOutputDirectory(path + "/report");
        testService.execute(tp);
    }
}
