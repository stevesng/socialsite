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

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AbstractManagerImpl;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.InitializationException;
import com.sun.socialsite.business.ListenerManager;
import com.sun.socialsite.business.PermissionManager;
import com.sun.socialsite.business.URLStrategy;
import com.sun.socialsite.business.startup.Startup;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.AppData;
import com.sun.socialsite.pojos.AppInstance;
import com.sun.socialsite.pojos.AppRegistration;
import com.sun.socialsite.pojos.PermissionGrant;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.OAuthEntryRecord;
import com.sun.socialsite.security.AppPermission;
import com.sun.socialsite.security.FeaturePermission;
import com.sun.socialsite.security.HttpPermission;
import com.sun.socialsite.util.MailUtil;
import com.sun.socialsite.util.TextUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.persistence.NonUniqueResultException;
import javax.persistence.NoResultException;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.http.BasicHttpFetcher;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;





/**
 * JPA implementation of AppManager.
 */
@Singleton
public class JPAAppManagerImpl extends AbstractManagerImpl implements AppManager {

    private static Log log = LogFactory.getLog(JPAAppManagerImpl.class);

    private final JPAPersistenceStrategy strategy;

    private final HttpFetcher gadgetSpecFetcher;


    @Inject
    protected JPAAppManagerImpl(JPAPersistenceStrategy strategy, BasicHttpFetcher fetcher) {
        log.debug("Instantiating JPA AppManager");
        this.gadgetSpecFetcher = fetcher;
        this.strategy = strategy;
    }


    public void initialize() throws InitializationException {

        String preLoadDirectoryPath = Config.getProperty("socialsite.gadgets.preload.path");
        log.debug("socialsite.gadgets.preload.path: " + preLoadDirectoryPath);

        String baseUrl = Config.getProperty("socialsite.gadgets.preload.base.url");
        log.debug("socialsite.gadgets.preload.base.url: " + baseUrl);

        ListenerManager listenerManager = Factory.getSocialSite().getListenerManager();
        listenerManager.addListener(AppRegistration.class, new AppRegistrationListener());

        PermissionManager permissionManager = Factory.getSocialSite().getPermissionManager();

        // For now, auto-grant so that any person can execute any app
        try {
            PermissionGrant pg = new PermissionGrant();
            pg.setType(AppPermission.class.getName());
            pg.setName("*");
            pg.setActions("read,execute");
            pg.setProfileId("*");
            permissionManager.savePermissionGrant(pg);
            Factory.getSocialSite().flush();
        } catch (SocialSiteException e) {
            String msg = "Failed to grant AppPermission(*,'read,execute') to all users";
            log.error(msg, e);
        }

        // For now, auto-grant so that any app can retrieve any URL via makeRequest
        try {
            PermissionGrant pg = new PermissionGrant();
            pg.setType(HttpPermission.class.getName());
            pg.setName("*");
            pg.setActions("DELETE,GET,HEAD,POST,PUT");
            pg.setGadgetDomain("*");
            permissionManager.savePermissionGrant(pg);
            Factory.getSocialSite().flush();
        } catch (SocialSiteException e) {
            String msg = "Failed to grant HttpPermission(*,*) to all apps";
            log.error(msg, e);
        }

        if (preLoadDirectoryPath != null) {

            File preLoadDirectory = new File(preLoadDirectoryPath);

            if (!preLoadDirectory.exists()) {
                String msg = String.format("Gadgets dir (%s) not found", preLoadDirectoryPath);
                throw new InitializationException(msg);
            }

            if (!preLoadDirectory.exists()) {
                String msg = String.format("Gadgets dir (%s) is not a directory", preLoadDirectoryPath);
                throw new InitializationException(msg);
            }

            File[] files = preLoadDirectory.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().endsWith(".xml");
                }
            });

            log.debug("Loading registered apps, count = " + files.length);
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    try {
                        URL url = new URL(baseUrl + "/" + files[i].getName());
                        App app = getAppByURL(url, true);
                        if (app == null) {
                            log.info("Loading Gadget " + files[i].getAbsolutePath() + " to URL " + url);
                            app = App.readFromStream(new FileInputStream(files[i]), url);
                            strategy.store(app);
                            strategy.flush();
                            strategy.release();
                        }
                        if (permissionManager.getPermissionGrants(app, 0, -1).size() == 0) {
                            grantPermission(permissionManager, FeaturePermission.class, "*", app);
                            Factory.getSocialSite().flush();
                        }

                    } catch (Exception e) {
                        String msg = String.format("Failed to read gadget spec: %s", files[i].getAbsolutePath());
                        log.error(msg, e);
                    }
                }
            }

            try {
                List<AppRegistration> appregs = getAppRegistrations(null, "APPROVED");
                log.debug("Loading registered apps, count = " + appregs.size());
                for (AppRegistration appreg : appregs) {
                    try {
                        log.info("Checking Gadget Spec URL " + appreg.getAppUrl());
                        URL url = new URL(appreg.getAppUrl());
                        App app = getAppByURL(url, true);
                        if (app == null) {
                            log.info("   Adding " + appreg.getAppUrl());
                            app = App.readFromStream(new BufferedInputStream(url.openConnection().getInputStream()), url);
                            app.setShowInDirectory(Boolean.TRUE);
                            strategy.store(app);
                            strategy.flush();
                            strategy.release();
                        }
                        if (permissionManager.getPermissionGrants(app, 0, -1).size() == 0) {
                            grantPermission(permissionManager, FeaturePermission.class, "*", app);
                            Factory.getSocialSite().flush();
                        }

                    } catch (Exception ex) {
                        log.error("ERROR reading a registered gadget at URL: " + appreg.getAppUrl(), ex);
                    } 
                }
            } catch (SocialSiteException ex) {
                String msg = String.format("Failed to read app registration data");
                log.error(msg, ex);
            }
        }

    }

    public void release() {
    }


    public App getApp(String id) throws SocialSiteException {
        return (App)strategy.load(App.class, id);
    }


    public void saveApp(App app) throws SocialSiteException {
        strategy.store(app);
    }


    public void removeApp(App app) throws SocialSiteException {
        strategy.remove(app);
    }


    public GadgetSpec getGadgetSpecByURL(URL url) throws SocialSiteException {

        if (url == null) {
            throw new SocialSiteException("url is null");
        }

        try {

            URI javaUri = url.toURI();
            Uri shindigUri = Uri.fromJavaUri(javaUri);
            String specContents = null;

            if (javaUri.getScheme().equals("file")) {

                // TODO: Have a property to control whether these are allowed?
                File file = new File(javaUri);
                StringBuilder sb = new StringBuilder((int) (file.length()));
                FileReader reader = new FileReader(file);
                char[] buf = new char[8192];
                int len;
                while ((len = reader.read(buf)) != -1) {
                    sb.append(buf, 0, len);
                }
                specContents = sb.toString();

            } else {

                HttpRequest request = new HttpRequest(shindigUri);
                request.setIgnoreCache(false);
                HttpResponse response = gadgetSpecFetcher.fetch(request);
                specContents = response.getResponseAsString();

                if ((response.getHttpStatusCode() != HttpResponse.SC_OK) && (log.isWarnEnabled())) {
                    String msg = String.format("GadgetSpec HTTP Response:%n%s", response.toString());
                    log.warn(msg);
                } //else if (log.isDebugEnabled()) {
                    //String msg = String.format("GadgetSpec HTTP Response:%n%s", response.toString());
                    //log.debug(msg);
                //}

            }

            return new GadgetSpec(shindigUri, specContents);

        } catch (Exception e) {
            String msg = String.format("Failed to Retrieve GadgetSpec[%s]", url);
            throw new SocialSiteException(msg, e);
        }

    }


    public App getAppByURL(URL url) throws SocialSiteException {
        return getAppByURL(url, true);
    }


    public App getAppByURL(URL url, boolean mustAlreadyExist) throws SocialSiteException {

        App app = null;

        if (url == null) {
            throw new SocialSiteException("url is null");
        }

        // First, try to get known App from DB
        Query query = strategy.getNamedQuery("App.getByURL");
        query.setParameter(1, url.toExternalForm());
        try {
            app = (App)(query.getSingleResult());
        } catch (NonUniqueResultException e) {
            String msg = String.format("More than one App with url='%s'", url);
            throw new SocialSiteException(msg, e);
        } catch (NoResultException e) {
        }

        // If that didn't work, see if we should read gadget spec from the network
        if ((app == null) && (mustAlreadyExist == false)) {
            try {
                app = App.readFromStream(url.openStream(), url);
                strategy.store(app);
                strategy.flush();
                strategy.release();
            } catch (Exception e) {
                String msg = String.format("Failed to read gadget spec from url='%s'", url);
                log.error(msg, e);
            }
        }

        return app;
    }


    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<App> getApps(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("App.getAll");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<App>) query.getResultList();
    }


    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<App> getAppsInDirectory(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("App.getAllInDirectory");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<App>) query.getResultList();
    }


    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<App> getOldestApps(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("App.getOldest");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        return (List<App>) query.getResultList();
    }


    //------------------------------------------------------- AppData

    public void setAppData(App app, Profile profile, String propName, String propValue) throws SocialSiteException {

        if (app == null)  throw new SocialSiteException("app is null");
        if (profile == null)  throw new SocialSiteException("profile is null");
        if (propName == null)  throw new SocialSiteException("propName is null");
        if (propValue == null) throw new SocialSiteException("propValue is null");

        AppData appData = null;

        // does property already exist?
        Query query = strategy.getNamedQuery("AppData.getByProfileAndName");
        query.setParameter(1, app);
        query.setParameter(2, profile);
        query.setParameter(3, propName);
        try {
            appData = (AppData)query.getSingleResult();
        } catch (NonUniqueResultException e) {
            String msg = String.format("More than one appData property with appId='%s' and name='%s'", app.getId(), propName);
            throw new SocialSiteException(msg, e);
        } catch (NoResultException e) {
            // no problem, we'll create a new one
        }

        if (appData == null) {
            appData = new AppData();
            appData.setApp(app);
            appData.setProfile(profile);
            appData.setName(propName);
        }

        appData.setValue(propValue);
        saveAppData(appData);
    }


    public AppData getAppData(App app, Profile profile, String propName) throws SocialSiteException {

        if (app == null)  throw new SocialSiteException("app is null");
        if (profile == null)  throw new SocialSiteException("profile is null");
        if (propName == null)  throw new SocialSiteException("propName is null");

        AppData appData = null;

        Query query = strategy.getNamedQuery("AppData.getByProfileAndName");
        query.setParameter(1, app);
        query.setParameter(2, profile);
        query.setParameter(3, propName);
        try {
            appData = (AppData)query.getSingleResult();
        } catch (NonUniqueResultException e) {
            String msg = String.format("More than one appData property with appId='%s' and name='%s'", app.getId(), propName);
            throw new SocialSiteException(msg, e);
        } catch (NoResultException e) {
            if (log.isDebugEnabled()) {
                String msg = String.format("%s returned no results", query);
                log.debug(msg, e);
            }
        }

        return appData;
    }


    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<AppData> getAppData(App app, Profile profile) throws SocialSiteException {

        if (app == null) throw new SocialSiteException("app is null");
        if (profile == null) throw new SocialSiteException("profile is null");

        Query query = strategy.getNamedQuery("AppData.getByProfile");
        query.setParameter(1, app);
        query.setParameter(2, profile);
        List<AppData> results = (List<AppData>)query.getResultList();
        return results;
    }


    public void saveAppData(AppData appData) throws SocialSiteException {
        strategy.store(appData);
    }


    public void removeAppData(AppData appData) throws SocialSiteException {
        strategy.remove(appData);
    }


    //------------------------------------------------------- AppInstance

    public void saveAppInstance(AppInstance appInstance) throws SocialSiteException {
        strategy.store(appInstance);
    }


    public void removeAppInstance(AppInstance appInstance) throws SocialSiteException {
        strategy.remove(appInstance);
    }


    public AppInstance getAppInstance(Long id) throws SocialSiteException {
        if (id == null) {
            throw new SocialSiteException("id is null");
        }
        Query query = strategy.getNamedQuery("AppInstance.getById");
        query.setParameter(1, id);
        try {
            return (AppInstance)query.getSingleResult();
        } catch (NonUniqueResultException e) {
            String msg = String.format("More than one appInstance with id='%s'", id);
            throw new SocialSiteException(msg, e);
        } catch (NoResultException e) {
            return null;
        }
    }


    public AppInstance getDefaultAppInstance(App app, Group group) throws SocialSiteException {

        AppInstance appInstance = null;

        if (app == null) {
            throw new SocialSiteException("app is null");
        }

        if (group == null) {
            throw new SocialSiteException("group is null");
        }

        // First, try to get from DB
        Query query = strategy.getNamedQuery("AppInstance.getByAppAndGroup");
        query.setParameter(1, app);
        query.setParameter(2, group);
        try {
            appInstance = (AppInstance)query.getSingleResult();
        } catch (NonUniqueResultException e) {
            String msg = String.format("More than one appInstance with app='%s' and group=%s", app, group);
            throw new SocialSiteException(msg, e);
        } catch (NoResultException e) {
        }

        // If that didn't work, create a new one
        if (appInstance == null) {
            try {
                // TODO: fix logging level
                if (log.isWarnEnabled()) {
                    String msg = String.format("Creating default AppInstance for app='%s' and group='%s'", app, group);
                    log.warn(msg);
                }
                appInstance = new AppInstance();
                appInstance.setApp(app);
                appInstance.setGroup(group);
                strategy.store(appInstance);
                strategy.flush();
                strategy.release();
            } catch (Exception e) {
                log.error("Unexpected Failure", e);
            }
        }

        return appInstance;

    }


    public AppInstance getDefaultAppInstance(App app, Profile profile) throws SocialSiteException {

        AppInstance appInstance = null;

        if (app == null) {
            throw new SocialSiteException("app is null");
        }

        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }

        // First, try to get from DB
        Query query = strategy.getNamedQuery("AppInstance.getByAppAndProfile");
        query.setParameter(1, app);
        query.setParameter(2, profile);
        try {
            appInstance = (AppInstance)query.getSingleResult();
        } catch (NonUniqueResultException e) {
            String msg = String.format("More than one appInstance with app='%s' and profile=%s", app, profile);
            throw new SocialSiteException(msg, e);
        } catch (NoResultException e) {
        }

        // If that didn't work, create a new one
        if (appInstance == null) {
            try {
                // TODO: fix logging level
                if (log.isWarnEnabled()) {
                    String msg = String.format("Creating default AppInstance for app='%s' and profile='%s'", app, profile);
                    log.warn(msg);
                }
                appInstance = new AppInstance();
                appInstance.setApp(app);
                appInstance.setProfile(profile);
                strategy.store(appInstance);
                strategy.flush();
                strategy.release();
            } catch (Exception e) {
                log.error("Unexpected Failure", e);
            }
        }

        return appInstance;

    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<AppInstance> getAppInstancesByCollection(Group group, String collection) throws SocialSiteException {
        Query query = strategy.getNamedQuery("AppInstance.getByGroupAndCollection");
        query.setParameter(1, group);
        query.setParameter(2, collection);
        return (List<AppInstance>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<AppInstance> getAppInstancesByCollection(Profile profile, String collection) throws SocialSiteException {
        Query query = strategy.getNamedQuery("AppInstance.getByProfileAndCollection");
        query.setParameter(1, profile);
        query.setParameter(2, collection);
        return (List<AppInstance>) query.getResultList();
    }

    /**
     * Convenience method to grant a permission to an app.
     *
     * @param pm the PermissionManager which will save the new PermissionGrant.
     * @param pc the class of the Permission which will be granted.
     * @param name the name of the Permission which will be granted.
     * @param app the App to which the Permission will be granted.
     */
    private void grantPermission(PermissionManager pm, Class pc, String name, App app) throws SocialSiteException {
        PermissionGrant permissionGrant = new PermissionGrant();
        permissionGrant.setType(pc.getName());
        permissionGrant.setName(name);
        permissionGrant.setApp(app);
        pm.savePermissionGrant(permissionGrant);
    }

    public AppRegistration getAppRegistration(String id)
            throws SocialSiteException {
        return (AppRegistration)strategy.load(AppRegistration.class, id);
    }

    public AppRegistration getAppRegistrationByURL(String url)
            throws SocialSiteException {
        try {
            Query query = strategy.getNamedQuery("AppRegistration.getByURL");
            query.setParameter(1, url);
            return (AppRegistration)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings(value="unchecked")
    public List<AppRegistration> getAppRegistrations(
            String profileid, String status) throws SocialSiteException {
        if (profileid != null && status != null) {
            Profile profile = Factory.getSocialSite().getProfileManager().getProfile(profileid);
            Query query = strategy.getNamedQuery("AppRegistration.getByProfileAndStatus");
            query.setParameter(1, profile);
            query.setParameter(2, status);
            return (List<AppRegistration>)query.getResultList();

        } else if (profileid != null) {
            Profile profile = Factory.getSocialSite().getProfileManager().getProfile(profileid);
            Query query = strategy.getNamedQuery("AppRegistration.getByProfile");
            query.setParameter(1, profile);
            return (List<AppRegistration>)query.getResultList();

        } else if (status != null) {
            Query query = strategy.getNamedQuery("AppRegistration.getByStatus");
            query.setParameter(1, status);
            return (List<AppRegistration>)query.getResultList();
        }
        Query query = strategy.getNamedQuery("AppRegistration.getAll");
        return (List<AppRegistration>)query.getResultList();
    }

    public void registerApp(String profileid, String appurl, String serviceName) throws SocialSiteException {
        if (profileid == null || appurl == null) {
            throw new SocialSiteException("ERROR: must specify both profileid and appurl");
        }
        Profile profile = Factory.getSocialSite().getProfileManager().getProfile(profileid);
        AppRegistration reg = new AppRegistration();
        reg.setAppUrl(appurl);
        reg.setProfile(profile);
        reg.setStatus("PENDING");
        reg.setServiceName(serviceName);
        strategy.store(reg);
    }

    public void approveAppRegistration(String id, String comment) throws SocialSiteException {
        try {
            AppRegistration reg = getAppRegistration(id);
            reg.setComment(comment);
            reg.setStatus("APPROVED");
            reg.setConsumerKey(UUID.randomUUID().toString());
            reg.setConsumerSecret(UUID.randomUUID().toString());
            strategy.store(reg);
            URL url = new URL(reg.getAppUrl());
            App app = getAppByURL(url, true);
            if (app == null) {
                log.info("   Adding " + reg.getAppUrl());
                app = App.readFromStream(new BufferedInputStream(url.openConnection().getInputStream()), url);
                app.setShowInDirectory(Boolean.TRUE);
                strategy.store(app);
            }
        } catch (Exception ex) {
            throw new SocialSiteException("ERROR adding newly registered app", ex);
        }
    }

    public void rejectAppRegistration(String id, String comment) throws SocialSiteException {
        AppRegistration reg = getAppRegistration(id);
        reg.setComment(comment);
        reg.setStatus("REJECTED");
        strategy.store(reg);
    }

    public void removeAppRegistration(String id, String comment) throws SocialSiteException {
        AppRegistration reg = getAppRegistration(id);
        strategy.remove(reg);
    }

    public AppRegistration getAppRegistrationByConsumerKey(String consumerKey) throws SocialSiteException {
        try {
            Query query = strategy.getNamedQuery("AppRegistration.getByConsumerKey");
            query.setParameter(1, consumerKey);
            return (AppRegistration)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void saveOAuthEntry(OAuthEntry entry) throws SocialSiteException {
        OAuthEntryRecord record = (OAuthEntryRecord)
            strategy.load(OAuthEntryRecord.class, entry.token);
        if (record == null) {
            record = new OAuthEntryRecord(entry);
        } else {
            record.update(entry);
        }
        strategy.store(record);
    }

    public void removeOAuthEntry(OAuthEntry entry) throws SocialSiteException {
        strategy.remove(OAuthEntryRecord.class, entry.token);
    }

    public OAuthEntry getOAuthEntry(String token) throws SocialSiteException {
        OAuthEntryRecord record = (OAuthEntryRecord)
            strategy.load(OAuthEntryRecord.class, token);
        if (record != null) {
            return record.getOAuthEntry();
        } else {
            return null;
        }
    }
}


class AppRegistrationListener {
    private static Log log = LogFactory.getLog(AppRegistrationListener.class);
    private static String siteName = RuntimeConfig.getProperty("site.name");
    private static boolean doEmails =
        Config.getBooleanProperty("socialsite.notifications.email.appRequest.enabled");
    private static final String JNDI_KEY = "java:comp/env/mail/SocialSite/Session";
    private static Session mailSession;

    static {
        if (doEmails) {
            log.info("Configuring App Registration email message");
            try {
                mailSession = Startup.getMailProvider().getSession();
            } catch (Exception e) {
                log.error("Unable to obtain mailSession", e);
            }
        }
    }

    @PostPersist
    public void persisted(AppRegistration appreg) {
        if (!doEmails) return;

        String adminEmail = Config.getProperty("socialsite.notifications.email.admin-address");
        String fromEmail = Config.getProperty("socialsite.notifications.email.from-address");
        String userEmail = String.format("%s <%s>",
            appreg.getProfile().getName(), appreg.getProfile().getPrimaryEmail());
        String cc = null;
        String bcc = null;

        // first, send confirmation email to user
        {
            Object[] subjectArgs = {siteName};
            String subject = TextUtil.format("socialsite.notifications.email.appRequestConfirm.subject", subjectArgs);
            Object[] contentArgs = {appreg.getProfile().getName(), appreg.getAppUrl()};
            String content = TextUtil.format("socialsite.notifications.email.appRequestConfirm.body", contentArgs);
            try {
                log.debug(String.format("Sending Mail [from=%s, to=%s, subject=%s, content=%s]", fromEmail, userEmail, subject, content));
                MailUtil.sendTextMessage(mailSession, fromEmail, userEmail, cc, bcc, subject, content);
            } catch (MessagingException e) {
                log.error("Failed to send AppRequest confirmation mail message", e);
            }
        }

        // second, send notification email to admin user
        {
            URLStrategy urlStrategy = Factory.getSocialSite().getURLStrategy();
            Object[] subjectArgs = {siteName};
            String subject = TextUtil.format("socialsite.notifications.email.appRequestNotification.subject", subjectArgs);
            Object[] contentArgs = {appreg.getProfile().getName(), urlStrategy.getBaseURL() + "/app-ui/admin/GadgetRegistration"};
            String content = TextUtil.format("socialsite.notifications.email.appRequestNotification.body", contentArgs);
            try {
                log.debug(String.format("Sending Mail [from=%s, to=%s, subject=%s, content=%s]", fromEmail, adminEmail, subject, content));
                MailUtil.sendTextMessage(mailSession, fromEmail, userEmail, cc, bcc, subject, content);
            } catch (MessagingException e) {
                log.error("Failed to send AppRequest notification mail message", e);
            }
        }
    }

    @PostUpdate
    public void updated(AppRegistration appreg) {
        if (!doEmails) return;

        String adminEmail = Config.getProperty("socialsite.notifications.email.admin-address");
        String fromEmail = Config.getProperty("socialsite.notifications.email.from-address");
        String userEmail = String.format("%s <%s>",
            appreg.getProfile().getName(), appreg.getProfile().getPrimaryEmail());
        String cc = null;
        String bcc = null;

        if ("APPROVED".equals(appreg.getStatus())) {
            Object[] subjectArgs = {siteName};
            String subject = TextUtil.format("socialsite.notifications.email.appRequestApproved.subject", subjectArgs);
            Object[] contentArgs = {appreg.getProfile().getName(), appreg.getAppUrl()};
            String content = TextUtil.format("socialsite.notifications.email.appRequestApproved.body", contentArgs);
            try {
                log.debug(String.format("Sending Mail [from=%s, to=%s, subject=%s, content=%s]", fromEmail, adminEmail, subject, content));
                MailUtil.sendTextMessage(mailSession, fromEmail, userEmail, cc, bcc, subject, content);
            } catch (MessagingException e) {
                log.error("Failed to send AppRequest approved mail message", e);
            }
        } else if ("REJECTED".equals(appreg.getStatus())) {
            Object[] subjectArgs = {siteName};
            String subject = TextUtil.format("socialsite.notifications.email.appRequestRejected.subject", subjectArgs);
            Object[] contentArgs = {appreg.getProfile().getName(), appreg.getAppUrl(), appreg.getComment()};
            String content = TextUtil.format("socialsite.notifications.email.appRequestRejected.body", contentArgs);
            try {
                log.debug(String.format("Sending Mail [from=%s, to=%s, subject=%s, content=%s]", fromEmail, adminEmail, subject, content));
                MailUtil.sendTextMessage(mailSession, fromEmail, userEmail, cc, bcc, subject, content);
            } catch (MessagingException e) {
                log.error("Failed to send AppRequest rejected mail message", e);
            }
        }
    }

    @PostRemove
    public void removed(AppRegistration appreg) {
        if (!doEmails) return;

        String adminEmail = Config.getProperty("socialsite.notifications.email.admin-address");
        String fromEmail = Config.getProperty("socialsite.notifications.email.from-address");
        String userEmail = String.format("%s <%s>",
            appreg.getProfile().getName(), appreg.getProfile().getPrimaryEmail());
        String cc = null;
        String bcc = null;

        Object[] subjectArgs = {siteName};
        String subject = TextUtil.format("socialsite.notifications.email.appRequestRemoved.subject", subjectArgs);
        Object[] contentArgs = {appreg.getProfile().getName(), appreg.getAppUrl(), appreg.getComment()};
        String content = TextUtil.format("socialsite.notifications.email.appRequestRemoved.body", contentArgs);
        try {
            log.debug(String.format("Sending Mail [from=%s, to=%s, subject=%s, content=%s]", fromEmail, adminEmail, subject, content));
            MailUtil.sendTextMessage(mailSession, fromEmail, userEmail, cc, bcc, subject, content);
        } catch (MessagingException e) {
            log.error("Failed to send AppRequest removed mail message", e);
        }
    }
}

