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

package com.sun.socialsite.web.rest.opensocial;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ThemeSettingsManager;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.pojos.ThemeSettings;
import com.sun.socialsite.util.PropertyExpander;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.config.ContainerConfigException;
import org.apache.shindig.config.JsonContainerConfig;
import org.apache.shindig.expressions.Expressions;
import org.json.JSONObject;


/**
 * Extends Shindig's ContainerConfig so that property expansion is performed 
 * on configs. Values for expansion come from Config.
 */
public class ExpandingContainerConfig extends JsonContainerConfig {

    private static Log log = LogFactory.getLog(ExpandingContainerConfig.class);


    /**
     * Creates a new, empty configuration.
     */
    @Inject
    public ExpandingContainerConfig(
            @Named("shindig.containers.default") String containers,
            Expressions expressions) throws ContainerConfigException {
        super(containers, expressions);
        log.debug(String.format("%s constructed", this));
    }


    @Override
    public void loadFromString(String json, JSONObject all) throws ContainerConfigException {
        String expandedJson = PropertyExpander.expand(json, Config.toMap());
        if (log.isDebugEnabled()) {
            log.debug(String.format("expandedJson:%n----------%n%s%n----------%n", expandedJson));
        }
        super.loadFromString(expandedJson, all);
    }
   
 
    @Override
    public Object getProperty(String container, String parameter) {

        Object o = super.getProperty(container, parameter);
        
        if (log.isDebugEnabled()) {
            String msg = String.format("getJson(container=%s, parameter=%s) -> %s", container, parameter, o);
            log.debug(msg);
        }
        
        // TODO: remove this workaround once we have good support for configuring multiple containers
        if ((o == null) && (!"socialsite".equals(container))) {
            o = super.getProperty("socialsite", parameter);
            if (log.isDebugEnabled()) {
                String msg = String.format("getJson(container=%s, parameter=%s) -> %s", "socialsite", parameter, o);
                log.debug(msg);
            }
        }

        if (parameter.equals("gadgets.features")) {
            if (o instanceof JSONObject) {       
                // reset the gadget theme properties
                try {
                    ThemeSettingsManager tsm = Factory.getSocialSite().getThemeSettingsManager();
                    ThemeSettings ts = null;
                    ts = tsm.getThemeSettingsByDestination(container);
                    if (ts == null) {
                        // get default theme settings
                        ts = tsm.getThemeSettingsByDestination("socialsite");
                    }

                    JSONObject skinsObj = ((JSONObject)o).getJSONObject("skins");
                    JSONObject propObj = skinsObj.getJSONObject("properties");

                    propObj.put("ANCHOR_COLOR", ts.getAnchorColor());
                    propObj.put("BG_COLOR", ts.getBackgroundColor());
                    propObj.put("BG_IMAGE", ts.getBackgroundImage());
                    propObj.put("FONT_COLOR", ts.getFontColor());
                } catch (Exception ex) {
                    log.warn("Problems getting gadget skin properties. ", ex);
                }
            }
        }

        return o;
    }

}
