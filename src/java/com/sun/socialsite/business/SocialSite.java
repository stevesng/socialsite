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

package com.sun.socialsite.business;

import com.google.inject.ImplementedBy;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.impl.JPASocialSiteImpl;
import com.sun.socialsite.userapi.UserManager;
import org.apache.shindig.gadgets.oauth.OAuthStore;


/**
 * The main entry point interface of the SocialSite business tier.
 */
@ImplementedBy(JPASocialSiteImpl.class)
public interface SocialSite {

    /**
     * Initialize any resources necessary for this instance of SocialSite.
     */
    public void initialize() throws InitializationException;

    /**
     * Flush object states.
     */
    public void flush() throws SocialSiteException;

    /**
     * Release any resources associated with a session.
     */
    public void release();

    /**
     * Shutdown the application.
     */
    public void shutdown();

    /**
     * Get the configured URLStrategy.
     */
    public URLStrategy getURLStrategy();

    /**
     * Get SocialSiteActivityManager.
     */
    public SocialSiteActivityManager getSocialSiteActivityManager();

    /**
     * Get ThemesManager    
     */
    public ThemeSettingsManager getThemeSettingsManager();

    /**
     * Get AppManager.
     */
    public AppManager getAppManager();

    /**
     * Get PropertiesManager.
     */
    public ConfigPropertiesManager getConfigPropertiesManager();

    /**
     * Get ContentManager.
     */
    public ContentManager getContentManager();

    /**
     * Get ContextRuleManager.
     */
    public ContextRuleManager getContextRuleManager();

    /**
     * Get FriendManager.
     */
    public RelationshipManager getRelationshipManager();

    /**
     * Get GroupManager.
     */
    public GroupManager getGroupManager();

    /**
     * Get ListenerManager.
     */
    public ListenerManager getListenerManager();

   /**
     * Get NotificationManager.
     */
    public NotificationManager getNotificationManager();

    /**
     * Get PermissionManager.
     */
    public PermissionManager getPermissionManager();

    /**
     * Get ProfileManager.
     */
    public ProfileManager getProfileManager();

    /**
     * Get SearchManager.
     */
    public SearchManager getSearchManager();

    /**
     * Get UserManager.
     */
    public UserManager getUserManager();

    /**
     * Get OAuthStore, part of OAuth Consumer implementation.
     */
    public OAuthStore getOAuthStore();

    /**
     * Generic accessor for extending interface dynamically.
     */
    public <T extends Manager> T get(Class<T> clazz);

}
