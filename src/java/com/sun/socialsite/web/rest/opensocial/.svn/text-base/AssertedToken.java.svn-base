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

package com.sun.socialsite.web.rest.opensocial;

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AppManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.Group;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An implementation of SocialSiteToken which simply trusts the creator to assert
 * the identity of the viewer and owner.  This implementation is intended
 * for development and diagnostic purposes.  It should not be used in a
 * production setting.
 */
public class AssertedToken extends SocialSiteToken {

    private static Log log = LogFactory.getLog(AssertedToken.class);

    private static Long defaultTimeout = (long)(Config.getIntProperty("socialsite.gadgets.assertedtoken.timeout"));

    private static Timer timer = new Timer("AssertedToken Timeout Timer");

    private String viewerId;

    private String ownerId;

    private String groupHandle;

    private String appId;

    private String appUrl = null;

    private Long moduleId;

    private boolean containerPageFlag;


    /**
     * Constructs an AssertedToken.  If the specified timeout is null, a default timeout is
     * used instead.
     *
     * @param viewerProfile viewer for the token.
     * @param ownerProfile owner for the token.
     * @param group group for the token.
     * @param appId application ID for the token.
     * @param moduleId module ID for the token.
     * @param timeout timeout for the token (in seconds).
     */
    AssertedToken(Profile viewerProfile, Profile ownerProfile, Group group, String appId, Long moduleId, 
            String containerId, boolean containerPageFlag, Long timeout) {

        super(UUID.randomUUID().toString(), containerId);
        this.viewerId = ((viewerProfile != null) ? viewerProfile.getUserId() : null);
        this.ownerId = ((ownerProfile != null) ? ownerProfile.getUserId() : null);
        this.groupHandle = ((group != null) ? group.getHandle() : null);
        this.appId = appId;
        this.moduleId = moduleId;
        this.containerPageFlag = containerPageFlag;

        if (this.appId != null) try {
            AppManager amgr = Factory.getSocialSite().getAppManager();
            App app = amgr.getApp(this.appId);
            this.appUrl = app.getURL().toString();

        } catch (SocialSiteException ex) {
            log.error("ERROR looking up app URL", ex);
        }

        if (timeout != null) {
            timer.schedule(new TimeoutHandler(), timeout * 1000L);
        } else {
            timer.schedule(new TimeoutHandler(), defaultTimeout * 1000L);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerId() {
        return ownerId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getViewerId() {
        return viewerId;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getGroupHandle() {
        return groupHandle;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasPermission(String permissionName) {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppId() {
        return appId;
    }

    /**
     * {@inheritDoc}
     */
    public String getAppUrl() {
        return appUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getModuleId() {
        return moduleId;
    }


    // TODO: proper implementation
    public String getUpdatedToken() {
        return null;
    }


    // TODO: proper implementation
    public String getTrustedJson() {
        return null;
    }


    // TODO: proper implementation
    public boolean isAnonymous() {
        return false;
    }


    public boolean isForContainerPage() {
        return containerPageFlag;
    }

    public String getContainer() {
        return Config.getProperty("socialsite.oauth.container");
    }


    /**
     * Removes our internal references to token when its timeout has expired.
     */
    private class TimeoutHandler extends TimerTask {

        public void run() {
            log.debug(String.format("Timeout for %s", AssertedToken.this));
            SocialSiteTokenDecoder.removeToken(AssertedToken.this);
        }

    }

}
