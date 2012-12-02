package com.orange.wro.taglib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @author Angelo Tata
 */
public class WroTagLibProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(WroTagLibProperties.class);

	private static final String PROPERTIES_LOCATION_DEFAULT = "/etc";
	private static final String PROPERTIES_FILENAME_DEFAULT = "wro4j-taglib.properties";

	private Properties properties = new Properties();
	private String propertiesLocation;


	public WroTagLibProperties(String propertiesLocation) {
		this.propertiesLocation = this.getPropertiesLocation(propertiesLocation);
		this.loadProperties();
	}

	private void loadProperties() {
		this.properties = new Properties();

		// We try loading from file first
		InputStream resourceAsStream = loadResourceFromFile();

		if (resourceAsStream == null) {
			// If it fails, we try loading from classpath
			resourceAsStream = loadResourceFromClasspath();

			if (resourceAsStream == null) {
				LOGGER.warn("No properties ({}) found in classpath.", PROPERTIES_FILENAME_DEFAULT);
			} else {
				LOGGER.info("Properties ({}) found in classpath.", PROPERTIES_FILENAME_DEFAULT);
			}
		} else {
			LOGGER.info("Properties found in {}.", this.propertiesLocation);
		}

		if (resourceAsStream != null) {
			try {
				this.properties.load(resourceAsStream);
				LOGGER.info("Properties loaded.");
			} catch (IOException ioEx) {
				LOGGER.warn("Unable to load properties. Cause: {}", ioEx.getMessage());
			} finally {
				try {
					resourceAsStream.close();
				} catch (IOException ioEx) {
					LOGGER.warn("Unable to close input stream for properties file. Cause: {}", ioEx.getMessage());
				}
			}
		}
	}

	private InputStream loadResourceFromFile() {
		String propertiesFullPath = this.propertiesLocation + File.separator + PROPERTIES_FILENAME_DEFAULT;
		File propertiesFile = new File(propertiesFullPath);

		if (propertiesFile.exists()) {
			try {
				return new FileInputStream(propertiesFile);
			} catch (FileNotFoundException fnfEx) {
				LOGGER.warn("Unable to open {}. Cause: {}", propertiesFullPath, fnfEx.getMessage());
			}
		} else {
			LOGGER.warn("No properties found in {}", propertiesFullPath);
		}

		return null;
	}

	private InputStream loadResourceFromClasspath() {
		return this.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME_DEFAULT);
	}

	private String getPropertiesLocation(String propertiesLocation) {
		if (propertiesLocation == null || propertiesLocation.isEmpty()) {
			propertiesLocation = PROPERTIES_LOCATION_DEFAULT;
		}

		return propertiesLocation;
	}

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public boolean isAvailable() {
		return !this.properties.isEmpty();
	}

	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}
}
