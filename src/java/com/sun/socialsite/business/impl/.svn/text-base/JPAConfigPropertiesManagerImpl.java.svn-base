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
 *  * When distributing the software, include this License Header Notice in each
 * file and include the License file at legal/LICENSE.txt.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided by Sun
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *  * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or  * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AbstractManagerImpl;
import com.sun.socialsite.business.InitializationException;
import com.sun.socialsite.business.ConfigPropertiesManager;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.config.runtime.ConfigDef;
import com.sun.socialsite.config.runtime.DisplayGroup;
import com.sun.socialsite.config.runtime.PropertyDef;
import com.sun.socialsite.config.runtime.RuntimeConfigDefs;
import com.sun.socialsite.pojos.RuntimeConfigProperty;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JPA implementation of the PropertiesManager.
 */
@Singleton
public class JPAConfigPropertiesManagerImpl extends AbstractManagerImpl implements ConfigPropertiesManager {

    private static Log log = LogFactory.getLog(JPAConfigPropertiesManagerImpl.class);

    private final JPAPersistenceStrategy strategy;


    /**
     * Creates a new instance of JPAPropertiesManagerImpl
     */
    @Inject
    protected JPAConfigPropertiesManagerImpl(JPAPersistenceStrategy strategy) {
        log.debug("Instantiating JPA Properties Manager");
        this.strategy = strategy;
    }


    /**
     * Retrieve a single property by name.
     */
    public RuntimeConfigProperty getProperty(String name) throws SocialSiteException {
        return (RuntimeConfigProperty)strategy.load(RuntimeConfigProperty.class, name);
    }


    /**
     * Retrieve all properties.
     *
     * Properties are returned in a Map to make them easy to lookup.  The Map
     * uses the property name as the key and the RuntimeConfigProperty object
     * as the value.
     */
    public Map<String, RuntimeConfigProperty> getProperties() throws SocialSiteException {

        Map<String, RuntimeConfigProperty> props;
        List list = strategy.getNamedQuery("RuntimeConfigProperty.getAll").getResultList();
        props = new HashMap<String, RuntimeConfigProperty>(2*list.size());

        /*
         * for convenience sake we are going to put the list of props
         * into a map for users to access it.  The value element of the
         * hash still needs to be the RuntimeConfigProperty object so that
         * we can save the elements again after they have been updated
         */
        RuntimeConfigProperty prop = null;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            prop = (RuntimeConfigProperty) it.next();
            props.put(prop.getName(), prop);
        }

        return props;
    }


    /**
     * Save a single property.
     */
    public void saveProperty(RuntimeConfigProperty property) throws SocialSiteException {
        strategy.store(property);
    }


    /**
     * Save all properties.
     */
    public void saveProperties(Map<String, RuntimeConfigProperty> properties) throws SocialSiteException {

        // just go through the list and store each property
        Iterator props = properties.values().iterator();
        while (props.hasNext()) {
            strategy.store((RuntimeConfigProperty) props.next());
        }
    }


    /**
     * @inheritDoc
     */
    @Override
    public void initialize() throws InitializationException {

        Map<String, RuntimeConfigProperty> props = null;

        try {
            props = this.getProperties();
            props = initializeMissingProps(props);
            this.saveProperties(props);
            strategy.flush();
        } catch (Exception e) {
            log.fatal("Failed to initialize runtime configuration properties", e);
            throw new RuntimeException(e);
        }

    }


    /**
     * This method compares the property definitions in the RuntimeConfigDefs
     * file with the properties in the given Map and initializes any properties
     * that were not found in the Map.
     *
     * If the Map of props is empty/null then we will initialize all properties.
     **/
    private Map<String, RuntimeConfigProperty> initializeMissingProps(Map<String, RuntimeConfigProperty> props) {

        if (props == null) {
            props = new HashMap<String, RuntimeConfigProperty>();
        }

        // Start by getting our runtimeConfigDefs
        RuntimeConfigDefs runtimeConfigDefs = RuntimeConfig.getRuntimeConfigDefs();

        // Can't do initialization without our config defs
        if (runtimeConfigDefs == null) return props;

        // Add any properties that are not already in our props map
        for (ConfigDef configDef : runtimeConfigDefs.getConfigDefs()) {
            for (DisplayGroup displayGroup : configDef.getDisplayGroups()) {
                for (PropertyDef propertyDef : displayGroup.getPropertyDefs()) {
                    if (!props.containsKey(propertyDef.getName())) {
                        String name = propertyDef.getName();
                        String value = propertyDef.getDefaultValue();
                        RuntimeConfigProperty newprop = new RuntimeConfigProperty(name, value);
                        props.put(name, newprop);
                        if (log.isInfoEnabled()) {
                            String msg = String.format("Found uninitialized property %s (initializing to %s)", name, value);
                            log.info(msg);
                        }
                    }
                }
            }
        }

        return props;
    }


    public void release() {
    }

}
