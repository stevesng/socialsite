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

package com.sun.socialsite.web.filters;

import com.sun.socialsite.config.Config;
import com.sun.socialsite.web.rest.opensocial.AssertedToken;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AuthInfo;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.social.core.oauth.OAuthSecurityToken;


/**
 * If anonymous access not allowed then reject any request that does not
 * have either a SocialSite security token or an OAuth token.
 */
public class AnonymousAccessFilter implements Filter {
    private static Log log = LogFactory.getLog(AnonymousAccessFilter.class);
    private static boolean allowAnonymous = false;
    static {
        allowAnonymous = Config.getBooleanProperty("socialsite.services.anonymousAccess.allowed");
    }

    public void init(FilterConfig filterConfig) {
    }

    /**
     * If anonymous access not allowed then reject any request that does not
     * have either a SocialSite security token or an OAuth token.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        log.debug("--- entering");
        if (allowAnonymous) {
            chain.doFilter(req, res);
        } else {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            log.debug(request.getMethod() + " " + request.getRequestURL().toString());
            log.debug("st=" + request.getParameter("st"));

            SecurityToken st = new AuthInfo(request).getSecurityToken();
            if (st != null && (st instanceof SocialSiteToken 
                            || st instanceof OAuthSecurityToken
                            || st instanceof AssertedToken )) {
                chain.doFilter(req, res);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "No suitable security token found in request");
            }
        }
        log.debug("--- exiting");
    }

    public void destroy() {}
}
