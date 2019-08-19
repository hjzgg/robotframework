package com.wmh.robotframework.service;

import com.wmh.robotframework.browser.DriverManagerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestProperties {
    //浏览器driver类型
    private DriverManagerType driverManagerType;
    //浏览器driver版本
    private String driverVersion;
    //测试脚本所在位置
    private String testCasesDirectory;
    //测试结果输出位置
    private String outputDirectory;
    //测试debug输出记录
    private String debugLogFile;
    //测试唯一标识
    private String testCaseId;
}
