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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AnonymousSecurityToken;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.auth.SecurityTokenException;


/**
 * A SecurityTokenDecoder implementation that "decodes" a token string by
 * looking up the associated object from an in-memory map.
 */
public class SocialSiteTokenDecoder implements SecurityTokenDecoder {

    private static Log log = LogFactory.getLog(SocialSiteTokenDecoder.class);

    private static Map<String, SecurityToken> tokens = new ConcurrentHashMap<String, SecurityToken>();


    public static void addToken(SecurityToken token) {
        SocialSiteToken sstoken = (SocialSiteToken)token;
        tokens.put(sstoken.toSerialForm(), token);
    }


    public static void removeToken(SecurityToken token) {
        SocialSiteToken sstoken = (SocialSiteToken)token;
        tokens.remove(sstoken.toSerialForm());
    }


    /**
     *
     */
    public SocialSiteTokenDecoder() {
        log.trace("Entered Constructor");
    }


    /**
     * {@inheritDoc}
     */
    public SecurityToken createToken(Map<String, String> parameters) throws SecurityTokenException {

        String tokenString = parameters.get(SecurityTokenDecoder.SECURITY_TOKEN_NAME);
        SecurityToken token = null;

        if (tokenString == null || tokenString.length() == 0) {
            token = new AnonymousSecurityToken();
            log.debug("returning anonymous token " + token);
        } else {
            token = tokens.get(tokenString);
            log.debug("found token " + token);
        }

        return token;

    }

}
