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
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ListenerManager;
import com.sun.socialsite.business.SearchManager;
import com.sun.socialsite.business.startup.Startup;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.SocialSiteActivity;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupDefinition;
import com.sun.socialsite.pojos.GroupProperty;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.GroupRequest;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.util.TextUtil;
import com.sun.socialsite.util.MailUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.persistence.NoResultException;
import javax.persistence.PostPersist;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * JPA implementation Group manager.
 */
@Singleton
public class JPAGroupManagerImpl extends AbstractManagerImpl implements GroupManager {

    private static Log log = LogFactory.getLog(JPAGroupManagerImpl.class);
    private final JPAPersistenceStrategy strategy;
    private final GroupDefinition groupDef;

    @Inject
    protected JPAGroupManagerImpl(JPAPersistenceStrategy strat, GroupDefinition groupDef, ListenerManager listenerManager) {
        log.debug("Instantiating JPA Group Manager");
        this.strategy = strat;
        this.groupDef = groupDef;
        // TODO: Move this to initialize() method?
        listenerManager.addListener(GroupRequest.class, new GroupRequestListener());
    }

    public void release() {
    }

    public void createGroup(Group group, Profile creator) throws SocialSiteException {

        // TBD: add checks for permissions to create groups.
        // needs to tie in to config settings

        if (getGroupByHandle(group.getHandle()) != null) {
            throw new SocialSiteException("A Group with handle " +
                    group.getHandle() + " already exists");
        }
        saveGroup(group);

        GroupRelationship rel = new GroupRelationship();
        rel.setGroup(group);
        rel.setUserProfile(creator);
        rel.setRelcode(GroupRelationship.Relationship.FOUNDER);
        saveGroupRelationship(rel);

        SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
        amgr.recordActivity(creator, group, SocialSiteActivity.CREATED_GROUP, group.getName());
    }

    public void saveGroup(Group group) throws SocialSiteException {
        strategy.store(group);
    }

    public void removeGroup(Group group) throws SocialSiteException {
        List<GroupRelationship> groupRels = getMembershipsByGroup(group,0,-1);
        for (GroupRelationship groupRel : groupRels) {
            strategy.remove(groupRel);
        }
        List<GroupRequest> groupReqs = getMembershipRequestsByGroup(group,0,-1);
        for (GroupRequest groupReq : groupReqs) {
            strategy.remove(groupReq);
        }
        strategy.remove(group);
    }

    public void saveGroupRelationship(GroupRelationship grel) throws SocialSiteException {
        strategy.store(grel);
    }

    public Group getGroupById(String id) throws SocialSiteException {
        if (id == null) {
            throw new SocialSiteException("id is null");
        }
        Query query = strategy.getNamedQuery("Group.getById");
        query.setParameter(1, id);
        try {
            return (Group) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Group getGroupByHandle(String handle) throws SocialSiteException {
        if (handle == null) {
            throw new SocialSiteException("handle is null");
        }
        Query query = strategy.getNamedQuery("Group.getByHandle");
        query.setParameter(1, handle);
        try {
            return (Group) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Group> getGroups(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("Group.getAll");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<Group>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Group> getMostRecentlyUpdatedGroups(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("Group.getMostRecentlyUpdated");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<Group>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Group> getOldestGroups(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("Group.getOldest");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<Group>) query.getResultList();
    }

    public List<Group> searchGroups(int offset, int length, String pattern) throws SocialSiteException {
        if (pattern == null) {
            throw new SocialSiteException("Pattern to match is null");
        }

        try {
            SearchManager searchManager = Factory.getSocialSite().getSearchManager();
            return searchManager.getGroups(this, offset, length, pattern);
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }
    }

    public void createMembership(Group group, Profile profile,
            GroupRelationship.Relationship relcode) throws SocialSiteException {
        GroupRelationship rel = new GroupRelationship();
        rel.setGroup(group);
        rel.setUserProfile(profile);
        rel.setRelcode(relcode);
        strategy.store(rel);
    }

    public boolean isMember(Group group, Profile profile) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroupAndUserProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        try {
            return (query.getSingleResult() != null);
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean isFounder(Group group, Profile profile) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroupAndUserProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        try {
            GroupRelationship rel = (GroupRelationship) query.getSingleResult();
            if (rel != null) {
                return (rel.getRelcode() == GroupRelationship.Relationship.FOUNDER);
            }
        } catch (NoResultException e) {
            // Do nothing
        }
        return false;
    }

    public boolean isAdmin(Group group, Profile profile) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroupAndUserProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        try {
            GroupRelationship rel = (GroupRelationship) query.getSingleResult();
            if (rel != null) {
                return (rel.getRelcode() == GroupRelationship.Relationship.ADMIN) ||
                       (rel.getRelcode() == GroupRelationship.Relationship.FOUNDER);
            }
        } catch (NoResultException e) {
            // Do nothing
        }
        return false;
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<GroupRelationship> getMembershipsByProfile(Profile profile, int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByUserProfile");
        query.setParameter(1, profile);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<GroupRelationship>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<GroupRelationship> getAdminsOfGroup(Group group, int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getAdminsOfGroup");
        query.setParameter(1, group);
        query.setParameter(2, GroupRelationship.Relationship.ADMIN);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<GroupRelationship>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<GroupRelationship> getMembershipsByGroup(Group group, int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroup");
        query.setParameter(1, group);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<GroupRelationship>) query.getResultList();
    }

    public GroupRelationship getMembership(Group group, Profile profile) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroupAndProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        try {
            return (GroupRelationship)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Group> getPopularGroups(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getPopularGroups");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<Group>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Group> getFriendsGroups(Profile profile) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getFriendsGroups");
        query.setParameter(1, profile);
        List<Group> groups = query.getResultList();
        return groups;
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public Set<Group> getGroupMembersGroups(Group group) throws SocialSiteException {
        /* TBD : This is total kludge code; should be replaced by an efficient QL */
        List<GroupRelationship> groupMembers = getMembershipsByGroup(group, 0, -1);
        Query query = strategy.getNamedQuery("GroupRelationship.getByUserProfile");
        java.util.HashSet<Group> resultSet = new java.util.HashSet<Group>();
        for(GroupRelationship gr : groupMembers) {
            query.setParameter(1, gr.getUserProfile());
            List<GroupRelationship> ret = query.getResultList();
            for(GroupRelationship gr1 : ret) {
                if(group.getHandle().equals(gr1.getGroup().getHandle()))
                    continue;
                resultSet.add(gr1.getGroup());
            }
        }
        return resultSet;
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Profile> getCommonMembersInGroups(Group thisGroup, Group thatGroup)
            throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getCommonMembers");
        query.setParameter(1, thisGroup);
        query.setParameter(2, thatGroup);
        List<Profile> commonMembers = query.getResultList();
        return commonMembers;
    }

    public boolean requestMembership(Group group, Profile profile) throws SocialSiteException {
        if (requestExists(group, profile)) {
            return false;
        }
        GroupRequest groupReq = new GroupRequest();
        groupReq.setGroup(group);
        groupReq.setProfileFrom(profile);
        groupReq.setStatus(GroupRequest.Status.PENDING);
        strategy.store(groupReq);
        return true;
    }

    public void removeGroupRequest(GroupRequest groupReq) throws SocialSiteException {
        strategy.remove(groupReq);
    }

    public GroupRequest getMembershipRequest(Group group, Profile profile) throws SocialSiteException {
        if (group == null) {
            throw new SocialSiteException("Group is null");
        }
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("GroupRequest.getByGroupAndProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        try {
            return (GroupRequest) query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<GroupRequest> getMembershipRequestsByGroup(Group group, int offset, int length) throws SocialSiteException {
        if (group == null) {
            throw new SocialSiteException("Group is null");
        }
        Query query = strategy.getNamedQuery("GroupRequest.getByGroup");
        query.setParameter(1, group);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<GroupRequest>) query.getResultList();
    }

    public void acceptAsGroupAdmin(GroupRequest groupReq) throws SocialSiteException {
        Group group = groupReq.getGroup();

        GroupRelationship rel = new GroupRelationship();
        rel.setGroup(groupReq.getGroup());
        rel.setUserProfile(groupReq.getProfileFrom());
        rel.setRelcode(GroupRelationship.Relationship.ADMIN);
        strategy.store(rel);

        strategy.remove(groupReq);

        SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
        amgr.recordActivity(groupReq.getProfileFrom(), group, SocialSiteActivity.NEW_ADMIN, group.getName());
    }

    public void grantAdminRights(Group group, Profile profile) throws SocialSiteException {
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroupAndUserProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        GroupRelationship rel = null;
        try {
            rel = (GroupRelationship)query.getSingleResult();
        } catch (NoResultException intentionallyIgnored) {}

        if (rel == null) {
            rel = new GroupRelationship();
            rel.setGroup(group);
            rel.setUserProfile(profile);
        }
        // If user is the founder, they don't need ADMIN rights
        if (!GroupRelationship.Relationship.FOUNDER.equals(rel.getRelcode())) {
            rel.setRelcode(GroupRelationship.Relationship.ADMIN);
        }
        strategy.store(rel);
    }

    public void acceptMembership(GroupRequest groupReq) throws SocialSiteException {
        Group group = groupReq.getGroup();

        GroupRelationship rel = new GroupRelationship();
        rel.setGroup(groupReq.getGroup());
        rel.setUserProfile(groupReq.getProfileFrom());
        rel.setRelcode(GroupRelationship.Relationship.MEMBER);
        strategy.store(rel);

        strategy.remove(groupReq);

        SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
        // Record activity for this person
        amgr.recordActivity(groupReq.getProfileFrom(), group, SocialSiteActivity.NEW_MEMBERSHIP, group.getName());
    }

    public void declineMembership(GroupRequest groupReq) throws SocialSiteException {
        strategy.remove(groupReq);
    }

    public boolean removeMembership(Group group, Profile profile) throws SocialSiteException {
        if (isFounder(group, profile)) {
            return false;
        }
        Query query = strategy.getNamedQuery("GroupRelationship.getByGroupAndUserProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        GroupRelationship rel = (GroupRelationship) query.getSingleResult();
        strategy.remove(rel);
        SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
        amgr.recordActivity(profile, group, SocialSiteActivity.LEFT_GROUP, group.getName());
        return true;
    }

    /**
     * Returns true if there is already a matching request (or more than one, though
     * this should not happen).
     *
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    private boolean requestExists(Group group, Profile profile) throws SocialSiteException {
        assert (group != null);
        assert (profile != null);
        Query query = strategy.getNamedQuery("GroupRequest.getByGroupAndProfile");
        query.setParameter(1, group);
        query.setParameter(2, profile);
        List results = query.getResultList();
        return (results.size() > 0);
    }

    public GroupDefinition getGroupDefinition() {
        return groupDef;
    }

    public void saveGroupProperty(GroupProperty groupProp) throws SocialSiteException {
        strategy.store(groupProp);
    }

    public void removeGroupProperty(GroupProperty groupProp) throws SocialSiteException {
        strategy.remove(groupProp);
    }

    public Map<String, GroupProperty> getGroupPropertyMap(String groupHandle, String viewerId) throws SocialSiteException {
        Group group = getGroupByHandle(groupHandle);
        // TODO: subset properties by visibility
        return group.getProperties();
    }

    public List<Map<String, GroupProperty>> getGroupPropertyMaps(
            String userId, String groupId, String viewerId,
            SortOrder sort, FilterType filter,
            int first, int max, Set<String> profileDetails)
            throws SocialSiteException {
        // TBD:
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
class GroupRequestListener {

    private static Log log = LogFactory.getLog(GroupRequestListener.class);
    private static boolean doEmails = Config.getBooleanProperty("socialsite.notifications.email.Grouprequest.enabled");
    private static final String JNDI_KEY = "java:comp/env/mail/SocialSite/Session";
    private static Session mailSession;


    static {
        if (doEmails) {
            try {
                mailSession = Startup.getMailProvider().getSession();
            } catch (Exception e) {
                log.error("Unable to obtain mailSession", e);
            }
        }
    }

    @PostPersist
    public void GroupRequestCreated(GroupRequest GroupRequest) {

        Group group = GroupRequest.getGroup();
        Profile fromUser = GroupRequest.getProfileFrom();
        String url = Factory.getSocialSite().getURLStrategy().getDashBoardURL();

        if (doEmails) {

            List<Profile> founders = null;
            try {
                founders = getFounders(group);
            } catch (SocialSiteException e) {
                log.error("Failed to generate GroupRequest mail message", e);
                return;
            }

            for (Profile founder : founders) {

                String from = Config.getProperty("socialsite.notifications.email.from-address");
                String to = String.format("%s <%s>", founder.getName(), founder.getPrimaryEmail());
                String cc = null;
                String bcc = null;

                Object[] contentArgs = {
                    founder.getFirstName(), /* {0} */
                    founder.getName(), /* {1} */
                    fromUser.getFirstName(), /* {2} */
                    fromUser.getName(), /* {3} */
                    group.getName(), /* {4} */
                    url                       /* {5} */

                };
                String subject = TextUtil.format("socialsite.notifications.email.Grouprequest.subject", contentArgs);
                String content = TextUtil.format("socialsite.notifications.email.Grouprequest.body", contentArgs);

                try {
                    log.debug(String.format("Sending Mail [from=%s, to=%s, subject=%s, content=%s]", from, to, subject, content));
                    MailUtil.sendTextMessage(mailSession, from, to, cc, bcc, subject, content);
                } catch (MessagingException e) {
                    log.error("Failed to send GroupRequest mail message", e);
                }
            }
        }
    }

    private List<Profile> getFounders(Group group) throws SocialSiteException {
        List<Profile> founders = new ArrayList<Profile>();
        GroupManager GroupManager = Factory.getSocialSite().getGroupManager();
        for (GroupRelationship grel : GroupManager.getMembershipsByGroup(group, 0, -1)) {
            Profile profile = grel.getUserProfile();
            if (GroupManager.isFounder(group, profile)) {
                founders.add(profile);
            }
        }

        if (founders.size() == 0) {
            String msg = String.format("Could not find founder for %s", group);
            throw new SocialSiteException(msg);
        }

        return founders;
    }
}
