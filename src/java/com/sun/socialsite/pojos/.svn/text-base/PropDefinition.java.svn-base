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

package com.sun.socialsite.pojos;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.util.TextUtil;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * <p>Defines the SocialSite personal Profile properties.
 * Properties are organized into Display Groups.
 * A Display Group can have Properties, Property Objects and Collections.
 * Property Object is an object that has Properties, PropertyObjects and Collections.
 * A Collection is a collection of objects that have Properties, PropertyObjects and Collections.
 * Properties, PropertyObject and Collection names are mapped to the OpenSocial Person model.</p>
 *
 * <h2>Property naming system</h2>
 *
 * <p>Here's some rough EBNF for a full property name:</p>
 *
 * <pre>
 * fullpropertypath ::= sectionname, "_", propertypath
 * propertypath ::= [pathpart], { "_", [pathpart] }, propertyname
 * pathpart ::= objectname | collectionspec
 * collectionspec ::= collectionname, "_", indexnumber
 * </pre>
 *
 * <p>Here's some rough EBNF for a full property object name:</p>
 *
 * <pre>
 * fullpropertyobjectpath ::= sectionname, "_", objectpath
 * objectpath ::= [pathpart], { "_", [pathpart] }, objectname
 * pathpart ::= objectname | collectionspec
 * collectionspec ::= collectionname, "_", indexnumber
 * </pre>
 *
 * <p>Here's some rough EBNF for a full collection object name:</p>
 *
 * <pre>
 * fullcollcetionobjectpath ::= sectionname, "_", collectionobjectpath
 * collectionobjectpath ::= [pathpart], { "_", [pathpart] }, collectionspec
 * pathpart ::= objectname | collectionspec
 * collectionspec ::= collectionname, "_", indexnumber
 * </pre>
 *
 * <h2>Property Definition format</h2>
 *
 * <p>Here's a RELAX-NG schema (not verified) for the XML format that SocialSite uses
 * to specify personal profile properties:</p>
 *
 * <pre>
 * propertyContent = {
 *      attribute name { text },
 *      attribute namekey { text },
 *      attribute type { "string" | "text" | "integer" | "datetime" | "boolean" },
 *      attribute default-value { text }?,
 *      element allowed-values {
 *          element value { text }*
 *      }?
 * }
 * objectContent = {
 *      attribute name { text },
 *      attribute namekey { text },
 *      element property { propertyContent }*,
 *      element object { objectContent }*
 *      element collection { collectionContent }*
 * }
 * collectionContent = {
 *      attribute name { text },
 *      attribute namekey { text },
 *      element property { propertyContent }*,
 *      element object { objectContent }*
 *      element collection { collectionContent }*
 * }
 * start = element profile {
 *      element display-section {
 *          attribute name { text },
 *          attribute namekey { text },
 *          element property { propertyContent }*,
 *          element object { objectContent }*,
 *          element collection { collectionContent }*
 *      }*
 *  }
 * </pre>
 *
 * <h2>Profile privacy settings</h2>
 *
 * <p>Each property has visibility settings, because we may need settings there
 * for some use cases and/or performance, but currently visibility is controlled
 * at the section level. We'll store a property for each section, with the
 * name of {section}.visibility and the visibility of the property will
 * determine the visibility of the section.</p>
 */
public abstract class PropDefinition {
    private static Log log = LogFactory.getLog(PropertyDefinition.class);

    /** Display sections, each has property defs */
    private Map<String, DisplaySectionDefinition> sectionDefs =
            new LinkedHashMap<String, DisplaySectionDefinition>();

    /** All properties defs in map form */
    private Map<String, PropertyDefinition> propertyDefs =
            new LinkedHashMap<String, PropertyDefinition>();

    /** All properties object defs in map form */
    private Map<String, PropertyObjectDefinition> propertyObjectDefs =
            new LinkedHashMap<String, PropertyObjectDefinition>();

    /** All properties object collection defs in map form */
    private Map<String, PropertyObjectCollectionDefinition> propertyObjectCollectionDefs =
            new LinkedHashMap<String, PropertyObjectCollectionDefinition>();


    protected PropDefinition() { 
    };
    
//    public PropDefinition(PropDefinition propDef, String defFile) throws SocialSiteException {
//        init(propDef, getClass().getResourceAsStream(defFile));
//        init(this, getClass().getResourceAsStream("/profiledefs.xml"));
//    }

    public Collection<DisplaySectionDefinition>
            getDisplaySectionDefinitions() { return sectionDefs.values() ;}

    public Collection<PropertyDefinition>
            getPropertyDefinitions() { return propertyDefs.values() ;}

    public Collection<PropertyObjectDefinition>
            getPropertyObjectDefinitions() { return propertyObjectDefs.values() ;}

    public Collection<PropertyObjectCollectionDefinition>
            getPropertyObjectCollectionDefinitions() { return propertyObjectCollectionDefs.values() ;}

    /** Gets property definiton for a property specified by name */
    public PropertyObjectDefinition getPropertyObjectDefinition(String name) {
        return propertyObjectDefs.get(name);
    }

    /** Gets property definiton for a property specified by name */
    public PropertyObjectCollectionDefinition getPropertyObjectCollectionDefinition(String name) {
        return propertyObjectCollectionDefs.get(name);
    }

    public String getId() { 
        return "dummy";
    }

    public abstract static class PropertyDefinitionHolder {
        private String namekey = null;   // I18N name key of property
        private String path = null;      // path to property
        private String shortName = null; // short name of property
        private List<PropertyDefinition> properties = new ArrayList<PropertyDefinition>();
        private List<PropertyObjectDefinition> propertyObjects = new ArrayList<PropertyObjectDefinition>();
        private List<PropertyObjectCollectionDefinition> propertyObjectCollections = new ArrayList<PropertyObjectCollectionDefinition>();

        public PropertyDefinitionHolder(String path, String shortName, String namekey) {
            this.path = path;
            this.shortName = shortName;
            this.namekey = namekey;
        }

        public abstract String getBasePath();

        public String getName() { return path + "_" + shortName; }
        public String getShortName() { return shortName; }
        public String getNamekey() { return namekey; }

        public List<PropertyDefinition> getPropertyDefinitions() { return properties; }
        public void setPropertyDefinitions(List<PropertyDefinition> properties) { this.properties = properties; }

        public List<PropertyObjectDefinition> getPropertyObjectDefinitions() { return propertyObjects; }
        public void setPropertyObjectDefinitions(List<PropertyObjectDefinition> propertyObjects) { this.propertyObjects = propertyObjects; }

        public List<PropertyObjectCollectionDefinition> getPropertyObjectCollectionDefinitions() {return propertyObjectCollections;}
        public void setPropertyObjectCollectionDefinitions(List<PropertyObjectCollectionDefinition> propertyObjectCollections) {this.propertyObjectCollections = propertyObjectCollections;}

        public JSONObject toJSON() {
            try {
                JSONObject jsonSection = new JSONObject();
                jsonSection.put("name", getName());
                jsonSection.put("short_name", getShortName());
                jsonSection.put("local_name", TextUtil.format(getNamekey()));

                JSONArray jsonProperties = new JSONArray();
                for (PropertyDefinition p : this.getPropertyDefinitions()) {
                    jsonProperties.put(p.toJSON());
                }
                jsonSection.put("properties", jsonProperties);

                JSONArray jsonPropertyObjects = new JSONArray();
                for (PropertyObjectDefinition po : this.getPropertyObjectDefinitions()) {
                    jsonPropertyObjects.put(po.toJSON());
                }
                jsonSection.put("propertyObjects", jsonPropertyObjects);

                JSONArray jsonPropertyObjectCollections = new JSONArray();
                for (PropertyObjectCollectionDefinition po : this.getPropertyObjectCollectionDefinitions()) {
                    jsonPropertyObjectCollections.put(po.toJSON());
                }
                jsonSection.put("propertyObjectCollections", jsonPropertyObjectCollections);

                return jsonSection;
            } catch (JSONException ex) {
                log.error("ERROR outputting JSON data", ex);
                return null;
            }
        }
    }


    /** Represents section of profile properties and objects. */
    public static class DisplaySectionDefinition extends PropertyDefinitionHolder {
        private String sectionName = null;

        public DisplaySectionDefinition(String shortName, String namekey) {
            super("", shortName, namekey);
            this.sectionName = shortName;
        }

        public String getBasePath() {
            return getName();
        }

        public String getName() {
            return sectionName;
        }


    }


    /** Represents a profile property object, such as an address, with multiple properties */
    public static class PropertyObjectDefinition extends PropertyDefinitionHolder {

        public PropertyObjectDefinition(String path, String shortName, String namekey) {
            super(path, shortName, namekey);
            log.debug("Created propery object definition: " + getName());
        }

        public String getBasePath() {
            return getName();
        }

        public JSONObject toJSON() {
            try {
                JSONObject jsonPropertyObject = new JSONObject();
                jsonPropertyObject.put("name", getName());
                jsonPropertyObject.put("short_name", getShortName());
                jsonPropertyObject.put("local_name", TextUtil.format(getNamekey()));

                JSONArray jsonProperties = new JSONArray();
                for (PropertyDefinition p : this.getPropertyDefinitions()) {
                    jsonProperties.put(p.toJSON());
                }
                jsonPropertyObject.put("properties", jsonProperties);

                JSONArray jsonPropertyObjects = new JSONArray();
                for (PropertyObjectDefinition po : this.getPropertyObjectDefinitions()) {
                    jsonPropertyObjects.put(po.toJSON());
                }
                jsonPropertyObject.put("propertyObjects", jsonPropertyObjects);

                JSONArray jsonPropertyObjectCollections = new JSONArray();
                for (PropertyObjectCollectionDefinition po : this.getPropertyObjectCollectionDefinitions()) {
                    jsonPropertyObjectCollections.put(po.toJSON());
                }
                jsonPropertyObject.put("propertyObjectCollections", jsonPropertyObjectCollections);

                return jsonPropertyObject;

            } catch (JSONException ex) {
                log.error("ERROR outputting JSON data", ex);
                return null;
            }
        }
    }


    /** Represents a profile property object, such as an address, with multiple properties */
    public static class PropertyObjectCollectionDefinition extends PropertyDefinitionHolder {

        public PropertyObjectCollectionDefinition(String path, String shortName, String namekey) {
            super(path, shortName + "_{n}", namekey);
            log.debug("Created propery collection definition: " + getName());
        }

        public String getBasePath() {
            return getName();
        }
    }


    /** Represents profile property definition */
    public static class PropertyDefinition {
        private String path = null;
        private String shortName = null;
        private String namekey = null;
        private String type = null;
        private String defaultValue = null;
        private List<AllowedValue> allowedValues = new ArrayList<AllowedValue>();

        public PropertyDefinition(String path, String shortName, String namekey, String type) {
            this.path = path;
            this.shortName = shortName;
            this.namekey = namekey;
            this.type = type;
            log.debug("Created propery definition: " + getName());
        }
        public String getName() { return path + "_" + shortName; }
        public String getShortName() { return shortName; }
        public String getNamekey() { return namekey; }
        public String getType() { return type; }

        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
        public List<AllowedValue> getAllowedValues() { return allowedValues; }
        public void setAllowedValues(List<AllowedValue> allowedValues) { this.allowedValues = allowedValues; }

        public JSONObject toJSON() {
            try {
                JSONObject jsonProperty = new JSONObject();
                jsonProperty.put("name", getName());
                jsonProperty.put("local_name", TextUtil.format(namekey));
                jsonProperty.put("short_name", shortName);
                jsonProperty.put("type", type);
                if (defaultValue != null) {
                    jsonProperty.put("defaultvalue", defaultValue);
    }
                if (allowedValues != null && allowedValues.size() > 0) {
                    JSONArray jsonAllowedValues = new JSONArray();
                    for (AllowedValue allowedValue : allowedValues) {
                        jsonAllowedValues.put(allowedValue.toJSON());
                    }
                    jsonProperty.put("allowedValues", jsonAllowedValues);
                }
                return jsonProperty;
            } catch (JSONException ex) {
                log.error("ERROR outputting JSON data", ex);
                return null;
            }
        }
    }


    /** Represents profile property allowed value */
    public static class AllowedValue {
        private String name = null;
        private String namekey = null;
        public AllowedValue(String name, String namekey) {
            this.name = name;
            this.namekey = namekey;
        }
        public String getName() { return name; }
        public String getNamekey() { return namekey; }
        public JSONObject toJSON() {
            try {
                JSONObject jsonAllowedObject = new JSONObject();
                jsonAllowedObject.put("name", name);
                if (namekey != null) {
                    jsonAllowedObject.put("namekey", namekey);
                    jsonAllowedObject.put("local_name", TextUtil.format(namekey));
                }
                return jsonAllowedObject;
            } catch (JSONException ex) {
                log.error("ERROR outputting JSON data", ex);
                return null;
            }
        }
    }


    protected void init(PropDefinition profileDef, InputStream input) throws SocialSiteException {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(input);
            String ns = null; // TODO: namespace for ProfileDef

            // hold the current things we're working on
            Map<String, DisplaySectionDefinition> sdefs = new LinkedHashMap<String, DisplaySectionDefinition>();
            Stack<PropertyDefinitionHolder> propertyHolderStack = new Stack<PropertyDefinitionHolder>();
            List<AllowedValue> allowedValues = null;
            PropertyDefinition pdef = null;

            for (int event = parser.next();
                    event != XMLStreamConstants.END_DOCUMENT;
                    event = parser.next()) {
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        log.debug("START ELEMENT -- " + parser.getLocalName());

                        if ("display-section".equals(parser.getLocalName())) {
                            propertyHolderStack.push(new DisplaySectionDefinition(
                                parser.getAttributeValue(ns, "name"),
                                parser.getAttributeValue(ns, "namekey")));

                        } else if ("property".equals(parser.getLocalName())) {
                            PropertyDefinitionHolder holder = propertyHolderStack.peek();
                            pdef = new PropertyDefinition(
                                holder.getBasePath(), parser.getAttributeValue(ns, "name"),
                                parser.getAttributeValue(ns, "namekey"),
                                parser.getAttributeValue(ns, "type"));

                        } else if ("object".equals(parser.getLocalName())) {
                            PropertyDefinitionHolder holder = propertyHolderStack.peek();
                            propertyHolderStack.push(new PropertyObjectDefinition(
                                holder.getBasePath(), parser.getAttributeValue(ns, "name"),
                                parser.getAttributeValue(ns, "namekey")));

                        } else if ("collection".equals(parser.getLocalName())) {
                            PropertyDefinitionHolder holder = propertyHolderStack.peek();
                            propertyHolderStack.push(new PropertyObjectCollectionDefinition(
                                holder.getBasePath(), parser.getAttributeValue(ns, "name"),
                                parser.getAttributeValue(ns, "namekey")));

                        } else if ("allowed-values".equals(parser.getLocalName())) {
                            allowedValues = new ArrayList<AllowedValue>();

                        } else if ("value".equals(parser.getLocalName())) {
                            AllowedValue allowedValue = new AllowedValue(
                                parser.getAttributeValue(ns, "name"),
                                parser.getAttributeValue(ns, "namekey"));
                            allowedValues.add(allowedValue);

                        } else if ("default-value".equals(parser.getLocalName())) {
                            pdef.setDefaultValue(parser.getText());
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        log.debug("END ELEMENT -- " + parser.getLocalName());

                        if ("display-section".equals(parser.getLocalName())) {
                            DisplaySectionDefinition sdef =
                                    (DisplaySectionDefinition)propertyHolderStack.pop();
                            sdefs.put(sdef.getName(), sdef);

                        } else if ("property".equals(parser.getLocalName())) {
                            PropertyDefinitionHolder holder = propertyHolderStack.peek();
                            holder.getPropertyDefinitions().add(pdef);
                            propertyDefs.put(pdef.getName(), pdef);
                            pdef = null;

                        } else if ("object".equals(parser.getLocalName())) {
                            PropertyObjectDefinition odef =
                                    (PropertyObjectDefinition)propertyHolderStack.pop();
                            PropertyDefinitionHolder holder = propertyHolderStack.peek();
                            holder.getPropertyObjectDefinitions().add(odef);

                            // add to list of all property object defs
                            propertyObjectDefs.put(odef.getName(), odef);
                            odef = null;

                        } else if ("collection".equals(parser.getLocalName())) {
                            PropertyObjectCollectionDefinition cdef =
                                    (PropertyObjectCollectionDefinition)propertyHolderStack.pop();
                            PropertyDefinitionHolder holder = propertyHolderStack.peek();
                            holder.getPropertyObjectCollectionDefinitions().add(cdef);

                            // add to list of all property object defs
                            propertyObjectCollectionDefs.put(cdef.getName(), cdef);
                            cdef = null;

                        } else if ("allowed-values".equals(parser.getLocalName())) {
                            pdef.setAllowedValues(allowedValues);
                            allowedValues = null;
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        break;

                    case XMLStreamConstants.CDATA:
                        break;

                } // end switch
            } // end while

            parser.close();

            profileDef.sectionDefs = sdefs;

        } catch (Exception ex) {
            throw new SocialSiteException("ERROR parsing profile definitions", ex);
        }
    }

    public JSONObject toJSON() {
        try {
            JSONObject jsonProfile = new JSONObject();
            JSONArray jsonSections = new JSONArray();
            for (String key : this.sectionDefs.keySet()) {
                DisplaySectionDefinition section = this.sectionDefs.get(key);
                jsonSections.put(section.toJSON());
            }
            jsonProfile.put("sections", jsonSections);
            return jsonProfile;
        } catch (JSONException ex) {
            log.error("ERROR outputting JSON data", ex);
            return null;
        }
    }
}
