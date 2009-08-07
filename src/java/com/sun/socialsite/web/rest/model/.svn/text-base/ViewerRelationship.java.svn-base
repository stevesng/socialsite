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

package com.sun.socialsite.web.rest.model;

import com.sun.socialsite.pojos.*;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Represents status of relationship of viewer with person, including a list
 * of shared groups. Intended for use with Shinding and thus to be serialized
 * to JSON and XML via reflection. Should only exist if there is a relationship.
 */
public class ViewerRelationship {
    private static Log log = LogFactory.getLog(ViewerRelationship.class);

    /** Relationship level that viewer has assigned to relationship */
    private int level;

    /** True if relationship level is considered to be "friend" */
    private boolean friend;

    /** String indicating how viewer/person met */
    private String howknow;

    /** Collection of groups shared by viewer and person */
    private List<GroupWrapper> sharedGroups;

    /** Collection of groups viewer could invite person to join */
    private List<GroupWrapper> suggestedGroups;

    /** Collection of groups of person */
    private List<GroupWrapper> groups;

    /** Array of relationship level names allowed by system */
    private List<String> relationshipLevelNames = null;

    /** Relationship level that is considered a friendship */
    private int friendshipLevel = 0;

    /**
     * Status of relationship from perspective of viewer.
     * Would have liked to use an enum but the Shindig bean converter is
     * written to the Shindig Enum class, which is not easy to extend.
     */
    private String status;
    public static final String YOU     = "YOU";
    public static final String NONE    = "NONE";
    public static final String ONEWAY  = "ONEWAY";
    public static final String TWOWAY  = "TWOWAY";
    public static final String MUTUAL  = "MUTUAL";
    public static final String PENDING = "PENDING";
    public static final String PENDING_VIEWER = "PENDING_VIEWER";


    public ViewerRelationship() {}

    public ViewerRelationship(Profile viewer, Profile person) {
        init(viewer, person);
    }

    private void init(Profile viewer, Profile person) {
        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        RelationshipManager  fmgr = Factory.getSocialSite().getRelationshipManager();
        GroupManager   gmgr = Factory.getSocialSite().getGroupManager();
        try {
            relationshipLevelNames = Arrays.asList(fmgr.getRelationshipLevelNames());

            friendshipLevel = fmgr.getFriendshipLevel();

            Relationship viewerRel = fmgr.getRelationship(viewer, person);
            Relationship personRel = fmgr.getRelationship(person, viewer);

            if (viewer.getId().equals(person.getId())) {
                status = YOU;
                level = 0;
            } else if (viewerRel == null && personRel == null) {
                status = NONE;
                level = 0;
            } else if (viewerRel != null && personRel != null) {
                    level = viewerRel.getLevel();
                if (StringUtils.isNotEmpty(viewerRel.getHowknow())
                 && StringUtils.isNotEmpty(personRel.getHowknow())
                 && viewerRel.getHowknow().equals(personRel.getHowknow())) {
                    status = MUTUAL;
                    howknow = viewerRel.getHowknow();
                } else {
                    status = TWOWAY;
                }
            } else if (viewerRel != null) {
                status = ONEWAY;
                level = viewerRel.getLevel();
            } else if (personRel != null) {
                status = NONE;
                level = 0;
            }

            RelationshipRequest viewerRequest = fmgr.getRelationshipRequest(viewer, person);
            RelationshipRequest personRequest = fmgr.getRelationshipRequest(person, viewer);

            if (viewerRequest != null) {
                status = PENDING;
                howknow = viewerRequest.getHowknow();
            } else if (personRequest != null) {
                status = PENDING_VIEWER;
                howknow = personRequest.getHowknow();
            }

            List<GroupRelationship> personGroupRels =
                gmgr.getMembershipsByProfile(person, 0, -1);

            List<GroupRelationship> viewerGroupRels =
                gmgr.getMembershipsByProfile(viewer, 0, -1);

            setGroups(new ArrayList<GroupWrapper>());
            for (Iterator<GroupRelationship>
                    grit = personGroupRels.iterator(); grit.hasNext();) {
                GroupRelationship groupRelationship = grit.next();
                groups.add(new GroupWrapper(groupRelationship.getGroup()));
            }

            setSharedGroups(new ArrayList<GroupWrapper>());
            setSuggestedGroups(new ArrayList<GroupWrapper>());
            for (Iterator<GroupRelationship>
                    grit = viewerGroupRels.iterator(); grit.hasNext();) {
                GroupRelationship groupRelationship = grit.next();
                if (personGroupRels.contains(groupRelationship)) {
                    sharedGroups.add(new GroupWrapper(groupRelationship.getGroup()));
                } else {
                    suggestedGroups.add(new GroupWrapper(groupRelationship.getGroup()));
                }
            }

        } catch (SocialSiteException ex) {
            log.error("ERROR determining viewer relationship", ex);
        }
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the friend
     */
    public boolean isFriend() {
        return friend;
    }

    /**
     * @param friend the friend to set
     */
    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    /**
     * @return the howknow
     */
    public String getHowknow() {
        return howknow;
    }

    /**
     * @param howknow the howknow to set
     */
    public void setHowknow(String howknow) {
        this.howknow = howknow;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the sharedGroups
     */
    public List<GroupWrapper> getSharedGroups() {
        return sharedGroups;
    }

    /**
     * @param sharedGroups the sharedGroups to set
     */
    public void setSharedGroups(List<GroupWrapper> sharedGroups) {
        this.sharedGroups = sharedGroups;
    }

    /**
     * @return the levelNames
     */
    public List<String> getRelationshipLevelNames() {
        return relationshipLevelNames;
    }

    /**
     * @param levelNames the levelNames to set
     */
    public void setRelationshipLevelNames(List<String> levelNames) {
        this.relationshipLevelNames = levelNames;
    }

    /**
     * @return the friendshipLevel
     */
    public int getFriendshipLevel() {
        return friendshipLevel;
    }

    /**
     * @param friendshipLevel the friendshipLevel to set
     */
    public void setFriendshipLevel(int friendshipLevel) {
        this.friendshipLevel = friendshipLevel;
    }

    /**
     * @return the groups
     */
    public List<GroupWrapper> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<GroupWrapper> groups) {
        this.groups = groups;
    }

    /**
     * @return the suggestedGroups
     */
    public List<GroupWrapper> getSuggestedGroups() {
        return suggestedGroups;
    }

    /**
     * @param suggestedGroups the suggestedGroups to set
     */
    public void setSuggestedGroups(List<GroupWrapper> suggestedGroups) {
        this.suggestedGroups = suggestedGroups;
    }
}

