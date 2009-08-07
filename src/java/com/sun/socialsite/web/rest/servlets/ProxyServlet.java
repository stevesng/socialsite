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

package com.sun.socialsite.web.rest.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Proxies requests to some other URL.
 */
public class ProxyServlet extends HttpServlet {
    private static final long serialVersionUID = 0L;
    private static Log log = LogFactory.getLog(ProxyServlet.class);

    private static final Set<String> unproxiedHeaders = new HashSet<String>();
    static {
        // Be sure all entries are lowercase
        unproxiedHeaders.add("connection");
        unproxiedHeaders.add("cookie");
        unproxiedHeaders.add("host");
        unproxiedHeaders.add("keep-alive");
        unproxiedHeaders.add("user-agent");
    }

    /**
     * Public constructor.
     */
    public ProxyServlet() {
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param req servlet request
     * @param resp servlet response
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {

            URL url = getURL(req, req.getParameter("uri"));
            HttpURLConnection con = (HttpURLConnection)(url.openConnection());
            con.setAllowUserInteraction(false);
            con.setUseCaches(false);

            // TODO: figure out why this is necessary for HTTPS URLs
            if (con instanceof HttpsURLConnection) {
                HostnameVerifier hv = new HostnameVerifier() {
                    public boolean verify(String urlHostName, SSLSession session) {
                        if ("localhost".equals(urlHostName) && "127.0.0.1".equals(session.getPeerHost())) {
                            return true;
                        } else {
                            log.error("URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                            return false;
                        }
                    }
                };
                ((HttpsURLConnection)con).setDefaultHostnameVerifier(hv);
            }
            // pass along all appropriate HTTP headers
            Enumeration headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String hname = (String)headerNames.nextElement();
                if (! unproxiedHeaders.contains(hname.toLowerCase())) {
                    con.addRequestProperty(hname, req.getHeader(hname));
                }
            }
            con.connect();

            // read result headers of GET, write to response
            Map<String, List<String>> headers = con.getHeaderFields();
            for (String key : headers.keySet()) {
                if (key != null) { // TODO: why is this check necessary!
                    List<String> header = headers.get(key);
                    if (header.size() > 0) resp.setHeader(key, header.get(0));
                }
            }

            InputStream in = con.getInputStream();
            OutputStream out = resp.getOutputStream();
            final byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param req servlet request
     * @param resp servlet response
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        URL url = getURL(req, req.getParameter("uri"));
        HttpURLConnection con = (HttpURLConnection)(url.openConnection());
        con.setDoOutput(true);
        con.setAllowUserInteraction(false);
        con.setUseCaches(false);

        // TODO: figure out why this is necessary for HTTPS URLs
        if (con instanceof HttpsURLConnection) {
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    if ("localhost".equals(urlHostName) && "127.0.0.1".equals(session.getPeerHost())) {
                        return true;
                    } else {
                        log.error("URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                        return false;
                    }
                }
            };
            ((HttpsURLConnection)con).setDefaultHostnameVerifier(hv);
        }
        // pass along all appropriate HTTP headers
        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String hname = (String)headerNames.nextElement();
            if (! unproxiedHeaders.contains(hname.toLowerCase())) {
                con.addRequestProperty(hname, req.getHeader(hname));
            }
        }
        con.connect();

        // read POST data from incoming request, write to outgoing request
        BufferedInputStream in =
            new BufferedInputStream(req.getInputStream());
        BufferedOutputStream out =
            new BufferedOutputStream(con.getOutputStream());
        byte buffer[] = new byte[8192];
        for (int count = 0; count != -1;) {
            count = in.read(buffer, 0, 8192);
            if (count != -1) out.write(buffer, 0, count);
        }
        in.close();
        out.close();
        out.flush();

        // read result headers of POST, write to response
        Map<String, List<String>> headers = con.getHeaderFields();
        for (String key : headers.keySet()) {
            if (key != null) { // TODO: why is this check necessary!
                List<String> header = headers.get(key);
                if (header.size() > 0) resp.setHeader(key, header.get(0));
            }
        }

        // read result data of POST, write out to response
        in = new BufferedInputStream(con.getInputStream());
        out = new BufferedOutputStream(resp.getOutputStream());
        for (int count = 0; count != -1;) {
            count = in.read(buffer, 0, 8192);
            if (count != -1) out.write(buffer, 0, count);
        }
        in.close();
        out.close();
        out.flush();

        con.disconnect();
    }

    private URL getURL(HttpServletRequest req, String uriString)
            throws MalformedURLException {
        String s = uriString.toLowerCase();
        if ((s.startsWith("http://")) || (s.startsWith("https://"))) {
            return new URL(uriString);
        } else if (s.startsWith("/")) {
            return new URL(req.getScheme(), req.getServerName(), req.getServerPort(), uriString);
        } else {
            return new URL(req.getScheme(), req.getServerName(), req.getServerPort(), (req.getContextPath() + "/" + uriString));
        }
    }
}
