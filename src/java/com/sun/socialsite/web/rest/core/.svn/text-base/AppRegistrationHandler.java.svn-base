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

import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.impl.JPAOAuthStore;
import com.sun.socialsite.pojos.AppRegistration;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.model.AppRegistrationWrapper;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;


/**
 * <p>Returns and accepts AppRegistrations.</p>
 *
 *    /appregistry
 *        - GET - get collection of gadget registrations associated with caller
 *        - POST - register a new Gadget on behalf of caller
 *
 *    /appregistry/{appid}
 *        - DELETE - delete Gadget registration record
 */
@Service(name = "appregistry", path="/{id}")
public class AppRegistrationHandler extends RestrictedDataRequestHandler {
    private static Log log = LogFactory.getLog(AppRegistrationHandler.class);

    private static final String COLLECTION_PATH = "/appregistry";
    private static final String ITEM_PATH = "/appregistry/{id}";

    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(COLLECTION_PATH);
        SocialSiteToken sstoken = (SocialSiteToken)reqItem.getToken();
        String viewerId = sstoken.getViewerId();

        Future<?> result = null;
        try {
            AppManager amgr = Factory.getSocialSite().getAppManager();
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            JPAOAuthStore oauthStore = (JPAOAuthStore)Factory.getSocialSite().getOAuthStore();

            // get callers profile
            Profile viewer = pmgr.getProfileByUserId(viewerId); 

            // get all app registrations owned by caller
            List<AppRegistration> appregs = amgr.getAppRegistrations(viewer.getId(), null);
            List<AppRegistrationWrapper> appwraps = new ArrayList<AppRegistrationWrapper>();
            for (AppRegistration appreg : appregs) {

                BasicOAuthStoreConsumerKeyAndSecret keyAndSecret =
                    oauthStore.getConsumerInfo(appreg.getAppUrl(), appreg.getServiceName());

                if (keyAndSecret != null) {
                    appwraps.add(new AppRegistrationWrapper(appreg, 
                        keyAndSecret.getConsumerKey(),
                        keyAndSecret.getConsumerSecret(),
                        keyAndSecret.getKeyType().name()));
                } else {
                    appwraps.add(new AppRegistrationWrapper(appreg));
                }
            }
            RestfulCollection<AppRegistrationWrapper> appCollection =
                new RestfulCollection<AppRegistrationWrapper>(appwraps, 0, appwraps.size());

            log.trace("END");
            result = ImmediateFuture.newInstance(appCollection);

        } catch (Exception e) {
            log.error("ERROR: error fetching app registrations", e);
            result = ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, "ERROR: error fetching app registrations"));
        }
        return result;
    }


    @Operation(httpMethods="POST", bodyParam="registration")
    public Future<?> post(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(COLLECTION_PATH);
        SocialSiteToken sstoken = (SocialSiteToken)reqItem.getToken();
        String viewerId = sstoken.getViewerId();

        Future<?> result = null;
        try {
            AppManager amgr = Factory.getSocialSite().getAppManager();
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            JPAOAuthStore oauthStore = (JPAOAuthStore)Factory.getSocialSite().getOAuthStore();

            // get callers profile
            Profile viewer = pmgr.getProfileByUserId(viewerId);

            // get incoming registration data and register it
            AppRegistrationWrapper regwrap =
                reqItem.getTypedParameter("registration", AppRegistrationWrapper.class);
            amgr.registerApp(viewer.getId(), regwrap.getAppUrl(), regwrap.getServiceName());
            Factory.getSocialSite().flush();

            oauthStore.saveConsumerInfo(
                    regwrap.getAppUrl(),
                    regwrap.getServiceName(),
                    regwrap.getServiceConsumerKey(),
                    regwrap.getServiceConsumerSecret(),
                    regwrap.getServiceKeyType());

            // TODO: how do you return an HTTPD 202 here?
            result = ImmediateFuture.newInstance(null);

            log.trace("END");

        } catch (Exception e) {
            log.error("Unexpected Exception", e);
            result = ImmediateFuture.errorInstance(e);
        }

        return result;
    }


    /**
     * Unregister an app
     */
    @Operation(httpMethods="POST")
    public Future<?> delete(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(COLLECTION_PATH);
        SocialSiteToken sstoken = (SocialSiteToken)reqItem.getToken();
        String viewerId = sstoken.getViewerId();

        Future<?> result = null;
        try {
            AppManager amgr = Factory.getSocialSite().getAppManager();
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            JPAOAuthStore oauthStore = (JPAOAuthStore)Factory.getSocialSite().getOAuthStore();

            // get callers profile
            Profile viewer = pmgr.getProfileByUserId(viewerId);

            // get incoming registration data and register it
            String appid = reqItem.getParameter("id");

            AppRegistration appreg = amgr.getAppRegistration(appid);
            if (appreg.getProfile().equals(viewer)) {
                String gadgetUri = appreg.getAppUrl();
                String serviceName = appreg.getServiceName();

                amgr.removeAppRegistration(appid,"Removed by user");
                Factory.getSocialSite().flush();

                oauthStore.removeConsumerInfo(gadgetUri, serviceName);

                // TODO: how do you return an HTTPD 200 here?
                result = ImmediateFuture.newInstance(null);
                
            } else {
                result = ImmediateFuture.newInstance(new ResponseItem(
                    ResponseError.BAD_REQUEST,
                    "Cannot remove app registration you do not own"));
            }
            log.trace("END");

        } catch (Exception e) {
            log.error("Unexpected Exception", e);
            result = ImmediateFuture.errorInstance(e);
        }

        return result;
    }
}
