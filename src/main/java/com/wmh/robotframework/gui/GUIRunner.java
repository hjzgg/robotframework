package com.wmh.robotframework.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wmh.robotframework.service.TestService;

@Component
public class GUIRunner implements ApplicationRunner {

	@Autowired
	TestService testService;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		new GUIDemo(testService).init();
		System.out.println("是否为空" + (null == testService));
		System.out.println("init");
	}

}
