package com.demo.nebula.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;

public class ModelService {

	public ModelManager mgr;
	List result = new ArrayList<TestSet>();
	Map<TestSet,IFile> fileList=new HashMap<>();

	public ModelService(ModelManager manager) {
		this.mgr = manager;
	}

	public ModelService() {

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<TestModel> getModel(int numberOfPersons) {
		return result;
	}
	
	public IFile getIFileFromSelection(TestSet tetSet){
		return fileList.get(tetSet);
	}

	public void setTestFileModel(Map<Integer, String> contents, IFile Testfile) {
		String testCaseName = null;
		String testCaseTypes = null;
		String partOfTestSet = null;
		String description = null;
		Iterator<Entry<Integer, String>> fileinfo = contents.entrySet().iterator();
		while (fileinfo.hasNext()) {
			Entry<Integer, String> file = fileinfo.next();
			String fileCont = file.getValue();
			if (file.getKey() == 1) {
				testCaseName = fileCont.substring(fileCont.indexOf(":") + 1);

			} else if (file.getKey() == 2) {
				testCaseTypes = fileCont.substring(fileCont.indexOf(":") + 1);
			} else if (file.getKey() == 3) {
				partOfTestSet = fileCont.substring(fileCont.indexOf(":") + 1);
			} else if (file.getKey() == 4) {
				description = fileCont.substring(fileCont.indexOf(":") + 1);
			}

		}
		TestSet test = new TestSet(partOfTestSet, testCaseName, description, testCaseTypes);
		System.out.println("This is test object "+test);
		fileList.put(test, Testfile);
		result.add(test);
	}

}
