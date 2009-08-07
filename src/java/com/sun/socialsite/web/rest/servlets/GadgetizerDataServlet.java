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

package com.sun.socialsite.web.rest.servlets;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.PermissionManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.URLStrategy;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.AppInstance;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.security.AppPermission;
import com.sun.socialsite.web.rest.opensocial.ConsumerContext;
import com.sun.socialsite.web.rest.opensocial.ConsumerContextHandler;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import com.sun.socialsite.web.rest.opensocial.SocialSiteTokenBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.security.Permission;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.servlet.InjectedServlet;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.ModulePrefs;
import org.apache.shindig.gadgets.spec.UserPref;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Provides a simple service which allows for Gadgetizer Widgets to retrieve
 * context data which they need in order to utilize a SocialSite server's Gadget
 * rendering facilities.
 *
 * Request parameters are expected in a simple GET.  Response parameters are
 * returned as part of a JSON object.
 */
public class GadgetizerDataServlet extends InjectedServlet {

    private static final long serialVersionUID = 0L;

    private static Log log = LogFactory.getLog(GadgetizerDataServlet.class);

    /**
     * The JavaScript URLs that will be returned to clients.
     */
    private static final String[] libs = {
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/resources/jmaki.js"),
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/resources/jmaki/lightboxManager/extension.js"),
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/gadgets/js/socialsite-0.1.js"),
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/gadgets/files/container/util.js"),
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/gadgets/files/container/gadgets.js"),
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/gadgets/files/container/socialsite_gadgets.js"),
    };

    /**
     * The CSS URLs that will be returned to clients.
     */
    private static final String[] css = {
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/gadgets/files/container/gadgets.css"),
        Factory.getSocialSite().getURLStrategy().getAbsoluteURL("/resources/jmaki/lightboxManager/lightbox.css"),
    };

    /** The builder we'll use for constructing token instances. */
    private SocialSiteTokenBuilder tokenBuilder = new SocialSiteTokenBuilder();

    /** Object which will be notified each time a <code>ConsumerContext</code> is created. */
    private ConsumerContextHandler contextHandler;


    /**
     * Public constructor.
     */
    public GadgetizerDataServlet() {
        super();
    }


    @Inject
    public void setConsumerContextHandler(ConsumerContextHandler contextHandler) {
        this.contextHandler = contextHandler;
        log.debug("contextHandler="+contextHandler);
    }


    /**
     * <p>
     *  Override GET to retrieve gadgetizer data based on the specified request.
     *  The data is returned in JSON format.
     * </p>
     * <p>
     *  Required Request Parameters:
     *  <ul>
     *   <li>context: the context (such as viewerId/ownerId) in which the client is operating</li>
     *   <li>items: the actual items (gadgets) which will the client expects to eventually render</li>
     *  </ul>
     * </p>
     *
     * @param request the servlet request object.
     * @param response the servlet response object.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        try {

            String callback = request.getParameter("callback");
            URL referer = ((request.getHeader("Referer") != null) ? new URL(request.getHeader("Referer")) : null);
            ConsumerContext context = new ConsumerContext(referer, new JSONObject(request.getParameter("context")));
            contextHandler.handleContext(context);

            SocialSiteToken containerPageToken = tokenBuilder.buildContainerPageToken(context);
            JSONObject items = new JSONObject(request.getParameter("items"));

            JSONObject json = new JSONObject();
            json.put("libs", new JSONArray(libs));
            json.put("css", new JSONArray(css));
            json.put("containerHelper", getContainerHelperJSON(containerPageToken, context, request));
            json.put("gadgets", getGadgetsJSON(containerPageToken, context, items, request));

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/x-javascript; charset=utf-8");
            PrintWriter out = response.getWriter();
            if (callback != null) {
              out.format("%s(%s)", callback, json.toString());
            } else {
              out.print(json.toString());
            }
            out.close();

        } catch (Exception e) {
            log.error("Failed to produce response", e);
            sendErrorJSON(response, e.getMessage());
        }

    }


    private JSONObject getContainerHelperJSON(SocialSiteToken containerPageToken, ConsumerContext context, HttpServletRequest request) throws SocialSiteException {

        try {

            JSONObject json = new JSONObject();
            AppManager appManager = Factory.getSocialSite().getAppManager();
            URLStrategy urlStrategy = Factory.getSocialSite().getURLStrategy();
            URL spec = new URL(urlStrategy.getAbsoluteURL("/local_gadgets/container_helper.xml"));
            App app = appManager.getAppByURL(spec);

            if (app != null && isExecutionAlloweded(containerPageToken, app)) {
                Object subject = null;
                AppInstance appInstance = null;
                if (containerPageToken.getOwnerId() != null) {
                    ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                    Profile ownerProfile = profileManager.getProfileByUserId(containerPageToken.getOwnerId());
                    subject = ownerProfile;
                    appInstance = appManager.getDefaultAppInstance(app, ownerProfile);
                } else if (containerPageToken.getGroupHandle() != null) {
                    GroupManager groupManager = Factory.getSocialSite().getGroupManager();
                    Group group = groupManager.getGroupByHandle(containerPageToken.getGroupHandle());
                    subject = group;
                    appInstance = appManager.getDefaultAppInstance(app, group);
                } else if (log.isWarnEnabled()) {
                    String msg = String.format("%s has neither an ownerId or groupHandle", containerPageToken);
                    log.warn(msg);
                }
                if (appInstance != null) {
                    JSONObject item = new JSONObject();
                    Long moduleId = appInstance.getId();
                    String appId = app.getId();
                    String containerId = context.getString("containerId");
                    SocialSiteToken token = tokenBuilder.buildAppToken(context, appId, moduleId);
                    json = getGadgetJSON(item, token, moduleId, containerId, appManager.getGadgetSpecByURL(spec), request);
                } else if (log.isWarnEnabled()) {
                    String msg = String.format("No AppInstance found for app='%s' and subject='%s'", app, subject);
                    log.warn(msg);
                }
            }

            return json;

        } catch (SocialSiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }

    }


    /**
     * Gets the JSON which should be client for the specified context and items.
     * <p>
     *  Each element in the "items" JSON must have one of:
     *  <ul>
     *   <li>collection: the identifier for a collection of app instances (such as "PROFILE" or "GROUP")</li>
     *   <li>spec: the URL of a Gadget specification; if this is a relative URL, it'll be interpreted against our base URL</li>
     *  </ul>
     * </p>
     *
     * @param context the context in which these gadgets (and their tokens) will operate.
     * @param items the actual items (gadgets) which the client expects to eventually render.
     * @return the JSON which should be sent to the client.
     */
    private JSONObject getGadgetsJSON(SocialSiteToken containerPageToken, ConsumerContext context, JSONObject items, HttpServletRequest request) throws SocialSiteException {

        try {

            JSONObject json = new JSONObject();
            Iterator<?> i = items.keys();
            while (i.hasNext()) {
                String key = (String)(i.next());
                JSONObject item = items.getJSONObject(key);
                if (item.has("collection")) {
                    JSONArray gadgets = new JSONArray();
                    AppManager appManager = Factory.getSocialSite().getAppManager();
                    List<AppInstance> appInstances = Collections.emptyList();
                    if (containerPageToken.getOwnerId() != null) {
                      ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                      Profile ownerProfile = profileManager.getProfileByUserId(containerPageToken.getOwnerId());
                      appInstances = appManager.getAppInstancesByCollection(ownerProfile, item.getString("collection"));
                    } else if (containerPageToken.getGroupHandle() != null) {
                      GroupManager groupManager = Factory.getSocialSite().getGroupManager();
                      Group group = groupManager.getGroupByHandle(containerPageToken.getGroupHandle());
                      appInstances = appManager.getAppInstancesByCollection(group, item.getString("collection"));
                    }
                    for (AppInstance appInstance : appInstances) {
                        GadgetSpec spec = appInstance.getGadgetSpec();
                        if (spec != null) {
                            Long moduleId = appInstance.getId();
                            String appId = appInstance.getApp().getId();
                            String containerId = context.getString("containerId");
                            SocialSiteToken token = tokenBuilder.buildAppToken(context, appId, moduleId);
                            if (isExecutionAlloweded(token, appInstance.getApp())) {
                                gadgets.put(getGadgetJSON(new JSONObject(),token, moduleId, containerId, appInstance.getGadgetSpec(), request));
                            }
                        } else {
                            String msg = String.format("Failed to get spec for AppInstance(%d)", appInstance.getId());
                            log.warn(msg);
                        }
                    }
                    json.put(key, gadgets);
                } else if (item.has("spec")) {
                    JSONArray gadgets = new JSONArray();
                    AppManager appManager = Factory.getSocialSite().getAppManager();
                    URLStrategy urlStrategy = Factory.getSocialSite().getURLStrategy();
                    URL spec = new URL(urlStrategy.getAbsoluteURL(item.getString("spec")));
                    App app = appManager.getAppByURL(spec);
                    if (app != null && isExecutionAlloweded(containerPageToken, app)) {
                        Object subject = null;
                        AppInstance appInstance = null;
                        if (containerPageToken.getOwnerId() != null) {
                            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
                            Profile ownerProfile = profileManager.getProfileByUserId(containerPageToken.getOwnerId());
                            subject = ownerProfile;
                            appInstance = appManager.getDefaultAppInstance(app, ownerProfile);
                        } else if (containerPageToken.getGroupHandle() != null) {
                            GroupManager groupManager = Factory.getSocialSite().getGroupManager();
                            Group group = groupManager.getGroupByHandle(containerPageToken.getGroupHandle());
                            subject = group;
                            appInstance = appManager.getDefaultAppInstance(app, group);
                        } else if (log.isWarnEnabled()) {
                            String msg = String.format("%s has neither an ownerId or groupHandle", containerPageToken);
                            log.warn(msg);
                        }
                        if (appInstance != null) {
                            Long moduleId = appInstance.getId();
                            String appId = app.getId();
                            String containerId = context.getString("containerId");
                            SocialSiteToken token = tokenBuilder.buildAppToken(context, appId, moduleId);
                            gadgets.put(getGadgetJSON(item, token, moduleId, containerId, appManager.getGadgetSpecByURL(spec), request));
                            json.put(key, gadgets);
                        } else if (log.isWarnEnabled()) {
                            String msg = String.format("No AppInstance found for app='%s' and subject='%s'", app, subject);
                            log.warn(msg);
                        }
                    }
                } else {
                    throw new SocialSiteException(String.format("%s has no 'collection' or 'spec' key", item));
                }
            }
            return json;

        } catch (SocialSiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }

    }


    /**
     * Gets the JSON which should be sent to the client for a specified gadget.
     *
     * @param item the client's JSON request for this gadget.
     * @param token the token which has been assigned for this response.
     * @param moduleId the moduleId which has been assigned for this response.
     * @param containerId the container name which was specified.
     * @param spec the gadget's specification.
     * @return the JSON which should be sent to the client.
     */
    private JSONObject getGadgetJSON(JSONObject item, SocialSiteToken token, Long moduleId, String containerId, GadgetSpec spec, HttpServletRequest request) throws JSONException {

        URLStrategy urlStrategy = Factory.getSocialSite().getURLStrategy();

        String title = item.optString("title", spec.getModulePrefs().getTitle());
        Integer height = getIntegerValue("height", item, spec.getModulePrefs());
        Integer width = getIntegerValue("width", item, spec.getModulePrefs());

        JSONObject json = new JSONObject();
        json.put("serverBase", urlStrategy.getGadgetServerURL(request, moduleId.toString()));
        json.put("resourceBase", urlStrategy.getBaseURL()+"/gadgets");
        json.put("moduleId", moduleId);
        json.put("token", token.toSerialForm());
        json.put("spec", spec.getUrl().toString());
        json.put("containerId", containerId);
        if (title != null) json.put("title", title);
        if (height != null) json.put("height", height);
        if (width != null) json.put("width", width);

        JSONObject userPrefs = new JSONObject();
        for (UserPref userPref : spec.getUserPrefs()) {
            // TODO: allow users to actually customize their values
            userPrefs.put(userPref.getName(), userPref.getDefaultValue());
        }
        json.put("userPrefs", userPrefs);

        if (log.isDebugEnabled()) {
            log.debug("json="+json.toString());
        }
        return json;

    }


    /**
     * Gets a value for the key (looking first in the specified JSON, then the specified ModulePrefs).
     *
     * @return the value associated with the key (looking first in the specified JSON and then in the
     *  specified ModulePrefs).
     */
    private Integer getIntegerValue(String key, JSONObject json, ModulePrefs modulePrefs) throws JSONException {
        if (json.has(key)) {
            return json.optInt(key);
        } else {
            String value = modulePrefs.getAttribute(key);
            return (value != null) ? Integer.parseInt(value) : null;
        }
    }


    // todo: propose caching mechanism and listener interface in perm manager
    private boolean isExecutionAlloweded(SocialSiteToken token, App app) throws SocialSiteException {

        try {
            PermissionManager permissionManager = Factory.getSocialSite().getPermissionManager();
            Permission p = new AppPermission(app.getURL().toString(), "execute");
            permissionManager.checkPermission(p, token);
            return true;
        } catch (SecurityException se) {
            // Only log the exception if debug logging is enabled
            if (log.isDebugEnabled()) {
                log.debug(se.getMessage(), se);
            }
            if (log.isWarnEnabled()) {
                String msg = String.format("token[%s] does not have acess to execute app[%s]", token.toSerialForm(), app.getURL());
                log.warn(msg);
            }
            return false;
        }

    }


    /**
     * Sends an error message to the specified HttpServletResponse object (in JSON format).
     *
     * @param response the HttpServletResponse to which the error message will be written.
     * @param message the contents of the error message that will be written.
     */
    private void sendErrorJSON(HttpServletResponse response, String message) throws IOException, ServletException {

        try {

            JSONObject json = new JSONObject();
            json.put("error", message);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.close();

        } catch (JSONException e) {
            log.error("Failed to build error message", e);
            throw new ServletException(e);
        }

    }

}
