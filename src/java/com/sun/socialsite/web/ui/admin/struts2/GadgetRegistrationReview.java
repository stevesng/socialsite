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
import com.sun.socialsite.pojos.AppRegistration;
import com.sun.socialsite.web.ui.core.struts2.CustomizedActionSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.SpecParserException;
import org.apache.struts2.interceptor.ParameterAware;


/**
 * Gadget registration review: accept, reject or remove
 */
public class GadgetRegistrationReview 
        extends CustomizedActionSupport implements ParameterAware {

    private static Log log = LogFactory.getLog(GadgetRegistrationReview.class);
    private Map parameters = Collections.EMPTY_MAP;
    private AppRegistration appRegistration = null;
    private GadgetSpec gadgetSpec = null;
    private String id = null;
    private String comment = null;
    private boolean reviewComplete = false;


    public GadgetRegistrationReview() {
        setPageTitle("GadgetRegistrationReview.pageTitle");
        this.desiredMenu = "admin";
    }

    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }

    @Override
    public String execute() {
        try {
            com.sun.socialsite.business.AppManager amgr = Factory.getSocialSite().getAppManager();
            if (id == null) {
                String[] idArray = (String[])parameters.get("id");
                if (idArray == null) {
                    addError("GadgerRegistrationReview.needId");
                } else {
                    id = idArray[0];
                }
            }
            appRegistration = amgr.getAppRegistration(id);
            gadgetSpec = amgr.getGadgetSpecByURL(new URL(appRegistration.getAppUrl()));

        } catch (MalformedURLException ex) {
            // error parsing gadget spec url
            addError("GadgerRegistrationReview.badUrl");

        } catch (SocialSiteException ex) {
            if (ex.getCause() instanceof SpecParserException) {
                // error parsing gadget spec, could be 404 or bad XML
                // try to give as much info as possible
                addError("GadgerRegistrationReview.cannotParseGadgetSpec");
                if (ex.getCause() != null) {
                    addError(ex.getCause().getMessage());
                    if (ex.getCause().getCause() != null) {
                        addError(ex.getCause().getCause().getMessage());
                    }
                }
            } else {
                // error fetching app registration from database
                // try to give as much info as possible
                addError("GadgerRegistrationReview.cannotFindRegistration");
                if (ex.getCause() != null) {
                    addError(ex.getCause().getMessage());
                    if (ex.getCause().getCause() != null) {
                        addError(ex.getCause().getCause().getMessage());
                    }
                }
            }
        }
        return INPUT;
    }

    public AppRegistration getAppRegistration() {
        return appRegistration;
    }

    public GadgetSpec getGadgetSpec() {
        return gadgetSpec;
    }

    public String rejectAppRegistration() {
        try {
            com.sun.socialsite.business.AppManager amgr = Factory.getSocialSite().getAppManager();
            amgr.rejectAppRegistration(id, getComment());
            Factory.getSocialSite().flush();
            this.addMessage("GadgetRegistrationReview.rejected");
            reviewComplete = true;

        } catch (SocialSiteException ex) {
            return INPUT;
        }
        return SUCCESS;
    }

    public String approveAppRegistration() {
        try {
            com.sun.socialsite.business.AppManager amgr = Factory.getSocialSite().getAppManager();
            amgr.approveAppRegistration(id, getComment());
            Factory.getSocialSite().flush();
            this.addMessage("GadgetRegistrationReview.approved");
            reviewComplete = true;

        } catch (SocialSiteException ex) {
            return INPUT;
        }
        return SUCCESS;
    }

    public String removeAppRegistration() {
        try {
            com.sun.socialsite.business.AppManager amgr = Factory.getSocialSite().getAppManager();
            amgr.removeAppRegistration(id, getComment());
            Factory.getSocialSite().flush();
            this.addMessage("GadgetRegistrationReview.removed");
            reviewComplete = true;

        } catch (SocialSiteException ex) {
            return INPUT;
        }
        return SUCCESS;
    }

    public boolean getReviewComplete() {
        return reviewComplete;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return "";
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
