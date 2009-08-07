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
import com.sun.socialsite.business.impl.JPAAppManagerImpl;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.AppData;
import com.sun.socialsite.pojos.AppInstance;
import com.sun.socialsite.pojos.AppRegistration;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.Group;
import java.net.URL;
import java.util.List;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;


/**
 * Manages App metadata and persistence.
 */
@ImplementedBy(JPAAppManagerImpl.class)
public interface AppManager extends Manager {

    public GadgetSpec getGadgetSpecByURL(URL url) throws SocialSiteException;

    //------------------------------------------------------------- App CRUD

    public App getApp(String appId) throws SocialSiteException;

    public void saveApp(App app) throws SocialSiteException;

    public void removeApp(App app) throws SocialSiteException;

    /**
     * Retrieve an App with the specified URL.  If no such App is known to the
     * system (i.e. exists within our internal data store), no attempt will be made
     * to create a new App by reading the contents of the URL.
     */
    public App getAppByURL(URL url) throws SocialSiteException;

    /**
     * Retrieve an App with the specified URL.  If mustAlreadyExist is false and 
     * no such App is known (exists * within our internal data store), the system
     * will attempt will be made to create a new App by reading the contents of the URL.
     */
    public App getAppByURL(URL url, boolean mustAlreadyExist) throws SocialSiteException;

    public List<App> getApps(int offset, int length) throws SocialSiteException;

    /**
     * Gets all apps that are to be displayed in the end-user directory, with paging.
     */
    public List<App> getAppsInDirectory(int startIndex, int count) throws SocialSiteException;

    public List<App> getOldestApps(int offset, int length) throws SocialSiteException;


    //---------------------------------------------------- AppData CRUD

    /**
     * Set an AppData property for an App.
     *
     * @param app       App
     * @param propName  Name of property, must be unique within scope
     * @param propValue Value of property to be set
     * @throws SocialSiteException
     */
    public void setAppData(App app, Profile profile, String propName, String propValue) throws SocialSiteException;

    /**
     * Gets AppData properties which the specified user has set for an app.
     *
     * @param app          App
     * @param profile      Profile for the user
     * @return             Requested AppData object or null if not found
     * @throws SocialSiteException
     */
    public List<AppData> getAppData(App app, Profile profile) throws SocialSiteException;

    /**
     * Save AppData. If you just want to set a property use setAppData() instead.
     *
     * @param appData Property to be saved
     * @throws SocialSiteException
     */
    public void saveAppData(AppData appData) throws SocialSiteException;

    /**
     * Remove an AppData property.
     *
     * @param appData Propery to be deleted.
     * @throws SocialSiteException
     */
    public void removeAppData(AppData appData) throws SocialSiteException;


    //--------------------------------------------------- AppInstance CRUD

    public void saveAppInstance(AppInstance appInstance) throws SocialSiteException;

    public void removeAppInstance(AppInstance appInstance) throws SocialSiteException;

    public AppInstance getAppInstance(Long id) throws SocialSiteException;

    /**
     * Retrieve an AppInstance with the specified app and group.  Only "default"
     * AppInstance objects will be considered (meaning those with a null collection).
     */
    public AppInstance getDefaultAppInstance(App app, Group group)
        throws SocialSiteException;

    /**
     * Retrieve an AppInstance with the specified app and profile.  Only "default"
     * AppInstance objects will be considered (meaning those with a null collection).
     */
    public AppInstance getDefaultAppInstance(App app, Profile profile)
        throws SocialSiteException;

    public List<AppInstance> getAppInstancesByCollection(Group group, String collection)
        throws SocialSiteException;

    public List<AppInstance> getAppInstancesByCollection(Profile profile, String collection)
        throws SocialSiteException;


    //-------------------------------------------------------- App Registration

    /**
     * Get AppRegistraion by id.
     *
     * @param id ID of registration to fetch
     * @return Registration or null if not found
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public AppRegistration getAppRegistration(String id) throws SocialSiteException;

    /**
     * Get AppRegistraion by URL.
     *
     * @param url of registration to fetch
     * @return Registration or null if not found
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public AppRegistration getAppRegistrationByURL(String url) throws SocialSiteException;

    /**
     * Get AppRegistraion by Consumer Key.
     *
     * @param consumerKey of registration to fetch
     * @return Registration or null if not found
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public AppRegistration getAppRegistrationByConsumerKey(String consumerKey) throws SocialSiteException;

    /**
     * Get AppRegistrations.
     *
     * @param profileid Filter by profileid, or null for no filtering
     * @param status  Filter by status, or null for no filtering
     * @return List of registrations, empty if no results
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public List<AppRegistration> getAppRegistrations(String profileid, String status) throws SocialSiteException;

    /**
     * Register a new OpenSocial application, will queue it up for admin approval.
     * Notifies socialsite admin of new registration.
     * Sends confirmation email to profile owner.
     *
     * @param profileid Profile ID of application owner (required)
     * @param appurl URL of application's Gadget Specification (required)
     * @param serviceName nickname of remote service, or null for none
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public void registerApp(String profileid, String appurl, String serviceName) throws SocialSiteException;

    /**
     * Approve application registration by ID
     * Notifies profile owner of approval.
     *
     * @param id AppRegistration ID to be approved
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public void approveAppRegistration(String id, String comment) throws SocialSiteException;

    /**
     * Reject application registration by ID, removes registration.
     * Notifies profile owner of rejection.
     *
     * @param id AppRegistration ID to be approved
     * @throws com.sun.socialsite.SocialSiteException on error
     */

    public void rejectAppRegistration(String id, String comment) throws SocialSiteException;

    /**
     * Remove an app from the registry.
     * 
     * @param id AppRegistration ID to be removed
     * @throws com.sun.socialsite.SocialSiteException on error
     */
    public void removeAppRegistration(String id, String comment) throws SocialSiteException;


    //------------------------------------------------------ OAuthEntry storage

    // TODO: move these to their own manager?

    public void saveOAuthEntry(OAuthEntry entry)  throws SocialSiteException;

    public void removeOAuthEntry(OAuthEntry entry)  throws SocialSiteException;
    
    public OAuthEntry getOAuthEntry(String token)  throws SocialSiteException;

}
