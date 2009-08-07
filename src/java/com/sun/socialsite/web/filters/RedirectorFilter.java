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
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
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


/**
 * A filter which ensures that clients use consistent URLs to access our app.
 * This is important so that the same cookie domain is always available, etc.
 * It will also ensure that users are redirected back to our HTTP URL(s) after
 * being sent to an HTTPS URL for login (assuming that an HTTP URL is our base).
 */
public class RedirectorFilter implements Filter {

    private static Log log = LogFactory.getLog(RedirectorFilter.class);

    // TODO: Make this configurable?
    private static Collection<String> exceptions = new ArrayList<String>();
    static {
        exceptions.add("/gadgets/");
        exceptions.add("/social/rest");
        exceptions.add("/social/rpc");
        exceptions.add("/js/");
        exceptions.add("/local_gadgets/");
        exceptions.add("/gadgetizerdata");
        exceptions.add("/app-ui/login");
        exceptions.add("/app-ui/logout");
        exceptions.add("/app-ui/images/");
        exceptions.add("/app-ui/themes/");
        exceptions.add("/app-ui/thumbnails/");
        exceptions.add("/uploads");
    }

    private URI baseUri;
    private int basePort;


    public void init(FilterConfig filterConfig) {
        try {
            String s = Config.getProperty("socialsite.base.url");
            if (s != null) {
                s = s.replaceAll("/$", ""); // eliminate trailing slashes
                baseUri = new URI(s);
                basePort = baseUri.getPort();
                if (basePort == -1) {
                    if ("http".equals(baseUri.getScheme())) basePort = 80;
                    if ("https".equals(baseUri.getScheme())) basePort = 443;
                }
            }
        } catch (Exception e) {
            log.error("Failed to set baseUri", e);
        }
    }


    /**
     *
     */
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;

        String redirectionUri = getRedirectionUri(httpReq);
        if (redirectionUri == null) {
            chain.doFilter(req, resp);
        } else {
            httpResp.sendRedirect(redirectionUri);
        }

    }


    public void destroy() {
    }


    private String getUrlAndQueryString(HttpServletRequest httpReq) {
        String uri = httpReq.getRequestURI();
        String query = httpReq.getQueryString();
        return (uri + ((query != null) ? ("?"+query) : ""));
    }


    private String getRedirectionUri(HttpServletRequest httpReq) {

        if (baseUri == null) {
            return null;
        }

        String serverName = httpReq.getServerName();
        String scheme = httpReq.getScheme();
        int port = httpReq.getServerPort();

        for (String exception : exceptions) {
            if (httpReq.getServletPath().startsWith(exception)) {
                return null;
            }
        }

        if (serverName.equalsIgnoreCase(baseUri.getHost()) && scheme.equalsIgnoreCase(baseUri.getScheme()) && (port == basePort)) {
            return null;
        } else {
            log.debug(String.format("Redirecting Because ((%s != %s) || (%s != %s) || (%d != %d))",
                serverName, baseUri.getHost(), scheme, baseUri.getScheme(), port, basePort));
            StringBuilder sb = new StringBuilder(baseUri.toString());
            if (httpReq.getServletPath() != null) sb.append(httpReq.getServletPath());
            if (httpReq.getPathInfo() != null) sb.append(httpReq.getPathInfo());
            if (httpReq.getQueryString() != null) sb.append("?").append(httpReq.getQueryString());
            return sb.toString();
        }
    }

}
