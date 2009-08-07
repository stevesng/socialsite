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

import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.pojos.GroupDefinition;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;


/**
 * <p>Handles requests to GET Group Definition metadata.</p>
 *
 * <p>Supports these URIs and HTTP methods:</p>
 *    /groupdef - GET JSON representation of group definition metadata.<br />
 */
@Service(name = "groupdef", path="/{userid}/{groupId}")
public class GroupDefinitionHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(GroupDefinitionHandler.class);

    private static final String GROUPDEF_PATH = "/groupdef/{userid}/{groupId}";


    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem request) {
        log.trace("BEGIN");
        authorizeRequest(request);

        //request.applyUrlTemplate(GROUPDEF_PATH);
        ResponseItem res = null;
        try {
            GroupManager grpMgr = Factory.getSocialSite().getGroupManager();
            GroupDefinition def = grpMgr.getGroupDefinition();
            return ImmediateFuture.newInstance(def.toJSON());
        } catch (Exception ex) {
            String msg = "ERROR getting group definition";
            res = new ResponseItem(ResponseError.INTERNAL_ERROR, msg);
            log.error(msg, ex);
        }
        log.trace("END");
        return ImmediateFuture.newInstance(res);
    }
}
