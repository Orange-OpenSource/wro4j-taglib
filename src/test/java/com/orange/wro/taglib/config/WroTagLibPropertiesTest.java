/*
* Copyright 2011, 2012 France Télécom
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.orange.wro.taglib.config;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import static com.orange.wro.taglib.config.WroConfigTestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Angelo Tata
 */
public class WroTagLibPropertiesTest {
    private static final String PROPERTIES_DEFAULT_LOCATION;
    private static final String PROPERTIES_CUSTOM_LOCATION;

    static {
   		StringBuilder tempFolder = new StringBuilder(System.getProperty("java.io.tmpdir"));

        PROPERTIES_DEFAULT_LOCATION = tempFolder.toString();
   		PROPERTIES_CUSTOM_LOCATION = tempFolder.append("wro4jtest").toString();
   	}

    @BeforeClass
   	public static void setUp() throws IOException {
        new File(PROPERTIES_CUSTOM_LOCATION).mkdirs();

        createProperties(PROPERTIES_DEFAULT_LOCATION, new String[]
            { DEFAULT_BASE_URL, DEFAULT_LESS_PATH, DEFAULT_RESOURCE_DOMAIN }
        );

        createProperties(PROPERTIES_CUSTOM_LOCATION, new String[]
            { CUSTOM_BASE_URL, CUSTOM_LESS_PATH, CUSTOM_RESOURCE_DOMAIN}
        );
   	}

    @AfterClass
   	public static void tearDown() {
   		new File(PROPERTIES_DEFAULT_LOCATION).delete();
   		new File(PROPERTIES_CUSTOM_LOCATION).delete();
   	}


    @Test
    public void isNotAvailableWhenNoPropertiesAreFound() {
        WroTagLibProperties wroTagLibProperties = new WroTagLibProperties(null, null, null);

        assertFalse("Should not be available, no properties anywhere", wroTagLibProperties.isAvailable());
    }

    @Test
    public void loadsPropertiesFromCustomLocationFirst() {
        WroTagLibProperties wroTagLibProperties = new WroTagLibProperties(
            PROPERTIES_DEFAULT_LOCATION, PROPERTIES_CUSTOM_LOCATION, null
        );

        assertTrue("Should be available", wroTagLibProperties.isAvailable());
        assertEquals("Should return custom base url",
                CUSTOM_BASE_URL, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.BASE_URL.getKey())
        );
        assertEquals("Should return custom Less path",
            CUSTOM_LESS_PATH, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.LESS_PATH.getKey())
        );
        assertEquals("Should return custom resource domain",
            CUSTOM_RESOURCE_DOMAIN, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.RESOURCE_DOMAIN.getKey())
        );
    }

    @Test
    public void loadsPropertiesFromDefaultLocationWhenCustomLocationUnavailable() {
        WroTagLibProperties wroTagLibProperties = new WroTagLibProperties(
            PROPERTIES_DEFAULT_LOCATION, null, null
        );

        assertTrue("Should be available", wroTagLibProperties.isAvailable());
        assertEquals("Should return default base url",
            DEFAULT_BASE_URL, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.BASE_URL.getKey())
        );
        assertEquals("Should return default Less path",
            DEFAULT_LESS_PATH, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.LESS_PATH.getKey())
        );
        assertEquals("Should return default resource domain",
            DEFAULT_RESOURCE_DOMAIN, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.RESOURCE_DOMAIN.getKey())
        );
    }

    @Test
    public void loadsPropertiesFromClasspathWhenNoPropertyFilesAvailable() {
        WroTagLibProperties wroTagLibProperties = new WroTagLibProperties(
            null, null, PROPERTIES_TEST_FILENAME
        );

        assertTrue("Should be available", wroTagLibProperties.isAvailable());

        assertEquals("Should return custom base url from classpath",
            CLASSPATH_BASE_URL, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.BASE_URL.getKey())
        );
        assertEquals("Should return custom Less path from classpath",
            CLASSPATH_LESS_PATH, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.LESS_PATH.getKey())
        );
        assertEquals("Should return custom resource domain from classpath",
            CLASSPATH_RESOURCE_DOMAIN, wroTagLibProperties.getProperty(WroTagLibConfig.InitParam.RESOURCE_DOMAIN.getKey())
        );
    }

    private static void createProperties(String location, String[] values) throws IOException {
        File defaultPropertiesFile = new File(location, WroTagLibProperties.PROPERTIES_FILENAME_DEFAULT);

        Properties defaultProperties = new Properties();
        defaultProperties.setProperty(WroTagLibConfig.InitParam.BASE_URL.getKey(), values[0]);
        defaultProperties.setProperty(WroTagLibConfig.InitParam.LESS_PATH.getKey(), values[1]);
        defaultProperties.setProperty(WroTagLibConfig.InitParam.RESOURCE_DOMAIN.getKey(), values[2]);

        defaultProperties.store(new FileWriter(defaultPropertiesFile), null);
    }
}
