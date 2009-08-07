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

package com.sun.socialsite.config;

import com.sun.socialsite.util.PropertyExpander;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;


/**
 * This is the single entry point for accessing configuration properties in SocialSite.
 */
public class Config {

    private static Log log = LogFactory.getLog(Config.class);

    /** The config file from which we'll load our default properties. */
    public static final String DEFAULT_CONFIG = "/com/sun/socialsite/config/socialsite.properties";

    /** Custom config file we'll load (if available). */
    public static final String CUSTOM_CONFIG = "/socialsite.properties";

    /** If you wish to override the CUSTOM_CONFIG path, set this JVM param. */
    public static final String CUSTOM_CONFIG_JVM_PARAM = "socialsite.config";

    /** Our properties, in their raw (unexpanded) form. */
    private static Map<String, String> rawProperties = new ConcurrentHashMap<String, String>();

    /** Our properties, in their final (expanded) form. */
    private static Map<String, String> expandedProperties = new ConcurrentHashMap<String, String>();


    /*
     * Static block run once at class loading
     *
     * We load the default properties and any custom properties we find
     */
    static {

        try {

            Map<String, String> tmpProps;

            // first, load our default properties

            if ((tmpProps = getPropertiesFromResource(DEFAULT_CONFIG)) != null) {
                rawProperties.putAll(tmpProps);
                System.out.println("successfully loaded default properties.");
            } else {
                System.err.println("failed to load default properties.");
            }

            // now, see if we can find our custom config
            if ((tmpProps = getPropertiesFromResource(CUSTOM_CONFIG)) != null) {
                rawProperties.putAll(tmpProps);
                System.out.println("successfully loaded custom properties file from classpath");
            } else {
                System.out.println("no custom properties file found in classpath");
            }

            // finally, check for an external config file
            String envFileName = System.getProperty(CUSTOM_CONFIG_JVM_PARAM);
            if (envFileName != null && envFileName.length() > 0) {
                File f = new File(envFileName);
                if ((tmpProps = getPropertiesFromFile(f)) != null) {
                    rawProperties.putAll(tmpProps);
                    System.out.println("successfully loaded custom properties from " + f.getAbsolutePath());
                } else {
                    System.out.println("failed to load custom properties from " + f.getAbsolutePath());
                }
            } else {
                System.out.println("no custom properties file specified via jvm option");
            }

            buildExpandedProperties();

            // initialize Log4J logging subsystem
            Properties log4jprops = new Properties();
            PropertyConfigurator.configure(Config.getPropertiesStartingWith("log4j."));

            // some debugging for those that want it
            if (log.isDebugEnabled()) {
                log.debug("Config looks like this ...");

                for (String key : expandedProperties.keySet()) {
                    log.debug(key+"="+expandedProperties.get(key));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Expands any properties listed in the config.properties list,
     * replacing them with their expanded values.
     */
    private static void buildExpandedProperties() {

        List<Map> basePropsList = new ArrayList<Map>(2);
        basePropsList.add(rawProperties);
        basePropsList.add(System.getProperties());

        String propertiesDef = rawProperties.get("config.properties");
        String[] expandedPropertyNames = ((propertiesDef != null) ? propertiesDef.split(",") : new String[0]);

        Map<String, String> tmpProps = PropertyExpander.expandMap(rawProperties, basePropsList, expandedPropertyNames);
        expandedProperties.clear();
        expandedProperties.putAll(tmpProps);

    }


    // no, you may not instantiate this class :p
    private Config() {}


    /**
     * Retrieve a property value
     * @param     key Name of the property
     * @return    String Value of property requested, null if not found
     */
    public static String getProperty(String key) {
        log.debug("Fetching property ["+key+"="+expandedProperties.get(key)+"]");
        return expandedProperties.get(key);
    }

    /**
     * Retrieve a property value
     * @param     key Name of the property
     * @param     defaultValue Default value of property if not found
     * @return    String Value of property requested or defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        log.debug("Fetching property ["+key+"="+expandedProperties.get(key)+",defaultValue="+defaultValue+"]");
        String value = expandedProperties.get(key);
        if (value == null)
          return defaultValue;

        return value;
    }

    /**
     * Retrieve a property as a boolean ... defaults to false if not present.
     */
    public static boolean getBooleanProperty(String name) {
        return getBooleanProperty(name,false);
    }

    /**
     * Retrieve a property as a boolean ... with specified default if not present.
     */
    public static boolean getBooleanProperty(String name, boolean defaultValue) {
        // get the value first, then convert
        String value = Config.getProperty(name);

        if (value == null)
            return defaultValue;

        return (new Boolean(value)).booleanValue();
    }

    /**
     * Retrieve a property as an int ... defaults to 0 if not present.
     */
    public static int getIntProperty(String name) {
        return getIntProperty(name, 0);
    }

    /**
     * Retrieve a property as a int ... with specified default if not present.
     */
    public static int getIntProperty(String name, int defaultValue) {
        // get the value first, then convert
        String value = Config.getProperty(name);

        if (value == null)
            return defaultValue;

        return (new Integer(value)).intValue();
    }

    /**
     * Returns a Map containing all properties in their final (expanded) form.
     * @return A Map object container all properties.
     **/
    public static Map<String, String> toMap() {
        return new HashMap<String, String>(expandedProperties);
    }

    /**
     * Retrieve all property keys
     * @return A set of all keys
     **/
    public static Set<String> keySet() {
        return expandedProperties.keySet();
    }

    /**
     * Adds a property to the system.  Note that such properties will not be
     * automatically persisted.  So (unless some other mechanism performs such
     * persistence) they will cease to be available when the application or JVM
     * is restarted.
     */
    public static void setProperty(String propertyName, String propertyValue) {
        rawProperties.put(propertyName, propertyValue);
        buildExpandedProperties();
    }

    private static Map<String, String> getPropertiesFromResource(String resourceName) throws IOException {
        Map<String, String> results = null;
        InputStream in = null;
        try {
            in = Config.class.getResourceAsStream(resourceName);
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                results = new HashMap<String, String>();
                for (Map.Entry entry : props.entrySet()) {
                    results.put((String)(entry.getKey()), (String)(entry.getValue()));
                }
            }
        } finally {
            if (in != null) in.close();
        }
        return results;
    }

    private static Map<String, String> getPropertiesFromFile(File propertiesFile) throws IOException {
        Map<String, String> results = null;
        if (propertiesFile != null && propertiesFile.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(propertiesFile);
                Properties props = new Properties();
                props.load(in);
                results = new HashMap<String, String>();
                for (Map.Entry entry : props.entrySet()) {
                    results.put((String)(entry.getKey()), (String)(entry.getValue()));
                }
            } finally {
                if (in != null) in.close();
            }
        }
        return results;
    }
    
    /**
     * Get properties starting with a specified string.
     */
    public static Properties getPropertiesStartingWith(String startingWith) {
        Properties ret = new Properties();
        for (Iterator it = expandedProperties.keySet().iterator(); it.hasNext();) {
            String key = (String)it.next();
            ret.put(key, expandedProperties.get(key));
        }
        return ret;
    }
}
