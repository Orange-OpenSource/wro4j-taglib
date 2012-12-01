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
import java.util.Set;

public class WroTagLibContext {
    public static final String WRO_RESOURCE_DOMAIN_KEY_ATTRIBUTE = "com.orange.wro.resource.domain";
    private static final String WRO_PROPERTIES_LOCATION_ATTRIBUTE = "com.orange.wro.properties.location";
    private static final String WRO_BASE_URL_ATTRIBUTE = "com.orange.wro.base.url";
    private static final String LESS_SCRIPT_ATTRIBUTE = "com.orange.wro.less.path";

    final ServletContext servletContext;

    public WroTagLibContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getBaseUrl() {
        return this.servletContext.getInitParameter(WRO_BASE_URL_ATTRIBUTE);
    }

    public String getResourceDomainKey() {
        return this.servletContext.getInitParameter(WRO_RESOURCE_DOMAIN_KEY_ATTRIBUTE);
    }

    public String getPropertiesLocation() {
        return this.servletContext.getInitParameter(WRO_PROPERTIES_LOCATION_ATTRIBUTE);
    }

    public String getLessPath() {
        return servletContext.getInitParameter(LESS_SCRIPT_ATTRIBUTE);
    }

    public WroModel getModel() {
        try {
            return this.getServletContextAttributeHelper(this.servletContext).
                                    getManagerFactory().create().getModelFactory().create();
        } catch (Exception ex) {
            throw new ConfigurationException("Unable to retrieve wro4j model");
        }
    }

    private ServletContextAttributeHelper getServletContextAttributeHelper(ServletContext servletContext) {
        return new ServletContextAttributeHelper(this.servletContext);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getResourcePaths() {
        String wroBaseUrl = this.getBaseUrl();
        return this.servletContext.getResourcePaths(wroBaseUrl);
    }
}
