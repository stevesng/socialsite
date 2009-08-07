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
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * User Access Manager Form Action.
 */
public class UserMetrics extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(UserMetrics.class);
    private String pattern = null;

    public UserMetrics() {
        this.desiredMenu = "admin";
    }

    public void prepare() throws Exception {
    }

    public String execute() {
        return INPUT;
    }

    public String getTotalUsers() throws Exception {
        String total = "undefined";
        try {
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            List<Profile> profiles = pmgr.getOldestProfiles(0, -1);            
            total = String.valueOf(profiles.size());
        } catch (SocialSiteException snex) {
            log.error(snex);
            throw new SocialSiteException("Error getting profile info");
        }
        return total;
    }

    public String getTotalGroups() throws Exception {
        String total = "undefined";
        try {
            GroupManager pmgr = Factory.getSocialSite().getGroupManager();
            List<Group> grps = pmgr.getOldestGroups(0, -1);            
            total = String.valueOf(grps.size());
        } catch (SocialSiteException snex) {
            throw new SocialSiteException("Error getting group info");
        }
        return total;
    }

   public List<String> getPopularGroups() throws Exception {
       List<String> popular = new ArrayList<String>();
        try {
            GroupManager pmgr = Factory.getSocialSite().getGroupManager();
            // TBD - update this to use a query to return popular groups.
            List<Group> grps = pmgr.getPopularGroups(0, 5);            
            for (Group gr: grps) {
                popular.add(gr.getHandle());
            }
         } catch (SocialSiteException snex) {
            throw new SocialSiteException("Error getting group info");
        }
       
       return popular;
    }

    public String save() {
        return INPUT;
    }

}
