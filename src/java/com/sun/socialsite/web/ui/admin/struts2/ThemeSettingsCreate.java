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
import com.sun.socialsite.business.ThemeSettingsManager;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.pojos.ThemeSettings;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Theme settings create action. Also used for editing an existing action.
 */
public class ThemeSettingsCreate extends CustomizedActionSupport {

    private static Log log = LogFactory.getLog(ThemeSettingsCreate.class);

    // runtime config keys
    private static final String DEF_ANCHOR_COLOR = "gadget.anchorColor";
    private static final String DEF_BG_COLOR = "gadget.bgColor";
    private static final String DEF_BG_IMAGE = "gadget.bgImage";
    private static final String DEF_FONT_COLOR = "gadget.fontColor";

    private ThemeSettings themeSettings;
    
    // used for editing existing settings
    private String dest;

    public ThemeSettingsCreate() {
        this.setPageTitle("ThemeSettingsCreate.pageTitle");
        this.desiredMenu = "admin";
    }

    @Override
    public void prepare() {
        setPageTitle("ThemeSettingsCreate.pageTitle");
    }

    @Override
    public String execute() {
        if (themeSettings == null) {
            if (dest != null) {
                ThemeSettingsManager tMan =
                    Factory.getSocialSite().getThemeSettingsManager();
                try {
                    themeSettings = tMan.getThemeSettingsByDestination(dest);
                } catch (SocialSiteException sse) {
                    log.error("ERROR retrieving theme settings", sse);
                    this.setError("ThemeSettings.error", sse.getMessage());
                    return ERROR;
                }
            }
        }
        return INPUT;
    }

    /*
     * If the 'dest' param is set, then an existing
     * theme settings is being edited. If someone changes the
     * destination however, make changes to existing settings
     * if they exist. So we will ignore the 'dest' param and
     * check for an existing destination by the name included
     * in the form.
     */
    public String save() {
        try {
            ThemeSettingsManager tsMan =
                Factory.getSocialSite().getThemeSettingsManager();
            ThemeSettings ts = tsMan.getThemeSettingsByDestination(
                themeSettings.getDestination());

            if (ts == null) {
                // this is a new group of settings
                tsMan.saveThemeSettings(themeSettings);
            } else {
                // edit existing settings and save
                ts.setAnchorColor(themeSettings.getAnchorColor());
                ts.setBackgroundColor(
                    themeSettings.getBackgroundColor());
                ts.setBackgroundImage(
                    themeSettings.getBackgroundImage());
                ts.setFontColor(themeSettings.getFontColor());
                tsMan.saveThemeSettings(ts);
            }
            Factory.getSocialSite().flush();
            return SUCCESS;
        } catch (SocialSiteException snex) {
            return ERROR;
        }
    }

    public ThemeSettings getThemeSettings() {
        return themeSettings;
    }

    public void setThemeSettings(ThemeSettings themeSettings) {
        this.themeSettings = themeSettings;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

}
