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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.sf.ehcache.constructs.web.AlreadyGzippedException;
import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.PageInfo;


/**
 * Extends an ehcache PageInfo object to ease support for conditional
 * GETs.
 */
class ExtendedPageInfo extends PageInfo implements Serializable {

    private static final long serialVersionUID = 0L;
    private Long lastModifiedTime = 0L;
    private String eTag;

    /**
     * Constructor.
     */
    public ExtendedPageInfo(final GenericResponseWrapper resp, final byte[] body, boolean storeGzipped)
            throws AlreadyGzippedException {
        super(resp.getStatus(), resp.getContentType(), resp.getHeaders(), resp.getCookies(), body, storeGzipped);
        determineLastModified(resp);
        determineEtag(resp);
    }

    /**
     * Returns true if <code>req</code> represents a conditional
     * GET which does not require a full response--false otherwise.
     */
    public boolean clientCanUseLocalCopy(HttpServletRequest req) {
        boolean methodPassed = false;
        boolean comparisonsPassed = false;

        long ifModifiedSinceTime = req.getDateHeader("If-Modified-Since");
        String ifNoneMatchHeader = req.getHeader("If-None-Match");

        if (("GET".equals(req.getMethod())) || ("HEAD".equals(req.getMethod()))) {
            methodPassed = true;
        }

        // prefer last-modified over etags
        if (ifModifiedSinceTime != -1) {
            comparisonsPassed = lastModifiedTime <= ifModifiedSinceTime;
        } else if (ifNoneMatchHeader != null && eTag != null) {
            comparisonsPassed = (eTag.equals(ifNoneMatchHeader));
        }
        return (methodPassed && comparisonsPassed);
    }

    private void determineLastModified(GenericResponseWrapper resp) {
        String lastModifiedString = getHeader("Last-Modified");
        if (lastModifiedString != null) {
            try {
                lastModifiedTime = getDateFormat().parse(lastModifiedString).getTime();
            } catch (ParseException pe) {
                lastModifiedTime = 0L;
            }
        } else {
        }
    }

    private void determineEtag(GenericResponseWrapper resp) {
        eTag = getHeader("ETag");
    }

    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
    }

    /**
     * Note: using SuppressWarnings annotation because the ehcache API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    private String getHeader(String name) {
        List<String[]> headers = getHeaders();
        for (String[] header : headers) {
            if ((header.length == 2) && (name.equals(header[0]))) {
                return header[1];
            }
        }
        return null;
    }

}
