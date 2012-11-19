package com.orange.wro.taglib.config;

import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.model.WroModel;

import javax.servlet.ServletContext;
import java.util.Set;

/**
 * User: atata
 * Date: 17/11/12
 * Time: 19:28
 */
public class Context {
    public static final String WRO_RESOURCE_DOMAIN_ATTRIBUTE = "com.orange.wro.resource.domain";
    private static final String WRO_PROPERTIES_LOCATION_ATTRIBUTE = "com.orange.wro.properties.location";
    private static final String WRO_BASE_URL_ATTRIBUTE = "com.orange.wro.base.url";
    private static final String LESS_SCRIPT_ATTRIBUTE = "com.orange.wro.less.path";

    final ServletContext servletContext;

    public Context(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public String getBaseUrl() {
        return this.servletContext.getInitParameter(WRO_BASE_URL_ATTRIBUTE);
    }

    public String getResourceDomain() {
        return this.servletContext.getInitParameter(WRO_RESOURCE_DOMAIN_ATTRIBUTE);
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
