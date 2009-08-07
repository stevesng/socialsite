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

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AbstractManagerImpl;
import com.sun.socialsite.business.InitializationException;
import com.sun.socialsite.business.ThemeSettingsManager;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.config.runtime.ConfigDef;
import com.sun.socialsite.config.runtime.DisplayGroup;
import com.sun.socialsite.config.runtime.PropertyDef;
import com.sun.socialsite.config.runtime.RuntimeConfigDefs;
import com.sun.socialsite.pojos.ThemeSettings;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JPA implementation of ThemeSettings manager.
 */
@Singleton
public class JPAThemeSettingsManagerImpl extends AbstractManagerImpl implements ThemeSettingsManager {

    private static Log log = LogFactory.getLog(JPAThemeSettingsManagerImpl.class);

    private final JPAPersistenceStrategy strategy;


    @Inject
    protected JPAThemeSettingsManagerImpl(JPAPersistenceStrategy strat) {
        log.debug("Instantiating JPA ThemeSettings Manager");
        this.strategy = strat;
    }

    public void release() {}

    public void createThemeSettings(String destination, String anchorColor,
            String bgColor, String bgImage, String fontColor) throws SocialSiteException {
        if(getThemeSettingsByDestination(destination) != null) {
            throw new SocialSiteException("ERROR : A destination with name " + destination +
                    " has already been set");
        }
        ThemeSettings t = new ThemeSettings();
        t.setDestination(destination);
        t.setAnchorColor(anchorColor);
        t.setBackgroundColor(bgColor);
        t.setBackgroundImage(bgImage);
        t.setFontColor(fontColor);
        saveThemeSettings(t);
    }

    public void saveThemeSettings(ThemeSettings theme) throws SocialSiteException {
        strategy.store(theme);
    }

    public void removeThemeSettings(ThemeSettings theme) throws SocialSiteException {
        strategy.remove(theme);
    }

    public void removeThemeSettings(String destination) throws SocialSiteException {
        ThemeSettings t = getThemeSettingsByDestination(destination);
        if(t == null) {
            throw new SocialSiteException("ERROR : A destination with name " + destination +
                    " is not present");
        }
        removeThemeSettings(t);
    }

    public ThemeSettings getThemeSettingsByDestination(String id) throws SocialSiteException {
        if (id == null) {
            throw new SocialSiteException("id is null");
        }
        Query query = strategy.getNamedQuery("ThemeSettings.getByDestination");
        query.setParameter(1, id);
        try {
            return (ThemeSettings)query.getSingleResult();
        } catch (NonUniqueResultException ne) {
            throw new SocialSiteException("ERROR: more than one destination with id: " + id, ne);
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<ThemeSettings> getThemeSettings() throws SocialSiteException {
        Query query = strategy.getNamedQuery("ThemeSettings.getAll");
        return (List<ThemeSettings>)query.getResultList();
    }

}
