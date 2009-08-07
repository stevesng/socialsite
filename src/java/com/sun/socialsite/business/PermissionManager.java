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

package com.sun.socialsite.business;

import com.google.inject.ImplementedBy;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.impl.JPAPermissionManagerImpl;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.PermissionGrant;
import com.sun.socialsite.pojos.Profile;
import java.security.Permission;
import java.util.List;
import org.apache.shindig.auth.SecurityToken;


/**
 * Manages Permissions.
 */
@ImplementedBy(JPAPermissionManagerImpl.class)
public interface PermissionManager extends Manager {

    /**
     * Throws a SecurityException if the specified token is not permitted the requested access
     * (as defined by the specified permission).
     * @param requiredPermission the specified permission.
     * @param token defines the security context in which access is being requested.
     * @throws SecurityException if the requested access is not permitted.
     */
    public void checkPermission(Permission requiredPermission, SecurityToken token) throws SocialSiteException;

    /**
     * Create or update the specified PermissionGrant in our underlying storage mechanism.
     */
    public void savePermissionGrant(PermissionGrant permissionGrant) throws SocialSiteException;

    /**
     * Delete the specified PermissionGrant from our underlying storage mechanism.
     */
    public void removePermissionGrant(PermissionGrant permissionGrant) throws SocialSiteException;
 
    /**
     * Gets the PermissionGrant having the specified id.
     */
    public PermissionGrant getPermissionGrant(String id) throws SocialSiteException; 
 
    /**
     * Gets all PermissionGrant objects (starting at the specified offset and continuing for
     * the specified length).
     */
    public List<PermissionGrant> getPermissionGrants(int offset, int length) throws SocialSiteException;

    /**
     * Gets all PermissionGrant objects which apply to the specified App (starting at the specified
     * offset and continuing for the specified length).
     */
    public List<PermissionGrant> getPermissionGrants(App app, int offset, int length) throws SocialSiteException;

    /**
     * Gets all PermissionGrant objects which apply to the specified gadget domain (starting at the specified
     * offset and continuing for the specified length).
     */
    public List<PermissionGrant> getPermissionGrants(String gadgetDomain, int offset, int length) throws SocialSiteException;
 
    /**
     * Gets all PermissionGrant objects which apply to the specified App (starting at the specified
     * offset and continuing for the specified length).
     */
    public List<PermissionGrant> getPermissionGrants(Profile profile, int offset, int length) throws SocialSiteException;
 
}
