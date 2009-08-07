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

package com.sun.socialsite.web.ui.admin.struts2;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.PermissionManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.PermissionGrant;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.security.AppPermission;
import com.sun.socialsite.security.FeaturePermission;
import com.sun.socialsite.security.HttpPermission;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ParameterAware;

/**
 * Gadget Permissions Form Action.
 */
public class GadgetPermissions extends CustomizedActionSupport
    implements ParameterAware {

    // permission class names
    static final String APP_PERM = AppPermission.class.getName();
    static final String FEATURE_PERM = FeaturePermission.class.getName();
    static final String HTTP_REQ_PERM = HttpPermission.class.getName();

    private static Log log = LogFactory.getLog(GadgetPermissions.class);

    private static final PermissionGrantComparator comparator =
        new PermissionGrantComparator();

    // App permissions can be granted by app, gadget domain, profile, or group
    private List<PermissionGrant> appPermsByUser = null;
    private List<PermissionGrant> appPermsByGroup = null;

    private List<PermissionGrant> featurePerms = null;
    private List<PermissionGrant> httpPermsByApp = null;
    private List<PermissionGrant> httpPermsByDomain = null;

    private boolean initialized = false;
    private Map parameters = Collections.EMPTY_MAP;

    public GadgetPermissions() {
        setPageTitle("GadgetPerms.pageTitle");
        this.desiredMenu = "admin";
    }

    @Override
    public String execute() {
        return INPUT;
    }

    public List<PermissionGrant> getAppPermsByGroup() {
        if (!initialized) {
            initLists();
        }
        return appPermsByGroup;
    }

    public void setAppPermsByGroup(List<PermissionGrant> appPermsByGroup) {
        this.appPermsByGroup = appPermsByGroup;
    }

    public List<PermissionGrant> getAppPermsByUser() {
        if (!initialized) {
            initLists();
        }
        return appPermsByUser;
    }

    public void setAppPermsByUser(List<PermissionGrant> appPermsByUser) {
        this.appPermsByUser = appPermsByUser;
    }

    public List<PermissionGrant> getFeaturePerms() {
        if (!initialized) {
            initLists();
        }
        return featurePerms;
    }

    public void setFeaturePerms(List<PermissionGrant> featurePerms) {
        this.featurePerms = featurePerms;
    }

    public List<PermissionGrant> getHttpPermsByApp() {
        if (!initialized) {
            initLists();
        }
        return httpPermsByApp;
    }

    public void setHttpPermsByApp(List<PermissionGrant> httpPermsByApp) {
        this.httpPermsByApp = httpPermsByApp;
    }

    public List<PermissionGrant> getHttpPermsByDomain() {
        if (!initialized) {
            initLists();
        }
        return httpPermsByDomain;
    }

    public void setHttpPermsByDomain(List<PermissionGrant> httpPermsByDomain) {
        this.httpPermsByDomain = httpPermsByDomain;
    }

    public Map getParameters() {
        return parameters;
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    public String remove() {
        log.debug("removing selected permission grants");
        PermissionManager pManager = Factory.getSocialSite().getPermissionManager();
        Iterator i = parameters.keySet().iterator();
        try {
            while (i.hasNext()) {
                String id = (String) i.next();
                PermissionGrant grant = pManager.getPermissionGrant(id);
                if (grant != null) {
                    pManager.removePermissionGrant(grant);
                }
            }
            Factory.getSocialSite().flush();
        } catch (SocialSiteException sse) {
            log.error("ERROR removing permission grants", sse);
            this.setError("GadgetPerms.remError", sse.getMessage());
            return ERROR;
        }
        setSuccess("GadgetPerms.saveSucceeded");
        return INPUT;
    }

    private void initLists() {
        try {
            PermissionManager pManager = Factory.getSocialSite().getPermissionManager();

            List<PermissionGrant> grants = pManager.getPermissionGrants(0, -1);
            appPermsByGroup = new ArrayList<PermissionGrant>();
            appPermsByUser = new ArrayList<PermissionGrant>();
            featurePerms = new ArrayList<PermissionGrant>();
            httpPermsByApp = new ArrayList<PermissionGrant>();
            httpPermsByDomain = new ArrayList<PermissionGrant>();

            String type = null;
            for (PermissionGrant grant : grants) {
                type = grant.getType();
                if (APP_PERM.equals(type)) {
                    if (grant.getProfileId() != null) {
                        appPermsByUser.add(grant);
                    } else if (grant.getGroupId() != null) {
                        appPermsByGroup.add(grant);
                    } else {
                        // this would be odd
                        log.warn("Ignoring execute permission grant with no " +
                            "app, domain, profile, or group. Id: " +
                            grant.getId());
                    }
                } else if (FEATURE_PERM.equals(type)) {
                    featurePerms.add(grant);
                } else if (HTTP_REQ_PERM.equals(type)) {
                    if (grant.getApp() != null) {
                        httpPermsByApp.add(grant);
                    } else if (grant.getGadgetDomain() != null) {
                        httpPermsByDomain.add(grant);
                    } else {
                        log.warn(String.format(
                            "Ignoring http permission grant %s with no " +
                            "application or domain", grant.getId()));
                    }
                } else {
                    // this would be odd
                    log.warn("Ignoring permission grant without type.");
                }
            }

            Collections.sort(appPermsByGroup, comparator);
            Collections.sort(appPermsByUser, comparator);
            Collections.sort(featurePerms, comparator);
            Collections.sort(httpPermsByApp, comparator);
            Collections.sort(httpPermsByDomain, comparator);

            initialized = true;
        } catch (SocialSiteException sse) {
            log.error("ERROR retrieving permission grants", sse);
            this.setError("GadgetPerms.retError", sse.getMessage());
        }
    }

    /**
     * Comparator for permission grants. TODO: deeper checks?
     * Note: only like-typed grants should be prepared, for example
     * comparing feature permission grants to feature permission grants.
     */
    static class PermissionGrantComparator implements Comparator<PermissionGrant> {

        private static final Log logger = LogFactory.getLog(PermissionGrantComparator.class);

        public int compare(PermissionGrant pg0, PermissionGrant pg1) {
            // execute permissions are divided into groups that either all have
            // an app, all have a domain, etc
            if (APP_PERM.equals(pg0.getType())) {
                if (pg0.getGroupId() != null) {
                    assert pg1.getGroupId() != null;
                    String identifier0 = getGroupIdentifier(pg0.getGroupId());
                    String identifier1 = getGroupIdentifier(pg1.getGroupId());
                    return identifier0.compareTo(identifier1);
                } else if (pg0.getProfileId() != null) {
                    assert pg1.getProfileId() != null;
                    String identifier0 = getProfileIdentifier(pg0.getProfileId());
                    String identifier1 = getProfileIdentifier(pg1.getProfileId());
                    return identifier0.compareTo(identifier1);
                } else {
                    log.warn(String.format(
                        "Grant %s has exec permission but no subject",
                        pg0.getId()));
                    return 0;
                }
            } else if (FEATURE_PERM.equals(pg0.getType())) {
                String title0 =
                    ((pg0.getApp() == null) ? "*" : pg0.getApp().getTitle());
                String title1 =
                    ((pg1.getApp() == null) ? "*" : pg1.getApp().getTitle());
                int retVal = title0.compareTo(title1);
                if (retVal == 0) {
                    return pg0.getName().compareTo(pg1.getName());
                } else {
                    return retVal;
                }
            } else if (HTTP_REQ_PERM.equals(pg0.getType())) {
                int retVal = pg0.getName().compareTo(pg1.getName());
                if (retVal == 0) {
                    return pg0.getGadgetDomain().compareTo(pg1.getGadgetDomain());
                } else {
                    return retVal;
                }
            } else {
                // this would be odd
                logger.warn("Attempt made to compare grant without type");
                return 0;
            }
        }

        private String getGroupIdentifier(String groupId) {
            try {
                if ("*".equals(groupId)) {
                    return groupId;
                } else {
                    GroupManager groupManager = Factory.getSocialSite().getGroupManager();
                    Group group = groupManager.getGroupById(groupId);
                    return ((group != null) ? group.getName() : "");
                }
            } catch (SocialSiteException e) {
                log.error("Unexpected Failure", e);
                throw new RuntimeException(e);
            }
        }

        private String getProfileIdentifier(String profileId) {
            try {
                if ("*".equals(profileId)) {
                    return profileId;
                } else {
                    ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                    Profile profile = profileManager.getProfile(profileId);
                    return ((profile != null) ? profile.getUserId() : "");
                }
            } catch (SocialSiteException e) {
                log.error("Unexpected Failure", e);
                throw new RuntimeException(e);
            }
        }

    }

}
