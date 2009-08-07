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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.userapi.UserManager;
import com.sun.socialsite.util.PropertyExpander;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.oauth.OAuthStore;


/**
 * Guice module for configuring JPA as SocialSite backend.
 */
public class JPASocialSiteModule implements Module {

    private static Log log = LogFactory.getLog(JPASocialSiteModule.class);

    /**
     * Configures bindings which are appropriate for a JPA-based SocialSite backend.
     */
    public void configure(Binder binder) {

        // We don't need many explicit bindings here, since they'd just mirror
        // the @ImplementedBy defaults which are set on each interface.

        // Read Shindig properties from standard SocialSite configuration file
        // Backend needs them too
        Properties shindigProperties =
            Config.getPropertiesStartingWith("shindig.");
        Properties properties = PropertyExpander.expandProperties(
            shindigProperties, Config.toMap(), null);

        if (log.isDebugEnabled()) {
            for (Object key : properties.keySet()) {
                log.debug(String.format("%s[%s]=%s",
                   "socialsite.properties", key, properties.get(key)));
            }
        }
        Names.bindProperties(binder, properties);

        binder.bind(UserManager.class).to(DelegatingUserManagerImpl.class);

        binder.bind(OAuthStore.class).to(JPAOAuthStore.class);

    }

}
