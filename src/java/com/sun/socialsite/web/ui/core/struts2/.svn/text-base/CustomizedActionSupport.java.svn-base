/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.web.ui.core.struts2;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.ui.admin.menu.Menu;
import com.sun.socialsite.web.ui.admin.menu.MenuHelper;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Extends the Struts2 ActionSupport class to add in support for handling an
 * error and status success.  Other actions extending this one only need to
 * calle setError() and setSuccess() accordingly.
 *
 * NOTE: as a small convenience, all errors and messages are assumed to be keys
 * which point to a success in a resource bundle, so we automatically call
 * getText(key) on the param passed into setError() and setSuccess().
 */
public abstract class CustomizedActionSupport extends ActionSupport implements Preparable {

    private static Log log = LogFactory.getLog(CustomizedActionSupport.class);

    // status params
    private String error = null;
    private String warning = null;
    private String success = null;

    protected String userId = null;
    protected Profile viewerProfile = null;

    // the name of the menu this action wants to show, or null for no menu
    protected String desiredMenu = null;

    // action name (used by tabbed menu utility)
    protected String actionName = null;

    // page title
    private String pageTitle = null;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = getText(error);
    }

    public void setError(String error, String param) {
        this.error = getText(error, error, param);
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = getText(warning);
    }

    public void setWarning(String warning, String param) {
        this.warning = getText(warning, warning, param);
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String message) {
        this.success = getText(message);
    }

    public void setSuccess(String message, String param) {
        this.success = getText(message, message, param);
    }

    public String getPageTitle() {
        return getText(pageTitle);
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void prepare() throws Exception {
        // no-op
    }

    public String getProp(String key) {
        // first try static config
        String value = Config.getProperty(key);
        if (value == null) {
            value = RuntimeConfig.getProperty(key);
        }
        return (value == null) ? key : value;
    }

    public boolean getBooleanProp(String key) {
        // first try static config
        String value = Config.getProperty(key);
        if (value == null) {
            value = RuntimeConfig.getProperty(key);
        }
        return (value == null) ? false : (new Boolean(value)).booleanValue();
    }

    public int getIntProp(String key) {
        // first try static config
        String value = Config.getProperty(key);
        if (value == null) {
            value = RuntimeConfig.getProperty(key);
        }
        return (value == null) ? 0 : (new Integer(value)).intValue();
    }

    /** The current authenticated user */
    public String getUserId() {
        return userId;
    }

    /** The profile of the current authenticated user */
    public Profile getViewerProfile() {
        return viewerProfile;
    }

    public void addError(String errorKey) {
        addActionError(getText(errorKey));
    }

    public void addError(String errorKey, String param) {
        addActionError(getText(errorKey, errorKey, param));
    }

    public void addError(String errorKey, List args) {
        addActionError(getText(errorKey, args));
    }

    public void addMessage(String msgKey) {
        addActionMessage(getText(msgKey));
    }

    public void addMessage(String msgKey, String param) {
        addActionMessage(getText(msgKey, msgKey, param));
    }

    public void addMessage(String msgKey, List args) {
        addActionMessage(getText(msgKey, args));
    }

    public String getActionName() {
        if (this.actionName == null) {
            return getClass().getSimpleName();
        }
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getDesiredMenu() {
        return desiredMenu;
    }

    public void setDesiredMenu(String desiredMenu) {
        this.desiredMenu = desiredMenu;
    }

    public Menu getMenu() {
        return MenuHelper.getMenu(getDesiredMenu(), getActionName());
    }

}
