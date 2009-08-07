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
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.ListenerManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.SocialSiteActivityManager;
import com.sun.socialsite.business.startup.Startup;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.Relationship;
import com.sun.socialsite.pojos.RelationshipRequest;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.SocialSiteActivity;
import com.sun.socialsite.util.TextUtil;
import com.sun.socialsite.util.MailUtil;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.persistence.NoResultException;
import javax.persistence.PostPersist;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




/**
 * JPA implementation of friend manager.
 */
@Singleton
public class JPARelationshipManagerImpl
        extends AbstractManagerImpl implements RelationshipManager {

    private static Log log = LogFactory.getLog(JPARelationshipManagerImpl.class);

    private final JPAPersistenceStrategy strategy;

    private String[] relationshipLevelKeys = null;
    private String[] relationshipLevelNames = null;

    private int friendshipLevel = 0;

    private boolean twoWayRequiredForFriendship = true;
    
    // For testing purposes only!
    public void setTwoWayRequiredForFriendship(boolean flag) {
        twoWayRequiredForFriendship = flag;
    }


    @Inject
    protected JPARelationshipManagerImpl(
            JPAPersistenceStrategy strategy, ListenerManager listenerManager) {
        log.debug("Instantiating JPA Relationship Manager");
        this.strategy = strategy;

        // TODO: Move this to initialize() method?
        listenerManager.addListener(RelationshipRequest.class,
                new RelationshipRequestListener());

        twoWayRequiredForFriendship = Config.getBooleanProperty(
                "socialsite.relationship.twoWayRequiredForFriendship");

        friendshipLevel = Config.getIntProperty(
                "socialsite.relationship.friendshiplevel");

        String levels = Config.getProperty("socialsite.relationship.levels");
        relationshipLevelKeys = levels.split(",");

        relationshipLevelNames = new String[relationshipLevelKeys.length];
        for (int i = 0; i < relationshipLevelKeys.length; i++) {
            relationshipLevelNames[i] = TextUtil.getResourceString(relationshipLevelKeys[i]);
        }
    }

    public void release() {}

    public String[] getRelationshipLevelKeys() {
        return relationshipLevelKeys;
    }

    public String[] getRelationshipLevelNames() {
        return relationshipLevelNames;
    }

    public int getFriendshipLevel() {
        return friendshipLevel;
    }

    public void createRelationship(
            Profile from, Profile to, int level) throws SocialSiteException {

        Relationship rel = new Relationship();
        rel.setProfileFrom(from);
        rel.setProfileTo(to);
        rel.setHowknow("n/a");
        rel.setLevel(level);
        strategy.store(rel);
    }

    public void createMutualRelationship(
            Profile profile1, int level1,
            Profile profile2, int level2, String know) throws SocialSiteException {

        Relationship rel1 = new Relationship();
        rel1.setProfileFrom(profile1);
        rel1.setProfileTo(profile2);
        rel1.setHowknow(know);
        rel1.setLevel(level1);
        strategy.store(rel1);

        Relationship rel2 = new Relationship();
        rel2.setProfileFrom(profile2);
        rel2.setProfileTo(profile1);
        rel2.setHowknow(know);
        rel2.setLevel(level2);
        strategy.store(rel2);
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Relationship> getRelationships(
            Profile profile, int offset, int length) throws SocialSiteException {
        if (profile == null)
            throw new SocialSiteException("user is null");

        Query query = strategy.getNamedQuery("Relationship.getByProfileFrom");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        query.setParameter(1, profile);
        return (List<Relationship>)query.getResultList();
    }

    public Relationship getRelationship(Profile from, Profile to)
            throws SocialSiteException {
        if (from == null)
            throw new SocialSiteException("from is null");
        if (to == null)
            throw new SocialSiteException("to is null");

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        Query query = strategy.getNamedQuery(
                "Relationship.getByProfileFromAndProfileTo");
        query.setParameter(1, from);
        query.setParameter(2, to);
        try {
            return (Relationship)query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void removeRelationships(
            Profile profile1, Profile profile2) throws SocialSiteException {

        Query query1 = strategy.getNamedQuery(
                "Relationship.getByProfileFromAndProfileTo");
        query1.setParameter(1, profile1);
        query1.setParameter(2, profile2);
        Relationship rel1 = (Relationship)query1.getSingleResult();
        strategy.remove(rel1);

        Query query2 = strategy.getNamedQuery(
                "Relationship.getByProfileFromAndProfileTo");
        query2.setParameter(1, profile2);
        query2.setParameter(2, profile1);
        Relationship rel2 = (Relationship)query2.getSingleResult();
        strategy.remove(rel2);
    }

    public void adjustRelationship(Profile from, Profile to, int level)
            throws SocialSiteException {
        Relationship rel = getRelationship(from, to);
        rel.setLevel(level);
        strategy.store(rel);
    }

    public void removeRelationship(
            Profile from, Profile to) throws SocialSiteException {

        Query query1 = strategy.getNamedQuery(
                "Relationship.getByProfileFromAndProfileTo");
        query1.setParameter(1, from);
        query1.setParameter(2, to);
        Relationship rel1 = (Relationship)query1.getSingleResult();
        strategy.remove(rel1);
    }

    public void requestRelationship(
            Profile from, Profile to, int level, String know) throws SocialSiteException {

        // If requestor already requested, then we have a dup
        RelationshipRequest duplicateFriendRequest =
            getRelationshipRequest(from, to);

        // If requestee already requested, then requestor is accepting
        RelationshipRequest existingFriendRequest =
            getRelationshipRequest(to, from);

        if (from.equals(to)) {
            log.debug("from.equals(to)");
            throw new SocialSiteException("Requestor same as requestee");

        } else if (duplicateFriendRequest != null) {
            log.debug("duplicateFriendRequest");
            throw new SocialSiteException("Duplicate friend request, ignored");

        } else if (existingFriendRequest != null) {
            // Requestor accepting relationship with requestee
            log.debug("existingFriendRequest");
            acceptRelationshipRequest(existingFriendRequest, level);

        } else if (level < getFriendshipLevel()) {
            // Requestor wants less than friendship level relation
            // so just create it, no need for request
            createRelationship(from, to, level);

        } else {
            // request relationship
            log.debug(String.format("creating request from %s to %s", from.getUserId(), to.getUserId()));
            RelationshipRequest rreq = new RelationshipRequest();
            rreq.setProfileFrom(from);
            rreq.setProfileTo(to);
            rreq.setLevelFrom(level);
            rreq.setHowknow(know);
            rreq.setStatus(RelationshipRequest.Status.PENDING);
            strategy.store(rreq);

            if (!twoWayRequiredForFriendship) {
                
                // if one doesn't already exist, create relationship from->to
                Relationship fromRel = getRelationship(from, to);
                if (fromRel == null) {
                    log.debug(String.format("creating relationship from %s to %s", from.getUserId(), to.getUserId()));
                    fromRel = new Relationship();
                    fromRel.setProfileFrom(rreq.getProfileFrom());
                    fromRel.setProfileTo(rreq.getProfileTo());
                }
                fromRel.setLevel(level);
                strategy.store(fromRel);
            }
        }
    }

    public void removeRelationshipRequest(
            RelationshipRequest rreq) throws SocialSiteException {
        strategy.remove(rreq);
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<RelationshipRequest> getRelationshipRequestsByToProfile(
            Profile to, int offset, int length) throws SocialSiteException {
        if (to == null)
            throw new SocialSiteException("touser is null");
        Query query = strategy.getNamedQuery(
                "RelationshipRequest.getByProfileTo");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        query.setParameter(1, to);
        return (List<RelationshipRequest>)query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<RelationshipRequest> getRelationshipRequestsByFromProfile(
            Profile from, int offset, int length) throws SocialSiteException {
        if (from == null)
            throw new SocialSiteException("profileFrom is null");
        Query query = strategy.getNamedQuery(
                "RelationshipRequest.getByProfileFrom");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        query.setParameter(1, from);
        return (List<RelationshipRequest>)query.getResultList();
    }

    public void acceptRelationshipRequest(
            RelationshipRequest rreq, int level) throws SocialSiteException {

        Relationship fromRel = getRelationship(rreq.getProfileFrom(), rreq.getProfileTo());
        if (fromRel == null) {
            fromRel = new Relationship();
            fromRel.setProfileFrom(rreq.getProfileFrom());
            fromRel.setProfileTo(rreq.getProfileTo());
            fromRel.setLevel(rreq.getLevelFrom());
        }
        // the from relationship gets agreed-upon howknow and the new level
        fromRel.setHowknow(rreq.getHowknow());
        strategy.store(fromRel);

        Relationship toRel = getRelationship(rreq.getProfileTo(), rreq.getProfileFrom());
        if (toRel == null) {
            toRel = new Relationship();
            toRel.setProfileFrom(rreq.getProfileTo());
            toRel.setProfileTo(rreq.getProfileFrom());
        }
        // the to relationship gets agreed-upon howknow and the new level
        toRel.setHowknow(rreq.getHowknow());
        toRel.setLevel(level);
        strategy.store(toRel);

        strategy.remove(rreq);

        SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
        amgr.recordActivity(rreq.getProfileFrom(), null,
                SocialSiteActivity.NEW_FRIENDSHIP,
                rreq.getProfileTo().getId().toString());
        amgr.recordActivity(rreq.getProfileTo(), null,
                SocialSiteActivity.NEW_FRIENDSHIP,
                rreq.getProfileFrom().getId().toString());
    }

    public void clarifyRelationshipRequest(
            RelationshipRequest rreq, int level, String know)
            throws SocialSiteException {

        int levelFrom = rreq.getLevelFrom();

        // set the level for the to user, i.e. the one clarifying the request
        int levelTo = level;

        // set the new how know message from the clarifying user
        rreq.setHowknow(know);

        // flip to and from so request goes back to the sender
        Profile to = rreq.getProfileTo();
        Profile from = rreq.getProfileFrom();

        rreq.setProfileTo(from);
        rreq.setLevelTo(levelFrom);

        rreq.setProfileFrom(to);
        rreq.setLevelFrom(levelTo);

        this.strategy.store(rreq);
    }

    public void ignoreRelationshipRequest(
            RelationshipRequest rreq) throws SocialSiteException {

        this.strategy.remove(rreq);

        // TODO: store IGNORED requests
        //rreq.setStatus(RelationshipRequest.Status.IGNORED);
        //this.strategy.store(rreq);
    }

    public RelationshipRequest getRelationshipRequest(
            Profile from, Profile to) throws SocialSiteException {
        if (from == null)
            throw new SocialSiteException("profileFrom is null");
        if (from == null)
            throw new SocialSiteException("profileTo is null");
        Query query = strategy.getNamedQuery(
                "RelationshipRequest.getByProfileFromAndProfileTo");
        query.setParameter(1, from);
        query.setParameter(2, to);
        try {
            return (RelationshipRequest)query.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Returns true if there is already a matching request
     * (or more than one, though this should not happen).
     *
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    private boolean requestExists(Profile from, Profile to)
        throws SocialSiteException {

        assert (from != null);
        assert (to != null);
        Query query = strategy.getNamedQuery(
            "RelationshipRequest.getByProfileFromAndProfileTo");
        query.setParameter(1, from);
        query.setParameter(2, to);
        List results = query.getResultList();
        return (results.size() > 0);
    }

}


class RelationshipRequestListener {
    private static Log log =
            LogFactory.getLog(RelationshipRequestListener.class);

    private static boolean doEmails = Config.getBooleanProperty(
            "socialsite.notifications.email.friendrequest.enabled");

    private static final String JNDI_KEY =
            "java:comp/env/mail/SocialSite/Session";
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
    public void relationshipRequestCreated(RelationshipRequest req) {

        if (doEmails) {

            Profile profileFrom = req.getProfileFrom();
            Profile profileTo = req.getProfileTo();
            String url = Factory.getSocialSite().getURLStrategy().getDashBoardURL();

            String from = Config.getProperty(
                    "socialsite.notifications.email.from-address");
            String to = String.format("%s <%s>",
                    profileTo.getName(), profileTo.getPrimaryEmail());
            String cc = null;
            String bcc = null;

            Object[] contentArgs = {
                profileTo.getFirstName(),    /* {0} */
                profileTo.getName(),         /* {1} */
                profileFrom.getFirstName(),  /* {2} */
                profileFrom.getName(),       /* {3} */
                url                          /* {4} */
            };
            String subject = TextUtil.format(
              "socialsite.notifications.email.friendrequest.subject", contentArgs);
            String content = TextUtil.format(
              "socialsite.notifications.email.friendrequest.body", contentArgs);

            try {
                log.debug(String.format(
                    "Sending Mail [from=%s, to=%s, subject=%s, content=%s]",
                    from, to, subject, content));
                MailUtil.sendTextMessage(
                        mailSession, from, to, cc, bcc, subject, content);
            } catch (MessagingException e) {
                log.error("Failed to send friendRequest mail message", e);
            }
        }
    }

}
