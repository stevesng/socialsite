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
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.PermissionManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.PermissionGrant;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.security.AppPermission;
import com.sun.socialsite.security.FeaturePermission;
import com.sun.socialsite.security.HttpPermission;
import com.sun.socialsite.util.TextUtil;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Gadget permissions form action for adding a new permission grant.
 */
public class GadgetPermissionCreate extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(GadgetPermissionCreate.class);

    // permission types
    private static final String APP_PERM_OPTION = "AppPermission";
    private static final String FEAT_PERM_OPTION = "FeaturePermission";
    private static final String HTTP_PERM_OPTION = "HttpPermission";

    // subject types
    private static final String APP_OPTION =
        TextUtil.getResourceString("GadgetPermCreate.appOption");
    private static final String DOMAIN_OPTION =
        TextUtil.getResourceString("GadgetPermCreate.domainOption");
    private static final String GROUP_OPTION =
        TextUtil.getResourceString("GadgetPermCreate.groupOption");
    private static final String PROFILE_OPTION =
        TextUtil.getResourceString("GadgetPermCreate.profileOption");
    private static final String APP_LIST_HEADER =
        TextUtil.getResourceString("GadgetPermCreate.appListHeader");

    private static final String WILDCARD = "*";

    private String appId;
    private String [] actions;
    private String domain;
    private String id; // group handle or profile id
    private String name;
    private String permissionType;
    private String subjectType;

    public GadgetPermissionCreate() {
        setPageTitle("GadgetPerms.pageTitle");
        this.desiredMenu = "admin";
    }

    @Override
    public String execute() {
        return INPUT;
    }

    @Override
    public void validate() {
        /*
         * Using the same action for display and save, so
         * check permissionType here to see if form was submitted
         * (todo: better way than this?). Permission type will
         * always be submitted from a valid form.
         */
        if (permissionType == null) {
            return;
        }
        if (APP_PERM_OPTION.equals(permissionType)) {
            if (id == null || id.trim().length() == 0) {
                addFieldError("id", TextUtil.getResourceString(
                    "GadgetPermCreateError.noIdOrHandle"));
            } else {
                try {
                    if (GROUP_OPTION.equals(subjectType)) {
                        GroupManager groupManager =
                            Factory.getSocialSite().getGroupManager();
                        Group group = groupManager.getGroupByHandle(id);
                        if (group == null) {
                            addFieldError("id", TextUtil.format(
                                "GadgetPermCreateError.unknownGroupId", id));
                        }
                    } else if (PROFILE_OPTION.equals(subjectType)) {
                        ProfileManager profileManager =
                            Factory.getSocialSite().getProfileManager();
                        Profile profile = profileManager.getProfileByUserId(id);
                        if (profile == null) {
                            addFieldError("id", TextUtil.format(
                                "GadgetPermCreateError.unknownProfileId", id));
                        }
                    } else {
                        // can't happen normally
                        addFieldError("subjectType", TextUtil.getResourceString(
                            "GadgetPermCreateError.unknownSubjectType"));
                    }
                } catch (SocialSiteException sse) {
                    addError(sse.getMessage());
                }
            }
        } else if (FEAT_PERM_OPTION.equals(permissionType)) {
            // subject will come from dropdown list, nothing to check
        } else if (HTTP_PERM_OPTION.equals(permissionType)) {
            if (APP_OPTION.equals(subjectType)) {
                if (APP_LIST_HEADER.equals(appId)) {
                    addFieldError("appId", TextUtil.getResourceString(
                        "GadgetPermCreateError.noAppChosen"));
                }
            } else if (DOMAIN_OPTION.equals(subjectType)) {
                if (domain == null || domain.trim().length() == 0) {
                addFieldError("domain", TextUtil.getResourceString(
                    "GadgetPermCreateError.noGadgetDomain"));
                }
            } else {
                // can't happen normally
                addFieldError("subjectType", TextUtil.getResourceString(
                    "GadgetPermCreateError.unknownSubjectType"));
            }
        } else {
            // should not happen
            addActionError(TextUtil.format(
                "GadgetPermCreateError.unknownPermissionType",
                permissionType));
        }

        // following not specific to permission type
        if (name == null || name.trim().length() == 0) {
            if (FEAT_PERM_OPTION.equals(permissionType)) {
                addFieldError("name", TextUtil.getResourceString(
                    "GadgetPermCreateError.noFeature"));
            } else {
                addFieldError("name", TextUtil.getResourceString(
                    "GadgetPermCreateError.noURLPattern"));
            }
        }
        if (!FEAT_PERM_OPTION.equals(permissionType)) {
            if (actions == null || actions.length == 0) {
                addFieldError("actions", TextUtil.getResourceString(
                    "GadgetPermCreateError.noActions"));
            }
        }
    }
    
    public String save() {
        log.debug("Handling permission grant request");
        try {
            PermissionManager pManager =
                Factory.getSocialSite().getPermissionManager();
            PermissionGrant newGrant = new PermissionGrant();

            if (APP_PERM_OPTION.equals(permissionType)) {
                newGrant.setType(AppPermission.class.getName());
                if (GROUP_OPTION.equals(subjectType)) {
                    if (WILDCARD.equals(id)) {
                        newGrant.setGroupId(WILDCARD);
                    } else {
                        GroupManager groupManager =
                            Factory.getSocialSite().getGroupManager();
                        Group group = groupManager.getGroupByHandle(id);
                        if (group == null) {
                            String msg = String.format(
                                "No group with handle: %s", id);
                            return handleError(msg);
                        }
                        newGrant.setGroupId(group.getId());
                    }
                } else if (PROFILE_OPTION.equals(subjectType)) {
                    if (WILDCARD.equals(id)) {
                        newGrant.setProfileId(WILDCARD);
                    } else {
                        ProfileManager profileManager =
                            Factory.getSocialSite().getProfileManager();
                        Profile profile = profileManager.getProfileByUserId(id);
                        if (profile == null) {
                            String msg = String.format(
                                "No profile with userid: %s", id);
                            return handleError(msg);
                        }
                        newGrant.setProfileId(profile.getId());
                    }
                } else {
                    String msg = String.format(
                        "Unknown subject type: %s", subjectType);
                    return handleError(msg);
                }
            } else if (FEAT_PERM_OPTION.equals(permissionType)) {
                newGrant.setType(FeaturePermission.class.getName());
                if (WILDCARD.equals(appId)) {
                    // cannot set app, so use "*" domain to mean all apps
                    newGrant.setGadgetDomain(WILDCARD);
                } else {
                    AppManager appManager =
                        Factory.getSocialSite().getAppManager();
                    App app = appManager.getApp(appId);
                    if (app == null) {
                        String msg = String.format("Unknown app id: %s", appId);
                        return handleError(msg);
                    }
                    newGrant.setApp(app);
                }
            } else if (HTTP_PERM_OPTION.equals(permissionType)) {
                newGrant.setType(HttpPermission.class.getName());
                if (APP_OPTION.equals(subjectType)) {
                    AppManager appManager =
                        Factory.getSocialSite().getAppManager();
                    App app = appManager.getApp(appId);
                    if (app == null) {
                        String msg = String.format("Unknown app id: %s", appId);
                        return handleError(msg);
                    }
                    newGrant.setApp(app);
                } else if (DOMAIN_OPTION.equals(subjectType)) {
                    newGrant.setGadgetDomain(domain);
                } else {
                    String msg = "Http request permission only allowed for domains/applications";
                    return handleError(msg);
                }
            }

            newGrant.setName(name);
            newGrant.setActions(getActionsAsString());

            pManager.savePermissionGrant(newGrant);
            Factory.getSocialSite().flush();
            setSuccess("GadgetPerms.saveSucceeded");
            return SUCCESS;
        } catch (SocialSiteException sse) {
            log.error("ERROR adding permission grants", sse);
            setError("GadgetPerms.addError", sse.getMessage());
            return ERROR;
        }
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String[] getActions() {
        return actions;
    }

    public void setActions(String[] actions) {
        this.actions = actions;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /* Grant subject types */
    public static String getAppOption() {
        return APP_OPTION;
    }

    public static String getDomainOption() {
        return DOMAIN_OPTION;
    }

    public static String getGroupOption() {
        return GROUP_OPTION;
    }

    public static String getProfileOption() {
        return PROFILE_OPTION;
    }
    /* End grant subject types */

    // Used to create a dropdown list of installed applications.
    public List getApps() {
        try {
            AppManager manager = Factory.getSocialSite().getAppManager();
            List<App> appList = manager.getApps(0, -1);
            Collections.sort(appList, new Comparator<App>() {
                public int compare(App app0, App app1) {
                    return app0.getTitle().compareTo(app1.getTitle());
                }
            });
            return appList;
        } catch (SocialSiteException sse) {
            log.error(sse.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

   // util method to avoid redundancy
    private String handleError(String msg) {
        log.error(msg);
        setError(msg);
        return ERROR;
    }

    // content validated in validate(), not here
    private String getActionsAsString() {
        if (actions == null || actions.length == 0) {
            return null;
        }
        if (actions.length == 1) {
            return actions[0];
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<actions.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(actions[i]);
        }
        return sb.toString();
    }

}
