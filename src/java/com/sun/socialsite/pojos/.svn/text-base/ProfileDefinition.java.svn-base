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

package com.sun.socialsite.pojos;

import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Defines the SocialSite personal Profile properties.
 */
@Singleton
public class ProfileDefinition extends PropDefinition {

    private Map<String, String> ssByOs = new HashMap<String, String>();
    private Map<String, String> osBySs = new HashMap<String, String>();

    private ProfileDefinition() throws SocialSiteException {
        super();
        init(this, getClass().getResourceAsStream("/profiledefs.xml"));

        Collection<DisplaySectionDefinition> sections = getDisplaySectionDefinitions();
        for (DisplaySectionDefinition section : sections) {
            
            Collection<PropertyDefinition> propDefs = section.getPropertyDefinitions();
            for (PropertyDefinition propDef : propDefs) {
                ssByOs.put(propDef.getShortName(), 
                        section.getShortName() + "_" + propDef.getShortName());
                osBySs.put(section.getShortName() + "_" + propDef.getShortName(), 
                        propDef.getShortName());
            }
            
            Collection<PropertyObjectDefinition> objectDefs = section.getPropertyObjectDefinitions();
            for (PropertyObjectDefinition objectDef : objectDefs) {
                for (PropertyDefinition propDef : objectDef.getPropertyDefinitions()) {
                    ssByOs.put(objectDef.getShortName() + "." + propDef.getShortName(), 
                            section.getShortName() + "_" + objectDef.getShortName() + "_" + propDef.getShortName());
                    osBySs.put(section.getShortName() + "_" + objectDef.getShortName() + "_" + propDef.getShortName(), 
                            objectDef.getShortName() + "." + propDef.getShortName());
                }
            }
            
        }
    }

    public String getOpenSocialFieldName(String profileFieldName) {
        return osBySs.get(profileFieldName);
    }

    public String getProfileFieldName(String openSocialFieldName) {
        return ssByOs.get(openSocialFieldName);
    }
}
