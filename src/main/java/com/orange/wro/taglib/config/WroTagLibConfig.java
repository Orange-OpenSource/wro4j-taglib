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

import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.model.WroModel;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Angelo Tata
 */
public class WroTagLibConfig {
	enum Param {
		propertiesLocation("com.orange.wro.properties.location"),
		baseUrl("com.orange.wro.base.url"),
		lessPath("com.orange.wro.less.path"),
		resourceDomain("com.orange.wro.resource.domain");

		private String key;

		private Param(String key) {
			this.key = key;
		}

		public String getKey() {
			return this.key;
		}
	}

    final ServletContext servletContext;
	final WroTagLibProperties wroTagLibProperties;
	final Map<String, String> configValues;

    public WroTagLibConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.wroTagLibProperties = new WroTagLibProperties(this.servletContext.getInitParameter(Param.propertiesLocation.getKey()));
		this.configValues =  new HashMap<String, String>(3);

	    this.initialize();
    }

	private void initialize() {
		for (Param param : Param.values()) {
			this.setParam(param.key);
		}
	}

	private void setParam(String paramName) {
		if (this.wroTagLibProperties.isAvailable()) {
			this.configValues.put(paramName,  this.wroTagLibProperties.getProperty(paramName));
		} else {
			this.configValues.put(paramName, this.servletContext.getInitParameter(paramName));
		}
	}

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getBaseUrl() {
	    return this.configValues.get(Param.baseUrl.getKey());
    }

    public String getLessPath() {
	    return this.configValues.get(Param.lessPath.key);
    }

	public String getResourceDomain() {
		return this.configValues.get(Param.resourceDomain.key);
	}

    public WroModel getModel() {
        try {
            return this.getServletContextAttributeHelper().
                getManagerFactory().create().getModelFactory().create();
        } catch (Exception ex) {
            throw new ConfigurationException("Unable to retrieve wro4j model");
        }
    }

    private ServletContextAttributeHelper getServletContextAttributeHelper() {
        return new ServletContextAttributeHelper(this.servletContext);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getResourcePaths() {
        String wroBaseUrl = this.getBaseUrl();
        return this.servletContext.getResourcePaths(wroBaseUrl);
    }
}
