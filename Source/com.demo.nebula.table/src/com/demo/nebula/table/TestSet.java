package com.demo.nebula.table;

public class TestSet {

	String partTestSet;

	String testCaseName;
	String testCaseDescription;
	String testCaseTypes;

	public TestSet(String partTestSet, String testCaseName, String testCaseDescription, String testCaseTypes) {
		this.partTestSet = partTestSet;
		this.testCaseName = testCaseName;
		this.testCaseDescription = testCaseDescription;
		this.testCaseTypes = testCaseTypes;
	}

	public String getPartTestSet() {
		return partTestSet;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public String getTestCaseDescription() {
		return testCaseDescription;
	}

	public String getTestCaseTypes() {
		return testCaseTypes;
	}

}
