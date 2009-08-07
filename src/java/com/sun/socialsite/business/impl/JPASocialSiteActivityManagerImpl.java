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
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.SocialSiteActivity;
import com.sun.socialsite.util.TextUtil;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.URLStrategy;

import java.util.List;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JPA implementation activity manager.
 */
@Singleton
public class JPASocialSiteActivityManagerImpl extends AbstractManagerImpl implements SocialSiteActivityManager {

    private static Log log = LogFactory.getLog(JPASocialSiteActivityManagerImpl.class);

    private final JPAPersistenceStrategy strategy;

    @Inject
    protected JPASocialSiteActivityManagerImpl(JPAPersistenceStrategy strat) {
        log.debug("Instantiating JPA Activity Manager");
        this.strategy = strat;
    }

    public void recordActivity(Profile profile, Group group, String descType,
            String title) throws SocialSiteException {
        SocialSiteActivity a = new SocialSiteActivity();
        a.setProfile(profile);
        if(group != null)
            a.setGroup(group);
        a.setType(descType);
        setTitleAndBody(a, profile, group, descType, title);
        saveActivity(a);
    }

    public void saveActivity(SocialSiteActivity activity) throws SocialSiteException {
        strategy.store(activity);
    }

    public void removeActivity(SocialSiteActivity activity) throws SocialSiteException {
        strategy.remove(activity);
    }

    public SocialSiteActivity getActivity(String id) throws SocialSiteException {
        return (SocialSiteActivity) strategy.load(SocialSiteActivity.class, id);
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<SocialSiteActivity> getUserActivities(Profile profile, int offset, int length) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("Activity.getByProfile");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        return (List<SocialSiteActivity>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<SocialSiteActivity> getUserActivities(Profile profile, String type,
            int offset, int length) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("Activity.getByProfileAndType");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        query.setParameter(2, type);
        return (List<SocialSiteActivity>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<SocialSiteActivity> getUserAndFriendsActivities(Profile profile,
            int offset, int length) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("Activity.getFriendsActivityByProfile");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        return (List<SocialSiteActivity>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public SocialSiteActivity getLatestStatus(Profile profile) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("Activity.getStatusByProfile");
        query.setParameter(1, profile);
        query.setParameter(2, SocialSiteActivity.STATUS);
        query.setFirstResult(0);
        query.setMaxResults(1);
        List<SocialSiteActivity> ac =(List<SocialSiteActivity>) query.getResultList();
        if ((ac != null ) && (ac.size() > 0)) {
            return ac.get(0);
        } else {
            return null;
        }
    }

    public void release() {
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<SocialSiteActivity> getActivitiesByGadget(String gadgetId,
            int offset, int length) throws SocialSiteException {
        if (gadgetId == null) {
            throw new SocialSiteException("Gadget ID is null");
        }
        Query query = strategy.getNamedQuery("Activity.getByGadget");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, gadgetId);
        return (List<SocialSiteActivity>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<SocialSiteActivity> getActivitiesByGroup(Group group,
            int offset, int length) throws SocialSiteException {
        if(group == null) {
            throw new SocialSiteException("Group cannot be null");
        }
        Query query = strategy.getNamedQuery("Activity.getByGroup");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, group);
        return (List<SocialSiteActivity>) query.getResultList();
    }

    private void setTitleAndBody(SocialSiteActivity a, Profile user,
            Group group, String type, String title) throws SocialSiteException {

        String baseURL = getURLStrategy().getBaseURL();
        String userURL = getURLStrategy().getViewURL(user);

        String aTitle = null;
        String aBody = null;
        if (SocialSiteActivity.EDITED_PROFILE.equals(type)) {
            Object[] args = {
                baseURL,
                userURL,
                user.getName()
            };
            aTitle = TextUtil.format("socialsite.activity.body.profile_edit", args);
            aBody = TextUtil.format("socialsite.activity.body.profile_edit", args);
        } else if (SocialSiteActivity.NEW_MEMBERSHIP.equals(type)) {
            String groupURL = getURLStrategy().getViewURL(group);
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                groupURL,
                title
            };
            aTitle = TextUtil.format("socialsite.activity.body.new_membership", args);
            aBody = TextUtil.format("socialsite.activity.body.new_membership", args);
        } else if (SocialSiteActivity.NEW_ADMIN.equals(type)) {
            String groupURL = getURLStrategy().getViewURL(group);
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                groupURL,
                title
            };
            aTitle = TextUtil.format("socialsite.activity.body.new_admin", args);
            aBody = TextUtil.format("socialsite.activity.body.new_admin", args);
        } else if (SocialSiteActivity.LEFT_GROUP.equals(type)) {
            String groupURL = getURLStrategy().getViewURL(group);
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                groupURL,
                title
            };
            aTitle = TextUtil.format("socialsite.activity.body.left_group", args);
            aBody = TextUtil.format("socialsite.activity.body.left_group", args);
        } else if (SocialSiteActivity.CREATED_GROUP.equals(type)) {
            String groupURL = getURLStrategy().getViewURL(group);
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                groupURL,
                title
            };
            aTitle = TextUtil.format("socialsite.activity.body.created_group", args);
            aBody = TextUtil.format("socialsite.activity.body.created_group", args);
        } else if (SocialSiteActivity.NEW_FRIENDSHIP.equals(type)) {
            Profile friend = Factory.getSocialSite().getProfileManager().getProfile(title);
            String friendURL = getURLStrategy().getViewURL(friend);
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                friendURL,
                friend.getName()
            };
            aTitle = TextUtil.format("socialsite.activity.body.new_friendship", args);
            aBody = TextUtil.format("socialsite.activity.body.new_friendship", args);
        } else if (SocialSiteActivity.STATUS.equals(type)) {
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                title
            };
            aTitle = TextUtil.format("socialsite.activity.body.status", args);
            aBody = TextUtil.format("socialsite.activity.body.status", args);
        } else if (SocialSiteActivity.APP_MESSAGE.equals(type)) {
            Object[] args = {
                baseURL,
                userURL,
                user.getName(),
                title
            };
            aTitle = TextUtil.format("socialsite.activity.body.app_message", args);
            aBody = TextUtil.format("socialsite.activity.body.app_message", args);
        } else {
            log.warn("Do not know how to generate HTML for: " + this);
        }
        a.setTitle(aTitle);
        a.setBody(aBody);
        return;
    }

    private URLStrategy getURLStrategy() {
        return Factory.getSocialSite().getURLStrategy();
    }

}
