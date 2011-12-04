package com.francetelecom.saasstore.wro4j.taglib;

public class ConfigurationException extends RuntimeException {
	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String desc) {
		super(desc);
	}
	
	public ConfigurationException(Throwable t) {
		super(t);
	}
}
