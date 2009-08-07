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

import com.sun.socialsite.web.rest.model.ViewerGroupRelationship;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.URLStrategy;
import com.sun.socialsite.business.impl.JPAListenerManagerImpl;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Group information.
 */
@Entity
@Table(name = "ss_group")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name = "Group.getAll",      query = "SELECT g FROM Group g"),
    @NamedQuery(name = "Group.getByHandle", query = "SELECT g FROM Group g WHERE g.handle=?1"),
    @NamedQuery(name = "Group.getById",     query = "SELECT g FROM Group g WHERE g.id=?1"),
    @NamedQuery(name = "Group.getMostRecentlyUpdated", query = "SELECT g FROM Group g ORDER BY g.updated DESC"),
    @NamedQuery(name = "Group.getOldest",   query = "SELECT g FROM Group g ORDER BY g.created ASC")
})
public class Group implements Serializable {

    private static Log log = LogFactory.getLog(Group.class);

    /** Format of JSON returned */
    public static enum Format {
        FLAT,
        OPENSOCIAL,
        OPENSOCIAL_MINIMAL;
    }

    public static final String NAME =          "identification_name";
    public static final String HANDLE =        "identification_handle";
    public static final String DESCRIPTION =   "identification_description";
    public static final String VIEW_URL =      "viewUrl";
    public static final String THUMBNAIL_URL = "thumbnailUrl";

    // This is a temporary fix to be able to support group related widgets
    // that were developed before group profile implementations
    public static final String SIMPLE_NAME        = "name";
    public static final String SIMPLE_HANDLE      = "handle";
    public static final String SIMPLE_DESCRIPTION = "description";
    public static final String SIMPLE_URL         = "url";
    public static final String SIMPLE_IMAGE_URL   = "imageUrl";

    @Id
    @Column(nullable = false, updatable = false)
    protected String id = UUID.randomUUID().toString();

    // unique name of group
    protected String handle = null;
    protected String name = null;
    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @MapKey(name = "name")
    private Map<String, GroupProperty> properties;

    // description of group
    protected String description = null;
    @Basic(fetch = FetchType.LAZY)
    private Serializable image = null;
    private String imageType = null;
    private Timestamp created = new Timestamp(System.currentTimeMillis());
    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    // locale of group
    protected String locale = null;

    // public, invitational, private, network, friendsList
    public static enum Policy {
        allowAll, inviteOnly, adminOnly, emailRestriction, friendsList
    }
    @Enumerated(EnumType.STRING)
    protected Policy policy = null;
    protected String domainsAllowed = null;
    @OneToMany(mappedBy = "group", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<AppInstance> appInstances;

    //-------------------------------------------------------------- Properties
    public void addGroupProp(GroupProperty prop) {
        prop.setGroup(this);
        properties.put(prop.getName(), prop);
    }

    public GroupProperty getProperty(String name) {
        return properties.get(name);
    }

    public Map<String, GroupProperty> getProperties() {
        return properties;
    }

    public void removeProperty(GroupProperty prop) {
        properties.remove(prop.getName());
    }

    //-------------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Serializable getImage() {
        return image;
    }

    public void setImage(Serializable image) {
        this.image = image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getDomainsAllowed() {
        return domainsAllowed;
    }

    public void setDomainsAllowed(String domainsAllowed) {
        this.domainsAllowed = domainsAllowed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Timestamp(created.getTime());
    }

    public Date getUpdated() {
        return new Date(updated.getTime());
    }

    public void setUpdated(Date updated) {
        this.updated = new Timestamp(updated.getTime());
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Gets the app instances (for all collections).
     */
    public List<AppInstance> getAppInstances() {
        return appInstances;
    }

    /**
     * Sets the app instances (for all collections).
     */
    public void setAppInstances(List<AppInstance> appInstances) {
        this.appInstances = appInstances;
    }

    /**
     * Gets the app instances for a given collection.
     */
    public List<AppInstance> getAppInstances(String collection) {
        List<AppInstance> results = new ArrayList<AppInstance>(appInstances.size());
        for (AppInstance appInstance : appInstances) {
            if (collection.equals(appInstance.getCollection())) {
                results.add(appInstance);
            }
        }
        return results;
    }

    /**
     * Sets the app instances for a given collection.  Any existing app instances with
     * a different collection are unchanged.
     */
    public void setAppInstances(String collection, List<AppInstance> appInstances) {
        for (AppInstance appInstance : new ArrayList<AppInstance>(this.appInstances)) {
            if (collection.equals(appInstance.getCollection())) {
                this.appInstances.remove(appInstance);
            }
        }
        for (AppInstance appInstance : appInstances) {
            this.appInstances.add(appInstance);
        }
    }

    public String getViewURL() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getViewURL(this);
    }

    public String getViewActionName() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getViewActionName(this);
    }

    public String getEditURL() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getEditURL(this);
    }

    public String getEditActionName() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getEditActionName(this);
    }

     public String getAdminEditURL() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getAdminEditURL(this);
    }

    public String getAdminEditActionName() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getAdminEditActionName(this);
    }

     public String getMemberEditURL() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getMemberEditURL(this);
    }

    public String getMemberEditActionName() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getMemberEditActionName(this);
    }

    public String getImageURL() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getImageURL(this);
    }

    public String getThumbnailURL() {
        URLStrategy urlstrat = Factory.getSocialSite().getURLStrategy();
        return urlstrat.getThumbnailURL(this);
    }

    public JSONObject toJSON() {
        return toJSON(Format.FLAT, null);
    }

    /**
     * Returns Group data in either flat format, intended for use in our
     * Group editor Widget and in an OpenSocial-like format.
     * 
     * @param fmt    Format to be returned
     * @param viewer Specify viewer to get viewerRelationship field
     */
    public JSONObject toJSON(Format fmt, String viewerId) {
        JSONObject jo = new JSONObject();

        if (fmt.equals(Format.FLAT)) {
            try {
                for (GroupProperty prop : getProperties().values()) {
                    jo.put(prop.getName(), prop.getValue());
                }
                jo.put(NAME,          this.name);
                jo.put(HANDLE,        this.handle);
                jo.put(DESCRIPTION,   this.description);
                jo.put(VIEW_URL,           this.getViewURL());

                // Two hacks to support older widgets (still needed?)
                jo.put(THUMBNAIL_URL,     this.getImageURL());
                jo.put(SIMPLE_HANDLE, this.handle);

            } catch (JSONException ex) {
                String msg = "ERROR creating JSON for group: " + handle;
                if (log.isDebugEnabled()) {
                    log.debug(msg, ex);
                } else {
                    log.error(msg);
                }
            }
        } else {
            try {
                jo.put("id",           this.handle);
                jo.put("name",         this.name);
                jo.put("description",  this.description);
                jo.put("handle",       this.handle);
                jo.put("viewUrl",      this.getViewURL());
                jo.put("thumbnailUrl", this.getImageURL());

            } catch (JSONException ex) {
                String msg = "ERROR creating JSON for group: " + handle;
                if (log.isDebugEnabled()) {
                    log.debug(msg, ex);
                } else {
                    log.error(msg);
                }
            }
        }

        if (viewerId != null) {
            try {
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                Profile viewer = pmgr.getProfileByUserId(viewerId);
                ViewerGroupRelationship grel = new ViewerGroupRelationship(viewer, this);
                // Convert vrel to JSON. A null injector is OK,
                // its only needed for JSON to object
                BeanJsonConverter bcon = new BeanJsonConverter(null);
                jo.put("viewerRelationship", new JSONObject(bcon.convertToString(grel)));
                
            } catch (Exception ex) {
                log.error("ERROR fetching viewer group relationship", ex);
            }
        }

        return jo;
    }

    // update group based on incoming information from a JSON object
    public void update(JSONObject updatedValues) {

        // first, process the fields of the Group object
        for (Iterator it = updatedValues.keys(); it.hasNext();) {
            String key = (String) it.next();
            try {
                if (NAME.equals(key)) {
                    name = (String) updatedValues.get(key);
                } else if (HANDLE.equals(key)) {
                    handle = (String) updatedValues.get(key);
                } else if (DESCRIPTION.equals(key)) {
                    description = (String) updatedValues.get(key);
                }
            } catch (JSONException ex) {
                log.error("ERROR error reading JSON key: " + key, ex);
            }
        }
        DeleteMap deletes = new DeleteMap();
        GroupManager mgr = Factory.getSocialSite().getGroupManager();
        GroupDefinition grpDef = mgr.getGroupDefinition();
        for (GroupDefinition.DisplaySectionDefinition sdef : grpDef.getDisplaySectionDefinitions()) {
            updatePropertyHolder(sdef.getBasePath(), updatedValues, sdef, deletes);
        }
        processDeletes(deletes);        
    }
    

    /**This does the same thing that profile's prop edit does- 
     * need to refactor this - issue #52 */
    /**
     * Updates property values in a property holder: a display section, a
     * property object or a property object collection.
     *
     * @param instancePath   Full path of the property holder instance
     * @param updatedValues  JSON object with updated name/values
     * @param holder         Property holder definition
     * @param deletes        List of objects deleted from collections
     */
    private void updatePropertyHolder(
            String instancePath,
            JSONObject updatedValues,
            ProfileDefinition.PropertyDefinitionHolder holder,
            DeleteMap deletes) {

         GroupManager mgr = Factory.getSocialSite().getGroupManager();

        // process the properties defined in the holder
        boolean haveDeletes = false;
        for (ProfileDefinition.PropertyDefinition propDef : holder.getPropertyDefinitions()) {
            String fullPropertyName = instancePath + "_" + propDef.getShortName();
            GroupProperty prop = getProperty(fullPropertyName);
            String incomingProp = null;
            try {
                try {
                    incomingProp = (String) updatedValues.get(fullPropertyName);
                } catch (Exception intentionallyIgnored) {}

                if (incomingProp == null) {
                    continue;                    
                }                                
                if (prop == null) {
                    // If a property is not found, then create it based 
                    prop = new GroupProperty();
                    prop.setName(fullPropertyName);
                    prop.setValue("dummy");
                    prop.setNameKey(propDef.getNamekey());
                    prop.setVisibility(GroupProperty.VisibilityType.PRIVATE);
                    prop.setCreated(new Date());
                    prop.setUpdated(new Date());
                    mgr.saveGroupProperty(prop);
                    this.addGroupProp(prop);
                }

                // some special treatment for booleans
                if (prop.getValue() != null
                        && (prop.getValue().equals("true")
                         || prop.getValue().equals("false"))) {

                    if (incomingProp == null || !incomingProp.equals("on")) {
                        incomingProp = "false";
                    } else {
                        incomingProp = "true";
                    }
                }

                // only work on props that were submitted with the request
                if (incomingProp != null) {
                    prop.setValue(incomingProp.trim());
                    if (holder instanceof GroupDefinition.PropertyObjectCollectionDefinition
                            && "zzz_DELETE_zzz".equals(prop.getValue())) {
                        haveDeletes = true;
                    }
                }
            } catch (Exception ex) {
                log.error("ERROR setting property: " + fullPropertyName, ex);
            }
        }

        if (haveDeletes) {
            deletes.put((GroupDefinition.PropertyObjectCollectionDefinition)holder, instancePath);
        }

        // process the property objects defined in the holder
        for (GroupDefinition.PropertyObjectDefinition objectDef : holder.getPropertyObjectDefinitions()) {

            // check to see if any properties are children of this object
            String fullObjectName = instancePath + "_" + objectDef.getShortName();
            String[] names = JSONObject.getNames(updatedValues);
            for (int i=0; i<names.length; i++) {
                if (names[i].startsWith(fullObjectName)) {
                    updatePropertyHolder(fullObjectName, updatedValues, objectDef, deletes);
                }
            }
        }

        // and finally, process the property object collections defined in the holder
        for (GroupDefinition.PropertyObjectCollectionDefinition collectionDef : holder.getPropertyObjectCollectionDefinitions()) {

            // check to see if any properties are children of this collection
            for (int poi=1; poi<20; poi++) {
                String fullCollectionName = instancePath + "_" + collectionDef.getShortName().replace("{n}",Integer.toString(poi));
                String[] names = JSONObject.getNames(updatedValues);
                for (int i=0; i<names.length; i++) {
                    if (names[i].startsWith(fullCollectionName)) {
                        updatePropertyHolder(fullCollectionName, updatedValues, collectionDef, deletes);
                    }
                }
            }
        }
    }

    private void processDeletes(DeleteMap deletes) {
        for (GroupDefinition.PropertyObjectCollectionDefinition collectionDef : deletes.keySet()) {
            String collectionPath = deletes.get(collectionDef);

            collectionPath = collectionPath.substring(0, collectionPath.lastIndexOf("_"));
            log.debug("Processing deletes for collection at path: " + collectionPath);

            List<ObjectInstance> objectInstances = new ArrayList<ObjectInstance>();

            // Create collection of all object instances each with property instances
            for (int poi=1; poi<20; poi++) {
                ObjectInstance objectInstance = new ObjectInstance(collectionDef, collectionPath, poi);
                objectInstances.add(objectInstance);
            }

            // Delete any objects with property value zzz_DELETE_zzz
            log.debug("Deleting marked objects");
            for (ObjectInstance objectInstance : objectInstances) {
                objectInstance.deleteIfMarked();
            }

            try {
                Factory.getSocialSite().flush();
                log.debug("Flushed deletes out to database");
            } catch (Exception ex) {
                log.error("ERROR flushing property deletes", ex);
            }

            // Renumber remaining properties
            int count = 1;
            for (ObjectInstance objectInstance : objectInstances) {
                if (!objectInstance.deleted) {
                    objectInstance.resetIndex(count++);
                }
            }
        }
    }

    
    /** Helper class used only for processing property object deletes */
    class DeleteMap extends HashMap<GroupDefinition.PropertyObjectCollectionDefinition, String> {}


    /** Helper class used only for processing property object deletes */
    class ObjectInstance {
        String originalPath = null;
        String pathPattern = null;
        boolean deleted = false;

        public ObjectInstance(
                GroupDefinition.PropertyObjectCollectionDefinition collectionDef,
                String instancePath,
                int index) {
            this.originalPath = instancePath + "_" + index;
            this.pathPattern = instancePath + "_{n}";
            for (GroupDefinition.PropertyDefinition propDef : collectionDef.getPropertyDefinitions()) {
                try {
                    GroupProperty prop = getProperty(originalPath + "_" + propDef.getShortName());
                    if (prop != null && "zzz_DELETE_zzz".equals(prop.getValue())) {
                        deleted = true;
                    }

                } catch (Exception ex) {
                    log.error("ERROR processing deletes", ex);
                }
            }
        }

        public void resetIndex(int newIndex) {
            String newPath = pathPattern.replace("{n}", Integer.toString(newIndex));

            List<String> propnames = new ArrayList<String>(Group.this.getProperties().keySet());
            for (String propname : propnames) {
                if (propname.startsWith(originalPath)) {
                    GroupProperty prop = Group.this.getProperty(propname);
                    String newname = propname.substring(originalPath.length(), propname.length());
                    newname = newPath + newname;
                    if (propname.equals(newname)) continue;

                    // remove the old property from the map
                    Group.this.removeProperty(prop);

                    // rename and add the new property
                    prop.setName(newname);
                    Group.this.addGroupProp(prop);

                    try {
                        Factory.getSocialSite().flush();
                    } catch (Exception ex) {
                        log.error("ERROR flushing rename", ex);
                    }
                }
            }
        }

        public void deleteIfMarked() {
            if (deleted) {
                // delete all properties and nested properties
                List<String> propnames = new ArrayList<String>(Group.this.getProperties().keySet());
                for (String propname : propnames) {
                    if (propname.startsWith(originalPath)) {

                        // remove it from the map
                        GroupProperty prop = Group.this.getProperty(propname);
                        Group.this.removeProperty(prop);

                        // remove it from the database
                        GroupManager mgr = Factory.getSocialSite().getGroupManager();
                        try {
                            mgr.removeGroupProperty(prop);
                        } catch (Exception ex) {
                            log.error("ERROR removing property", ex);
                        }
                    }
                }
            }
        }
    }
    
    
    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return String.format("%s[handle=%s]", getClass().getSimpleName(), this.handle);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof Group != true) {
            return false;
        }
        Group o = (Group) other;
        return new EqualsBuilder().append(getHandle(), o.getHandle()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getHandle()).toHashCode();
    }
}
