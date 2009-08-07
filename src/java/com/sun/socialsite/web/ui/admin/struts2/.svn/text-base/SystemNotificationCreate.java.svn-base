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
import com.sun.socialsite.business.NotificationManager;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;

/**
 * System notification create action.
 */
public class SystemNotificationCreate extends CustomizedActionSupport {

    private String subject = null;
    private String content = null;

    public SystemNotificationCreate() {
        this.setPageTitle("SystemNotificationCreate.pageTitle");
        this.desiredMenu = "admin";
    }

    @Override
    public String execute() {
        // nothing to do right now
        return INPUT;
    }

    public String save() {

        try {
            NotificationManager mgr =
                Factory.getSocialSite().getNotificationManager();
            mgr.recordSystemNotification(subject, content);
            Factory.getSocialSite().flush();
            return SUCCESS;
        } catch (SocialSiteException snex) {
            addError(snex.getMessage());
            return ERROR;
        }
    }

    @Override
    public void prepare() {
        setPageTitle("SystemNotificationCreate.pageTitle");
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
