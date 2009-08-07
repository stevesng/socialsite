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

package com.sun.socialsite.web.ui.admin.struts2.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;

/**
 * Used by multiple actions to check if a
 * profile is in a group or not.
 */
public class GroupMemberValidator extends AbstractValidatorSupport {

    private boolean mustBeMember = true;

    /*
     * Checks to see if a user is already in a group or not. The 'groupid'
     * param or 'handle' must be included in the submitted form. If
     * there is no matching group, this validator returns. Validating
     * the group should be handled in another validator.
     */
    public void validate(Object o) throws ValidationException {
        String fieldName = getFieldName();
        String profileId = (String) getFieldValue(fieldName, o);
        GroupManager groupManager =
            Factory.getSocialSite().getGroupManager();

        try {
            Group group = getGroup(groupManager, o);
            if (group == null) {
                return;
            }
            ProfileManager profileManager =
                Factory.getSocialSite().getProfileManager();
            Profile profile = profileManager.getProfileByUserId(profileId);
            boolean isMember = groupManager.isMember(group, profile);

            if (mustBeMember && !isMember) {
                addFieldErrorWithMessage(fieldName, o,
                    "socialsite.form.group.userIsNotMember",
                    profileId, group.getHandle());
            } else if (!mustBeMember && isMember) {
                addFieldErrorWithMessage(fieldName, o,
                    "socialsite.form.group.userIsMember",
                    profileId, group.getHandle());
            }
        } catch (SocialSiteException sse) {
            addActionError(sse.getMessage());
        }
    }

    public boolean isMustBeMember() {
        return mustBeMember;
    }

    public void setMustBeMember(boolean mustBeMember) {
        this.mustBeMember = mustBeMember;
    }

    private Group getGroup(GroupManager gm, Object o)
        throws SocialSiteException, ValidationException {

        String groupId = (String) getFieldValue("groupid", o);
        if (groupId != null && groupId.trim().length() > 0) {
            return gm.getGroupById(groupId);
        }
        
        String handle = (String) getFieldValue("handle", o);
        if (handle != null && handle.trim().length() > 0) {
            return gm.getGroupByHandle(handle);
        }

        return null;
    }

}
