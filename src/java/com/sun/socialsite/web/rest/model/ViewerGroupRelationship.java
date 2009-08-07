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

package com.sun.socialsite.web.rest.model;

import com.sun.socialsite.pojos.*;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Represents status of relationship of viewer with a group.
 */
public class ViewerGroupRelationship {
    private static Log log = LogFactory.getLog(ViewerGroupRelationship.class);

    /** Relationship of viewer with group */
    private String relationship;

    public static final String NONE    = "NONE";
    public static final String MEMBER  = "MEMBER";
    public static final String FOUNDER = "FOUNDER";
    public static final String ADMIN   = "ADMIN";
    public static final String PENDING = "PENDING";

    public ViewerGroupRelationship() {}

    public ViewerGroupRelationship(Profile viewer, Group group) {
        init(viewer, group);
    }

    private void init(Profile viewer, Group group) {
        RelationshipManager  fmgr = Factory.getSocialSite().getRelationshipManager();
        GroupManager   gmgr = Factory.getSocialSite().getGroupManager();
        try {
            GroupRelationship groupRel = gmgr.getMembership(group, viewer);
            if (groupRel == null) {
                GroupRequest greq = gmgr.getMembershipRequest(group, viewer);
                if (greq != null && greq.getStatus().equals(GroupRequest.Status.PENDING)) {
                    setRelationship(PENDING);
                } else {
                    setRelationship(NONE);
                }
            } else {
                setRelationship(groupRel.getRelcode().toString());
            }
        } catch (SocialSiteException ex) {
            log.error("ERROR determining viewer group relationship", ex);
        }
    }

    /**
     * @return the relationship
     */
    public String getRelationship() {
        return relationship;
    }

    /**
     * @param relationship the relationship to set
     */
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
