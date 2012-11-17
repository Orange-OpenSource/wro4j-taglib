package com.orange.wro.taglib.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.WroManagerFactory;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * User: atata
 * Date: 17/11/12
 * Time: 02:39
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WroConfig.class, WroManager.class})
public class WroConfigTest {
    @After
    public void cleanUp() {
        WroConfig.instance = null;
    }

    @Test(expected = ConfigurationException.class)
    public void getInstanceFailsWhenNoInstanceAvailable() {
        WroConfig.getInstance();
    }

    @Test(expected = ConfigurationException.class)
    public void getInstanceFailsWhenNoModelAvailable() {
        ServletContext servletContext = mock(ServletContext.class);
        WroConfig.createInstance(servletContext);
        WroConfig.getInstance();
    }

    @Test
    public void getInstanceSucceedsWhenInstanceCreatedAndModelAvailable() throws Exception {
        ServletContext servletContext = mock(ServletContext.class);
        ServletContextAttributeHelper servletContextAttributeHelper = mock(ServletContextAttributeHelper.class);
        WroManagerFactory wroManagerFactory = mock(WroManagerFactory.class);
        WroManager wroManager = PowerMockito.spy(new WroManager());
        WroModelFactory wroModelFactory = mock(WroModelFactory.class);

        whenNew(ServletContextAttributeHelper.class).withArguments(servletContext).thenReturn(servletContextAttributeHelper);
        when(servletContextAttributeHelper.getManagerFactory()).thenReturn(wroManagerFactory);
        when(wroManagerFactory.create()).thenReturn(wroManager);
        when(wroManager.getModelFactory()).thenReturn(wroModelFactory);
        when(wroModelFactory.create()).thenReturn(new WroModel());

        WroConfig.createInstance(servletContext);

        assertNotNull("The wro configuration retrieved is null", WroConfig.getInstance());
    }

}
