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
    static final String PROPERTIES_FILENAME_DEFAULT = "wro4j-taglib.properties";

	private Properties properties = new Properties();
	private String propertiesLocation;
	private String propertiesFilename;


	public WroTagLibProperties(String defaultLocation, String location, String filename) {
		this.propertiesLocation = this.getPropertiesLocation(defaultLocation, location);
        this.propertiesFilename = this.getPropertiesFilename(filename);

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
				LOGGER.warn("No properties ({}) found in classpath.", this.propertiesFilename);
			} else {
				LOGGER.info("Properties ({}) found in classpath.", this.propertiesFilename);
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
		String propertiesFullPath = this.propertiesLocation + File.separator + this.propertiesFilename;
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
		return this.getClassLoader().getResourceAsStream(this.propertiesFilename);
	}

	private String getPropertiesLocation(String defaultLocation, String location) {
		if (location == null || location.isEmpty()) {
    		if (defaultLocation == null || defaultLocation.isEmpty()) {
                location = PROPERTIES_LOCATION_DEFAULT;
    		} else {
                location = defaultLocation;
            }
		}

		return location;
	}

    private String getPropertiesFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            filename = PROPERTIES_FILENAME_DEFAULT;
        }

        return filename;
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
