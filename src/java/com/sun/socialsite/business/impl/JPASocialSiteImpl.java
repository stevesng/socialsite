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
import com.sun.socialsite.business.SocialSiteActivityManager;
import com.sun.socialsite.business.ThemeSettingsManager;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.ConfigPropertiesManager;
import com.sun.socialsite.business.ContentManager;
import com.sun.socialsite.business.ContextRuleManager;
import com.sun.socialsite.business.ExtensionManager;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.InitializationException;
import com.sun.socialsite.business.ListenerManager;
import com.sun.socialsite.business.Manager;
import com.sun.socialsite.business.PermissionManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.SearchManager;
import com.sun.socialsite.business.SocialSite;
import com.sun.socialsite.business.URLStrategy;
import com.sun.socialsite.userapi.UserManager;
import com.sun.socialsite.business.NotificationManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.oauth.OAuthStore;

/**
 * Implements SocialSite, the entry point interface for the SocialSite
 * business tier APIs using the Java Persistence API (JPA).
 */
@Singleton
public class JPASocialSiteImpl extends AbstractManagerImpl implements SocialSite {

    private static Log log = LogFactory.getLog(JPASocialSiteImpl.class);

    // a persistence utility class
    private final JPAPersistenceStrategy strategy;

    // url strategy
    private final URLStrategy urlStrategy;

    // references to the managers we maintain
    private final SocialSiteActivityManager socialSiteActivityManager;
    private final ThemeSettingsManager themesManager;
    private final ConfigPropertiesManager configPropertiesManager;
    private final ContentManager contentManager;
    private final ContextRuleManager contextRuleManager;
    private final RelationshipManager friendManager;
    private final AppManager appManager;
    private final GroupManager groupManager;
    private final ListenerManager listenerManager;
    private final NotificationManager notificationManager;
    private final PermissionManager permissionManager;
    private final ProfileManager profileManager;
    private final SearchManager searchManager;
    private final UserManager userManager;
    private final ExtensionManager extensionManager;
    private final OAuthStore oauthStore;

    @Inject
    protected JPASocialSiteImpl(
            JPAPersistenceStrategy strategy,
            URLStrategy urlStrategy,
            SocialSiteActivityManager socialSiteActivityManager,
            ThemeSettingsManager themesManager,
            AppManager appManager,
            ConfigPropertiesManager configPropertiesManager,
            ContentManager contentManager,
            ContextRuleManager contextRuleManager,
            RelationshipManager friendManager,
            GroupManager groupManager,
            ListenerManager listenerManager,
            NotificationManager notificationManager,
            PermissionManager permissionManager,
            ProfileManager profileManager,
            SearchManager searchManager,
            UserManager userManager,
            OAuthStore oauthStore,
            ExtensionManager extManManager) throws SocialSiteException
    {
        this.strategy = strategy;
        this.urlStrategy = urlStrategy;
        this.socialSiteActivityManager = socialSiteActivityManager;
        this.themesManager = themesManager;
        this.appManager = appManager;
        this.configPropertiesManager = configPropertiesManager;
        this.contentManager = contentManager;
        this.contextRuleManager = contextRuleManager;
        this.friendManager = friendManager;
        this.groupManager = groupManager;
        this.listenerManager = listenerManager;
        this.notificationManager = notificationManager;
        this.permissionManager = permissionManager;
        this.profileManager = profileManager;
        this.searchManager = searchManager;
        this.userManager = userManager;
        this.oauthStore = oauthStore;
        this.extensionManager = extManManager;
    }

    @Override
    public void initialize() throws InitializationException {

        log.info("Initializing SocialSite business tier");

        try {

            Manager manager = null;

            manager = getConfigPropertiesManager();
            if (manager != null) {
                manager.initialize();
            }

            manager = getSearchManager();
            if (manager != null) {
                manager.initialize();
            }

            manager = getAppManager();
            if (manager != null) {
                manager.initialize();
            }

            manager = getThemeSettingsManager();
            if (manager != null) {
                manager.initialize();
            }

            // special case that manages other managers
            if (extensionManager != null) {
                extensionManager.initialize();
            }

            // we always need to do a flush after initialization because it's
            // possible that some changes need to be persisted
            flush();

        } catch (Exception e) {
            throw new InitializationException("Unexpected Failure", e);
        }

        log.info("SocialSite business tier successfully initialized");

    }

    public void flush() throws SocialSiteException {
        this.strategy.flush();
    }

    @Override
    public void release() {
        this.strategy.release();
    }

    @Override
    public void shutdown() {
        this.release();
        this.strategy.shutdown();
    }

    public URLStrategy getURLStrategy() {
        return this.urlStrategy;
    }

    public SocialSiteActivityManager getSocialSiteActivityManager() {
        return socialSiteActivityManager;
    }

    public AppManager getAppManager() {
        return appManager;
    }

    public ThemeSettingsManager getThemeSettingsManager() {
        return this.themesManager;
    }

    public ConfigPropertiesManager getConfigPropertiesManager() {
        return configPropertiesManager;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

    public ContextRuleManager getContextRuleManager() {
        return contextRuleManager;
    }
    
    public RelationshipManager getRelationshipManager() {
        return friendManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public OAuthStore getOAuthStore() {
        return oauthStore;
    }

    public <T extends Manager> T get(Class<T> clazz) {
        return extensionManager.getManager(clazz);
    }

}
