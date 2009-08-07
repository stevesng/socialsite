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
import com.sun.socialsite.business.impl.JPAContentManagerImpl;
import com.sun.socialsite.pojos.Content;
import com.sun.socialsite.pojos.Profile;
import java.io.InputStream;
import java.util.List;


/**
 * Record, retrieve and query content associated with users.
 */
@ImplementedBy(JPAContentManagerImpl.class)
public interface ContentManager extends Manager {

    /**
     * Record a content.
     * @param userName Name of user who originated content
     * @param appId App that recorded the content
     * @param title Title of activity
     * @param catscheme ATOM style category scheme URI.
     * @param catlabel Set of labels for the category and content.
     * @param contenttype Type of content
     * @param content The content itself
     * @throws com.sun.socialsite.SocialSiteException on error.
     */
    public void recordContent(
        Profile profile, String appId, String title,
        String catscheme, String catlabel, String contentType, String content)
        throws SocialSiteException;

    /**
     * Add string content.
     *
     * @param userName
     * @param appId
     * @param title
     * @param catscheme
     * @param catlabel
     * @param content
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void recordContent(
        Profile profile, String appId, String title,
        String catscheme, String catlabel, String content)
        throws SocialSiteException;


    /**
     * Get all of user's recent content in reverse chronological order.
     * @param username Username for which to return entries
     * @param offset   Offset into results for paging
     * @param length   Number of results to return (or -1 for no limit)
     * @return List of Content
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<Content> getUserContent(
        Profile profile, int offset, int length) throws SocialSiteException;

    /**
     * Get all of user and friend's recent activities.
     * @param username Username of user
     * @param offset   Offset into results for paging
     * @param length   Number of results to return (or -1 for no limit)
     * @return List of Activities
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<Content> getUserAndFriendsContentPostings(
        Profile profile, int offset, int length) throws SocialSiteException;

    /** Save content */
    public void saveContent(Content Content) throws SocialSiteException;

    /** Remove content */
    public void removeContent(Content Content) throws SocialSiteException;

    /** Get content by ID */
    public Content getContent(String id) throws SocialSiteException;

    /** Get content by category and tag**/
    public List<Content> getContentByCategory(Profile profile,
            String catscheme, String tag, int offset, int length) throws SocialSiteException;

}
