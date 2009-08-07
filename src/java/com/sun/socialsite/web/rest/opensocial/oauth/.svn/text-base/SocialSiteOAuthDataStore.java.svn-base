/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.sun.socialsite.web.rest.opensocial.oauth;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.AppInstance;
import com.sun.socialsite.pojos.AppRegistration;
import com.sun.socialsite.pojos.Profile;
import java.net.URL;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 * SocialSite implementation for OAuth data store.
 * Allows SocialSite to act as an OAuth Provider.
 */
public class SocialSiteOAuthDataStore implements OAuthDataStore {
    private static Log log = LogFactory.getLog(SocialSiteOAuthDataStore.class);
    private String domain = null;
    private String container = null;
    private final OAuthServiceProvider SERVICE_PROVIDER;
    

    @Inject
    public SocialSiteOAuthDataStore(@Named("shindig.oauth.base-url") String baseUrl) {
        this.SERVICE_PROVIDER = new OAuthServiceProvider(
            baseUrl + "requestToken", baseUrl + "authorize", baseUrl + "accessToken");
        this.domain = Config.getProperty("socialsite.oauth.domain");
        this.container = Config.getProperty("socialsite.oauth.container");
    }

    // Get the OAuthEntry that corresponds to the oauthToken
    public OAuthEntry getEntry(String oauthToken) {
        Preconditions.checkNotNull(oauthToken);
        AppManager amgr = Factory.getSocialSite().getAppManager();
        try {
            return amgr.getOAuthEntry(oauthToken);
        } catch (SocialSiteException ex) {
            log.error("ERROR fetching OAuthENtry", ex);
        }
        return null;
    }

    public OAuthConsumer getConsumer(String consumerKey) {
        try {
            AppManager amgr = Factory.getSocialSite().getAppManager();
            AppRegistration appreg = amgr.getAppRegistrationByConsumerKey(consumerKey);
            String consumerSecret = null;
            if (appreg != null && "APPROVED".equals(appreg.getStatus())) {
                consumerSecret = appreg.getConsumerSecret();
            }
            if (consumerSecret == null) {
                return null;
            }

            // null below is for the callbackUrl, which we don't have in the db
            OAuthConsumer consumer = new OAuthConsumer(
                null, consumerKey, consumerSecret, SERVICE_PROVIDER);

            return consumer;

        } catch (Exception e) {
            log.debug("Error fetching consumer secret for consumerKey: " + consumerKey, e);
            return null;
        }
    }

    // Generate a valid requestToken for the given consumerKey
    public OAuthEntry generateRequestToken(String consumerKey) {

        try {
            AppManager amgr = Factory.getSocialSite().getAppManager();
            AppRegistration appreg = amgr.getAppRegistrationByConsumerKey(consumerKey);
            String appId = amgr.getAppByURL(new URL(appreg.getAppUrl())).getId();
            
            OAuthEntry entry  = new OAuthEntry();
            entry.appId       = appId;
            entry.consumerKey = consumerKey;
            entry.domain      = this.domain;
            entry.container   = this.container;
            entry.token       = UUID.randomUUID().toString();
            entry.tokenSecret = UUID.randomUUID().toString();
            entry.type        = OAuthEntry.Type.REQUEST;
            entry.issueTime   = new Date();
            try {
                amgr.saveOAuthEntry(entry);
                Factory.getSocialSite().flush();
                
            } catch (SocialSiteException ex) {
                log.error("ERROR saving OAuthEntry", ex);
            }
            return entry;

        } catch (Exception ex) {
            log.error("ERROR creating request token", ex);
        }
        return null;
    }

    // Turns the request token into an access token
    public OAuthEntry convertToAccessToken(OAuthEntry entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkState(entry.type == OAuthEntry.Type.REQUEST, "Token must be a request token");

        OAuthEntry accessEntry = new OAuthEntry(entry);

        accessEntry.token = UUID.randomUUID().toString();
        accessEntry.tokenSecret = UUID.randomUUID().toString();

        accessEntry.type = OAuthEntry.Type.ACCESS;
        accessEntry.issueTime = new Date();

        AppManager amgr = Factory.getSocialSite().getAppManager();
        try {
            amgr.removeOAuthEntry(entry);
            amgr.saveOAuthEntry(accessEntry);
            Factory.getSocialSite().flush();

        } catch (SocialSiteException ex) {
            log.error("ERROR saving OAuthEntry", ex);
        }
        return accessEntry;
    }

    // Authorize the request token for the given user id
    public void authorizeToken(OAuthEntry entry, String userId) {
        Preconditions.checkNotNull(entry);
        entry.authorized = true;
        entry.userId = Preconditions.checkNotNull(userId);
    }

    // Return the proper security token for a 2 legged oauth request that has 
    // been validated for the given consumerKey. App specific checks like
    // making sure the requested user has the app installed should take place
    // in this method
    public SecurityToken getSecurityTokenForConsumerRequest(String consumerKey, String userId) {
        try {
            boolean authorized = false;
            AppManager amgr = Factory.getSocialSite().getAppManager();
            AppRegistration appreg = amgr.getAppRegistrationByConsumerKey(consumerKey);
            String appId = amgr.getAppByURL(new URL(appreg.getAppUrl())).getId();

            // only return token if specified user has installed specified app
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            Profile user = pmgr.getProfileByUserId(userId);
            List<AppInstance> instances = amgr.getAppInstancesByCollection(user, "PROFILE");
            for (AppInstance inst : instances) {
                if (inst.getApp().getURL().toString().equals(appreg.getAppUrl())) {
                    authorized = true;
                    break;
                }
            }
            if (authorized) {
                return new OAuthSecurityToken(
                    userId, appreg.getAppUrl(), appId, this.domain, this.container);
            }

        } catch (Exception e) {
            log.debug("Error fetching consumer secret for consumerKey: " + consumerKey, e);
        }
        return null;
    }
}
