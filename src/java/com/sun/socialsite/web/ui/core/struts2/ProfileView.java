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

package com.sun.socialsite.web.ui.core.struts2;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * ProfileView action.
 */
public class ProfileView extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(ProfileView.class);

    private Boolean isFriend = null;

    /** userId of owner of profile being viewed */
    private String ownerId = null;

    /** status of owner of profile being viewed */
    private String status = null;

    public String execute() {
        // If owner is not specifed, then show the current user's profile
        if (ownerId == null) {
            ownerId = getUserId();
        }
        return INPUT;
    }

    public void prepare() {
        setPageTitle("ProfileView.pageTitle");
    }


    // TODO: is there some way to get struts to automatically handle this for us?
    public void setEncodedOwnerId(String encodedOwnerId) {
        try {
            setOwnerId(URLDecoder.decode(encodedOwnerId, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            log.error("Failed to decode ownerId [" + encodedOwnerId + "]", ex);
        }
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isOwner() {
        return ownerId.equals(getUserId());
    }

}
