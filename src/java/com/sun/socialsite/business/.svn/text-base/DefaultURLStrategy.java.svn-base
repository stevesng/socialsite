/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.business;

import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Default SocialSite URL strategy.
 */
public class DefaultURLStrategy implements URLStrategy {

    private static Log log = LogFactory.getLog(DefaultURLStrategy.class);

    private String baseURL;
    private String profileURL;
    private String groupURL;
    private String dashboardURL;

    public DefaultURLStrategy() {
        
        StringBuilder sb = new StringBuilder(Config.getProperty("socialsite.base.url"));
        while (sb.toString().endsWith("/")) {
            sb.deleteCharAt(baseURL.length()-1);
        }
        baseURL = sb.toString();
        log.info("Using base URL " + baseURL);
        
        StringBuilder sb1 = new StringBuilder(Config.getProperty("socialsite.profile.url"));
        while (sb1.toString().endsWith("/")) {
            sb1.deleteCharAt(baseURL.length()-1);
        }
        profileURL = sb1.toString();
        log.info("Using profile URL " + profileURL);
        
        StringBuilder sb2 = new StringBuilder(Config.getProperty("socialsite.group.url"));
        while (sb2.toString().endsWith("/")) {
            sb2.deleteCharAt(baseURL.length()-1);
        }
        groupURL = sb2.toString();
        log.info("Using group URL " + groupURL);
        
        StringBuilder sb3 = new StringBuilder(Config.getProperty("socialsite.dashboard.url"));
        while (sb3.toString().endsWith("/")) {
            sb3.deleteCharAt(baseURL.length()-1);
        }
        dashboardURL = sb3.toString();
        log.info("Using group URL " + dashboardURL);
    }

    /**
     * Get base URL of application.  The returned URL will not end with
     * a trailing slash.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Get URL for dashboard
     */
    public String getDashBoardURL() {
        return dashboardURL;
    }
    
    /**
     * Get URL for viewing a user's profile.
     */
    public String getViewURL(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        return profileURL.replace("${userid}", profile.getUserId());
    }

    /**
     * Get URL for editing a user's profile.
     */
    public String getEditURL(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        return getActionURL("app-ui/core", getEditActionName(profile));
    }

    public String getEditActionName(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        return "edit/profile";
    }

    /**
     * Get URL for requesting a friendship with this user.
     */
    public String getFriendRequestURL(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        return getActionURL("app-ui/core", getFriendRequestActionName(profile));
    }

    public String getFriendRequestActionName(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        StringBuilder actionName = new StringBuilder();
        actionName.append("request/friendship/");
        actionName.append(encode(profile.getUserId()));
        return actionName.toString();
    }

    /**
     * Get URL for a user's full image.
     */
    public String getImageURL(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        StringBuilder url = new StringBuilder();
        url.append(getBaseURL());
        url.append("/images/person/");
        url.append(encode(profile.getUserId()));
        return url.toString();
    }

    /**
     * Get URL for a user's thumbnail image.
     */
    public String getThumbnailURL(Profile profile) {

        if (profile == null) {
            log.warn("profile is null");
            return null;
        }

        StringBuilder url = new StringBuilder();
        url.append(getBaseURL());
        url.append("/thumbnails/person/");
        url.append(encode(profile.getUserId()));
        return url.toString();
    }

    /**
     * Get URL for viewing a group.
     */
    public String getViewURL(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        return groupURL.replace("${groupid}", group.getHandle());
    }

    public String getViewActionName(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        StringBuilder actionName = new StringBuilder();
        actionName.append("group/");
        actionName.append(encode(group.getHandle()));
        return actionName.toString();
    }

    /**
     * Get URL for editing a group.
     */
    public String getEditURL(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        return getActionURL("app-ui/core", getEditActionName(group));
    }

    public String getEditActionName(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        StringBuilder actionName = new StringBuilder();
        actionName.append("edit/group/");
        actionName.append(encode(group.getHandle()));
        return actionName.toString();
    }

    /**
     * Get URL for admin to edit a group.
     */
    public String getAdminEditURL(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        return getActionURL("app-ui/admin", getAdminEditActionName(group));
    }

    public String getAdminEditActionName(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        StringBuilder actionName = new StringBuilder();
        actionName.append("adminedit/group/");
        actionName.append(encode(group.getHandle()));
        return actionName.toString();
    }

   /**
     * Get URL for editing a group's members. For use only by admin.
     */
    public String getMemberEditURL(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        return getActionURL("app-ui/admin", getMemberEditActionName(group));
    }

    public String getMemberEditActionName(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        StringBuilder actionName = new StringBuilder();
        actionName.append("adminbrowse/groups/members/");
        actionName.append(encode(group.getHandle()));
        return actionName.toString();
    }

    /**
     * Get URL for a group's full image.
     */
    public String getImageURL(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        StringBuilder url = new StringBuilder();
        url.append(getBaseURL());
        url.append("/images/group/");
        url.append(encode(group.getHandle()));
        return url.toString();
    }

    /**
     * Get URL for a group's thumbnail image.
     */
    public String getThumbnailURL(Group group) {

        if (group == null) {
            log.warn("group is null");
            return null;
        }

        StringBuilder url = new StringBuilder();
        url.append(getBaseURL());
        url.append("/thumbnails/group/");
        url.append(encode(group.getHandle()));
        return url.toString();
    }

    /**
     * Get url for a given actionName.
     */
    public String getActionURL(String namespace, String actionName) {
        StringBuilder url = new StringBuilder();
        url.append(getBaseURL());
        url.append("/").append(namespace);
        url.append("/").append(actionName);
        return url.toString();
    }

    /**
     * Get the appropriate Gadget Server URL for the specified request.
     * The returned URL will not end with a trailing slash.
     */
    public String getGadgetServerURL(HttpServletRequest request) {
        return getGadgetServerURL(request, "*");
    }  
 
    /**
     * Get the appropriate Gadget Server URL for the specified request.
     * The returned URL will not end with a trailing slash.  If the
     * "socialsite.gadgets.server.url" property is populated and contains
     * any wildcards ("*"), they will be replaced with the specified
     * replacementValue.
     */
    public String getGadgetServerURL(HttpServletRequest request, String replacementValue) {

        StringBuilder sb = new StringBuilder();

        try {

            String propVal = Config.getProperty("socialsite.gadgets.server.url");
            if (propVal != null) {
                String actualValue = propVal.replace("*", replacementValue);
                sb.append(actualValue);
            } else {
                if (Config.getBooleanProperty("socialsite.gadgets.use-cookie-jail")) {
                    // For now, we'll use an IP-based URL to provide a cookie jail
                    InetAddress addr = InetAddress.getByName(request.getServerName());
                    if (addr instanceof Inet6Address) {
                        sb.append(request.getScheme()).append("://[").append(addr.getHostAddress()).append("]");
                    } else {
                        sb.append(request.getScheme()).append("://").append(addr.getHostAddress());
                    } 
                } else {
                    sb.append(request.getScheme()).append("://").append(request.getServerName());
                }
                switch (request.getServerPort()) {
                    case 80:
                        if (!(request.getScheme().equalsIgnoreCase("http"))) {
                            sb.append(":").append(request.getServerPort());
                        }
                        break;
                    case 443:
                        if (!(request.getScheme().equalsIgnoreCase("https"))) {
                            sb.append(":").append(request.getServerPort());
                        }
                        break;
                    default:
                        sb.append(":").append(request.getServerPort());
                }
                sb.append(request.getContextPath());
                sb.append("/gadgets");
            }

        } catch (Exception e) {
            log.warn(e);
        }

        // We don't want our result to end with a slash
        while (sb.charAt(sb.length()-1) == '/') {
            sb.deleteCharAt(sb.length()-1);
        }

        return sb.toString();

    }

    /**
     * Return a String representing an asbolute URL which is equivalent
     * to the specified input URL Fragment.  If the input URL Fragment is
     * already an absolute URL, it will be returned unchanged.  If it is
     * a relative URL, it will be interpreted relative to our base URL.
     */
    public String getAbsoluteURL(String urlFragment) {

        // Case 1: have a fragment with a leading slash
        if (urlFragment.startsWith("/")) {
            return getBaseURL() + urlFragment;
        }

        // Case 2: already have an absolute URL
        try {
            return new URL(urlFragment).toExternalForm();
        } catch (MalformedURLException e) {
            // Do nothing, so we fall-through to next case
        }

        // Case 3: have a fragment without a leading slash
        return getBaseURL() + "/" + urlFragment;
    }

    /**
     * Compose a map of key=value params into a query string.
     */
    public String getQueryString(Map params) {

        if (params == null) {
            return null;
        }

        StringBuilder queryString = new StringBuilder();

        for (Iterator keys = params.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            String value = (String) params.get(key);

            if (queryString.length() == 0) {
                queryString.append("?");
            } else {
                queryString.append("&");
            }

            queryString.append(key);
            queryString.append("=");
            queryString.append(value);
        }

        return queryString.toString();
    }

    /**
     * URL encode a string using UTF-8.
     */
    public String encode(String str) {
        String encodedStr = str;
        try {
            encodedStr = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex);
        }
        return encodedStr;
    }

    /**
     * URL decode a string using UTF-8.
     */
    public String decode(String str) {
        String decodedStr = str;
        try {
            decodedStr = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.warn(ex);
        }
        return decodedStr;
    }
}
