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
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.userapi.User;
import com.sun.socialsite.userapi.UserManagementException;
import com.sun.socialsite.userapi.UserManager;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.json.JSONObject;


/**
 * Handles requests to GET and PUT SocialSite Profile data and metadata.
 * Data is sent and received in Profile object flat JSON format.
 *
 * <pre>
 * /profiles
 *     POST - add a new profile
 *
 * /profiles/{userId}
 *     GET - get one profile
 *     PUT - update profile
 * </pre>
 */
@Service(name = "profiles", path="/{userId}")
public class ProfileHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(ProfileHandler.class);

    private static final String PROFILE_PATH = "/profiles/{userId}";

    private static final String PROFILES_PATH = "/profiles";


    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem request) {
        log.trace("BEGIN");
        RestrictedDataRequestHandler.authorizeRequest(request);

        //request.applyUrlTemplate(PROFILE_PATH);
        if (request.getUsers() != null && request.getUsers().size() > 0) {

            // determine ownerId and viewerId
            UserId userIdObject = request.getUsers().iterator().next();
            String ownerId = userIdObject.getUserId(request.getToken());
            String viewerId = request.getToken().getViewerId();
            UserManager userManager = Factory.getSocialSite().getUserManager();
            try {
                // if viewer is an admin, they see data as owner
                if (userManager.hasRole("admin", viewerId)) {
                    viewerId = ownerId;
                }
            } catch (UserManagementException ex) {
                log.error("ERROR determining user role", ex);
            }

            ResponseItem res = null;
            try {
                ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                if (log.isDebugEnabled()) {
                    log.debug("Getting profile for: " + ownerId);
                }
                Profile profile = profileManager.getProfileByUserId(ownerId);
                if (profile == null) {
                    String msg = String.format("Cannot find userId=%s", ownerId);
                    log.warn(msg);
                    return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));

                } else {
                    return ImmediateFuture.newInstance(profile.toJSON(
                        Profile.Format.FLAT, viewerId));
                }

            } catch (Exception ex) {
                String msg = String.format("Failed to return JSON for userId=%s", ownerId);
                res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
                log.error(msg, ex);
            }
        }
        log.trace("END - ERROR");
        return ImmediateFuture.newInstance(new ResponseItem(
                ResponseError.BAD_REQUEST, "No user specified"));
    }

    @Operation(httpMethods="POST")
    public Future<? extends ResponseItem> post(SocialRequestItem request) {
        log.trace("BEGIN");
        RestrictedDataRequestHandler.authorizeRequest(request);

        try {

            // ensure caller is an admin, only admins can create new profiles
            String viewerId = request.getToken().getViewerId();
            UserManager userManager = Factory.getSocialSite().getUserManager();
            try {
                if (!userManager.hasRole("admin", viewerId)) {
                    return ImmediateFuture.newInstance(new ResponseItem(
                        ResponseError.UNAUTHORIZED, "Only admins can create new profiles"));
                }
            } catch (UserManagementException ex) {
                log.error("ERROR determining user role", ex);
                return ImmediateFuture.newInstance(new ResponseItem(
                    ResponseError.INTERNAL_ERROR, ex.getLocalizedMessage()));
            }

            // parse incoming data
            String postData = request.getTypedParameter("profileProps", String.class);
            if (log.isDebugEnabled()) {
                log.debug(postData);
            }

            // make sure we've got required parameters
            JSONObject incomingProfile = new JSONObject(postData);
            if ( !(incomingProfile.has("userId")
                && incomingProfile.has("identification_name_givenName")
                && incomingProfile.has("identification_name_familyName"))) {
                String msg = "Request must specify userId, first and last names";
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            String userId =    incomingProfile.getString("userId");
            String firstName = incomingProfile.getString("identification_name_givenName");
            String lastName =  incomingProfile.getString("identification_name_familyName");

            // make sure user exists
            User user = userManager.getUserByUserId(userId);
            if (user == null) {
                String msg = "User not found : " + userId;
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }

            // make sure user does not already have profile
            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
            Profile profile = profileManager.getProfileByUserId(userId);
            if (profile != null) {
                String msg = String.format("Profile already exists for userId=%s", userId);
                return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }

            // everything is cool, so create profile
            profile = new Profile();
            profile.setUserId(userId);
            profile.setFirstName(firstName);
            profile.setLastName(lastName);
            profile.setPrimaryEmail(user.getEmailAddress());
            profileManager.saveProfile(profile);
            Factory.getSocialSite().flush();
            if (log.isDebugEnabled()) {
                log.debug("Created profile for: " + userId);
            }
            log.trace("END");
            return ImmediateFuture.newInstance(null);

        } catch (Exception e) {
            String msg = "Failed to create profile : " + e.getMessage();
            log.error(msg, e);
            log.trace("END - ERROR");
            return ImmediateFuture.newInstance(new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }


    @Operation(httpMethods="PUT")
    public Future<? extends ResponseItem> put(SocialRequestItem request) {
        log.trace("BEGIN");
        RestrictedDataRequestHandler.authorizeRequest(request);

        //request.applyUrlTemplate(PROFILE_PATH);
        ResponseItem res = null;

        if (request.getUsers() != null && request.getUsers().size() > 0) {
            UserId userIdObject = request.getUsers().iterator().next();
            String ownerId = userIdObject.getUserId(request.getToken());

            // ensure only owner and admin can update profile
            String viewerId = request.getToken().getViewerId();
            UserManager userManager = Factory.getSocialSite().getUserManager();
            try {
                // if viewer is an admin, they see data as owner
                if (!viewerId.equals(ownerId) && !userManager.hasRole("admin", viewerId)) {
                    return ImmediateFuture.newInstance(new ResponseItem(
                        ResponseError.UNAUTHORIZED, "Only owner or admin can update a profile"));
                }
            } catch (UserManagementException ex) {
                log.error("ERROR determining user role", ex);
                return ImmediateFuture.newInstance(new ResponseItem(
                    ResponseError.INTERNAL_ERROR, ex.getLocalizedMessage()));
            }

            try {
                // Find profile to be udpated
                ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                Profile profile = profileManager.getProfileByUserId(ownerId);
                if (profile == null) {
                    String msg = String.format("Cannot find userId=%s", ownerId);
                    log.warn(msg);
                    res = new ResponseItem(ResponseError.BAD_REQUEST, msg);
                }

                String postData = request.getTypedParameter("profileProps", String.class);
                if (log.isDebugEnabled()) {
                    log.debug(postData);
                }
                JSONObject incomingProfile = new JSONObject(postData);

                // Update profile
                profile.update(Profile.Format.FLAT, incomingProfile);
                profileManager.saveProfile(profile);

                if (log.isDebugEnabled()) {
                    log.debug("Updating profile for: " + ownerId);
                }
                Factory.getSocialSite().flush();

                profile = profileManager.getProfile(profile.getId());
                res = new ResponseItem(profile.toJSON(Profile.Format.FLAT, viewerId));
                log.trace("END");
                return ImmediateFuture.newInstance(res);

            } catch (Exception e) {
                String msg = "Failed to update profile for userId= " + ownerId;
                res = new ResponseItem(ResponseError.BAD_REQUEST, msg);
                log.error(msg, e);
            }
        } else {
            res = new ResponseItem(ResponseError.BAD_REQUEST, "No user specified");
        }
        log.trace("END - ERROR");
        return ImmediateFuture.newInstance(res);
    }
}
