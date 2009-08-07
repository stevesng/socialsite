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
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.SearchManager;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.json.JSONObject;

/**
 * <p>Handles message requests.</p>
 * 
 * <p>Supports this URs and HTTP method:</p>
 *    /search/{searchString} - GET to get search results.<br/>
 */
@Service(name = "search", path="/{searchString}/{type}")
public class SearchHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(SearchHandler.class);
    private static final String SEARCH_PATH = "/search/{searchString}/{type}";
    
    private static final String GADGET = "gadget";
    private static final String GROUP = "group";
    private static final String PROFILE = "profile";

    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqItem) {
        RestrictedDataRequestHandler.authorizeRequest(reqItem);

        String searchString = null;
        try {
            ResponseItem res = null;
            log.trace("BEGIN");

            //reqItem.applyUrlTemplate(SEARCH_PATH);
            searchString = reqItem.getParameter("searchString");
            String type = reqItem.getParameter("type");
            int offset = reqItem.getStartIndex();
            int length = reqItem.getCount();

            String viewerId = reqItem.getToken().getViewerId();

            // todo: add an "all" check for initial search?
            if (PROFILE.equals(type)) {
                return profileSearch(viewerId, searchString, offset, length);
            }
            else if (GROUP.equals(type)) {
                return groupSearch(viewerId, searchString, offset, length);
            }
            else if (GADGET.equals(type)) {
                return gadgetSearch(viewerId, searchString, offset, length);
            }
            else {
                String msg = String.format("Unknown search type=%s", type);
                log.error(msg);
                return ImmediateFuture.newInstance(
                    new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
        } catch (Exception e) {
            String msg = String.format(
                "Exception while searching for searchString=%s", searchString);
            log.error(msg, e);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }


    /*
     * URL of form /search/profile/<searchStr>/<offset>/<maxResults>
     * This method allows more specific searches than getOnSearch().
     * TODO: also return total number of all results in reply (may need
     * a separate method for this.)
     */
    private Future<?> profileSearch(String viewerId, String searchStr, int offset, int length)
            throws Exception {
        
        SearchManager smgr = Factory.getSocialSite().getSearchManager();
        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();

        int totalProfiles = smgr.getTotalProfiles(searchStr);

        List<Profile> profiles = smgr.getProfiles(
            pmgr, offset, length, searchStr);
        
        List<JSONObject> people = new ArrayList<JSONObject>();

        JSONObject searchList = new JSONObject();
        if (profiles == null) {
            String msg = String.format(
                "Cannot find any string matches for \"%s\"", searchStr);
            log.debug(msg);
            return ImmediateFuture.newInstance(new ResponseItem(msg));
        }
        else {
            searchList.put("totalProfiles", totalProfiles);
            Set<String> fields = new HashSet<String>();
            fields.add("viewerRelationship");
            for (Profile profile : profiles) {
                people.add(profile.toJSON(Profile.Format.OPENSOCIAL, viewerId, fields));
            }
        }

        RestfulCollection<JSONObject> collection =
            new RestfulCollection<JSONObject>(people, offset, totalProfiles);

        log.trace("END");
        return ImmediateFuture.newInstance(collection);
    }

    /*
     * URL of form /search/group/<searchStr>/<offset>/<maxResults>
     * This method allows more specific searches than getOnSearch().
     * TODO: also return total number of all results in reply (may need
     * a separate method for this.)
     */
    private Future<?> groupSearch(String viewerId, String searchStr, int offset, int length)
            throws Exception {

        SearchManager searchManager =
            Factory.getSocialSite().getSearchManager();
        GroupManager groupManager = Factory.getSocialSite().getGroupManager();

        int totalGroups = searchManager.getTotalGroups(searchStr);

        List<Group> groups = searchManager.getGroups(
            groupManager, offset, length, searchStr);

        List<JSONObject> jsonGroups = new ArrayList<JSONObject>();
        for (Group group : groups) {
            jsonGroups.add(group.toJSON(Group.Format.OPENSOCIAL, viewerId));
        }

        RestfulCollection<JSONObject> collection =
            new RestfulCollection<JSONObject>(jsonGroups, offset, totalGroups);

        log.trace("END");
        return ImmediateFuture.newInstance(collection);
    }

    /*
     * URL of form /search/gadget/<searchStr>/<offset>/<maxResults>
     * This method allows more specific searches than getOnSearch().
     * TODO: also return total number of all results in reply (may need
     * a separate method for this.)
     */
    private Future<?> gadgetSearch(String viewerId, String searchStr, int offset, int length)
            throws Exception {
        SearchManager searchManager = Factory.getSocialSite().getSearchManager();
        AppManager appManager = Factory.getSocialSite().getAppManager();
        int totalApps = searchManager.getTotalApps(searchStr);
        List<App> apps = searchManager.getApps(appManager, offset, length, searchStr);

        JSONObject searchList = new JSONObject();
        if (apps == null) {
            String msg = String.format("Cannot find any string matches for \"%s\"", searchStr);
            log.debug(msg);
            return ImmediateFuture.newInstance(new ResponseItem(msg));
        } else {
            searchList.put("totalApps", totalApps);

            /* Gadget doesn't know how to remember this info, so it's sent
             * back to the gadget in response.
             */
            searchList.put("pageIndex", offset / length);

            for (App app : apps) {
                searchList.append("Gadgets", app.toJSON());
            }
        }
        log.trace("END");
        return ImmediateFuture.newInstance(searchList);
    }

}
