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

package com.sun.socialsite.web.ui.admin.struts2;

import com.opensymphony.xwork2.Preparable;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.ConfigPropertiesManager;
import com.sun.socialsite.business.ThemeSettingsManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.config.runtime.ConfigDef;
import com.sun.socialsite.config.runtime.RuntimeConfigDefs;
import com.sun.socialsite.pojos.RuntimeConfigProperty;
import com.sun.socialsite.pojos.ThemeSettings;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ParameterAware;


/**
 * Config Form Action.
 *
 * Handles editing of global runtime properties.
 *
 * TODO: validation and security.
 */
public class Config extends CustomizedActionSupport implements Preparable, ParameterAware {

    private static Log log = LogFactory.getLog(Config.class);

    // original request parameters
    private Map<String, String[]> parameters = Collections.emptyMap();

    // runtime properties data
    private Map<String, RuntimeConfigProperty> properties = Collections.emptyMap();

    // the runtime config def used to populate the display
    private ConfigDef configDef = null;

    private String titleKey = null;
    private String configDefName = null;

    public Config() {
        this.desiredMenu = "admin";
    }

    public void prepare() throws Exception {
        setPageTitle(titleKey);

        // just grab our properties map and put it in the request
        ConfigPropertiesManager cpm = Factory.getSocialSite().getConfigPropertiesManager();
        this.properties = cpm.getProperties();

        // set config def used to draw the view
        RuntimeConfigDefs defs = RuntimeConfig.getRuntimeConfigDefs();
        List<ConfigDef> configDefs = defs.getConfigDefs();
        for (ConfigDef configDef : configDefs) {
            if (configDefName.equals(configDef.getName())) {
                setConfigDef(configDef);
                break;
            }
        }
    }

    public String execute() {
        return INPUT;
    }

    public String save() {

        log.debug("Handling update request");

        try {
            // only set values for properties that are already defined
            String propName = null;
            RuntimeConfigProperty updProp = null;
            String incomingProp = null;
            Iterator propsIT = this.properties.keySet().iterator();
            while (propsIT.hasNext()) {
                propName = (String) propsIT.next();

                log.debug("Checking property ["+propName+"]");

                updProp = (RuntimeConfigProperty) this.properties.get(propName);
                String[] propValues = (String[]) this.parameters.get(updProp.getName());
                if (propValues != null && propValues.length > 0) {
                    // we don't deal with multi-valued props
                    incomingProp = propValues[0];
                } else {
                    // don't clobber props from other config-defs
                    incomingProp = null;
                }

                // some special treatment for booleans
                // this is a bit hacky since we are assuming that any prop
                // with a value of "true" or "false" is meant to be a boolean
                // it may not always be the case, but we should be okay for now
                if ( updProp.getValue() != null // null check needed w/Oracle
                        && (updProp.getValue().equals("true") || updProp.getValue().equals("false"))) {

                    if (incomingProp == null || !incomingProp.equals("on")) {
                        incomingProp = "false";
                    } else {
                        incomingProp = "true";
                    }
                }

                // only work on props that were submitted with the request
                if (incomingProp != null) {
                    log.debug("Setting new value for ["+propName+"]");

                    updProp.setValue(incomingProp.trim());
                }
            }

            // save it
            ConfigPropertiesManager cpm = Factory.getSocialSite().getConfigPropertiesManager();
            cpm.saveProperties(this.properties);
            Factory.getSocialSite().flush();
        } catch (SocialSiteException e) {
            log.error(e);
            setError("ConfigForm.error.saveFailed");
        }

        setSuccess("ConfigForm.message.saveSucceeded");
        return INPUT;
    }


    public Map getParameters() {
        return parameters;
    }

    /**
     * Note: using SuppressWarnings annotation because the Struts API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map<String, RuntimeConfigProperty> properties) {
        this.properties = properties;
    }

    public ConfigDef getConfigDef() {
        return configDef;
    }

    public void setConfigDef(ConfigDef configDef) {
        this.configDef = configDef;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public String getConfigDefName() {
        return configDefName;
    }

    public void setConfigDefName(String configDefName) {
        this.configDefName = configDefName;
    }

}
