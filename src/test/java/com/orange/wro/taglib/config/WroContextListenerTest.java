package com.orange.wro.taglib.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ro.isdc.wro.http.support.ServletContextAttributeHelper;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * User: atata
 * Date: 17/11/12
 * Time: 02:05
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WroConfig.class)
public class WroContextListenerTest {
    @Test(expected = ConfigurationException.class)
    public void contextListenerDoesNotCreateWroConfigInstanceAtCreationTime() {
        WroContextListener contextListener = new WroContextListener();

        WroConfig.getInstance();

        contextListener.contextDestroyed(null);
    }

    @Test
    public void contextListenerCreatesWroConfigInstanceWhenInitialized() throws Exception {
        ServletContextEvent servletContextEvent = mock(ServletContextEvent.class);
        ServletContext servletContext = mock(ServletContext.class);

        when(servletContextEvent.getServletContext()).thenReturn(servletContext);
        mockStatic(WroConfig.class);

        WroContextListener contextListener = new WroContextListener();
        contextListener.contextInitialized(servletContextEvent);

        verifyStatic(Mockito.times(1));
        WroConfig.createInstance(servletContext);
    }
}
