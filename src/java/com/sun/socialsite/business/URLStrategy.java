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

import com.google.inject.ImplementedBy;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * An interface representing the SocialSite URL strategy.
 *
 * Implementations of this interface provide methods which can be used to form
 * all of the public urls used by SocialSite.
 */
@ImplementedBy(DefaultURLStrategy.class)
public interface URLStrategy {

    /**
     * Get base URL of application.  The returned URL will not end with
     * a trailing slash.
     */
    public String getBaseURL();
    
    /*
     * Get URL where dashboard is accessible
     */
    public String getDashBoardURL();

    /**
     * Get URL for viewing a user's profile.
     */
    public String getViewURL(Profile profile);

    /**
     * Get URL for editing a user's profile.
     */
    public String getEditURL(Profile profile);

    /**
     * Get action name for editing a user's profile.
     */
    public String getEditActionName(Profile profile);

    /**
     * Get URL for requesting a friendship with this user.
     */
    public String getFriendRequestURL(Profile profile);

    /**
     * Get action name for requesting a friendship with this user.
     */
    public String getFriendRequestActionName(Profile profile);

    /**
     * Get URL for a user's full image.
     */
    public String getImageURL(Profile profile);

    /**
     * Get URL for a user's thumbnail image.
     */
    public String getThumbnailURL(Profile profile);

    public String getViewURL(Group group);

    /**
     * Get action name for viewing a group.
     */
    public String getViewActionName(Group group);

    /**
     * Get URL for editing a group.
     */
    public String getEditURL(Group group);

    /**
     * Get action name for editing a group.
     */
    public String getEditActionName(Group group);

    /**
     * Get URL for editing a group. Used by admin.
     */
    public String getAdminEditURL(Group group);

    /**
     * Get action name for editing a group. Used by admin.
     */
    public String getAdminEditActionName(Group group);

   /**
     * Get URL for editing members of a group. Used by admin.
     */
    public String getMemberEditURL(Group group);

    /**
     * Get action name for editing members of a group. Used by admin.
     */
    public String getMemberEditActionName(Group group);

    /**
     * Get URL for a group's full image.
     */
    public String getImageURL(Group group);

    /**
     * Get URL for a group's thumbnail image.
     */
    public String getThumbnailURL(Group group);

    /**
     * Get URL for an action.
     */
    public String getActionURL(String namespace, String actionName);

    /**
     * Get the appropriate Gadget Server URL for the specified request.
     * The returned URL will not end with a trailing slash.
     */
    public String getGadgetServerURL(HttpServletRequest request);
 
    /**
     * Get the appropriate Gadget Server URL for the specified request.
     * The returned URL will not end with a trailing slash.  If the
     * "socialsite.gadgets.server.url" property is populated and contains
     * any wildcards ("*"), they will be replaced with the specified 
     * replacementValue.
     */
    public String getGadgetServerURL(HttpServletRequest request, String replacementValue);

    /**
     * Return a String representing an asbolute URL which is equivalent
     * to the specified input URL Fragment.  If the input URL Fragment is
     * already an absolute URL, it will be returned unchanged.  If it is
     * a relative URL, it will be interpreted relative to our base URL.
     */
    public String getAbsoluteURL(String urlFragment);

    /**
     * Compose a map of key=value params into a query string.
     */
    public String getQueryString(Map params);

    /**
     * URL encode a string.
     */
    public String encode(String str);

    /**
     * URL decode a string.
     */
    public String decode(String str);
}
