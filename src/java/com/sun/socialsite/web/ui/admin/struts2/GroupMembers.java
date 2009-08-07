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
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Group members view action.
 */
public class GroupMembers extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(GroupMembers.class);
    private String groupid = null;
    private List<GroupRelationship> members = null;
    private Group groupEntity = null;
    
    public GroupMembers() {
        setPageTitle("GroupMembers.pageTitle");
        this.desiredMenu = "admin";
    }

    public String execute() {
        // nothing to do right now
        return INPUT;
    }

    public String save() throws SocialSiteException {
        return SUCCESS;
    }

    public void prepare() {
        setPageTitle("GroupMembers.pageTitle");
        this.desiredMenu = "admin";
        if (groupid != null) {
            try {
                com.sun.socialsite.business.GroupManager mgr = Factory.getSocialSite().getGroupManager();
                groupEntity = mgr.getGroupById(groupid);
            } catch (SocialSiteException e) {
                String msg = String.format("Failed to process groupId: %s", groupid);
                log.error(msg, e);
            }
        }
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public List<GroupRelationship> getMembers() throws Exception {
        if (groupEntity != null) {
            try {
                com.sun.socialsite.business.GroupManager mgr = Factory.getSocialSite().getGroupManager();
                members = mgr.getMembershipsByGroup(groupEntity, 0, -1);
            } catch (SocialSiteException e) {
                String msg = String.format("Failed to process groupId: %s", groupid);
                log.error(msg, e);
            }
        }
        return members;
    }    
}
