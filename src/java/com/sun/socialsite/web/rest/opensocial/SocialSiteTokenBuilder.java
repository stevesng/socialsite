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
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.URLStrategy;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.util.ProxyUtil;
import com.sun.socialsite.web.rest.opensocial.AssertedToken;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <p>
 *  Constructs SocialSiteToken instances for a specified context and application ID.
 * </p>
 */
public class SocialSiteTokenBuilder {

    private static Log log = LogFactory.getLog(SocialSiteTokenBuilder.class);


    /**
     * Constructs a SocialSiteToken instance using the specified context and application ID.
     *
     * @param context the context which will be used for the token's construction and operation.
     * @param appId the application ID to be associated with the token.
     * @return a SocialSiteToken instance corresponding to the specified context and application ID.
     */
    public SocialSiteToken buildAppToken(ConsumerContext context, String appId, Long moduleId) throws SocialSiteException {
        return constructToken(context, appId, moduleId, false);
    }


    public SocialSiteToken buildContainerPageToken(ConsumerContext context) throws SocialSiteException {
        return constructToken(context, null, null, true);
    }


    private SocialSiteToken constructToken(ConsumerContext context, String appId, Long moduleId, boolean containerPageFlag) throws SocialSiteException {

        try {

            ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
            GroupManager groupManager = Factory.getSocialSite().getGroupManager();

            String viewerId = context.getString("viewer.id");
            Profile viewerProfile = ((viewerId != null) ? profileManager.getProfileByUserId(viewerId) : null);

            String ownerId = context.getString("owner.id");
            Profile ownerProfile = ((ownerId != null) ? profileManager.getProfileByUserId(ownerId) : null);

            String groupHandle = context.getString("group.handle");
            Group group = ((groupHandle != null) ? groupManager.getGroupByHandle(groupHandle) : null);
            
            String containerId = context.getString("containerId");

            // MediaWiki insists on changing the case of usernames.  We'll do our best to work around it for now.
            // TODO: a cleaner approach to case-sensitivity issues.
            if ((viewerProfile == null) && (viewerId != null)) viewerProfile = profileManager.getProfileByUserId(viewerId.toLowerCase());
            if ((ownerProfile == null) && (ownerId != null)) ownerProfile = profileManager.getProfileByUserId(ownerId.toLowerCase());

            SocialSiteToken token = new AssertedToken(viewerProfile, ownerProfile, group, appId, moduleId, containerId, containerPageFlag, context.timeout());
            if (log.isDebugEnabled()) {
                log.debug(String.format("new AssertedToken(viewer=%s, owner=%s, group=%s, containerPageFlag=%s, appId=%s, moduleId=%d)",
                        viewerId, ownerId, groupHandle, containerPageFlag, appId, moduleId));
            }

            final String viewProfileUriTemplate = context.getString("viewProfileUriTemplate");
            if (viewProfileUriTemplate != null) {
                Object overrider = new Object() {
                    public String getViewURL(Profile profile) {
                        // TODO: replace this with a real URI Template implementation
                        return viewProfileUriTemplate.replace("{profile.userId}", profile.getUserId());
                    }
                };
                token.setURLStrategy((URLStrategy)(ProxyUtil.getOverrideProxy(token.getURLStrategy(), overrider)));
            }

            final String viewGroupUriTemplate = context.getString("viewGroupUriTemplate");
            if (viewGroupUriTemplate != null) {
                Object overrider = new Object() {
                    public String getViewURL(Group group) {
                        // TODO: replace this with a real URI Template implementation
                        return viewGroupUriTemplate.replace("{group.handle}", group.getHandle());
                    }
                };
                token.setURLStrategy((URLStrategy)(ProxyUtil.getOverrideProxy(token.getURLStrategy(), overrider)));
            }

            return token;

        } catch (SocialSiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }

    }

}
