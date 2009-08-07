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
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.SocialSite;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.userapi.User;
import com.sun.socialsite.userapi.UserManagementException;
import com.sun.socialsite.userapi.UserManager;
import com.sun.socialsite.userapi.UserManagerImpl;
import java.util.StringTokenizer;
import java.util.Date;
import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Default User Creator - creates users with IDs as defined in
 * socialsite.default.userids in socialsite.properties
 */
public class DefaultUserCreator {

    private static Log log = LogFactory.getLog(DefaultUserCreator.class);
    private ProfileManager profileManager;
    private JPAPersistenceStrategy strategy;
    private String defaultIds = null;


    public DefaultUserCreator(JPAPersistenceStrategy strategy, String userIds) {
        this.strategy = strategy;
        this.defaultIds = userIds;
        profileManager = Factory.getSocialSite().getProfileManager();
    }

    public void createDefaultUsers() {
        log.info("Beginning Default User Creation");

        StringTokenizer tokens = new StringTokenizer(defaultIds, ",");
        String userId = null;
        while (tokens.hasMoreTokens()) {
            try {
                userId = tokens.nextToken();
                createSocialSiteUser(userId);
                createSocialSiteProfile(userId);
            } catch (Throwable t) {
                log.error("ERROR while creating default userId : " + userId, t);
            }
        }
        log.info("End of Default User Creation");
    }

    private void createSocialSiteUser(String id) throws UserManagementException {
        EntityManager em = null;
        User user = null;

        try {
            em = strategy.getEntityManager(false);
            UserManager userManager = new UserManagerImpl(em);
            user = userManager.getUserByUserId(id);
            if (user == null) {
                Date now = new Date();
                user = new User();
                user.setUserId(id);
                user.setUserName(id);
                user.resetPassword(id, "SHA");
                user.setFullName(id);
                user.setEmailAddress(id+"@sun.com");
                user.setCreationDate(now);
                user.setUpdateDate(now);
                user.setAccessDate(now);
                user.setEnabled(true);

                em.getTransaction().begin();
                try {
                    userManager.registerUser(user);
                    userManager.grantRole("user", user);
                    em.persist(user);
                } catch (Throwable t) {
                    try {
                        if (em.getTransaction().isActive()) em.getTransaction().rollback();
                    } catch (Throwable t2) {
                        log.error("Failed to rollback transaction: " + em.getTransaction(), t2);
                    }
                    throw new UserManagementException(t);
                }
                em.getTransaction().commit();
            }
        } finally {
            try {
                if (em != null) {
                    em.close();
                }
            } catch (Throwable t) {
                throw new UserManagementException(t);
            }
        }
    }

    private void createSocialSiteProfile(String id) throws SocialSiteException {

        SocialSite socialsite = Factory.getSocialSite();
        try {
            Profile profile = profileManager.getProfileByUserId(id);

            if (profile == null) {
                profile = new Profile();
                profile.setUserId(id);
                profile.setFirstName(id);
                profile.setMiddleName(id);
                profile.setLastName(id);
                profile.setNickName(id);
                profile.setPrimaryEmail(id+"@sun.com");
                profileManager.saveProfile(profile, false);
                socialsite.flush();
            }
        } finally {
            socialsite.release();
        }
    }
}
