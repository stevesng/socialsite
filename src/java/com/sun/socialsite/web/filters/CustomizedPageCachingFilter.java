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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.ResponseHeadersNotModifiableException;
import net.sf.ehcache.constructs.web.PageInfo;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Extends the SimplePageCachingFilter from ehcache to add support for
 * conditional GETs.
 */
public class CustomizedPageCachingFilter extends SimplePageCachingFilter {

    public static final String NAME = "CustomizedPageCachingFilter";

    private static final Log log = LogFactory.getLog(CustomizedPageCachingFilter.class);

    private boolean enabled = false;


    @Override
    public void doInit(FilterConfig filterConfig) throws CacheException {
        enabled = Config.getBooleanProperty("socialsite.http.pagecache.enabled");
        log.info("Page Cache " + (enabled ? "enabled" : "disabled"));
        super.doInit(filterConfig);
    }


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws Exception
    {
        if (enabled) {
            response.reset();
            //response.setHeader("Cache-Control", "max-age=0, must-revalidate");
            super.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }


    @Override
    protected String calculateKey(final HttpServletRequest req) {

        StringBuffer sb;
        String queryString = null;

        /*
         * We want our keys to be absolute URLs (with scheme, hostname, etc) so that
         * they'll line-up with what our URLStrategy uses (which makes it easier for
         * other code to invalidate cache entries when an entity is updated).
         */

        if (req.getAttribute("javax.servlet.include.request_uri") != null) {
            sb = new StringBuffer();
            sb.append(req.getMethod());
            sb.append("|");
            sb.append(req.getScheme()).append("://").append(req.getServerName());
            switch (req.getServerPort()) {
                case 80:
                    if (req.getScheme().equalsIgnoreCase("http")) {
                        break;
                    }
                case 443:
                    if (req.getScheme().equalsIgnoreCase("https")) {
                        break;
                    }
                default:
                    sb.append(":").append(req.getServerPort());
            }
            sb.append((String)(req.getAttribute("javax.servlet.include.request_uri")));
            queryString = (String) (req.getAttribute("javax.servlet.include.query_string"));
        } else {
            sb = req.getRequestURL();
            queryString = req.getQueryString();
        }

        if (queryString != null) {
            sb.append("?").append(queryString);
        }

        String key = sb.toString();
        log.trace("key="+key);

        return key;
    }


    @Override
    protected String getCacheName() {
        return NAME;
    }


    @Override
    protected PageInfo buildPage(final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain)
            throws AlreadyGzippedException, Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GenericResponseWrapper wrappedResponse = new GenericResponseWrapper(response, out);
        chain.doFilter(request, wrappedResponse);
        wrappedResponse.flush();
        return new ExtendedPageInfo(wrappedResponse, out.toByteArray(), true);
    }


    @Override
    protected void writeResponse(final HttpServletRequest request,
            final HttpServletResponse response,
            final PageInfo pageInfo)
            throws IOException, DataFormatException, ResponseHeadersNotModifiableException
    {
        if (pageInfo instanceof ExtendedPageInfo) {
            ExtendedPageInfo extendedPageInfo = (ExtendedPageInfo) pageInfo;

            if (extendedPageInfo.clientCanUseLocalCopy(request)) {
                log.trace("Telling client to use local copy: " + request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }
        super.writeResponse(request, response, pageInfo);
    }

}
