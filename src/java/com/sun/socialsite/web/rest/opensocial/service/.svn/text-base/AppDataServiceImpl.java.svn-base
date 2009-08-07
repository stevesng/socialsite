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
package com.sun.socialsite.web.rest.opensocial.service;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.AppData;
import com.sun.socialsite.pojos.Profile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.DataCollection;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;


/**
 * Responsble for CRUD of application data, both user-specific application 
 * data and global applicaiton data.
 */
public class AppDataServiceImpl implements AppDataService {

    private static Log log = LogFactory.getLog(AppDataServiceImpl.class);


    public AppDataServiceImpl() {
    }


    public Future<DataCollection> getPersonData(Set<UserId> userIds, GroupId groupId,
            String appId, Set<String> fields, SecurityToken token) {
        return ImmediateFuture.newInstance(getPersonDataInternal(userIds, groupId, appId, fields, token));
    }


    public Future<Void> deletePersonData(UserId userId, GroupId groupId, String appId,
            Set<String> fields, SecurityToken token) {
        return ImmediateFuture.newInstance(deletePersonDataInternal(userId, groupId, appId, fields, token));
    }


    public Future<Void> updatePersonData(UserId userId, GroupId groupId, String appId,
            Set<String> fields, Map<String, String> values, SecurityToken token) {
        return ImmediateFuture.newInstance(updatePersonDataInternal(userId, groupId, appId, fields, values, token));
    }


    /**
     * Retrives app data for the specified user and group.
     * @param userId  The user
     * @param groupId The group
     * @param appId   The app
     * @param fields  The fields to filter the data by.
     * @param token   The security token
     * @return The data fetched
     */
    private DataCollection getPersonDataInternal(
            Set<UserId>   userIds, 
            GroupId       groupId,
            String        appId,
            Set<String>   fields,
            SecurityToken token) {

        UserId userIdObject = userIds.iterator().next();
        String uid = userIdObject.getUserId(token);
        log.debug(String.format("getPersonData(%s)", uid));

        Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();

        try {

            AppManager appManager = Factory.getSocialSite().getAppManager();
            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();

            // TODO: handle requests for friends and groups app data
            // TODO: honor fields requested

            Profile profile = profileManager.getProfileByUserId(uid);
            App app = appManager.getApp(appId);
            List<AppData> appDataList = appManager.getAppData(app, profile);
            HashMap<String, String> instanceData = new HashMap<String, String>();
            for (AppData appData : appDataList) {
                instanceData.put(appData.getName(), appData.getValue());
            }
            data.put(uid, instanceData);

        } catch (SocialSiteException e) {
            log.error("Failed to get person AppData", e);
            return newErrorMessageDataCollecton("Failed to get person AppData: " + e.getMessage());
        }

        return new DataCollection(data);

    }


    /**
     * Updates app data for the specified user and group with the new values.
     *
     * @param userId  The user
     * @param groupId The group
     * @param appId   The app
     * @param fields  The fields to filter the data by.
     * @param values  The values to set
     * @param token   The security token
     * @return an error if one occurs
     */
    private Void updatePersonDataInternal(
            UserId              userId,
            GroupId             groupId,
            String              appId,
            Set<String>         fields,
            Map<String, String> values,
            SecurityToken       token) {

        String uid = userId.getUserId(token);

        if (log.isDebugEnabled()) {
            StringBuilder sbkeys = new StringBuilder();
            for (String field : fields) {
                if (sbkeys.length() > 0) {
                    sbkeys.append(",");
                }
                sbkeys.append(field);
            }
            StringBuilder sbvalues = new StringBuilder();
            for (String value : values.keySet()) {
                if (sbkeys.length() > 0) {
                    sbkeys.append(",");
                }
                sbkeys.append(value);
            }
            log.debug(String.format("updatePersonData(%s, [%s], [%s])", uid, sbkeys, sbvalues));
        }

        try {

            AppManager appManager = Factory.getSocialSite().getAppManager();
            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();

            Profile profile = profileManager.getProfileByUserId(uid);
            App app = appManager.getApp(appId);
            for (String key : values.keySet()) {
                appManager.setAppData(app, profile, key, values.get(key));
            }
            Factory.getSocialSite().flush();

        } catch (SocialSiteException e) {
            log.error("Failed to update person AppData", e);
        }

        return null;
    }


    /**
     * Deletes data for the specified user and group.
     *
     * @param userId  The user
     * @param groupId The group
     * @param appId   The app
     * @param fields  The fields to delete.
     * @param token   The security token
     * @return an error if one occurs
     */
    private Void deletePersonDataInternal(
            UserId        userId, 
            GroupId       groupId,
            String        appId,
            Set<String>   fields,
            SecurityToken token) {

        // TODO: implement delete of person App Data via OpenSocial API       

        return null;
    }


    private static DataCollection newErrorMessageDataCollecton(String message) {
        Map<String, String> inner = new HashMap<String, String>();
        inner.put("message", message);
        Map<String, Map<String, String>> outer = new HashMap<String, Map<String, String>>();
        outer.put("error", inner);
        return new DataCollection(outer);
    }

}
