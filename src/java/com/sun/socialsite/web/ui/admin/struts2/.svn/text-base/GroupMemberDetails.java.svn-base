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
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship.Relationship;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Group edit action.
 */
public class GroupMemberDetails extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(GroupMemberDetails.class);
    private String userid = null;
    private boolean admin = true;
    private String groupid = null;
    private Profile prof = null;
    private Group group = null;
    
    public GroupMemberDetails() {
        this.desiredMenu = "admin";
    }

    @Override
    public String execute() {
        // nothing to do right now
        return INPUT;
    }

    @Override
    public void prepare() throws Exception {
        this.desiredMenu = "admin";
        if (userid != null && groupid != null) {
            try {
                com.sun.socialsite.business.GroupManager gm = Factory.getSocialSite().getGroupManager();
                group = gm.getGroupById(groupid);
                ProfileManager pm = Factory.getSocialSite().getProfileManager();
                prof = pm.getProfileByUserId(userid);
                admin = gm.isAdmin(group, prof);
            } catch (Exception snex) {
                log.error(String.format("Error in prepare: %s",
                    snex.getMessage()));
                throw new SocialSiteException("Error getting profile info for userid " + userid);
            }
        }
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    public String save() {
        try {
            com.sun.socialsite.business.GroupManager gm =
                Factory.getSocialSite().getGroupManager();
            ProfileManager pm = Factory.getSocialSite().getProfileManager();
            prof = pm.getProfileByUserId(userid);
            gm.createMembership(group, prof, Relationship.MEMBER);
            Factory.getSocialSite().flush();
            return SUCCESS;
        } catch (Exception snex) {
            log.error(snex.getMessage(), snex);
            return ERROR;
        }
    }

    public String remove() {
        try {            
            com.sun.socialsite.business.GroupManager gm = Factory.getSocialSite().getGroupManager();
            gm.removeMembership(group, prof);
            Factory.getSocialSite().flush();
            return SUCCESS;
        } catch (Exception snex) {
            log.error(snex.getMessage(), snex);
            return ERROR;
        }
    }

}
