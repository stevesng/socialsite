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

package com.sun.socialsite.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Allows for customizable MessageResource-based formatting, similar
 * to how com.sun.socialsite.config.Config allows for customizable
 * properties.  Specifically, a default value for a given message pattern
 * can be defined in the file that is embdedded into the SocialSite jar
 * (at /com/sun/socialsite/socialsite_resources.properties), and
 * then that value can be overridden for a specific deployment by
 * placing a "socialsite_resources.properties" file into the base of
 * the classpath and populating it with a different value for that key.
 */
public class TextUtil {

    private static Log log = LogFactory.getLog(TextUtil.class);

    private static final String DEFAULT_BUNDLE_NAME = "com.sun.socialsite.socialsite_resources";

    private static final String CUSTOM_BUNDLE_NAME = "socialsite_resources";

    /**
     * Non-instantiable.
     */
    private TextUtil() {
    }

    public static String format(String messageKey, Object... arguments) {
        String message = getResourceString(messageKey);
        return MessageFormat.format(message, arguments);
    }

    public static String format(String messageKey, Locale locale, Object... arguments) {
        String message = getResourceString(messageKey, locale);
        return MessageFormat.format(message, arguments);
    }

    public static String getResourceString(String key) {
        return getResourceString(key, null);
    }

    public static String getResourceString(String key, Locale locale) {
        String value = null;
        ResourceBundle defaultBundle = null;
        ResourceBundle customBundle = null;

        try {
            defaultBundle = getDefaultBundle(locale);
        } catch (Exception e) {
            // better be a default bundle for default locale...
            defaultBundle = getDefaultBundle();
        }

        try {
            customBundle = getCustomBundle(locale);
        } catch (Exception e) {
            log.debug("ERROR getting custom bundle for locale: " + locale, e);
        }

        if (customBundle == null) try {
            customBundle = getCustomBundle();
        } catch (Exception e) {
            log.debug("ERROR getting custom bundle", e);
        }

        if (customBundle != null) try {
            value = customBundle.getString(key);
        } catch (MissingResourceException e) {
            log.debug("String " + key + " not found in custom bundle, locale=" + locale);
            value = defaultBundle.getString(key);
        } else try {
            value = defaultBundle.getString(key);
        } catch (Exception e) {
            log.error("String " + key + " not found in default bundle, locale=" + locale);
        }

        return value != null ? value : key;
    }

    private static ResourceBundle getDefaultBundle() {
        return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME);
    }

    private static ResourceBundle getCustomBundle() {
        return ResourceBundle.getBundle(CUSTOM_BUNDLE_NAME);
    }

    private static ResourceBundle getDefaultBundle(Locale locale) {
        return ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, locale);
    }

    private static ResourceBundle getCustomBundle(Locale locale) {
        return ResourceBundle.getBundle(CUSTOM_BUNDLE_NAME, locale);
    }

}
