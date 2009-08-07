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

package com.sun.socialsite.business;

import com.google.inject.ImplementedBy;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.impl.JPANotificationManagerImpl;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import java.util.List;


/**
 * Record, retrieve and query notifications associated with users.
 */
@ImplementedBy(JPANotificationManagerImpl.class)
public interface NotificationManager extends MessageContentManager {

    public static final String INBOX = "inbox";
    public static final String SENT = "sent";

    /**
     * Record a user Message of a specific kind.
     * @param Profile Profile of user who originated Message
     * @param group The group in case this is an invitation to join. Otherwise null.
     * @param title Title of notification
     * @param summary Text about notification
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public void recordNotification(Profile fromProfile, Profile toProfile, 
            Group group, String type, String summary, String content, boolean storeSentCopy) throws SocialSiteException;

    public void recordGroupNotification(Profile fromProfile, Group grp,
        String title, String summary) throws SocialSiteException;

    /**
     * Record a system notification. Only admins are allowed to send system notifications.
     * @param title Title of notification
     * @param summary Text about notification
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public void recordSystemNotification(String title, String summary) throws SocialSiteException;

    /**
     * Get a notification with specified id
     * @param id of the notification to be obtained
     * @return the reqested notification
     * @throws com.sun.socialsite.SocialSiteException
     */
    public MessageContent getNotification(String id) throws SocialSiteException;

    /**
     * Remove a notification with specified id
     * @param id of the notification to be removed
     * @param id of user requesting removal
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void removeNotification(String id, Profile user) throws SocialSiteException;

    /**
     * Get all of user's inbox notifications 
     * @param profile Profile for which to return entries
     * @param offset   Offset into results for paging
     * @param length   Number of results to return (or -1 for no limit)
     * @return List of Notifications
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<MessageContent> getUserInbox(Profile profile,
                int offset, int length) throws SocialSiteException;

    /** 
     * Get all user's notification in sent box
     * @param profile Profile of user
     * @param offset
     * @param length
     * @return List of Notifications
     * @throws com.sun.socialsite.SocialSiteExceptioni
     */
    public List<MessageContent> getUserSentBox(Profile profile,
                int offset, int length) throws SocialSiteException;
    
    /** 
     * Get all user's invitations
     * @param profile Profile of user
     * @param offset
     * @param length
     * @return List of Notifications
     * @throws com.sun.socialsite.SocialSiteExceptioni
     */
    public List<MessageContent> getUserInvitations(Profile profile,
                int offset, int length) throws SocialSiteException;
    
    // get group notifications
    public List<MessageContent> getNotificationsByGroup(int offset, int length, String handle) 
            throws SocialSiteException;

    public List<MessageContent> getSystemNotifications(int offset, int length)
            throws SocialSiteException;  

    // TODO: this needs unit tests
    public void setNotifcationStatus(String id, String status)
            throws SocialSiteException;
    
}
