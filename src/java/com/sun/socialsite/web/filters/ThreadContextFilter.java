/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.web.filters;

import com.google.inject.Injector;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.SocialSite;
import com.sun.socialsite.business.URLStrategy;
import com.sun.socialsite.util.ProxyUtil;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import com.sun.socialsite.web.rest.opensocial.SocialSiteTokenDecoder;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shindig.common.servlet.GuiceServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.auth.SecurityTokenException;


/**
 * Performs setup and teardown of any per-thread context for a web
 * request.  This currently involves the following:
 * <ul>
 *  <li>
 *   Ensuring that each request's SocialSite persistence session
 *   is released at end of the request.
 *  </li>
 *  <li>
 *   If a SocialSiteToken with a custom URLStrategy is included
 *   in the current request, set that URLStrategy as the default
 *   for the current thread (and then clear it at the end of the
 *   request).
 *  </li>
 * </ul>
 */
public class ThreadContextFilter implements Filter {

    private static Log log = LogFactory.getLog(ThreadContextFilter.class);

    private static SocialSiteTokenDecoder decoder;


    /**
     * Release SocialSite persistence session at end of request processing.
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        log.debug("Entered ThreadContextFilter");

        SocialSite socialsite = Factory.getSocialSite();

        log.debug(String.format("url=%s decoder=%s st=%s", request.getRequestURL(), decoder, request.getParameter("st")));
        if ((decoder != null) && (request.getParameter("st") != null)) {
            Map<String, String> m = Collections.singletonMap(SecurityTokenDecoder.SECURITY_TOKEN_NAME, request.getParameter("st"));
            try {
                final SocialSiteToken token = (SocialSiteToken)(decoder.createToken(m));
                log.debug(String.format("url=%s token=%s", request.getRequestURL(), token));
                if ((token != null) && (!socialsite.getURLStrategy().equals(token.getURLStrategy()))) {
                    Object overrider = new Object() {
                        public URLStrategy getURLStrategy() {
                            log.debug(String.format("url=%s token=%s urlstrategy=%s", request.getRequestURL(), token, token.getURLStrategy()));
                            return token.getURLStrategy();
                        }
                    };
                    SocialSite ss = (SocialSite)(ProxyUtil.getOverrideProxy(socialsite, overrider));
                    log.debug(String.format("url=%s ss=%s", request.getRequestURL(), ss));
                    Factory.setThreadLocalSocialSite((SocialSite)(ProxyUtil.getOverrideProxy(socialsite, overrider)));
                    log.debug(String.format("url=%s Factory.getSocialSite=%s", request.getRequestURL(), Factory.getSocialSite()));
                }
            } catch (SecurityTokenException e) {
                log.error("Failed to get security token", e);
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            log.debug("Releasing SocialSite Session");
            if (Factory.isBootstrapped()) {
                Factory.setThreadLocalSocialSite(null);
                Factory.getSocialSite().release();
            }
        }

        log.debug("Exiting ThreadContextFilter");
    }


    /**
     * Initializes this filter.
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        Injector injector = (Injector)(context.getAttribute(GuiceServletContextListener.INJECTOR_ATTRIBUTE));
        SecurityTokenDecoder securityTokenDecoder = injector.getInstance(SecurityTokenDecoder.class);
        if (securityTokenDecoder instanceof SocialSiteTokenDecoder) {
            this.decoder = (SocialSiteTokenDecoder)securityTokenDecoder;
        }
    }


    /**
     * Destroys this filter.
     */
    public void destroy() {
    }

}
