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

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.AppInstance;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.model.Gadget;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.json.JSONObject;


/**
 * <p>Returns and accepts AppInstances.</p>
 *
 *    /gadgets/@all  [?search={searchString}]
 *        - GET - all gadgets known to system, with optional search string
 *
 *    /gadgets/@user/{subjectId} - [?subjectType={subjectTypeId}]
 *        - GET - all user's installed gadgets
 *
 *    /gadgets/@user/{subjectId}
 *        - POST - install a gadget for user (destination specified in content)
 *
 *    /gadgets/@group/{subjectId} - [?subjectType={subjectTypeId}]
 *        - GET - all group's installed gadgets
 *
 *    /gadgets/@group/{subjectId}
 *        - POST - install a gadget for group (subjectType specified in content)
 *
 *    /gadgets/{subjectType}/{subjectId}/{gadgetId}
 *        - DELETE - remove gadget instance with specified gadgetId
 */
@Service(name = "gadgets", path="/{subjectType}/{id}/{gadgetId}")
public class GadgetHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(GadgetHandler.class);

    /**
     * We override this method ourselves so that we can bypass <code>RestrictedDataRequestHandler</code>'s
     * access-control logic when we receive a DELETE operation.  Instead, we expect that our own
     * <code>handleDelete</code> method will implement appropriate (though different) access-control logic
     * in that case.  Any other operations are passed through <code>super.handleItem</code> (so that they
     * will receive <code>RestrictedDataRequestHandler</code>'s normal access-control logic.
    
    @Operation(httpMethods="GET")
    public Future<?> handleItem(SocialRequestItem request) {
        if ("DELETE".equals(request.getOperation())) {
            return handleDelete(request);
        } else {
            return super.handleItem(request);
        }
    }*/


    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(GET_GADGETS_PATH);
        String userId = null;
        String gadgetId = reqItem.getParameter("gadgetId");
        if (gadgetId == null) {
            String msg = "Request must specify gadgetId";
            log.error(msg);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
        Future<?> ret = null;
        try {
            if ("@all".equals(gadgetId)) {
                AppManager amgr = Factory.getSocialSite().getAppManager();
                List<App> allApps = amgr.getAppsInDirectory(0, -1);
                List<App> partialApps = amgr.getAppsInDirectory(reqItem.getStartIndex(), reqItem.getCount());

                List<Gadget> gadgets = new ArrayList<Gadget>();
                for (Iterator<App> it = partialApps.iterator(); it.hasNext();) {
                    App app = it.next();
                    Gadget gadget = new Gadget(app);
                    gadgets.add(gadget);
                }
                RestfulCollection<Gadget> appCollection =
                    new RestfulCollection<Gadget>(gadgets, reqItem.getStartIndex(), allApps.size());

                log.trace("END");
                ret = ImmediateFuture.newInstance(appCollection);

            } else {
                ret = ImmediateFuture.newInstance(
                    new ResponseItem(ResponseError.BAD_REQUEST, "No gadgetId specified"));
            }

        } catch (SocialSiteException sse) {
            log.error(sse.getMessage(), sse);
            ret = ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, sse.getMessage()));
        } catch (Exception e) {
            String msg = String.format(
                "Failed to retrieve gadget(s) for gagetId=", gadgetId);
            log.error(msg, e);
            ret = ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
        return ret;
    }


    @Operation(httpMethods="POST", bodyParam="TODO")
    public Future<?> post(SocialRequestItem reqItem) {

        authorizeRequest(reqItem);

        log.trace("BEGIN");
        Future<?> result = null;
        //reqItem.applyUrlTemplate(GADGETS_PATH);
        UserId userIdObject = reqItem.getUsers().iterator().next();
        String userId = userIdObject.getUserId(reqItem.getToken());

        String subjectType = reqItem.getParameter("subjectType");
        String collection = reqItem.getParameter("collection");
        String gadgetUrl = reqItem.getParameter("gadgetUrl");

        if (log.isDebugEnabled()) {
            log.debug("subjectType="+subjectType);
            log.debug("collection="+collection);
            log.debug("gadgetUrl="+gadgetUrl);
            log.debug("userId="+userId);
        }
        SocialSiteToken sstoken = (SocialSiteToken)reqItem.getToken();
        try {
            if ("@group".equals(subjectType)) {
                String groupHandle = reqItem.getParameter("id");
                if ("@current".equals(groupHandle)) {
                    groupHandle = sstoken.getGroupHandle();
                }
                createAppInstanceForGroup(groupHandle, sstoken.getViewerId(), collection, gadgetUrl);
                result = ImmediateFuture.newInstance(new JSONObject()
                    .put("code", 201).put("message", "AppInstance Created"));

            } else if ("@user".equals(subjectType)) {
                createAppInstanceForUser(userId, sstoken.getViewerId(), collection, gadgetUrl);
                result = ImmediateFuture.newInstance(new JSONObject()
                    .put("code", 201).put("message", "AppInstance Created"));

            } else {
                String msg = String.format("Unknown subjectType=%s", subjectType);
                log.warn(msg);
                result = ImmediateFuture.newInstance(msg);
            }
            log.trace("END");

        } catch (Exception e) {
            log.error("Unexpected Exception", e);
            result = ImmediateFuture.errorInstance(e);
        }

        return result;

    }


    /**
     * Handle a DELETE operation.  Note that unlike most of our operation-handling methods, this one
     * implements its own access-control logic.  Specifically, it enforces a requirement that a
     * request will only be honored if the token's moduleId matches the target <code>AppInstance</code>'s
     * moduleId.  In other words, a token is only empowered to delete its own <code>AppInstance</code>.
     */
    @Operation(httpMethods="DELETE")
    public Future<?> delete(SocialRequestItem reqItem) {

        authorizeRequest(reqItem);

        log.trace("BEGIN");
        Future<?> result = null;
        //reqItem.applyUrlTemplate(GADGET_DEL_PATH);

        try {
            Long id = Long.parseLong(reqItem.getParameter("gadgetId"));
            SecurityToken token = reqItem.getToken();
            if (!id.equals(token.getModuleId())) {
                String msg = String.format("token.moduleId[%s] != target.moduleId[%s]", token.getModuleId(), id);
                log.warn(msg);
                Exception e = new SocialSpiException(ResponseError.UNAUTHORIZED, msg);
                result = ImmediateFuture.errorInstance(e);
            } else {
                deleteAppInstance(id);
                result = ImmediateFuture.newInstance(new JSONObject()
                    .put("code", 200).put("message", "AppInstance Deleted"));
            }
            log.trace("END");
        } catch (Exception e) {
            log.error("Unexpected Exception", e);
            result = ImmediateFuture.errorInstance(e);
        }
        return result;

    }


    /**
     * Create a new AppInstance for a User.
     */
    private void createAppInstanceForUser(
        String ownerId, String viewerId, String collection, String gadgetUrlString) throws Exception {

        URL gadgetUrl = new URL(gadgetUrlString);
        if (collection == null) collection = "PROFILE";

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        Profile ownerProfile = pmgr.getProfileByUserId(ownerId);
        if (ownerProfile == null) {
            String msg = String.format("Cannot find profile with userid=%s", ownerId);
            log.warn(msg);
            return;
        }

        if (!viewerId.equals(ownerProfile.getUserId())) {
            throw new SocialSpiException(ResponseError.UNAUTHORIZED,
                "Only profile owner can install gadgets for profile");
        }

        AppManager amgr = Factory.getSocialSite().getAppManager();
        App app = amgr.getAppByURL(gadgetUrl, false);
        if (app == null) {
            String msg = String.format("Cannot find app with url=%s", gadgetUrl);
            log.warn(msg);
            return;
        }

        AppInstance appInstance = new AppInstance();
        appInstance.setCollection(collection);
        appInstance.setProfile(ownerProfile);
        appInstance.setApp(app);
        amgr.saveAppInstance(appInstance);
        Factory.getSocialSite().flush();

        if (log.isDebugEnabled()) {
            String msg = String.format("Created appInstance(%s)", appInstance);
            log.debug(msg);
        }

    }


    /**
     * Create a new AppInstance for a Group.
     */
    private void createAppInstanceForGroup(String groupHandle, String viewerId, String collection, String gadgetUrlString) throws Exception {

        URL gadgetUrl = new URL(gadgetUrlString);
        if (collection == null) collection = "GROUP";

        GroupManager gmgr = Factory.getSocialSite().getGroupManager();
        Group group = gmgr.getGroupByHandle(groupHandle);
        if (group == null) {
            String msg = String.format("Cannot find group with handle=%s", groupHandle);
            log.warn(msg);
            return;
        }

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        Profile viewer = pmgr.getProfileByUserId(viewerId);
        if (!gmgr.isAdmin(group, viewer)) {
            throw new SocialSpiException(ResponseError.UNAUTHORIZED,
                "Only profile owner can install gadgets for profile");
        }

        AppManager amgr = Factory.getSocialSite().getAppManager();
        App app = amgr.getAppByURL(gadgetUrl, false);
        if (app == null) {
            String msg = String.format("Cannot find app with url=%s", gadgetUrl);
            log.warn(msg);
            return;
        }

        AppInstance appInstance = new AppInstance();
        appInstance.setCollection(collection);
        appInstance.setGroup(group);
        appInstance.setApp(app);
        amgr.saveAppInstance(appInstance);
        Factory.getSocialSite().flush();

        if (log.isDebugEnabled()) {
            String msg = String.format("Created appInstance(%s)", appInstance);
            log.debug(msg);
        }

    }


    /**
     * Delete an existing AppInstance.
     */
    private void deleteAppInstance(Long id) throws SocialSiteException {
        AppManager appManager = Factory.getSocialSite().getAppManager();
        AppInstance appInstance = appManager.getAppInstance(id);
        appManager.removeAppInstance(appInstance);
        Factory.getSocialSite().flush();
    }

}
