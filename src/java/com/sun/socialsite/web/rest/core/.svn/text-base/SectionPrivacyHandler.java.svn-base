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
import com.sun.socialsite.pojos.SectionPrivacy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.UserId;


/**
 * <p>Handles requests to GET and PUT profile section privacy data.</p>
 *
 * <p>Supports these URIs and HTTP methods:</p>
 *    /sectionprivs/{userId} - GET and of user's section privacy objects.<br />
 *    /sectionprivs/{userId}/{section} - PUT of one section privacy.<br />
 */
@Service(name = "sectionprivs", path="/{userId}/{personId}/{sectionName}")
public class SectionPrivacyHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(SectionPrivacyHandler.class);

    private static final String FEED_PATH = "/sectionprivs/{userId}/{personId}";

    private static final String ITEM_PATH = "/sectionprivs/{userId}/{personId}/{sectionName}";


    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqitem) {
        log.trace("BEGIN");
        RestrictedDataRequestHandler.authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(FEED_PATH);

        if (reqitem.getUsers() != null && reqitem.getUsers().size() > 0) {
            UserId userIdObject = reqitem.getUsers().iterator().next();
            String userId = userIdObject.getUserId(reqitem.getToken());

            ResponseItem res = null;
            try {
                ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                log.debug("Getting privacy settings for: " + userId);
                Profile profile = profileManager.getProfileByUserId(userId);
                if (profile == null) {
                    String msg = String.format("Cannot find userId=%s", userId);
                    log.warn(msg);
                    res = new ResponseItem(ResponseError.BAD_REQUEST, msg);

                } else {
                    // return SectionPrivacy objects, let Shindig convert them to JSON or XML
                    Map<String, SectionPrivacy> privaciesMap = profileManager.getSectionPrivacies(profile);
                    List<SectionPrivacy> list = new ArrayList<SectionPrivacy>(privaciesMap.values());

                    // return entire collection, ignoring startIndex and totalResults
                    RestfulCollection<SectionPrivacy> col = new RestfulCollection<SectionPrivacy>(list, 0, list.size());
                    return ImmediateFuture.newInstance(col);
                }

            } catch (Exception ex) {
                String msg = String.format("Failed to return JSON for userId=%s", userId);
                res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
                log.error(msg, ex);
            }
        }
        return ImmediateFuture.newInstance(new ResponseItem(
            ResponseError.BAD_REQUEST, "No user specified"));
    }


    @Operation(httpMethods="PUT")
    public Future<?> put(SocialRequestItem reqitem) {
        log.trace("BEGIN");
        RestrictedDataRequestHandler.authorizeRequest(reqitem);

        //reqitem.applyUrlTemplate(ITEM_PATH);

        if (reqitem.getUsers() != null && reqitem.getUsers().size() > 0) {
            UserId userIdObject = reqitem.getUsers().iterator().next();
            String userId = userIdObject.getUserId(reqitem.getToken());

            String sectionName = reqitem.getParameter("sectionName");

            ResponseItem res = null;
            try {
                ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                log.debug("Updating section privacy [" + sectionName + "] for user: " + userId);
                Profile profile = profileManager.getProfileByUserId(userId);
                if (profile == null) {
                    String msg = String.format("Cannot find userId=%s", userId);
                    log.warn(msg);
                    res = new ResponseItem(ResponseError.BAD_REQUEST, msg);

                } else {
                    // let Shindig build the object from JSON or XML
                    SectionPrivacy sectionPrivacy = reqitem.getTypedParameter("sectionPrivacy", SectionPrivacy.class);

                    // update section privacy in database
                    profileManager.updateSectionPrivacy(profile, sectionPrivacy);

                    Factory.getSocialSite().flush();

                    log.trace("END");
                    return ImmediateFuture.newInstance(null);
                }

            } catch (Exception ex) {
                String msg = String.format("Failed to return JSON for userId=%s", userId);
                res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
                log.error(msg, ex);
                log.trace("END - ERROR");
                return ImmediateFuture.newInstance(res);
            }
        }
        log.trace("END - ERROR");
        return ImmediateFuture.newInstance(new ResponseItem(
            ResponseError.BAD_REQUEST, "User or section privacy name not specified"));
    }
}
