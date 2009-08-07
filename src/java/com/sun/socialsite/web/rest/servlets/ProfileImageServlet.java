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

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ListenerManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.filters.CustomizedPageCachingFilter;
import java.io.IOException;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A Servlet which allows the web retrieval and display of images from
 * SocialSite Profiles.
 */
public class ProfileImageServlet extends ImageServlet {

    private static final long serialVersionUID = 0L;

    private static Log log = LogFactory.getLog(ProfileImageServlet.class);

    private ListenerManager listenerManager;

    private ProfileListener profileListener = new ProfileListener();;


    /**
     * Public constructor.
     */
    public ProfileImageServlet() {
        super();
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        listenerManager = Factory.getSocialSite().getListenerManager();
        listenerManager.addListener(Profile.class, profileListener);
    }


    @Override
    public void destroy() {
        super.destroy();
        listenerManager.removeListener(Profile.class, profileListener);
    }


    protected Result getResult(String userId) throws IOException, ServletException {

        Profile profile;

        try {
            ProfileManager mgr = Factory.getSocialSite().getProfileManager();
            profile = mgr.getProfileByUserId(userId);

        } catch (SocialSiteException e) {
            log.debug("ERROR looking up profile image for profile.userId: " + userId);
            throw new ServletException(e);
        }

        if (profile == null) {
            throw new ServletException("no item found for display");
        }

        return new Result(profile.getUpdated(), profile.getImageType(), (byte[])(profile.getImage()));
    }


    static class ProfileListener {

        private Ehcache cache;

        public ProfileListener() {
            cache = CacheManager.getInstance().getEhcache(CustomizedPageCachingFilter.NAME);
            if (cache == null) {
                log.warn("cache is null");
            }
        }

        @PostRemove
        @PostUpdate
        public void profileChanged(Profile profile) {

            if (cache == null) {
                return;
            }

            if (log.isDebugEnabled()) {
                log.debug(String.format("Removing %s from cache", profile.getImageURL()));
                log.debug(String.format("Removing %s from cache", profile.getThumbnailURL()));
            }

            cache.remove(profile.getImageURL());
            cache.remove(profile.getThumbnailURL());
        }

    }

}
