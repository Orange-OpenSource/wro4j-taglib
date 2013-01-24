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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Angelo Tata
 */
public class WroTagLibConfig {
	public enum InitParam {
		PROPERTIES_DEFAULT_LOCATION("com.orange.wro.properties.default.location"),
		PROPERTIES_LOCATION("com.orange.wro.properties.location"),
		PROPERTIES_FILENAME("com.orange.wro.properties.filename"),

		BASE_URL("com.orange.wro.base.url"),
		BASE_URL_JS("com.orange.wro.base.url.js"),
		BASE_URL_CSS("com.orange.wro.base.url.css"),
		LESS_PATH("com.orange.wro.less.path"),
		RESOURCE_DOMAIN("com.orange.wro.resource.domain");

		private String key;

		private InitParam(String key) {
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

		this.wroTagLibProperties = new WroTagLibProperties(
			this.servletContext.getInitParameter(InitParam.PROPERTIES_DEFAULT_LOCATION.getKey()),
			this.servletContext.getInitParameter(InitParam.PROPERTIES_LOCATION.getKey()),
			this.servletContext.getInitParameter(InitParam.PROPERTIES_FILENAME.getKey())
		);

		this.configValues =  new HashMap<String, String>(InitParam.values().length - 3);

		this.initialize();
	}

	private void initialize() {
		for (InitParam param : InitParam.values()) {
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

    public String getBaseUrl() {
   		return this.configValues.get(InitParam.BASE_URL.getKey());
   	}

    public String getBaseUrlJs() {
   		return this.configValues.get(InitParam.BASE_URL_JS.getKey());
   	}

    public String getBaseUrlCss() {
   		return this.configValues.get(InitParam.BASE_URL_CSS.getKey());
   	}

	public String getLessPath() {
		return this.configValues.get(InitParam.LESS_PATH.key);
	}

	public String getResourceDomain() {
		return this.configValues.get(InitParam.RESOURCE_DOMAIN.key);
	}

	public WroModel getModel(ServletContextAttributeHelper servletContextAttributeHelper) {
		try {
			return servletContextAttributeHelper.
				getManagerFactory().create().getModelFactory().create();
		} catch (Exception ex) {
			throw new ConfigurationException("Unable to retrieve wro4j model");
		}
	}

	public ServletContextAttributeHelper getServletContextAttributeHelper() {
		return new ServletContextAttributeHelper(this.servletContext);
	}

	@SuppressWarnings("unchecked")
	public Set<String> getResourcePaths() {
		String wroBaseUrl = this.getBaseUrl();

        if (wroBaseUrl == null) {
            String wroBaseUrlJs = this.getBaseUrlJs();
            String wroBaseUrlCss = this.getBaseUrlCss();

            if ((wroBaseUrlJs == null) || (wroBaseUrlCss == null)) {
                throw new ConfigurationException("com.orange.wro.base.url was not configured. In this case, " +
                        "both com.orange.wro.base.url.js and com.orange.wro.base.url.css must be configured.");
            } else {
                Set wroBaseUrlJsPaths = this.servletContext.getResourcePaths(wroBaseUrlJs);
                Set wroBaseUrlCssPaths = this.servletContext.getResourcePaths(wroBaseUrlCss);
                Set<String> resourcePaths = new HashSet<String>();

                resourcePaths.addAll(wroBaseUrlJsPaths);
                resourcePaths.addAll(wroBaseUrlCssPaths);

                return resourcePaths;
            }
        } else {
            return this.servletContext.getResourcePaths(wroBaseUrl);
        }
	}
}
