/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
 * or legal/LICENSE.txt.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at legal/LICENSE.txt.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided by Sun
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.socialsite.business;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.DatabaseProvider;
import com.sun.socialsite.business.startup.Startup;
import com.sun.socialsite.config.Config;
import java.io.InputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Utility to get a JPA EntityManagerFactory which the application should use.
 */
public class EmfProvider {

    private static Log log = LogFactory.getFactory().getInstance(EmfProvider.class);

    private static EntityManagerFactory emf;

    private static String EMF_PROPS = "socialsite_emf.properties";

    /**
     * This class is non-instantiable.
     */
    private EmfProvider() {
    }

    /**
     * Gets a JPA EntityManagerFactory which the application should use.
     * @throws SocialSiteException on any error
     */
    public static EntityManagerFactory getEmf() throws SocialSiteException {
        synchronized(EmfProvider.class) {
            if (emf == null) {
                emf = initEmf();
            }
            return emf;
        }
    }

    /**
     * Called once (and only once) to find and initialize our EMF.
     */
    private static EntityManagerFactory initEmf() throws SocialSiteException {

        String jpaConfigurationType = Config.getProperty("jpa.configurationType");
        log.info("jpaConfigurationType="+jpaConfigurationType);

        if ("jndi".equals(jpaConfigurationType)) {

            String emfJndiName = "java:comp/env/" + Config.getProperty("jpa.emf.jndi.name");
            log.info("emfJndiName="+emfJndiName);
            try {
                return (EntityManagerFactory) new InitialContext().lookup(emfJndiName);
            } catch (NamingException e) {
                throw new SocialSiteException("Could not look up EntityManagerFactory in jndi at " + emfJndiName, e);
            }

        } else {

            DatabaseProvider dbProvider = Startup.getDatabaseProvider();

            // Pull in any settings defined in our EMF properties file
            Properties emfProps = loadPropertiesFromResourceName(EMF_PROPS, getContextClassLoader());

            // Add all OpenJPA and Toplinks properties found in Config
            for (String key : Config.keySet()) {
                if (key.startsWith("openjpa.") || key.startsWith("toplink.")) {
                    String value = Config.getProperty(key);
                    log.info(key + ": " + value);
                    emfProps.setProperty(key, value);
                }
            }

            if (dbProvider.getType() == DatabaseProvider.ConfigurationType.JNDI_NAME) {

                // We're doing JNDI, so set OpenJPA JNDI name property
                String jndiName = "java:comp/env/" + dbProvider.getJndiName();
                emfProps.setProperty("openjpa.ConnectionFactoryName", jndiName);

            } else {

                // So set JDBC properties for OpenJPA
                emfProps.setProperty("openjpa.ConnectionDriverName", dbProvider.getJdbcDriverClass());
                emfProps.setProperty("openjpa.ConnectionURL", dbProvider.getJdbcConnectionURL());
                emfProps.setProperty("openjpa.ConnectionUserName", dbProvider.getJdbcUsername());
                emfProps.setProperty("openjpa.ConnectionPassword", dbProvider.getJdbcPassword());

                // And Toplink JPA
                emfProps.setProperty("eclipselink.jdbc.driver", dbProvider.getJdbcDriverClass());
                emfProps.setProperty("eclipselink.jdbc.url", dbProvider.getJdbcConnectionURL());
                emfProps.setProperty("eclipselink.jdbc.user", dbProvider.getJdbcUsername());
                emfProps.setProperty("eclipselink.jdbc.password", dbProvider.getJdbcPassword());

                // And Toplink JPA
                emfProps.setProperty("toplink.jdbc.driver", dbProvider.getJdbcDriverClass());
                emfProps.setProperty("toplink.jdbc.url", dbProvider.getJdbcConnectionURL());
                emfProps.setProperty("toplink.jdbc.user", dbProvider.getJdbcUsername());
                emfProps.setProperty("toplink.jdbc.password", dbProvider.getJdbcPassword());

                // And Hibernate JPA
                emfProps.setProperty("hibernate.connection.driver_class", dbProvider.getJdbcDriverClass());
                emfProps.setProperty("hibernate.connection.url", dbProvider.getJdbcConnectionURL());
                emfProps.setProperty("hibernate.connection.username", dbProvider.getJdbcUsername());
                emfProps.setProperty("hibernate.connection.password", dbProvider.getJdbcPassword());
            }

            try {
                String puName = Config.getProperty("socialsite.puname", "SocialSite_PU");
                return Persistence.createEntityManagerFactory(puName, emfProps);
            } catch (PersistenceException pe) {
                log.error("Failed to create entity manager", pe);
                throw new SocialSiteException(pe);
            }

        }

    }

    /**
     * Loads properties from given resourceName using given class loader
     * @param resourceName The name of the resource containing properties
     * @param cl Classloeder to be used to locate the resouce
     * @return A properties object
     * @throws SocialSiteException
     */
    protected static Properties loadPropertiesFromResourceName(
            String resourceName, ClassLoader cl) throws SocialSiteException {
        Properties props = new Properties();
        InputStream in = null;
        in = cl.getResourceAsStream(resourceName);
        if (in == null) {
            throw new SocialSiteException(
                    "Could not locate properties to load " + resourceName);
        }
        try {
            props.load(in);
        } catch (IOException ioe) {
            throw new SocialSiteException(
                    "Could not load properties from " + resourceName);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                }
            }
        }

        return props;
    }

    /**
     * Get the context class loader associated with the current thread. This is
     * done in a doPrivileged block because it is a secure method.
     * @return the current thread's context class loader.
     */
    private static ClassLoader getContextClassLoader() {
        PrivilegedAction<ClassLoader> action = new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        };
        return AccessController.doPrivileged(action);
    }

}
