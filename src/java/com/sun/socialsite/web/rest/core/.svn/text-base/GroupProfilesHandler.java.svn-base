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
import com.sun.socialsite.pojos.GroupProperty;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.json.JSONObject;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import com.sun.socialsite.util.Utilities;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;


/**
 * <p>Handles requests to CRUD on GroupProfile data. Deals with GroupProfile
 * objects, which are a SocialSite specific way to handle group data.</p>
 *
 * <p>Supports for these URIs and HTTP methods:</p>
 *    /groupProfiles/@current - GET current group profile (specified in token)
 *    /groupProfiles/{groupId} - GET/DELETE group profile
 *    /groupProfiles/@public - POST group profile
 */
@Service(name = "groupprofiles", path="/{qualifier1}/{qualifier2}")
public class GroupProfilesHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(GroupProfilesHandler.class);
    private BeanJsonConverter jsonConverter;
    private static final String GROUPPROFILES_PATH = "/groupprofiles/{qualifier1}/{qualifier2}";

    @Inject
    public GroupProfilesHandler(
            @Named("shindig.bean.converter.json") BeanConverter jsonConverter) {
        this.jsonConverter = (BeanJsonConverter)jsonConverter;
    }

    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqitem) {
        authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(GROUPPROFILES_PATH);

        String groupId = reqitem.getParameter("groupId");
        String viewerId = reqitem.getToken().getViewerId();

        if("@current".equals(groupId)) {
            SocialSiteToken tok = (SocialSiteToken) reqitem.getToken();
            groupId = tok.getGroupHandle();
        }
        return getGroupProfile(groupId, viewerId);
    }

    @Operation(httpMethods="PUT")
    public Future<? extends ResponseItem> put(SocialRequestItem reqitem) {
        authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(GROUPPROFILES_PATH);
        String groupId = reqitem.getParameter("groupId");

        if("@current".equals(groupId)) {
            SocialSiteToken tok = (SocialSiteToken) reqitem.getToken();
            groupId = tok.getGroupHandle();
        }
        String postData = reqitem.getTypedParameter("profileProps", String.class);
        try {
            String viewerId = reqitem.getToken().getViewerId();
            JSONObject incomingGroup = new JSONObject(postData);

            // Ensure that a group exists
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group grp = gmgr.getGroupByHandle(groupId);
            if(grp == null) {
                String msg = String.format("Unable to find group with ID =%s", groupId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            // make sure user has a profile
            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
            Profile profile = profileManager.getProfileByUserId(viewerId);
            if (profile == null) {
                String msg = String.format("Unable to find profile for user=%s", viewerId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            // Ensure that the editor has the required permissions
            if(!gmgr.isAdmin(grp, profile)) {
                String msg = String.format(
                   "%s permission denied to edit group %s",viewerId, groupId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            grp.update(incomingGroup);
            gmgr.saveGroup(grp);
            Factory.getSocialSite().flush();
            return ImmediateFuture.newInstance(null);

        } catch (Exception e) {
            String msg = "Failed to update group profile details : " + e.getMessage();
            log.error(msg, e);
            log.trace("END - ERROR");
            return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }

    private Future<?> getGroupProfile(String grpHandle, String viewerId) {
        ResponseItem res = null;
        try {
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group grp = gmgr.getGroupByHandle(grpHandle);
            if(grp == null) {
                String msg = "Unable to find group : " + grpHandle;
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));                
            }
            return ImmediateFuture.newInstance(grp.toJSON(Group.Format.FLAT, viewerId));

        } catch (Exception ex) {
            String msg = String.format("Error while getting group : " +
                    ex.getLocalizedMessage());
            res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
            log.error(msg, ex);
        }
        return ImmediateFuture.newInstance(res);
    }

    @Operation(httpMethods="DELETE")
    public Future<?> delete(SocialRequestItem reqitem) throws SocialSpiException {
        authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(GROUPPROFILES_PATH);
        String groupId = reqitem.getParameter("groupId");

        if (groupId == null) {
            String msg = String.format("Valid group id is required for deleting groups");
            return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
        if("@current".equals(groupId)) {
            SocialSiteToken tok = (SocialSiteToken) reqitem.getToken();
            groupId = tok.getGroupHandle();
        }
        //this is a DELETE on /groups/@public/{groupId}
        try {
            String viewerId = reqitem.getToken().getViewerId();
            // Ensure that a group exists
            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            Group grp = gmgr.getGroupByHandle(groupId);
            if(grp == null) {
                String msg = String.format("Unable to find group with ID =%s", groupId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            // make sure user has a profile
            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
            Profile profile = profileManager.getProfileByUserId(viewerId);
            if (profile == null) {
                String msg = String.format("Unable to find profile for user=%s", viewerId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            // Ensure that the requestor has the required permissions
            if(!gmgr.isAdmin(grp, profile)) {
                String msg = String.format("You (%s) do not have permission to remove group %s",
                        viewerId, groupId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            gmgr.removeGroup(grp);
            Factory.getSocialSite().flush();
            return ImmediateFuture.newInstance(null);

        } catch (Exception e) {
            String msg = "Failed to remove group  : " + e.getMessage();
            log.error(msg, e);
            return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }

    @Operation(httpMethods="POST")
    public Future<?> post(SocialRequestItem reqitem) {
        authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(GROUPPROFILES_PATH);
        String qual1 = reqitem.getParameter("qualifier1");

        if("@public".equals(qual1)) {
            String postData = reqitem.getTypedParameter("groupDetails", String.class);
            try {
                String viewerId = reqitem.getToken().getViewerId();

                // make sure we've got required parameters
                JSONObject incomingGroup = new JSONObject(postData);
                if ( !(incomingGroup.has(Group.SIMPLE_NAME)
                    && incomingGroup.has(Group.SIMPLE_HANDLE))) {
                    String msg = "Request must specify name and handle";
                    return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
                }
                String groupName =    incomingGroup.getString(Group.SIMPLE_NAME);
                String groupHandle =  incomingGroup.getString(Group.SIMPLE_HANDLE);
                String description =  incomingGroup.getString(Group.SIMPLE_DESCRIPTION);
                String imageUrl =     incomingGroup.getString(Group.SIMPLE_IMAGE_URL);

                // make sure user has a profile
                ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                Profile profile = profileManager.getProfileByUserId(viewerId);
                if (profile == null) {
                    String msg = String.format("Unable to find profile for user=%s", viewerId);
                    return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
                }

                GroupManager gmgr = Factory.getSocialSite().getGroupManager();
                Group newGroup = new Group();
                newGroup.setName(groupName);
                newGroup.setHandle(groupHandle);
                newGroup.setDescription(description);
                if(imageUrl != null && imageUrl.length() != 0) {
                    if(!downloadImage(imageUrl, newGroup)) {
                        String msg = String.format("Unable to get image from %s", imageUrl);
                        return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));                        
                    }
                }
                gmgr.createGroup(newGroup, profile);
                Factory.getSocialSite().flush();
                newGroup = gmgr.getGroupByHandle(groupHandle);
                if (profile == null) {
                    String msg = String.format("Unable to find group %s that was just created !!!!", groupName);
                    return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
                }
                GroupProperty emailProp = new GroupProperty();
                emailProp.setName("contact_primaryemail");
                emailProp.setValue(profile.getPrimaryEmail());
                emailProp.setNameKey("socialsite.groupProfile.property.primaryemail");
                emailProp.setVisibility(GroupProperty.VisibilityType.PRIVATE);
                emailProp.setCreated(new Date());
                emailProp.setUpdated(new Date());
                gmgr.saveGroupProperty(emailProp);
                newGroup.addGroupProp(emailProp);
                Factory.getSocialSite().flush();
                return ImmediateFuture.newInstance(null);
            } catch (Exception e) {
                String msg = "Failed to create group : " + e.getMessage();
                log.error(msg, e);
                log.trace("END - ERROR");
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
        }
        String msg = String.format("Unsupported POST request for Groups");
        return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
    }
    
    private boolean downloadImage(String imageLocation, Group grp) {
        try {
            URLConnection conn = new URL(imageLocation).openConnection();
            InputStream inp = conn.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Utilities.copyInputToOutput(inp, bos);
            byte[] byteArray = bos.toByteArray();            
            grp.setImageType(conn.getContentType());
            grp.setImage(byteArray);
        } catch(Exception ex) {
            return false;
        }
        return true;
    }
}
