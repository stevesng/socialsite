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
import com.sun.socialsite.business.impl.JPAMessageContentManagerImpl;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import java.util.List;


/**
 * Record, retrieve and query messages associated with users.
 */
@ImplementedBy(JPAMessageContentManagerImpl.class)
public interface MessageContentManager extends ContentManager {

    /**
     * Record a user Message of a specific kind.
     * @param Profile Profile of user who originated Message
     * @param descType type of Message (see Message codes in MessageContent class)
     * @param title Description of Message, target user name or group
     * @throws com.sun.socialsite.SocialSiteException on error.
     */
    public void recordMessage(
        Profile profile, String descType, String title) throws SocialSiteException;

    /**
     * Record a user Message of a specific kind.
     * @param Profile Profile of user who originated Message
     * @param descType kind of Message (e.g. INBOX, SENT etc.)
     * @param title Description of Message, target user name or group
     * @param catlabel label of Message (just a tag)
     *
     * @throws com.sun.socialsite.SocialSiteException on error.
     */
    public void recordMessage(
        Profile profile, String descType, String title, String catlabel) throws SocialSiteException;

    /**
     * Get all of user's recent Messages in reverse chronological order.
     * @param username Username for which to return entries
     * @param offset   Offset into results for paging
     * @param length   Number of results to return (or -1 for no limit)
     * @return List of Messages
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<MessageContent> getUserMessages(
        Profile profile, int offset, int length) throws SocialSiteException;

    /* Get Messages by kind or type e.g. all Inbox messages **/
    public List<MessageContent> getUserMessages(
        Profile profile, String catlabel, int offset, int length) throws SocialSiteException;

    /** Save Message */
    public void saveMessage(MessageContent Message) throws SocialSiteException;

    /** Remove Message */
    public void removeMessage(MessageContent Message) throws SocialSiteException;

    /** Save Message by ID */
    public MessageContent getMessage(String id) throws SocialSiteException;

}
