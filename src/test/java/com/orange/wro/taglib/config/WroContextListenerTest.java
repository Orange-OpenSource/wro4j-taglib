package com.orange.wro.taglib.config;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * User: atata
 * Date: 17/11/12
 * Time: 02:05
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(WroConfig.class)
public class WroContextListenerTest {
    @After
    public void tearDown() {
        WroConfig.instance = null;
    }

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
        WroConfig.createInstance(Mockito.any(Context.class));
    }
}
