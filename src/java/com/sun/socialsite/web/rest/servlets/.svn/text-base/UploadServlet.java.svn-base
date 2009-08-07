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
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.security.FeaturePermission;
import com.sun.socialsite.util.Utilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AuthInfo;
import org.apache.shindig.auth.SecurityToken;

/**
 * Handles upload of profile and group images.
 * Support these URLs and 1) expects SocialSite token in 'st' parameter.
 * and 2) you must "own" the profile/group to upload an image to it.
 * <pre>
 * /uploads/user/{userId}   Allows POST of image data (GIF, JPG, PNG) for user
 * /uploads/group/{groupId} Allows POST of image data (GIF, JPG, PNG) for group
 * </pre>
 */
public class UploadServlet extends HttpServlet {

    private static Log log = LogFactory.getLog(UploadServlet.class);

    /** Calling Gadget/App needs permission to use SocialSite API */
    private static Permission requiredPerm = new FeaturePermission("socialsite-0.1");

    /** Only allow specific image content types */
    private static Set<String> types = new HashSet<String>();
    static {
        types.add("image/gif");
        types.add("image/png");
        types.add("image/jpg");
        types.add("image/jpeg");
    }

    /**
     * Note: using SuppressWarnings annotation because the Commons FileUpload API is
     * not genericized.
     */
    @Override
    @SuppressWarnings(value="unchecked") 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {

            // ensure calling app/gadget has perm to use SocialSite API
            SecurityToken token = new AuthInfo(req).getSecurityToken();
            Factory.getSocialSite().getPermissionManager().checkPermission(requiredPerm, token);

            GroupManager gmgr = Factory.getSocialSite().getGroupManager();
            ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
            int errorCode = -1;
            Group group = null;
            Profile profile = null;

            // parse URL to get route and subjectId
            String route = null;
            String subjectId = "";
            if (req.getPathInfo() != null) {
                String[] pathInfo = req.getPathInfo().split("/");
                route = pathInfo[1];
                subjectId = pathInfo[2];
            }

            // first, figure out destination profile or group and check the
            // caller's permission to upload an image for that profile or group

            if ("profile".equals(route)) {
                if (token.getViewerId().equals(subjectId)) {
                    profile = pmgr.getProfileByUserId(subjectId);
                } else {
                    errorCode = HttpServletResponse.SC_UNAUTHORIZED;
                }

            } else if ("group".equals(route)) {
                group = gmgr.getGroupByHandle(subjectId);
                if (group != null) {
                    // ensure called is group ADMIN or founder
                    Profile viewer = pmgr.getProfileByUserId(token.getViewerId());
                    GroupRelationship grel = gmgr.getMembership(group, viewer);
                    if (grel == null ||
                        (grel.getRelcode() != GroupRelationship.Relationship.ADMIN
                      && grel.getRelcode() != GroupRelationship.Relationship.FOUNDER)) {
                    } else {
                        errorCode = HttpServletResponse.SC_UNAUTHORIZED;
                    }
                } else {
                    // group not found
                    errorCode = HttpServletResponse.SC_NOT_FOUND;
                }
            }

            // next, parse out the image and save it in profile or group

            if (errorCode != -1 && group == null && profile == null) {
                errorCode = HttpServletResponse.SC_NOT_FOUND;

            } else if (errorCode == -1) {

                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                FileItem fileItem = null;
                List<FileItem> items = (List<FileItem>) upload.parseRequest(req);
                if (items.size() > 0) {
                    fileItem = items.get(0);
                }

                if ((fileItem != null) && (types.contains(fileItem.getContentType()))) {

                    // read incomining image via Commons Upload
                    InputStream is = fileItem.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Utilities.copyInputToOutput(is, baos);
                    byte[] byteArray = baos.toByteArray();

                    // save it in the profile or group indicated
                    if (profile != null) {
                        profile.setImageType(fileItem.getContentType());
                        profile.setImage(byteArray);
                        pmgr.saveProfile(profile);
                        Factory.getSocialSite().flush();

                    } else if (group != null) {
                        group.setImageType(fileItem.getContentType());
                        group.setImage(byteArray);
                        gmgr.saveGroup(group);
                        Factory.getSocialSite().flush();

                    } else {
                        // group or profile not indicated properly
                        errorCode = HttpServletResponse.SC_NOT_FOUND;
                    }
                }

            }

            if (errorCode == -1) {
                resp.sendError(HttpServletResponse.SC_OK);
                return;
            } else {
                resp.sendError(errorCode);
            }

        } catch (SecurityException sx) {
            log.error("Permission denied", sx);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

        } catch (FileUploadException fx) {
            log.error("ERROR uploading profile image", fx);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (SocialSiteException ex) {
            log.error("ERROR saving profile image", ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
