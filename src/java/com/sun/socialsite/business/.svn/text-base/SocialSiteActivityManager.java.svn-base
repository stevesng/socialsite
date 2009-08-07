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
import com.sun.socialsite.business.impl.JPASocialSiteActivityManagerImpl;
import com.sun.socialsite.pojos.SocialSiteActivity;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.Group;
import java.util.List;


/**
 * Record, retrieve and query activities associated with users.
 */
@ImplementedBy(JPASocialSiteActivityManagerImpl.class)
public interface SocialSiteActivityManager extends Manager {

    /**
     * Record a user activity of a specific kind.
     * @param Profile Profile of user who originated activity
     * @param Group Group profile if this is a group activity
     * @param descType type of activity (see activity codes in Activity class)
     * @param title Description of activity, target user name or group
     * @throws com.sun.socialsite.SocialSiteException on error.
     */
    public void recordActivity(
        Profile profile, Group group, String descType, String title) throws SocialSiteException;

    /**
     * Get all of user's recent activities in reverse chronological order.
     * @param username Username for which to return entries
     * @param offset   Offset into results for paging
     * @param length   Number of results to return (or -1 for no limit)
     * @return List of Activities
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<SocialSiteActivity> getUserActivities(
        Profile profile, int offset, int length) throws SocialSiteException;


    /* Get activities by kind or type e.g. all STATUS messages **/
    public List<SocialSiteActivity> getUserActivities(
        Profile profile, String descType, int offset, int length) throws SocialSiteException;

    /**
     * Get all of user and friend's recent activities.
     * @param username Username of user
     * @param offset   Offset into results for paging
     * @param length   Number of results to return (or -1 for no limit)
     * @return List of Activities
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<SocialSiteActivity> getUserAndFriendsActivities(
        Profile profile, int offset, int length) throws SocialSiteException;

    /** Save activity */
    public void saveActivity(SocialSiteActivity activity) throws SocialSiteException;

    /** Remove activity */
    public void removeActivity(SocialSiteActivity activity) throws SocialSiteException;

    /** Save activity by ID */
    public SocialSiteActivity getActivity(String id) throws SocialSiteException;

    public SocialSiteActivity getLatestStatus(Profile profile) throws SocialSiteException;

    /** Get activites recorded by a gadget **/
    public List<SocialSiteActivity> getActivitiesByGadget(String gadgetId,
            int offset, int length) throws SocialSiteException;

    /** Get Activities for a group **/
    public List<SocialSiteActivity> getActivitiesByGroup(Group group,
            int offset, int length) throws SocialSiteException;
}
