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
import com.sun.socialsite.business.impl.JPAGroupManagerImpl;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupDefinition;
import com.sun.socialsite.pojos.GroupProperty;
import com.sun.socialsite.pojos.GroupRequest;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.Profile;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create and manage groups and group memberships.
 */
@ImplementedBy(JPAGroupManagerImpl.class)
public interface GroupManager extends Manager {

    /**
     * Get group property metadata. Defines properties, objects and 
     * collections that make up the group object model. 
     * 
     * @return Group property metadata.
     */
    public GroupDefinition getGroupDefinition();

    //-------------------------------------------------------------- Group CRUD

    public void createGroup(
            Group group, Profile creator) throws SocialSiteException;

    public Group getGroupByHandle(
            String handle) throws SocialSiteException;
    
    public Group getGroupById(
            String id) throws SocialSiteException;

    public List<Group> getGroups(
            int offset, int length) throws SocialSiteException;

    public GroupRelationship getMembership(
            Group group, Profile profile) throws SocialSiteException;

    /**
     * Grant group admin rights to a user.
     * @param group Group in which to add ADMIN relationship
     * @param user Profile of user to be added as ADMIN
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void grantAdminRights(
            Group group, Profile user)  throws SocialSiteException;

    public List<Group> searchGroups(
            int offset, int length, String pattern) throws SocialSiteException;

    public void saveGroup(Group group) throws SocialSiteException;

    public void removeGroup(Group group) throws SocialSiteException;
    
    public List<Group> getMostRecentlyUpdatedGroups(
            int offset, int length) throws SocialSiteException;

    public List<Group> getOldestGroups(
            int offset, int length) throws SocialSiteException;


    //-------------------------------------------------------- Group membership
    
    public boolean isMember(
            Group group, Profile profile) throws SocialSiteException;

    public boolean isFounder(
            Group group, Profile profile) throws SocialSiteException;

    public boolean isAdmin(
            Group group, Profile profile) throws SocialSiteException;

    public void createMembership(
            Group group1, Profile profile, GroupRelationship.Relationship relcode)
            throws SocialSiteException;
    
    public boolean requestMembership(
            Group group, Profile profile) throws SocialSiteException;

    public boolean removeMembership(
            Group group, Profile profile) throws SocialSiteException;

    public void saveGroupRelationship(
            GroupRelationship nrel) throws SocialSiteException;

    public List<GroupRelationship> getMembershipsByProfile(
            Profile profile, int offset, int length) throws SocialSiteException;

    public List<GroupRelationship> getMembershipsByGroup(
            Group group, int offset, int length) throws SocialSiteException;
    
    public List<GroupRelationship> getAdminsOfGroup(
            Group group, int offset, int length) throws SocialSiteException;

    public List<Group> getFriendsGroups(
            Profile profile) throws SocialSiteException;
    
    public Set<Group> getGroupMembersGroups(
            Group group) throws SocialSiteException;

    public List<Profile> getCommonMembersInGroups(
            Group thisGroup, Group thatGroup) throws SocialSiteException;
    
    public List<Group> getPopularGroups(int offset, int length) 
                throws SocialSiteException;

    public void acceptAsGroupAdmin(
            GroupRequest groupReq) throws SocialSiteException;

    public void acceptMembership(
            GroupRequest groupReq) throws SocialSiteException;

    public void declineMembership(
            GroupRequest groupReq) throws SocialSiteException;

    public List<GroupRequest> getMembershipRequestsByGroup(
            Group group, int offset, int length) throws SocialSiteException;

    public GroupRequest getMembershipRequest(
            Group group, Profile profile) throws SocialSiteException;

    public void removeGroupRequest(GroupRequest nref) throws SocialSiteException;

     //-------------------------------------------------------- GroupProperty CRUD

    public enum SortOrder {
        topFriends, 
        name
    }

    public enum FilterType {
        all, 
        hasApp, 
        topFriends
    }
    
    public void saveGroupProperty(
            GroupProperty groupProp) throws SocialSiteException;

    public void removeGroupProperty(
            GroupProperty groupProp) throws SocialSiteException;

    /**
     * Get group properties of a user filtered by visibility of viewerId.
     * 
     * @param userId   User whose properties are to be returned
     * @param viewerId Viewer who is requesting the propertes
     * 
     * @return Properties visible to user
     * @throws com.sun.socialsite.SocialSiteException
     */
    public Map<String, GroupProperty> getGroupPropertyMap(
        String userId, 
        String viewerId) throws SocialSiteException;
    
    /**
     * Get list with one map of profile properties per user.
     * 
     * @param userId   User ID to filter on, or null for no user   
     * @param groupId  Group ID to filter on, or null for no group
     * @param viewerId Viewer who is requesting data
     * @param sort     Sort order
     * @param filter   Filter by 
     * @param first    Offset into collection
     * @param max      Max items to return in list
     * @param details  Set of property names to be returned, or null 
     * 
     * @return List with one map of profile properties per user.
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<Map<String, GroupProperty>> getGroupPropertyMaps(
        String      userId,
        String      groupId,
        String      viewerId,
        SortOrder   sort,
        FilterType  filter,
        int         first, 
        int         max,
        Set<String> profileDetails) throws SocialSiteException;
   
}
