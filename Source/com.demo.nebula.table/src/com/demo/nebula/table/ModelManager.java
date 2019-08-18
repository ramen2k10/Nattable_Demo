package com.demo.nebula.table;

import java.util.ArrayList;

public class ModelManager {

	static ArrayList<TestSet> testSets=new ArrayList<>();
	public static ArrayList<TestSet> getTestCases() {
		testSets.add(new TestSet("Demo", "TestSet/001/nebula/A", "This is a demo description", "JavaCard"));
		testSets.add(new TestSet("Test", "TestSet/001/nebula/B", "This is a demo description", "Native"));
		testSets.add(new TestSet("Demo", "TestSet/001/nebula/A", "This is a demo description", "JavaCard"));
		testSets.add(new TestSet("Test", "TestSet/001/nebula/B", "This is a demo description", "Native"));
		testSets.add(new TestSet("Demo", "TestSet/001/nebula/A", "This is a demo description", "JavaCard"));
		testSets.add(new TestSet("Test", "TestSet/001/nebula/B", "This is a demo description", "Native"));
		testSets.add(new TestSet("Demo", "TestSet/001/nebula/A", "This is a demo description", "JavaCard"));
		testSets.add(new TestSet("Test", "TestSet/001/nebula/B", "This is a demo description", "Native"));
		testSets.add(new TestSet("Demo", "TestSet/001/nebula/A", "This is a demo description", "JavaCard"));
		testSets.add(new TestSet("Test", "TestSet/001/nebula/B", "This is a demo description", "Native"));
		testSets.add(new TestSet("Demo", "TestSet/001/nebula/A", "This is a demo description", "JavaCard"));
		testSets.add(new TestSet("Test", "TestSet/001/nebula/B", "This is a demo description", "Native"));

		return testSets;
	}

	
}
