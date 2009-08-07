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

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AbstractManagerImpl;
import com.sun.socialsite.business.SocialSiteActivityManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.SearchManager;
import com.sun.socialsite.pojos.SocialSiteActivity;
import com.sun.socialsite.pojos.Relationship;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.ProfileDefinition;
import com.sun.socialsite.pojos.ProfileProperty;
import com.sun.socialsite.pojos.SectionPrivacy;
import com.sun.socialsite.userapi.User;
import com.sun.socialsite.userapi.UserManagementException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;


/**
 * JPA implementation of profile manager.
 */
@Singleton
public class JPAProfileManagerImpl extends AbstractManagerImpl implements ProfileManager {
    private static Log log = LogFactory.getLog(JPAProfileManagerImpl.class);
    private final JPAPersistenceStrategy strategy;
    private final ProfileDefinition profileDef;


    @Inject
    protected JPAProfileManagerImpl(JPAPersistenceStrategy strat, ProfileDefinition profileDef) {
        log.debug("Instantiating JPA Profile Manager");
        this.strategy = strat;
        this.profileDef = profileDef;
    }

    public void release() {}

    public Profile getProfile(String id) throws SocialSiteException {
        return (Profile)strategy.load(Profile.class, id);
    }

    public void saveProfile(Profile profile) throws SocialSiteException {
        saveProfile(profile, false);
    }

    public void saveProfile(Profile profile, boolean createActivity) throws SocialSiteException {

        // TODO: Is this the right place to set this?  And should we be using
        // the DB's date instead (i.e. CURRENT_TIMESTAMP).
        // Date now = new Date();
        // profile.setUpdated(now);

        strategy.store(profile);

        if (createActivity) {
            SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
            amgr.recordActivity(profile, null, SocialSiteActivity.EDITED_PROFILE, profile.getUserId());
        }

        // ensure that profile has section privacies
        getSectionPrivacies(profile, true);
    }

    public void removeProfile(Profile profile) throws SocialSiteException {
        strategy.remove(profile);
    }

    public Profile getProfileByUserId(String userid) throws SocialSiteException {
        if (userid == null)
            throw new SocialSiteException("userid is null");
        Query query = strategy.getNamedQuery("Profile.getByUserId");
        query.setParameter(1, userid);
        try {
            return (Profile)query.getSingleResult();
        } catch (NonUniqueResultException ne) {
            throw new SocialSiteException("ERROR: more than one user with userid: " + userid, ne);
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<Profile> searchProfiles(int offset, int length, String pattern) throws SocialSiteException {
        if (pattern == null)
            throw new SocialSiteException("Pattern to match is null");

        try {
            SearchManager searchManager = Factory.getSocialSite().getSearchManager();
            return searchManager.getProfiles(this, offset, length, pattern);
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Profile> getProfiles(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("Profile.getAll");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<Profile>)query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Profile> getMostRecentlyUpdatedProfiles(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("Profile.getMostRecentlyUpdated");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<Profile>)query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Profile> getOldestProfiles(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("Profile.getOldest");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<Profile>)query.getResultList();
    }

    public void saveProfileProperty(ProfileProperty profileProp) throws SocialSiteException {
        strategy.store(profileProp);
    }

    public void removeProfileProperty(ProfileProperty profileProp) throws SocialSiteException {
        strategy.remove(profileProp);
    }

    public ProfileDefinition getProfileDefinition() {
        return profileDef;
    }

    /**
     * Return Profiles for set of users.
     *
     * @param userId    User ID to filter on, or null if none
     * @param groupId   Group ID to filter on, or null if none
     * @param viewerId  User ID of viewer requesting data
     * @param sort      Sort orde
     * @param filter    Filter (all, topFriends, hasApp)
     * @param first     Index into result collection
     * @param max       Max results to return
     * @param details   Properties to be returned
     */
    public List<Profile> getProfiles(
            String userId,
            String groupId,
            String viewerId,
            CollectionOptions collectionOptions,
            int first,
            int max,
            Set<String> details) throws SocialSiteException {

        List<Profile> profiles = new ArrayList<Profile>();

        if (userId != null && groupId == null) {
            // return people related to user
            Profile profile = getProfileByUserId(userId);
            RelationshipManager fmgr = Factory.getSocialSite().getRelationshipManager();
            List<Relationship> friends = fmgr.getRelationships(profile, first, max);
            for (Relationship rel : friends) {
                profiles.add(rel.getProfileTo());
            }

        } else if (userId != null && groupId != null) {
            // return friends of user who are also members of the group
            Profile profile = getProfileByUserId(userId);
            RelationshipManager fmgr = Factory.getSocialSite().getRelationshipManager();
            List<Relationship> friends = fmgr.getRelationships(profile, first, max);
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group group = gmgr.getGroupByHandle(groupId);

            for (Relationship rel : friends) {
                Profile friend = rel.getProfileTo();
                if (gmgr.isMember(group, friend)) {
                    profiles.add(friend);
                }
           }

        } else if (groupId != null) {
            // return people related to group
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group group = gmgr.getGroupByHandle(groupId);
            List<GroupRelationship> groups = gmgr.getMembershipsByGroup(group, first, max);
            for (GroupRelationship rel : groups) {
                profiles.add(rel.getUserProfile());
            }

        } else {
            // return all people
            List<Profile> all = this.getProfiles(first, max);
            for (Profile profile : all) {
                profiles.add(profile);
            }
        }
        return profiles;
    }


    /**
     * Get section privacy objects for each defined property section.
     * You can modify these and use updateSectionPrivacy() to save the changes.
     * @param profile Profile for which to fetch
     * @return Section privacy objects
     * @throws com.sun.socialsite.SocialSiteException
     */
    public Map<String, SectionPrivacy> getSectionPrivacies(
            Profile profile) throws SocialSiteException {
        return getSectionPrivacies(profile, false);
    }

    private Map<String, SectionPrivacy> getSectionPrivacies(
            Profile profile, boolean create) throws SocialSiteException {
        Map<String, SectionPrivacy> retmap = new TreeMap<String, SectionPrivacy>();

        // Attempt to return privacy info in locale of user who owns it
        // TODO: should profile include a locale field?
        Locale locale = Locale.getDefault();
        try {
            User user = Factory.getSocialSite().getUserManager().getUserByUserId(profile.getUserId());
            if (user != null && user.getLocale() != null) {
                locale = new Locale(user.getLocale());
            }
        } catch (UserManagementException ex) {
            log.debug("ERROR looking up user for profile", ex);
        }

        // loop through section defintions
        ProfileDefinition pdef = getProfileDefinition();
        for (ProfileDefinition.DisplaySectionDefinition sdef : pdef.getDisplaySectionDefinitions()) {

            // if privacy setting property doesn't exist then create one
            ProfileProperty prop = getPrivacySettingProperty(
                profile, sdef.getName(), sdef.getNamekey(), create);

            // add privacy setting object to collection to be returned
            retmap.put(sdef.getName(), new SectionPrivacy(prop, locale));
        }
        return retmap;
    }


    /**
     * Update a section privacy.
     * @param profile Profile in which to update
     * @param privacy Updated section privacy object
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void updateSectionPrivacy(
            Profile profile, SectionPrivacy privacy) throws SocialSiteException {

        ProfileProperty visprop = getPrivacySettingProperty(
            profile, privacy.getSectionName(), privacy.getNamekey(), false);
        visprop.setVisibilityLevel(privacy.getRelationshipLevel());
        visprop.setVisibility(privacy.getVisibility());
        visprop.setSomeGroups(privacy.getSomeGroups());
        saveProfileProperty(visprop);

        Map<String, ProfileProperty> sectionProps =
                profile.getPropertiesInSection(privacy.getSectionName());
        for (String key : sectionProps.keySet()) {
            ProfileProperty prop = sectionProps.get(key);
            prop.setVisibility(privacy.getVisibility());
            prop.setVisibilityLevel(privacy.getRelationshipLevel());
            prop.setSomeGroups(privacy.getSomeGroups());
            saveProfileProperty(prop);
        }
    }

    private ProfileProperty getPrivacySettingProperty(
            Profile profile, String sectionName, String sectionNamekey, boolean create)
            throws SocialSiteException {
        String propName = sectionName + "_visibility";
        ProfileProperty prop = profile.getProperty(propName);
        if (prop == null && create) {
            prop = new ProfileProperty();
            prop.setName(propName);
            prop.setValue("dummy");
            prop.setNameKey(sectionNamekey);
            prop.setVisibility(Profile.VisibilityType.PRIVATE);
            prop.setVisibilityLevel(1);
            prop.setCreated(new Date());
            prop.setUpdated(new Date());
            saveProfileProperty(prop);
            profile.addProfileProp(prop);
        }
        return prop;
    }

}
