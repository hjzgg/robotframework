package com.wmh.robotframework.bean;

import java.io.Serializable;

public class CaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 *  测试用例名称
	 */
	private String caseName;
	
	/**
	 *  测试脚本
	 */
	private String caseScript;
	
	
	/**
	 * 浏览器版本 
	 */
	private String browserVersion;
	 
	/**
	 *  保存位置
	 */
	private String saveDirecotry;
	
	public String getSaveDirecotry() {
		return saveDirecotry;
	}


	public void setSaveDirecotry(String saveDirecotry) {
		this.saveDirecotry = saveDirecotry;
	}


	public String getCaseName() {
		return caseName;
	}


	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}


	public String getCaseScript() {
		return caseScript;
	}


	public void setCaseScript(String caseScript) {
		this.caseScript = caseScript;
	}


	public String getBrowserVersion() {
		return browserVersion;
	}


	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}


	@Override
	public String toString() {
		return "CaseEntity [caseName=" + caseName + ", caseScript=" + caseScript + ", browserVersion=" + browserVersion
				+ ", saveDirecotry=" + saveDirecotry + "]";
	}
	
	
}
