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

package com.sun.socialsite.web.rest.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.security.HttpPermission;
import java.io.IOException;
import java.security.Permission;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AuthInfo;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.http.RequestPipeline;
import org.apache.shindig.gadgets.rewrite.ContentRewriterRegistry;
import org.apache.shindig.gadgets.servlet.MakeRequestHandler;


/**
 * Ensure that HttpPermission checks are performed before retrieving a resource.
 */
@Singleton
public class RestrictedMakeRequestHandler extends MakeRequestHandler {

    private static Log log = LogFactory.getLog(RestrictedMakeRequestHandler.class);

    @Inject
    public RestrictedMakeRequestHandler(RequestPipeline pipeline, ContentRewriterRegistry crr) {
        super(pipeline, crr);
    }

    @Override
    public void fetch(HttpServletRequest request, HttpServletResponse response) throws GadgetException, IOException {
        super.fetch(request, response);
    }

    /**
     * TODO: enable this (once token access issues are addressed).
     */
    public void __fetch(HttpServletRequest request, HttpServletResponse response) throws GadgetException, IOException {
        log.debug("Entered");
        Permission requiredPermission = new HttpPermission(request.getParameter(URL_PARAM), request.getMethod());
        try {
            SecurityToken token = new AuthInfo(request).getSecurityToken();
            log.debug("token="+token);
            Factory.getSocialSite().getPermissionManager().checkPermission(requiredPermission, token);
            super.fetch(request, response);
        } catch (SecurityException e) {
            log.debug("Permission Denied", e);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e);
        } catch (Exception e) {
            log.error("Unexpected Exception", e);
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e);
        }
    }

}
