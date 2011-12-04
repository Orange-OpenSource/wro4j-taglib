package com.francetelecom.saasstore.wro4j.taglib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
	private String name;
	private Map<String,List<String>> group = new HashMap<String, List<String>>();
	private Map<String,String> minimizedFiles = new HashMap<String, String>();
	
	public Group(String name) {
		this.name = name;
	}
	
	public void put(String type, List<String> files) {
		group.put(type, files);
	}
	
	public List<String> get(String type) {
		return group.get(type);
	}
	
	public void putMinimizedFile(String type, String minimizedFile) {
		minimizedFiles.put(type, minimizedFile);
	}
	
	public String getMinimizedFile(String type) {
		return minimizedFiles.get(type);
	}
	
	public String getName() {
		return name;
	}
	
}
