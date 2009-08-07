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
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.web.filter.SimplePageFragmentCachingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Extends the SimplePageFragmentCachingFilter from ehcache.
 */
public class CustomizedPageFragmentCachingFilter extends SimplePageFragmentCachingFilter {

    public static final String NAME = "CustomizedPageFragmentCachingFilter";

    private static final Log log = LogFactory.getLog(CustomizedPageFragmentCachingFilter.class);

    private boolean enabled = false;


    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {
        enabled = Config.getBooleanProperty("socialsite.http.fragmentcache.enabled");
        log.info("Page Fragment Cache " + (enabled ? "enabled" : "disabled"));
        super.doInit(filterConfig);
    }


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws Exception
    {
        if (enabled) {
            super.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }


    @Override
    protected String calculateKey(final HttpServletRequest req) {
        String uri = null;
        String query = null;

        if (req.getAttribute("javax.servlet.include.request_uri") != null) {
            uri = (String) (req.getAttribute("javax.servlet.include.request_uri"));
            query = (String) (req.getAttribute("javax.servlet.include.query_string"));
        } else {
            uri = req.getRequestURI();
            query = req.getQueryString();
        }

        // TODO: something better than this anonymous/loggedin workaround
        StringBuilder sb = new StringBuilder();
        sb.append((req.getRemoteUser() != null) ? "LOGGED_IN" : "ANONYMOUS");
        sb.append("|");
        sb.append(req.getMethod());
        sb.append("|");
        sb.append(uri);
        if (query != null) {
            sb.append("?").append(query);
        }

        String key = sb.toString();
        log.trace("key="+key);

        return key;
    }


    @Override
    protected String getCacheName() {
        return NAME;
    }

}
