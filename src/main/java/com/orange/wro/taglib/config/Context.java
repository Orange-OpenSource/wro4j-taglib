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
    private static final String WRO_BASE_URL_ATTRIBUTE = "com.orange.wro.base.url";
    final ServletContext servletContext;

    public Context(ServletContext servletContext) {
        this.servletContext = servletContext;
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
        String wroBaseUrl = this.servletContext.getInitParameter(WRO_BASE_URL_ATTRIBUTE);
        return this.servletContext.getResourcePaths(wroBaseUrl);
    }
}
