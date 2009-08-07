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
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.NotificationManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import java.util.List;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JPA implementation Message manager.
 */
@Singleton
public class JPANotificationManagerImpl extends JPAMessageContentManagerImpl implements NotificationManager {

    private static Log log = LogFactory.getLog(JPANotificationManagerImpl.class);
    private final JPAPersistenceStrategy strategy;

    @Inject
    protected JPANotificationManagerImpl(JPAPersistenceStrategy strat) {
        super(strat);
        log.debug("Instantiating JPA Notification Manager");
        this.strategy = strat;
    }

    public void recordNotification(Profile fromProfile,
            Profile toProfile, Group group, String type,
            String summary, String content, boolean storeSentCopy) throws SocialSiteException {
        // cat label can be used to identify things like 'sent' message etc.
        if(storeSentCopy) {
            MessageContent a = new MessageContent();
            a.setProfile(fromProfile);
            a.setToProfileId(toProfile.getUserId());
            a.setReplyToId(fromProfile.getUserId());
            a.setGroup(group);
            a.setDescType(type);
            a.setSummary(summary);
            a.setContent(content);
            a.setCatLabel(SENT);
            a.setStatus(MessageContent.UNREAD);
            saveMessage(a);
        }
        MessageContent a1 = new MessageContent();
        a1.setProfile(fromProfile);
        a1.setToProfileId(toProfile.getUserId());
        a1.setReplyToId(fromProfile.getUserId());
        a1.setGroup(group);
        a1.setDescType(type);
        a1.setSummary(summary);
        a1.setContent(content);
        a1.setCatLabel(INBOX);
        a1.setStatus(MessageContent.UNREAD);
        saveMessage(a1);
    }

    public void recordGroupNotification(Profile fromProfile, Group grp, String title, String summary)
            throws SocialSiteException {
        if(title.length() == 0 &&
                summary.length() == 0)
            return;
        recordBulkNotification(fromProfile, grp, title, summary, 
                MessageContent.GROUP_NOTIFICATIONS);
    }

    public void recordSystemNotification(String title, String summary)
            throws SocialSiteException {
        // System notifications can only be sent by admins
        if(title.length() == 0 &&
                summary.length() == 0)
            return;
        recordBulkNotification(null, null, title, summary,
                MessageContent.SYS_NOTIFICATIONS);
    }

    private void recordBulkNotification(Profile fromProfile,
            Group grp, String title, String summary, String label)
            throws SocialSiteException {

        MessageContent a = new MessageContent();
        a.setProfile(fromProfile);
        a.setGroup(grp);
        a.setCatLabel(label);
        a.setDescType(MessageContent.NOTIFICATION);
        a.setSummary(title);
        a.setContent(summary);
        saveMessage(a);
        Factory.getSocialSite().flush();
    }

    public MessageContent getNotification(String id) throws SocialSiteException {
        MessageContent thisMsg = (MessageContent) strategy.load(MessageContent.class, id);
        saveMessage(thisMsg);
        strategy.flush();
        return thisMsg;
    }

    public void setNotifcationStatus(String id, String status) throws SocialSiteException {
        MessageContent thisMsg = (MessageContent) strategy.load(MessageContent.class, id);
        thisMsg.setStatus(MessageContent.READ);
        saveMessage(thisMsg);
        strategy.flush();
    }

    public void removeNotification(String id, Profile requestor) throws SocialSiteException {
        MessageContent thisMsg = getNotification(id);
        if(thisMsg == null)
            throw new SocialSiteException("No message found");

        /* Requestor can remove only messages in his INBOX or the one he sent,
         * unless message is a system notification (no sender).
         */
        if(!MessageContent.SYS_NOTIFICATIONS.equals(thisMsg.getCatLabel()) &&
            !requestor.equals(thisMsg.getProfile()) &&
            !requestor.getUserId().equals(thisMsg.getToProfileId())) {
            
            throw new SocialSiteException("You do not have permission to remove this message");
        }
        strategy.remove(MessageContent.class, id);
        strategy.flush();
    }

    public List<MessageContent> getUserInbox(Profile profile, int offset, int length)
            throws SocialSiteException {
        List<MessageContent> messages = getSystemNotifications(0, -1);
        messages.addAll(getNotificationsByTypeAndLabel(profile,
                MessageContent.NOTIFICATION, INBOX, offset, length));
        messages.addAll(this.getGroupNotificationsForUser(0, -1, profile));
        return messages;
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<MessageContent> getUserSentBox(Profile profile, int offset, int length)
            throws SocialSiteException {
        Query query = strategy.getNamedQuery("MessageContent.getByProfileTypeLabel");
        query.setParameter(1, profile);
        query.setParameter(2, MessageContent.NOTIFICATION);
        query.setParameter(3, SENT);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<MessageContent>)query.getResultList();
    }

    public List<MessageContent> getUserInvitations(Profile profile, int offset, int length)
            throws SocialSiteException {
        return(getNotificationsByTypeAndLabel(profile, MessageContent.GROUP_INVITE,
                INBOX, offset, length));
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    private List<MessageContent> getNotificationsByTypeAndLabel(Profile p,
            String type, String label, int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("MessageContent.getByToProfileTypeLabel");
        query.setParameter(1, p.getUserId());
        query.setParameter(2, type);
        query.setParameter(3, label);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<MessageContent>)query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<MessageContent> getSystemNotifications(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("MessageContent.getSystemNotifications");
        query.setParameter(1, MessageContent.SYS_NOTIFICATIONS);
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<MessageContent>)query.getResultList();
    }

    /**
     * Get notifications for a group
     *
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<MessageContent> getNotificationsByGroup(int offset, int length, String handle)
            throws SocialSiteException {

        GroupManager gmgr = Factory.getSocialSite().getGroupManager();
        Group grp = gmgr.getGroupByHandle(handle);
        if(grp == null)
            throw new SocialSiteException("No group found with handle " + handle);
        Query query = strategy.getNamedQuery("MessageContent.getGroupNotification");
        query.setParameter(1, grp);

        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<MessageContent>) query.getResultList();
    }
    

    /**
     * Get notifications for user's groups
     *
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<MessageContent> getGroupNotificationsForUser(int offset, int length, Profile user)
            throws SocialSiteException {

        Query query = strategy.getNamedQuery("MessageContent.getGroupNotificationsForUser");
        query.setParameter(1, user);

        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<MessageContent>) query.getResultList();
    }
    
}
