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
import com.sun.socialsite.business.SocialSiteActivityManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.impl.JPAListenerManagerImpl;
import com.sun.socialsite.util.TextUtil;
import com.sun.socialsite.web.rest.model.ViewerRelationship;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.model.ListField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.model.Name;


/**
 * User profile data consisting of identifying information only: names and ids.
 * Properties are stored as name value pairs, defined by profiledef.xml.
 * Key properties are also stored as fields on this object.
 * TODO: XML output
 */
@Entity
@Table(name="ss_profile")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="Profile.getAll",
        query="SELECT p FROM Profile p ORDER BY p.lastName ASC, p.firstName ASC, p.middleName ASC"),

    @NamedQuery(name="Profile.getByUserId",
        query="SELECT p FROM Profile p WHERE p.userId=?1"),

    @NamedQuery(name="Profile.getMostRecentlyUpdated",
        query="SELECT p FROM Profile p ORDER BY p.updated DESC"),

    @NamedQuery(name="Profile.getOldest",
        query="SELECT p FROM Profile p ORDER BY p.created ASC")
})
public class Profile implements Serializable {

    private static Log log = LogFactory.getLog(Profile.class);

    /** Maximum number of items in a property collection */
    private static int COLLECTION_MAX = 20;

    private static String DELETE_FLAG = "zzz_DELETE_zzz";
    
    /** Format of JSON returned */
    public static enum Format {
        FLAT,
        OPENSOCIAL,
        OPENSOCIAL_MINIMAL;
    }

    /** Visibilities from most public to least public */
    public static enum VisibilityType {
        PUBLIC,
        ALLGROUPS,
        SOMEGROUPS,
        FRIENDS,
        PRIVATE;
    }

    // property names
    public static final String USERID       = "identification_name_userid";
    public static final String FIRSTNAME    = "identification_name_givenName";
    public static final String MIDDLENAME   = "identification_name_additionalName";
    public static final String LASTNAME     = "identification_name_familyName";
    public static final String NICKNAME     = "identification_nickName";
    public static final String SURTITLE     = "identification_name_honorificPrefix";
    public static final String DISPLAYNAME  = "identification_name_displayName";

    // additional property names
    public static final String THUMBNAILURL  = "identification_thumbnailurl";
    public static final String VIEWURL  = "identification_property_viewurl";

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    @OneToMany(mappedBy="profile", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    @MapKey(name="name")
    private Map<String, ProfileProperty> properties = new HashMap<String, ProfileProperty>();

    @OneToMany(mappedBy="profile", cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private List<AppInstance> appInstances;

    private String userId = null;

    private String lastName = null;

    private String middleName = null;

    private String firstName = null;

    private String primaryEmail = null;

    private String displayName = null;

    private String surtitle = null;

    private String nickname = null;

    @Enumerated(EnumType.STRING)
    private VisibilityType vistype = VisibilityType.PRIVATE;

    private int vislevel = 0;

    @Basic(fetch=FetchType.LAZY)
    private Serializable image = null;

    private String imageType = null;

    private boolean enabled = true;

    private Timestamp created = new Timestamp(new Date().getTime());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());


    //-------------------------------------------------------------- Properties

    /**
     * Add new profile property without name or value checks. Does not check
     * name or value against what is specified in the profile definition.
     */
    public void addProfileProp(ProfileProperty prop) {
        prop.setProfile(this);
        properties.put(prop.getName(), prop);
    }


    /**
     * Sets string profile property with proper checking to ensure valid name.
     * @param name SocialSite style flat property name of property
     * @param value Property value
     */
    public void setProfileProp(String name, String value) throws SocialSiteException {
        try {
            JSONObject update = new JSONObject();
            update.put(name, value);
            update(Profile.Format.FLAT, update);
        } catch (JSONException ex) {
            log.error("ERROR creating JSON object for property " + name + ": " + value);
        }
    }


    public ProfileProperty getProperty(String name) {
        return properties.get(name);
    }


    public Map<String, ProfileProperty> getProperties() {
        return properties;
    }


    public void removeProperty(ProfileProperty prop) {
        properties.remove(prop.getName());
    }


    /**
     * Get properties within specified section.
     * @param viewerId ID of user requesting data (null for public data only)
     * @return Properties visible to user
     */
    public Map<String, ProfileProperty> getPropertiesInSection(String section) {
        Map<String, ProfileProperty> insection = new TreeMap<String, ProfileProperty>();
        for (String key : getProperties().keySet()) {
            ProfileProperty prop = getProperties().get(key);
            if (prop.getName().startsWith(section + "_")) {
                insection.put(prop.getName(), prop);
            }
        }
        return insection;
    }


    /**
     * Get properties visible to user specified by viewerId
     * @param viewerId ID of user requesting data (null for public data only)
     * @return Properties visible to user
     */
    public Map<String, ProfileProperty> getPropertiesForViewer(String viewerId) throws SocialSiteException {

        // if viewer is owner then return everything
        if (viewerId != null && viewerId.equals(getUserId())) return getProperties();

        // find viewer profile so we can determine relationship
        Integer relationshipLevel = null;
        Map<String, Group> sharedGroups = null;
        Profile viewer = null;
        try {
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            viewer = pmgr.getProfileByUserId(viewerId);

        } catch (SocialSiteException e) {
            throw new SocialSiteException("Failed to get viewer profile", e);
        }

        Map<String, ProfileProperty> filtered = new TreeMap<String, ProfileProperty>();

        // loop through properties, adding those visible to the return map
        for (String key : getProperties().keySet()) {
            ProfileProperty prop = getProperties().get(key);
            Profile.VisibilityType vis = prop.getVisibility();

            // fail fast if possible
            if (vis == Profile.VisibilityType.PRIVATE) continue;

            // determine friend level once and if we have to
            if (relationshipLevel == null && vis == Profile.VisibilityType.FRIENDS) {
                relationshipLevel = determineRelationshipLevel(this, viewer);
            }
            if (relationshipLevel.intValue() < 0 && vis == Profile.VisibilityType.FRIENDS) continue;

            // determine shared groups once and only if we have to
            if ((    vis == Profile.VisibilityType.ALLGROUPS
                  || vis == Profile.VisibilityType.SOMEGROUPS) && sharedGroups == null) {
                sharedGroups = determineSharedGroups(this, viewer);
            }
            if (vis == Profile.VisibilityType.ALLGROUPS && sharedGroups.size() == 0) continue;
            if (vis == Profile.VisibilityType.SOMEGROUPS && sharedGroups.size() == 0) continue;

            // handle friends first
            if (relationshipLevel != null) {
                if (vis.compareTo(Profile.VisibilityType.FRIENDS) < 0) {
                    filtered.put(key, prop);

                } else if (vis == Profile.VisibilityType.FRIENDS) {
                    if (relationshipLevel.intValue() >= prop.getVisibilityLevel()) {
                        filtered.put(key, prop);
                    }
                }

            // next, allgroups
            } else if (sharedGroups.size() != 0  && vis == Profile.VisibilityType.ALLGROUPS) {
                filtered.put(key, prop);

            // and finally, somegroups
            } else if (vis == Profile.VisibilityType.SOMEGROUPS) {
                for (String handle : prop.getSomeGroups()) {
                    if (sharedGroups.get(handle) != null) {
                        filtered.put(key, prop);
                        break;
                    }
                }
            }
        }
        return filtered;
    }


    private Integer determineRelationshipLevel(Profile p1, Profile p2) throws SocialSiteException {
        Integer level = null;

        RelationshipManager fmgr = Factory.getSocialSite().getRelationshipManager();
        Relationship relationship = fmgr.getRelationship(p1, p2);
        if (relationship != null) {
            level = new Integer(1); // TODO: handle relationship level properly
        } else {
            level = new Integer(-1);
        }
        return level;
    }


    private Map<String, Group> determineSharedGroups(Profile p1, Profile p2) throws SocialSiteException {
        Map<String, Group> sharedGroups = new TreeMap<String, Group>();

        GroupManager gmgr = Factory.getSocialSite().getGroupManager();
        List<GroupRelationship> p1rels = gmgr.getMembershipsByProfile(p1, 0, -1);
        List<GroupRelationship> p2rels = gmgr.getMembershipsByProfile(p2, 0, -1);

        for (GroupRelationship groupRel1 : p1rels) {

            for (GroupRelationship groupRel2 : p2rels) {
                if (groupRel1.getGroup().getHandle().equals(groupRel2.getGroup().getHandle())) {
                    sharedGroups.put(groupRel1.getGroup().getHandle(), groupRel1.getGroup());
                }
            }
        }
        return sharedGroups;
    }


    //-------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userName) {
        this.userId = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public String getName() {
        if (StringUtils.isNotEmpty(displayName)) {
            return displayName;
        } else if (StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(lastName)) {
            return firstName + " " + lastName;
        } else {
            return userId;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNickName() {
        return nickname;
    }

    public void setNickName(String nickName) {
        this.nickname = nickName;
    }

    public String getSurtitle() {
        return surtitle;
    }

    public void setSurtitle(String surtitle) {
        this.surtitle = surtitle;
    }

    public VisibilityType getVisibiltyType() {
        return vistype;
    }

    public void setVisibilityType(VisibilityType vistype) {
        this.vistype = vistype;
    }

    public int getVisibilityLevel() {
        return vislevel;
    }

    public void setVisibilityLevel(int vislevel) {
        this.vislevel = vislevel;
    }

    public String getViewURL() {
        return Factory.getSocialSite().getURLStrategy().getViewURL(this);
    }

    public String getEditURL() {
        return Factory.getSocialSite().getURLStrategy().getEditURL(this);
    }

    public String getEditActionName() {
        return Factory.getSocialSite().getURLStrategy().getEditActionName(this);
    }

    public String getFriendRequestURL() {
        return Factory.getSocialSite().getURLStrategy().getFriendRequestURL(this);
    }

    public String getFriendRequestActionName() {
        return Factory.getSocialSite().getURLStrategy().getFriendRequestActionName(this);
    }

    public String getImageURL() {
        return Factory.getSocialSite().getURLStrategy().getImageURL(this);
    }

    public String getThumbnailURL() {
        return Factory.getSocialSite().getURLStrategy().getThumbnailURL(this);
    }


    // ----------------------------------------------------- JSON serialization


    /**
     * Return publicly viewable portions of profile in JSON format
     * Either as a flat collection or name and value pairs or as a hierarchy
     * that matches the OpenSocial Person model.
     *
     * @param fmt Format of JSON data returned
     */
    public JSONObject toJSON(Format fmt) {
        return toJSON(fmt, null, null);
    }


    /**
     * Return profile in JSON format either as a flat collection or name and
     * value pairs or as a hierarchy that matches the OpenSocial Person model.
     *
     * @param viewerId Filter by this user (or null for publicly visiable data)
     * @param fmt      Format of JSON data returned
     */
    public JSONObject toJSON(Format fmt, String viewerId) {
        return toJSON(fmt, viewerId, null);
    }

    /**
     * Return profile in JSON format either as a flat collection or name and
     * value pairs or as a hierarchy that matches the OpenSocial Person model.
     *
     * @param viewerId Filter by this user (or null for publicly visiable data)
     * @param fmt      Format of JSON data returned
     * @param fields   Fields to include (ignored except for viewerRelationship
     */
    public JSONObject toJSON(Format fmt, String viewerId, Set<String> fields) {

        JSONObject jo = new JSONObject();

        try {
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();

            if (fmt.equals(Format.FLAT)) {

                // for most properties we can auto-convert to flat JSON
                Map<String, ProfileProperty> props = null;
                try {
                    // only return properties visible to viewer,
                    // or only public properties if viewerId is null
                    for (ProfileProperty prop : getPropertiesForViewer(viewerId).values()) {
                        if ("stringarray".equals(prop.getType())) {
                            StringBuilder sb = new StringBuilder();
                            JSONArray ja = toJSONArray(prop.getValue());
                            for (int i = 0; i < ja.length(); i++) {
                                sb.append(ja.getString(i));
                                sb.append("\n");
                            }
                            jo.put(prop.getName(), sb.toString());
                        } else {
                            jo.put(prop.getName(), prop.getValue());
                        }
                    }
                } catch (SocialSiteException ex) {
                    log.error("Failed to fetch extended properties", ex);
                }

                // No matter what we always return key ID fields
                
                if (StringUtils.isNotEmpty(userId)) {
                    jo.put(USERID, userId);
                }
                if (StringUtils.isNotEmpty(firstName) && getJSONString(jo, FIRSTNAME) == null) {
                    jo.put(FIRSTNAME, firstName);
                }
                if (StringUtils.isNotEmpty(middleName) && getJSONString(jo, MIDDLENAME) == null) {
                    jo.put(MIDDLENAME, middleName);
                }
                if (StringUtils.isNotEmpty(lastName) && getJSONString(jo, LASTNAME) == null) {
                    jo.put(LASTNAME, lastName);
                }
                if (StringUtils.isNotEmpty(displayName) && getJSONString(jo, DISPLAYNAME) == null) {
                    jo.put(DISPLAYNAME, displayName);
                }                
                if (StringUtils.isNotEmpty(nickname) && getJSONString(jo, NICKNAME) == null) {
                    jo.put(NICKNAME, nickname);
                }

                // Ensure that primary email address is returned
                if (getJSONString(jo, "contact_emails_1_value") == null) {
                    jo.put("contact_emails_1_value", primaryEmail);
                    jo.put("contact_emails_1_primary", "true");
                }

            } else { // OPENSOCIAL or OPENSOCIAL_MINIMAL

                if (!fmt.equals(Format.OPENSOCIAL_MINIMAL)) {

                    // TODO: filter by viewerId vs. visibility
                    Map<String, ProfileProperty> props = getProperties();

                    // for most properties we can auto-convert to JSON hierarchy
                    jo = toJSONHierarchy(props);
                }

                // for database fields, we need some hard-coding
                jo.put(Person.Field.ID.toString(), getUserId());
                jo.put(Person.Field.NICKNAME.toString(), nickname);

                // name is stored in database fields, so it must be hardcoded
                JSONObject name = null;
                try { name = (JSONObject)jo.get("name"); }
                catch (Exception ignored) {}
                if (name == null) {
                    jo.put("name", name = new JSONObject());
                }

                // No matter what we always return key ID fields
                
                String nameString = getName();
                if (StringUtils.isNotEmpty(nameString)) {
                    // This is just a workaround for some ambiguity in what the Shindig JS wants
                    // TODO: trim this down to just use one key (once the ambiguity is resolved)
                    jo.put(Person.Field.DISPLAY_NAME.toString(), nameString);
                    name.put(Name.Field.FORMATTED.toString(), nameString);
                }
                if (StringUtils.isNotEmpty(firstName)) {
                    name.put(Name.Field.GIVEN_NAME.toString(), firstName);
                }
                if (StringUtils.isNotEmpty(middleName)) {
                    name.put(Name.Field.ADDITIONAL_NAME.toString(), middleName);
                }
                if (StringUtils.isNotEmpty(lastName)) {
                    name.put(Name.Field.FAMILY_NAME.toString(), lastName);
                }

                // Ensure that primary email address is returned
                JSONArray emails = getJSONArray(jo, Person.Field.EMAILS.name());
                if (emails == null) {
                    emails = new JSONArray();
                    jo.put(Person.Field.EMAILS.name(), emails);
                }
                if (emails.length() == 0) {
                    JSONObject email = new JSONObject();
                    email.put(ListField.Field.VALUE.name(), primaryEmail);
                    email.put(ListField.Field.PRIMARY.name(), Boolean.TRUE);
                    emails.put(email);
                }

                // some properties are computed and not listed in metadata

                // such as image and thumbnail URL
                jo.put("imageUrl", getImageURL());
                jo.put(Person.Field.THUMBNAIL_URL.toString(), getThumbnailURL());

                // and profile URL
                jo.put(Person.Field.PROFILE_URL.toString(), getViewURL());

                // and status
                if (getProperty(Person.Field.STATUS.toString()) != null) {
                    jo.put(Person.Field.STATUS.toString(),
                        getProperty(Person.Field.STATUS.toString()).getValue());
                }

                // populate viewer relationship, if requested
                if (viewerId != null && fields != null && fields.contains("viewerRelationship")) {
                    try {
                        Profile viewer = pmgr.getProfileByUserId(viewerId);
                        ViewerRelationship vrel = new ViewerRelationship(viewer, this);
                        // Convert vrel to JSON. A null injector is OK,
                        // its only needed for JSON to object
                        BeanJsonConverter bcon = new BeanJsonConverter(null);
                        jo.put("viewerRelationship", new JSONObject(bcon.convertToString(vrel)));
                    } catch (SocialSiteException ex) {
                        log.error("Failed to fetch viewer relationship", ex);
                    }
                }

                // spit out formatted JSON for debugging purposes
                if (log.isDebugEnabled()) log.debug(jo.toString(4));

            }

        //} catch (SocialSiteException e) {
            //String msg = "Failed to create JSON for profile: " + id;
            //log.error(msg, e);
        } catch (JSONException e) {
            String msg = "Failed to create JSON for profile: " + id;
            log.error(msg, e);
        }

        return jo;

    }


    /**
     * Use this instead of <code>new JSONArray(s)</code> directly and you'll
     * be protected against cases where is is numm or empty.
     */
    private static JSONArray toJSONArray(String s) throws JSONException {
      if ((s == null) || ("".equals(s))) {
          return new JSONArray();
      } else {
          return new JSONArray(s);
      }
    }


    private static JSONObject toJSONHierarchy(Map<String, ProfileProperty> props) {
        JSONObject jsonObject = new JSONObject();
        try {
            // get collection of properties allowed by viewerId
            ProfileManager mgr = Factory.getSocialSite().getProfileManager();
            ProfileDefinition profileDef = mgr.getProfileDefinition();

            // loop through display sections, call toJSONHierarchy for each
            for (ProfileDefinition.DisplaySectionDefinition sdef : profileDef.getDisplaySectionDefinitions()) {
                toJSONHierarchy(jsonObject, sdef.getBasePath(), sdef, props);
            }

        } catch (JSONException e) {
            String msg = "Failed to create JSON for profile properties";
            log.error(msg, e);
        }

        return jsonObject;
    }


    /**
     * Add JSON fields to object in context of object model path, property
     * defintion and set of properties to be exposed, recursive.
     *
     * @param jsonObject   Add to this object and return it
     * @param instancePath Property name prefix for current level
     * @param holder       Property holder that defines current level
     * @param properties   Properties to be included
     *
     * @return Same JSON object that was passed in, but with added content
     */
    private static void toJSONHierarchy(
            JSONObject jsonObject,
            String     instancePath,
            ProfileDefinition.PropertyDefinitionHolder holder,
            Map<String, ProfileProperty> props) throws JSONException {

        // loop through properties defined in holder
        for (ProfileDefinition.PropertyDefinition propDef : holder.getPropertyDefinitions()) {

            String fullPropertyName = instancePath + "_" + propDef.getShortName();
            ProfileProperty profileProp = props.get(fullPropertyName);

            if (profileProp != null) {

                String shortName = propDef.getShortName();
                String type = propDef.getType();
                String value = profileProp.getValue();

                if (StringUtils.isEmpty(value)) continue;

                if (log.isDebugEnabled()) {
                    log.debug(String.format("Handling ProfileProp[type=%s,value=%s]", type, value));
                }

                // TODO: proper handling of OpenSocial string array fields
                if ("stringarray".equals(type)) {

                    // For string arrays, we are storing JSON in the database
                    jsonObject.put(shortName, toJSONArray(value));

                } else if ("integer".equals(type)) {

                    try {
                        jsonObject.put(shortName, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        log.warn("Cannot parse property '" + fullPropertyName + "' into an integer");
                    }

                } else if ("enum".equals(type)) {
                      
                    PropDefinition.AllowedValue allowedValue = null;
                    for (PropDefinition.AllowedValue av : propDef.getAllowedValues()) {
                        if (av.getName().equals(value)) {
                            allowedValue = av;
                            break;
                        }
                    }
                    if (allowedValue != null) {
                        jsonObject.put(shortName, value);
                    }

                } else if ("stringenum".equals(type)) {

                    PropDefinition.AllowedValue allowedValue = null;
                    for (PropDefinition.AllowedValue av : propDef.getAllowedValues()) {
                        if (av.getName().equals(value)) {
                            allowedValue = av;
                            break;
                        }
                    }
                    if (allowedValue != null) {
                        jsonObject.put(shortName, new JSONObject()
                            .put("value", allowedValue.getName())
                            .put("displayValue", TextUtil.getResourceString(allowedValue.getNamekey())));
                    }

                } else {

                    jsonObject.put(shortName, value);

                }

                // Set type on update: needed only to help migrate
                // users who were using system before type was part
                // of the ProfileProperty object
                profileProp.setType(type);

            }

        }

        // process the property objects defined in the holder
        for (ProfileDefinition.PropertyObjectDefinition objectDef : holder.getPropertyObjectDefinitions()) {
            String fullObjectName = instancePath + "_" + objectDef.getShortName();
            JSONObject newObject = new JSONObject();
            toJSONHierarchy(newObject, fullObjectName, objectDef, props);
            if (JSONObject.getNames(newObject) != null && JSONObject.getNames(newObject).length > 0) {
                jsonObject.put(objectDef.getShortName(), newObject);
            }
        }

        // and finally, process the property object collections defined in the holder
        for (ProfileDefinition.PropertyObjectCollectionDefinition collectionDef : holder.getPropertyObjectCollectionDefinitions()) {
            JSONArray newArray = new JSONArray();

            // check to see if any properties are children of this collection
            for (int poi=1; poi<COLLECTION_MAX; poi++) {
                String fullCollectionName = instancePath + "_" + collectionDef.getShortName().replace("{n}",Integer.toString(poi));
                JSONObject newObject = new JSONObject();
                toJSONHierarchy(newObject, fullCollectionName, collectionDef, props);
                if (JSONObject.getNames(newObject) != null && JSONObject.getNames(newObject).length > 0) {
                    log.debug("   collectionname: " + fullCollectionName);
                    newArray.put(newObject);
                } else {
                    // we're past the end of the collection
                    break;
                }
            }
            if (newArray.length() > 0) {
                String name = collectionDef.getShortName();
                jsonObject.put(name.substring(0, name.length()-4), newArray);
            }
        }
    }


    // ------------------------------------------------------ Update processing


   /**
     * <p>Update profile via JSON in either flat SocialSite format or
     * standard hierarchical OpenSocial format.</p>
    *
     * @param fmt Format of the updatedValues data
     * @param updatedValues JSON values to be updated
     */
    public void update(Format fmt, JSONObject updatedValues) throws SocialSiteException {
        if (fmt.equals(Format.FLAT)) {
            updateFlat(updatedValues);
        } else {
            updateOpenSocial(updatedValues);
        }
    }


    /**
     * <p>Update Profile and its ProfileProperties based on standard OpenSocial
     * Person data in JSON format.</p>
     *
     * @param updatedValues JSON values to be updated
     */
    private void updateOpenSocial(JSONObject updatedValues) throws SocialSiteException {
        ProfileManager mgr = Factory.getSocialSite().getProfileManager();
        ProfileDefinition profileDef = mgr.getProfileDefinition();

        // first process field of profile object
        JSONObject jsonName;
        try {
            jsonName = getJSONObject(updatedValues, Person.Field.NAME.name());
            if (jsonName != null) {
                if (getJSONString(jsonName, Name.Field.GIVEN_NAME.name()) != null) {
                    firstName = (String)jsonName.get(Name.Field.GIVEN_NAME.name());
                }
                if (getJSONString(jsonName, Name.Field.ADDITIONAL_NAME.name()) != null) {
                    middleName = (String)jsonName.get(Name.Field.ADDITIONAL_NAME.name());
                }
                if (getJSONString(jsonName, Name.Field.FAMILY_NAME.name()) != null) {
                    lastName = (String)jsonName.get(Name.Field.FAMILY_NAME.name());
                }
                if (getJSONString(jsonName, Name.Field.HONORIFIC_PREFIX.name()) != null) {
                    surtitle = (String)jsonName.get(Name.Field.HONORIFIC_PREFIX.name());
                }
                if (getJSONString(jsonName, Name.Field.FORMATTED.name()) != null) {
                    displayName = (String)jsonName.get(Name.Field.FORMATTED.name());
                }
            }

        } catch (JSONException ex) {
            throw new SocialSiteException("ERROR parsing key properties", ex);
        }

        for (ProfileDefinition.DisplaySectionDefinition sdef : profileDef.getDisplaySectionDefinitions()) {
            Profile.VisibilityType visibility = mgr.getSectionPrivacies(this).get(sdef.getShortName()).getVisibility();

            for (ProfileDefinition.PropertyDefinition propDef : sdef.getPropertyDefinitions()) {
                String fullPropertyName = sdef.getBasePath() + "_" + propDef.getShortName();
                if (updatedValues.has(propDef.getShortName())) {
                    ProfileProperty profileProp = getProperty(fullPropertyName);
                    try {
                        String value = null;
                        if ("stringenum".equals(propDef.getType())) {
                            value = updatedValues.getJSONObject(propDef.getShortName()).getString("value");
                        } else {
                            value = updatedValues.getString(propDef.getShortName());
                        }

                        if (profileProp == null) {
                            // If a property is not found, then create it based on its
                            // property definition, effectively creating all user properties
                            // the first time that the user edits his/her properties.
                            profileProp = new ProfileProperty();
                            profileProp.setType(propDef.getType());
                            profileProp.setName(fullPropertyName);
                            profileProp.setValue(value);
                            profileProp.setNameKey(propDef.getNamekey());
                            profileProp.setVisibility(visibility);
                            profileProp.setVisibilityLevel(1);
                            profileProp.setCreated(new Date());
                            profileProp.setUpdated(new Date());
                            mgr.saveProfileProperty(profileProp);
                            addProfileProp(profileProp);
                        } else {
                            profileProp.setValue(value);

                            // Set type on update: needed only to help migrate
                            // users who were using system before type was part
                            // of the ProfileProperty object
                            profileProp.setType(propDef.getType());
                        }

                    } catch (JSONException e) {
                        throw new SocialSiteException("Failed to get property " + profileProp.getName(), e);
                    }
                }
            }

            for (ProfileDefinition.PropertyObjectDefinition objectDef : profileDef.getPropertyObjectDefinitions()) {
                if (updatedValues.has(objectDef.getShortName())) {
                    try {
                        JSONObject updatedObject = updatedValues.getJSONObject(objectDef.getShortName());

                        for (ProfileDefinition.PropertyDefinition propDef : objectDef.getPropertyDefinitions()) {
                            String fullPropertyName = sdef.getBasePath() + "_" + objectDef.getShortName() + "_" + propDef.getShortName();

                            if (updatedObject.has(propDef.getShortName())) {
                                ProfileProperty profileProp = getProperty(fullPropertyName);
                                if (profileProp == null) {
                                    // If a property is not found, then create it based on its
                                    // porperty definition, effectively creating all user properties
                                    // the first time that the user edits his/her properties.
                                    log.debug("    New property");
                                    profileProp = new ProfileProperty();
                                    profileProp.setType(propDef.getType());
                                    profileProp.setName(fullPropertyName);
                                    profileProp.setValue("dummy");
                                    profileProp.setNameKey(propDef.getNamekey());
                                    profileProp.setVisibility(visibility);
                                    profileProp.setVisibilityLevel(1);
                                    profileProp.setCreated(new Date());
                                    profileProp.setUpdated(new Date());
                                    mgr.saveProfileProperty(profileProp);
                                    addProfileProp(profileProp);
                                }
                                profileProp.setValue(updatedObject.getString(propDef.getShortName()));
                            }
                        }
                    } catch (JSONException e) {
                        throw new SocialSiteException("Failed to get property in " + objectDef.getName(), e);
                    }
                }
            }

            for (ProfileDefinition.PropertyObjectCollectionDefinition collectionDef : profileDef.getPropertyObjectCollectionDefinitions()) {
                if (updatedValues.has(collectionDef.getShortName())) {
                    try {
                        JSONArray updatedCollection = updatedValues.getJSONArray(collectionDef.getShortName());
                        for (int i=0; i<updatedCollection.length() && i<COLLECTION_MAX; i++) {
                            JSONObject updatedObject = updatedCollection.getJSONObject(i);
                            String fullCollectionName = sdef.getBasePath() + "_" + collectionDef.getShortName().replace("{n}",Integer.toString(i));

                            for (ProfileDefinition.PropertyDefinition propDef : collectionDef.getPropertyDefinitions()) {
                                String fullPropertyName = fullCollectionName + "_" + propDef.getShortName();

                                if (updatedObject.has(propDef.getShortName())) {
                                    ProfileProperty profileProp = getProperty(fullPropertyName);
                                    if (profileProp == null) {
                                        // If a property is not found, then create it based on its
                                        // porperty definition, effectively creating all user properties
                                        // the first time that the user edits his/her properties.
                                        log.debug("    New property");
                                        profileProp = new ProfileProperty();
                                        profileProp.setType(propDef.getType());
                                        profileProp.setName(fullPropertyName);
                                        profileProp.setValue("dummy");
                                        profileProp.setNameKey(propDef.getNamekey());
                                        profileProp.setVisibility(visibility);
                                        profileProp.setVisibilityLevel(1);
                                        profileProp.setCreated(new Date());
                                        profileProp.setUpdated(new Date());
                                        mgr.saveProfileProperty(profileProp);
                                        addProfileProp(profileProp);
                                    }
                                    profileProp.setValue(updatedObject.getString(propDef.getShortName()));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new SocialSiteException("Failed to get property in " + collectionDef.getName(), e);
                    }
                }
            }
        }
    }


    /**
     * <p>Update Profile and its ProfileProperties based on data in JSON format
     * with name and value pairs that are named according to the naming scheme
     * defined in ProfileDefinition.java.</p>
     *
     * TODO: type and error checking for incoming values<br />
     * TODO: Ability to sort collections based on field specified in definition<br />
     *
     * @param updatedValues JSON values to be updated
     */
    private void updateFlat(JSONObject updatedValues) throws SocialSiteException {
        ProfileManager mgr = Factory.getSocialSite().getProfileManager();
        ProfileDefinition profileDef = mgr.getProfileDefinition();

        // first, process the fields of the Profile object
        for (Iterator it = updatedValues.keys(); it.hasNext();) {
            String key = (String)it.next();
            try {
                if (FIRSTNAME.equals(key)) {
                    firstName = (String) updatedValues.get(key);
                } else if (MIDDLENAME.equals(key)) {
                    middleName = (String)updatedValues.get(key);
                } else if (LASTNAME.equals(key)) {
                    lastName = (String)updatedValues.get(key);
                } else if (SURTITLE.equals(key)) {
                    surtitle = (String)updatedValues.get(key);
                } else if (NICKNAME.equals(key)) {
                    nickname = (String)updatedValues.get(key);
                } else if (DISPLAYNAME.equals(key)) {
                    displayName = (String)updatedValues.get(key);
                }

                for (int i=1; i<COLLECTION_MAX; i++) {
                    try {
                        String value = updatedValues.getString("contact_emails_" + i + "_value");
                        boolean primary = updatedValues.getBoolean("contact_emails_" + i + "_primary");
                        if (primary && !DELETE_FLAG.equals(value)) {
                            primaryEmail = value;
                            break;
                        }
                    } catch (Exception e) {
                        log.error("ERROR processing updated primary email", e);
                    }
                }

            } catch (JSONException e) {
                log.error("Failed to read JSON key: " + key, e);
            }
        }
        try {
            // process status update if there is one
            if (updatedValues.has(Person.Field.STATUS.toString())) {
                String status = (String)updatedValues.get(Person.Field.STATUS.toString());
                ProfileProperty statusProp = getProperty(Person.Field.STATUS.toString());
                if (statusProp == null) {
                    statusProp = new ProfileProperty();
                    statusProp.setName(Person.Field.STATUS.toString());
                    statusProp.setValue(status);
                    statusProp.setNameKey("ProfileView.statusLabel");
                    statusProp.setVisibility(Profile.VisibilityType.PRIVATE);
                    statusProp.setVisibilityLevel(1);
                    statusProp.setCreated(new Date());
                    statusProp.setUpdated(new Date());
                    mgr.saveProfileProperty(statusProp);
                    addProfileProp(statusProp);
                } else {
                    statusProp.setValue(status);
                    mgr.saveProfileProperty(statusProp);
                }

                // create an activity to reflect the status update
                SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
                amgr.recordActivity(this, null, SocialSiteActivity.STATUS, status);
            }
        } catch (Exception e) {
            log.error("Failed to update status", e);
        }

        DeleteMap deletes = new DeleteMap();
        for (ProfileDefinition.DisplaySectionDefinition sdef : profileDef.getDisplaySectionDefinitions()) {
            Profile.VisibilityType visibility = mgr.getSectionPrivacies(this).get(sdef.getShortName()).getVisibility();
            updatePropertyHolder(sdef.getBasePath(), updatedValues, sdef, visibility, deletes);
        }
        processDeletes(deletes);
    }

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
            Profile.VisibilityType visibility,
            DeleteMap deletes) {

         ProfileManager mgr = Factory.getSocialSite().getProfileManager();

        // process the properties defined in the holder
        boolean haveDeletes = false;
        for (ProfileDefinition.PropertyDefinition propDef : holder.getPropertyDefinitions()) {
            String fullPropertyName = instancePath + "_" + propDef.getShortName();
            ProfileProperty profileProp = getProperty(fullPropertyName);
            String incomingPropValue = null;
            try {
                try {
                    if ("stringarray".equals(propDef.getType())) {
                        incomingPropValue = (String)updatedValues.get(fullPropertyName);
                        JSONArray ja = new JSONArray();
                        StringTokenizer toker = new StringTokenizer(incomingPropValue, "\n");
                        while (toker.hasMoreElements()) {
                            ja.put(toker.nextToken());
                        }
                        incomingPropValue = ja.toString();
                    } else if ("boolean".equals(propDef.getType())) {
                        if (updatedValues.getBoolean(fullPropertyName)) {
                            incomingPropValue = "true";
                        } else {
                            incomingPropValue = "false";
                        }
                    } else {
                        incomingPropValue = (String)updatedValues.get(fullPropertyName);
                    }
                } catch (Exception ex) {
                    log.error("ERROR reading incoming property: " + fullPropertyName, ex);
                }

                if (incomingPropValue == null) {
                    continue;
                }
                log.debug("Processing property [" + fullPropertyName + "]");
                log.debug("    Request value is [" + incomingPropValue + "]");

                if (profileProp == null) {
                    // If a property is not found, then create it based on its
                    // porperty definition, effectively creating all user properties
                    // the first time that the user edits his/her properties.
                    log.debug("    New property");
                    profileProp = new ProfileProperty();
                    profileProp.setType(propDef.getType());
                    profileProp.setName(fullPropertyName);
                    profileProp.setValue("dummy");
                    profileProp.setNameKey(propDef.getNamekey());
                    profileProp.setVisibility(visibility);
                    profileProp.setVisibilityLevel(1);
                    profileProp.setCreated(new Date());
                    profileProp.setUpdated(new Date());
                    mgr.saveProfileProperty(profileProp);
                    addProfileProp(profileProp);
                } else {
                    log.debug("    Existing property");
                }

                // some special treatment for booleans
                if (profileProp.getValue() != null
                        && (profileProp.getValue().equals("true")
                         || profileProp.getValue().equals("false"))) {

                    if (incomingPropValue == null || !incomingPropValue.equals("true")) {
                        incomingPropValue = "false";
                    } else {
                        incomingPropValue = "true";
                    }
                }

                // only work on props that were submitted with the request
                if (incomingPropValue != null) {
                    log.debug("Setting new value for [" + fullPropertyName + "]");
                    profileProp.setValue(incomingPropValue.trim());
                    if (holder instanceof ProfileDefinition.PropertyObjectCollectionDefinition
                            && DELETE_FLAG.equals(profileProp.getValue())) {
                        haveDeletes = true;
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to set property: " + fullPropertyName, ex);
            }
        }

        if (haveDeletes) {
            deletes.put((ProfileDefinition.PropertyObjectCollectionDefinition)holder, instancePath);
        }

        // process the property objects defined in the holder
        for (ProfileDefinition.PropertyObjectDefinition objectDef : holder.getPropertyObjectDefinitions()) {

            // check to see if any properties are children of this object
            String fullObjectName = instancePath + "_" + objectDef.getShortName();
            String[] names = JSONObject.getNames(updatedValues);
            for (int i=0; i<names.length; i++) {
                if (names[i].startsWith(fullObjectName)) {
                    updatePropertyHolder(fullObjectName, updatedValues, objectDef, visibility, deletes);
                }
            }
        }

        // and finally, process the property object collections defined in the holder
        for (ProfileDefinition.PropertyObjectCollectionDefinition collectionDef : holder.getPropertyObjectCollectionDefinitions()) {

            // determine if collection is an OpenSocial ListField
            // assume it is if it has value, type and primary fields
            boolean listField = false;
            Map<String, ProfileDefinition.PropertyDefinition> propDefMap =
                new HashMap<String, ProfileDefinition.PropertyDefinition>();
            for (ProfileDefinition.PropertyDefinition propDef : collectionDef.getPropertyDefinitions()) {
                propDefMap.put(propDef.getShortName(), propDef);
            }
            if (   propDefMap.get("value") != null
                && propDefMap.get("type") != null
                && propDefMap.get("primary") != null) {
                listField = true;
            }

            // process collection defs of this type
            for (int poi=1; poi<COLLECTION_MAX; poi++) {
                String fullCollectionName = instancePath + "_" + collectionDef.getShortName().replace("{n}",Integer.toString(poi));
                String[] names = JSONObject.getNames(updatedValues);
                for (int i=0; i<names.length; i++) {
                    if (names[i].startsWith(fullCollectionName)) {
                        updatePropertyHolder(fullCollectionName, updatedValues, collectionDef, visibility, deletes);
                    }
                }
            }

            // special handling for ListFields: ensure no more than one primary
            int listCount = 0;
            boolean gotPrimary = false;
            if (listField) for (int poi=1; poi<COLLECTION_MAX; poi++) {
                listCount++;
                String fullCollectionName = instancePath + "_" + collectionDef.getShortName().replace("{n}",Integer.toString(poi));
                String primaryPropName = fullCollectionName + "_primary";
                ProfileProperty primaryProp = getProperty(primaryPropName);
                if (!gotPrimary && primaryProp != null && "true".equals(primaryProp.getValue())) {
                    // good, we have a primary
                    gotPrimary = true;
                } else if (gotPrimary && primaryProp != null && "true".equals(primaryProp.getValue())) {
                    // sorry, already got a primary
                    primaryProp.setValue("false");
                }
            }

            // special handling for ListFields: ensure at least one primary
            if (!gotPrimary && listCount > 0) {
                // no primary, so use first item
                String fullCollectionName = instancePath + "_" + collectionDef.getShortName().replace("{n}","1");
                String primaryPropName = fullCollectionName + "_primary";
                ProfileProperty primaryProp = getProperty(primaryPropName);
                if (primaryProp != null) {
                    primaryProp.setValue("true");
                }
            }
        }
    }

    private void processDeletes(DeleteMap deletes) {
        for (ProfileDefinition.PropertyObjectCollectionDefinition collectionDef : deletes.keySet()) {
            String collectionPath = deletes.get(collectionDef);

            collectionPath = collectionPath.substring(0, collectionPath.lastIndexOf("_"));
            log.debug("Processing deletes for collection at path: " + collectionPath);

            List<ObjectInstance> objectInstances = new ArrayList<ObjectInstance>();

            // Create collection of all object instances each with property instances
            for (int poi=1; poi<COLLECTION_MAX; poi++) {
                ObjectInstance objectInstance = new ObjectInstance(collectionDef, collectionPath, poi);
                objectInstances.add(objectInstance);
            }

            // Delete any objects with property value DELETE_FLAG
            log.debug("Deleting marked objects");
            for (ObjectInstance objectInstance : objectInstances) {
                objectInstance.deleteIfMarked();
            }

            try {
                Factory.getSocialSite().flush();
                log.debug("Flushed deletes out to database");
            } catch (SocialSiteException ex) {
                log.error("Failed to flush property deletes", ex);
            }

            // Renumber remaining properties
            log.debug("Renumbering remaining objects");
            int count = 1;
            for (ObjectInstance objectInstance : objectInstances) {
                if (!objectInstance.deleted) {
                    objectInstance.resetIndex(count++);
                }
            }
        }
    }


    /** Helper class used only for processing property object deletes */
    class DeleteMap extends HashMap<ProfileDefinition.PropertyObjectCollectionDefinition, String> {}


    /** Helper class used only for processing property object deletes */
    class ObjectInstance {
        String originalPath = null;
        String pathPattern = null;
        boolean deleted = false;

        public ObjectInstance(
                ProfileDefinition.PropertyObjectCollectionDefinition collectionDef,
                String instancePath,
                int index) {
            this.originalPath = instancePath + "_" + index;
            this.pathPattern = instancePath + "_{n}";
            for (ProfileDefinition.PropertyDefinition propDef : collectionDef.getPropertyDefinitions()) {
                try {
                    ProfileProperty profileProp = getProperty(originalPath + "_" + propDef.getShortName());
                    if (profileProp != null && DELETE_FLAG.equals(profileProp.getValue())) {
                        deleted = true;
                    }

                } catch (Exception e) {
                    log.error("Failed to process deletes", e);
                }
            }
        }

        public void resetIndex(int newIndex) {
            String newPath = pathPattern.replace("{n}", Integer.toString(newIndex));

            List<String> propnames = new ArrayList<String>(Profile.this.getProperties().keySet());
            for (String propname : propnames) {
                if (propname.startsWith(originalPath)) {
                    ProfileProperty prop = Profile.this.getProperty(propname);
                    String newname = propname.substring(originalPath.length(), propname.length());
                    newname = newPath + newname;
                    if (propname.equals(newname)) continue;

                    log.debug("Renaming: " + propname + " to " + newname);

                    // remove the old property from the map
                    Profile.this.removeProperty(prop);

                    // rename and add the new property
                    prop.setName(newname);
                    Profile.this.addProfileProp(prop);

                    try {
                        Factory.getSocialSite().flush();
                    } catch (SocialSiteException e) {
                        log.error("Failed to flush rename", e);
                    }
                }
            }
        }

        public void deleteIfMarked() {
            if (deleted) {
                // delete all properties and nested properties
                List<String> propnames = new ArrayList<String>(Profile.this.getProperties().keySet());
                for (String propname : propnames) {
                    if (propname.startsWith(originalPath)) {

                        // remove it from the map
                        ProfileProperty prop = Profile.this.getProperty(propname);
                        Profile.this.removeProperty(prop);

                        log.debug("Removing property: " + prop.getName());

                        // remove it from the database
                        ProfileManager mgr = Factory.getSocialSite().getProfileManager();
                        try {
                            mgr.removeProfileProperty(prop);
                        } catch (SocialSiteException e) {
                            log.error("Failed to remove property", e);
                        }
                    }
                }
            }
        }
    }


    //--------------------------------------------------------------- Utilities

    private String getJSONString(JSONObject jo, String key) {
        try {
            return jo.getString(key);
        } catch (JSONException ex) {
            return null;
        }
    }

    private JSONObject getJSONObject(JSONObject jo, String key) {
        try {
            return jo.getJSONObject(key);
        } catch (JSONException ex) {
            return null;
        }
    }

    private JSONArray getJSONArray(JSONObject jo, String key) {
        try {
            return jo.getJSONArray(key);
        } catch (JSONException ex) {
            return null;
        }
    }

    //------------------------------------------------------- Good citizenship

    public String toString() {
        return String.format("%s[userId=%s]", getClass().getSimpleName(), this.userId);
    }

    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof Profile != true) return false;
        Profile o = (Profile)other;
        return new EqualsBuilder().append(getUserId(), o.getUserId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getUserId()).toHashCode();
    }

}
