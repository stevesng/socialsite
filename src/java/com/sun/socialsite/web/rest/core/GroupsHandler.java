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

package com.sun.socialsite.web.rest.core;

import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.util.concurrent.Future;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.json.JSONObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.ArrayList;
import java.util.Set;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;


/**
 * <p>Handles read-only access to Groups data. Deals with Group objects, which
 * may eventually become part of OpenSocial.</p>
 *
 * <p>Supports for these URIs and HTTP methods:</p>
 *    /groups/@public - GET of all public groups<br/>
 *    /groups/@public/{groupId} - GET of a given group<br/>
 *    /groups/{userId} - GET of user's groups<br/>
 *    /groups/{userId/@friends - GET of user's friend's groups
 */
@Service(name = "groups", path="/{qualifier1}/{qualifier2}")
public class GroupsHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(GroupsHandler.class);
    private static final String GROUPS_PATH = "/groups/{qualifier1}/{qualifier2}";

    private BeanJsonConverter jsonConverter;

    @Inject
    public GroupsHandler(
            @Named("shindig.bean.converter.json") BeanConverter jsonConverter) {
        this.jsonConverter = (BeanJsonConverter)jsonConverter;
    }

    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqitem) throws SocialSpiException {
        authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(GROUPS_PATH);
        String qual1 = reqitem.getParameter("qualifier1");
        String qual2 = reqitem.getParameter("qualifier2");
        String viewerId = reqitem.getToken().getViewerId();
        String userId = null;
        if (reqitem.getUsers() != null && reqitem.getUsers().size() >0) {
            UserId userIdObject = reqitem.getUsers().iterator().next();
            userId = userIdObject.getUserId(reqitem.getToken());
        }
        if ("@public".equals(qual1)) {
            if (qual2 == null || "@self".equals(qual2)) {
                // this is a GET on /groups/@public
                // return list of all public groups
                return getAllPublicGroups(viewerId,
                        reqitem.getStartIndex(), reqitem.getCount());
            }
            //this is a GET on /groups/@public/{groupId}
            // return details of the requested group
            if ("@current".equals(qual2)) {
                SocialSiteToken tok = (SocialSiteToken) reqitem.getToken();
                qual2 = tok.getGroupHandle();
            }
            return getAPublicGroup(viewerId, qual2, userId);
        }
        // URL does not have @public
        if ("@friends".equals(qual2)) {
            // URL is /groups/{userid}/@friends
            return(getUsersFriendsGroups(userId));
        }
        
        if ("@groups".equals(qual1)) {
            // URL is /groups/@groups/{groupId}
            if ("@current".equals(qual2)) {
                SocialSiteToken tok = (SocialSiteToken) reqitem.getToken();
                qual2 = tok.getGroupHandle();
            }
            
            return getMembersGroups(qual2);
        }
        // URL is /groups/{userId}
        return getUsersGroups(viewerId, userId,
            reqitem.getStartIndex(), reqitem.getCount());
    }


    private Future<?> getAPublicGroup(String viewerId, String grpHandle, String userId) throws SocialSpiException {
        ResponseItem res = null;
        try {
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group grp = gmgr.getGroupByHandle(grpHandle);
            if (grp == null) {
                String msg = "Unable to find group : " + grpHandle;
                throw new SocialSpiException(ResponseError.BAD_REQUEST, msg);
            }
            return ImmediateFuture.newInstance(grp.toJSON(Group.Format.OPENSOCIAL, viewerId));

        } catch (Exception ex) {
            String msg = String.format(
                "Error while getting group : " + ex.getLocalizedMessage());
            log.error(msg, ex);
            throw new SocialSpiException(ResponseError.INTERNAL_ERROR, msg, ex);
        }
    }


    private Future<?> getAllPublicGroups(String viewerId, int offset, int length) {
        ResponseItem res = null;
        try {
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            List<Group> groups = gmgr.getGroups(offset, length);
            List<Group> allGroups = gmgr.getGroups(0, -1);
            List<JSONObject> jsonGroups = new ArrayList<JSONObject>();
            if (!groups.isEmpty()) {
                for(Group g : groups) {
                    jsonGroups.add(g.toJSON(Group.Format.OPENSOCIAL, viewerId));
                }
            }
            RestfulCollection<JSONObject> collection =
                new RestfulCollection<JSONObject>(jsonGroups, offset, allGroups.size());
            return ImmediateFuture.newInstance(collection);

        } catch (Exception ex) {
            String msg = String.format("Error while getting list of public groups : " +
                    ex.getLocalizedMessage());
            res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
            log.error(msg, ex);
        }
        return ImmediateFuture.newInstance(res);
    }


    private Future<?> getUsersGroups(
            String viewerId, String userId, int offset, int length) {
        ResponseItem res = null;
        try {
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            Profile userProfile = pmgr.getProfileByUserId(userId);
            if(userProfile == null) {
                String msg = String.format("Unable to find user " + userId);
                res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
                return ImmediateFuture.newInstance(res);
            }
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            List<GroupRelationship> groupRels =
                gmgr.getMembershipsByProfile(userProfile, offset, length);
            List<GroupRelationship> allGroups =
                gmgr.getMembershipsByProfile(userProfile, 0, -1);
            List<JSONObject> groups = new ArrayList<JSONObject>();
            if (!groupRels.isEmpty()) {
                for(GroupRelationship g : groupRels) {
                    groups.add(g.getGroup().toJSON(Group.Format.OPENSOCIAL, viewerId));
                }
            }
            RestfulCollection<JSONObject> collection =
                new RestfulCollection<JSONObject>(groups, offset, allGroups.size());
            return ImmediateFuture.newInstance(collection);
            
        } catch (Exception ex) {
            String msg = String.format("Error while getting list of users groups : " +
                    ex.getLocalizedMessage());
            res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
            log.error(msg, ex);
        }
        return ImmediateFuture.newInstance(res);
    }


    private Future<?> getUsersFriendsGroups(String userId) {
        ResponseItem res = null;
        try {
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            Profile userProfile = pmgr.getProfileByUserId(userId);
            if(userProfile == null) {
                String msg = String.format("Unable to find user " + userId);
                res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
                return ImmediateFuture.newInstance(res);
            }
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            List<Group> friendsGroups = gmgr.getFriendsGroups(userProfile);
            List<JSONObject> groups = new ArrayList<JSONObject>();
            if (!friendsGroups.isEmpty()) {
                for(Group g : friendsGroups) {
                    groups.add(g.toJSON());
                }
            }
            RestfulCollection<JSONObject> collection =
                new RestfulCollection<JSONObject>(groups, 0, groups.size());
            return ImmediateFuture.newInstance(collection);

        } catch (Exception ex) {
            String msg = String.format("Error while getting list of friend's groups : " +
                    ex.getLocalizedMessage());
            res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
            log.error(msg, ex);
        }
        return ImmediateFuture.newInstance(res);
    }
    
    
    private Future<?> getMembersGroups(String groupId) {
        ResponseItem res = null;
        try {
            // Ensure that a group exists
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group grp = gmgr.getGroupByHandle(groupId);
            if (grp == null) {
                String msg = String.format("Unable to find group with ID =%s", groupId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }

            Set<Group> membersGroups = gmgr.getGroupMembersGroups(grp);
            List<JSONObject> groups = new ArrayList<JSONObject>();
            if (!membersGroups.isEmpty()) {
                JSONObject json = new JSONObject();
                for (Group g : membersGroups) {
                    groups.add(g.toJSON());
                }
            }
            RestfulCollection<JSONObject> collection =
                new RestfulCollection<JSONObject>(groups, 0, groups.size());
            return ImmediateFuture.newInstance(collection);

        } catch (Exception ex) {
            String msg = String.format("Error while getting list of group member's groups : " +
                    ex.getLocalizedMessage());
            res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
            log.error(msg, ex);
        }
        
        return ImmediateFuture.newInstance(res);
    }


}
