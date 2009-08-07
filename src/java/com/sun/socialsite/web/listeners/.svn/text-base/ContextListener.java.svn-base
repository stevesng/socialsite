/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.web.listeners;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.BootstrapException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.startup.Startup;
import com.sun.socialsite.business.startup.StartupException;
import com.sun.socialsite.config.Config;


/**
 * Responds to app init/destroy events and holds SocialSite instance.
 */
public class ContextListener implements ServletContextListener {

    private static Log log = LogFactory.getLog(ContextListener.class);

    // reference to ServletContext object
    private static ServletContext servletContext;


    public ContextListener() {
        super();
    }


    /**
     * Get the ServletContext.
     *
     * @return ServletContext
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }


    /**
     * Responds to context initialization event by processing context
     * parameters for easy access by the rest of the application.
     */
    public void contextInitialized(ServletContextEvent sce) {

        log.info("SocialSite Initializing ... ");

        // Keep a reference to ServletContext object
        servletContext = sce.getServletContext();

        // Set a "context.realpath" property, allowing others to find our filesystem basedir
        Config.setProperty("context.realpath", sce.getServletContext().getRealPath("/"));

        try {
            URL baseUrl = new URL(Config.getProperty("socialsite.base.url"));
            Config.setProperty("context.contextpath", baseUrl.getPath());
            log.debug("Config[context.contextpath]="+Config.getProperty("context.contextpath"));
        } catch (MalformedURLException ex) {
            String msg = String.format("Could not decode socialsite.base.url[%s]", Config.getProperty("socialsite.base.url"));
            log.error(msg, ex);
        }

        // Log system information (if enabled)
        // TODO: move this down to DEBUG level
        if (log.isInfoEnabled()) {
            logClassLoaderInfo(ClassLoader.getSystemClassLoader(), "systemClassLoader");
            logClassLoaderInfo(Thread.currentThread().getContextClassLoader(), "contextClassLoader");
        }

        // Set a "context.contextpath" property, allowing others to find our webapp base
        // Now prepare the core services of the app so we can bootstrap
        try {
            Startup.prepare();
        } catch (StartupException ex) {
            log.fatal("SocialSite startup failed during app preparation", ex);
            return;
        }

        // If preparation failed or is incomplete then we are done
        if (!Startup.isPrepared()) {
            log.info("SocialSite startup requires interaction from user to continue");
            return;
        }

        try {
            Factory.bootstrap();
            Factory.getSocialSite().initialize();
        } catch (BootstrapException ex) {
            log.fatal("Factory bootstrap failed", ex);
        } catch (SocialSiteException ex) {
            log.fatal("Factory initialization failed", ex);
        }

        log.info("SocialSite Initialization Complete");
    }


    /**
     * Responds to app-destroy.
     */
    public void contextDestroyed(ServletContextEvent sce) {
        Factory.shutdown();
    }


    /**
     * Logs some information about the specified <code>ClassLoader</code>
     * (and its ancestors).
     *
     * @param classLoader the ClassLoader for which info will be logged.
     * @param name the name which will be included in log entries.
     */
    private void logClassLoaderInfo(ClassLoader classLoader, String name) {
        log.debug(String.format("%s=%s", name, toSimpleString(classLoader)));
        if (classLoader instanceof URLClassLoader) {
            URL[] urls = ((URLClassLoader)classLoader).getURLs();
            for(int i = 0; i < urls.length; i++) {
                log.debug(String.format("%s.classPath[%d]=%s", name, i, urls[i]));
            }
        }
        ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            logClassLoaderInfo(parent, (name+".parent"));
        }
    }


    /**
     * Returns the equivalent of the original <code>Object.toString()</code> value
     * for the specified object, even if its own <code>toString</code> method has been
     * overridden.
     */
    private String toSimpleString(Object o) {
        return (o.getClass().getName() + '@' + Integer.toHexString(o.hashCode()));
    }

}
