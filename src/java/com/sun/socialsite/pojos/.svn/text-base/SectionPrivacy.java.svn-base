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

import com.sun.socialsite.util.TextUtil;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Privacy setting for one section of a user's profile.
 */
public class SectionPrivacy {

    private static Log log = LogFactory.getLog(Profile.class);

    private String name;
    private String localizedName;
    private String namekey;
    private Profile.VisibilityType visibility;
    private Integer relationshipLevel = new Integer(1);
    private List<String> groups;

    public SectionPrivacy() {
    }

    /** Construct with same privacy settings as specified property */
    public SectionPrivacy(ProfileProperty prop, Locale locale) {
        if (prop.getName().indexOf("_") > 0) {
            this.name = prop.getName().substring(0, prop.getName().indexOf("_"));
        } else {
            this.name = prop.getName();
        }

        this.namekey     = prop.getNameKey();
        this.relationshipLevel = prop.getVisibilityLevel();
        this.visibility  = prop.getVisibility();
        this.groups      = prop.getSomeGroups();

        this.localizedName = TextUtil.getResourceString(this.namekey, locale);
    }

    /** Section name, uniquely identifies section */
    public String getSectionName() {
        return name;
    }

    /** Section name, uniquely identifies section */
    public void setSectionName(String name) {
        this.name = name;
    }

    /** Section name key, for looking up localized name */
    public String getNamekey() {
        return namekey;
    }

    /** Section name key, for looking up localized name */
    public void setNamekey(String namekey) {
        this.namekey = namekey;
    }

    /** Localized display name of section */
    public String getLocalizedName() {
        return localizedName;
    }

    /** Localized display name of section */
    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    /** Visibility level from PUBLIC to PRIVATE */
    public Profile.VisibilityType getVisibility() {
        return visibility;
    }

    /** Visibility level from PUBLIC to PRIVATE */
    public void setVisibility(Profile.VisibilityType visibility) {
        this.visibility = visibility;
    }

    /** List of group handles for the SOME_GROUPS case */
    public List<String> getSomeGroups() {
        return groups;
    }

    /** List of group handles for the SOME_GROUPS case */
    public void setSomeGroups(List<String> groups) {
        this.groups = groups;
    }

    public Integer getRelationshipLevel() {
        return relationshipLevel;
    }

    public void setRelationshipLevel(Integer rlevel) {
        this.relationshipLevel = rlevel;
    }

}
