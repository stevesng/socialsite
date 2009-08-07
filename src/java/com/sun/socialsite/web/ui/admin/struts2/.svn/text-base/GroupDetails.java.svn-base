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
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Group edit action.
 */
public class GroupDetails extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(GroupDetails.class);
    private String groupId = null;
    private Group group = null;
    private File image;
    private String imageContentType;
    private String imageFileName;

    public GroupDetails() {
        setPageTitle("GroupDetails.pageTitle");
        this.desiredMenu = "admin";
    }

    @Override
    public void prepare() {
        setPageTitle("GroupDetails.pageTitle");
        this.desiredMenu = "admin";
    }

    @Override
    public String execute() {
        if (groupId != null) {
            try {
                com.sun.socialsite.business.GroupManager mgr = Factory.getSocialSite().getGroupManager();
                group = mgr.getGroupById(groupId);
            } catch (SocialSiteException e) {
                String msg = String.format("Failed to process groupId: %s", groupId);
                log.error(msg, e);
            }
        }
        return INPUT;
    }

    public String save() throws FileNotFoundException, IOException, SocialSiteException {
        com.sun.socialsite.business.GroupManager mgr = Factory.getSocialSite().getGroupManager();
        Group origGroup = mgr.getGroupById(groupId);
        if ((image != null) && (imageContentType != null)) {
            if (image.length() > Integer.MAX_VALUE) {
                throw new SocialSiteException(String.format("Image is too large (%d)", image.length()));
            }

            byte[] imageBytes = new byte[(int) (image.length())];
            int totalRead = 0;
            int currentRead = 0;
            FileInputStream in = new FileInputStream(image);
            while ((totalRead < imageBytes.length) && (currentRead = in.read(imageBytes, totalRead, imageBytes.length - totalRead)) >= 0) {
                totalRead += currentRead;
            }
            in.close();

            if (totalRead < imageBytes.length) {
                throw new SocialSiteException(String.format("Image is truncated (%d<%d)", totalRead, imageBytes.length));
            }

            origGroup.setImage(imageBytes);
            origGroup.setImageType(imageContentType);
        }
        origGroup.setName(group.getName());
        origGroup.setDescription(group.getDescription());
        mgr.saveGroup(origGroup);
        Factory.getSocialSite().flush();
        return SUCCESS;
    }

    public String getGroupid() {
        return groupId;
    }

    public void setGroupid(String groupid) {
        this.groupId = groupid;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


    public void setImage(File image) {
        this.image = image;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

}
