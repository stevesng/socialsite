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

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.userapi.Permission;
import com.sun.socialsite.userapi.User;
import com.sun.socialsite.userapi.UserManagementException;
import com.sun.socialsite.userapi.UserManager;
import com.sun.socialsite.userapi.UserManagerImpl;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An implementation of Usermanager that simply delegate to some underlying
 * UserManager implementation (currently
 * com.sun.socialsite.userapi.UserManagerImpl).  This delegation/wrapping
 * is required to ensure that the current thread's entity manager is used.
 */
@Singleton
public class DelegatingUserManagerImpl implements UserManager {

    private static Log log = LogFactory.getLog(DelegatingUserManagerImpl.class);

    private final JPAPersistenceStrategy strategy;

    /**
     * Creates a new instance of JPAPropertiesManagerImpl
     */
    @Inject
    protected DelegatingUserManagerImpl(JPAPersistenceStrategy strat) {
        log.debug("Instantiating SocialSite User Manager");
        this.strategy = strat;
    }

    public void registerUser(User arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.registerUser(arg0);
    }

    public void removeUser(User arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.removeUser(arg0);
    }

    public long getUserCount() throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUserCount();
    }

    public User getUserByUserId(String arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUserByUserId(arg0);
    }

    public User getUserByUserId(String arg0, Boolean arg1) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUserByUserId(arg0, arg1);
    }

    public User getUserByActivationCode(String arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUserByActivationCode(arg0);
    }

    public List<User> getUsers(Boolean arg0, Date arg1, Date arg2, int arg3, int arg4) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUsers(arg0, arg1, arg2, arg3, arg4);
    }

    public List<User> getUsersStartingWith(String arg0, Boolean arg1, int arg2, int arg3) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUsersStartingWith(arg0, arg1, arg2, arg3);
    }

    public List<User> getUsersByLetter(char arg0, int arg1, int arg2) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUsersByLetter(arg0, arg1, arg2);
    }

    public Map<String, Long> getUserIdLetterMap() throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getUserIdLetterMap();
    }

    public void grantRole(String arg0, User arg1) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.grantRole(arg0, arg1);
    }

    public void revokeRole(String arg0, User arg1) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.revokeRole(arg0, arg1);
    }

    public List<String> getRoles(String arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getRoles(arg0);
    }

    public void grantPermission(String arg0, String arg1, User arg2, List<String> arg3) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.grantPermission(arg0, arg1, arg2, arg3);
    }

    public void grantPermissionPending(String arg0, String arg1, User arg2, List<String> arg3) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.grantPermissionPending(arg0, arg1, arg2, arg3);
    }

    public void revokePermission(String arg0, User arg1, List<String> arg2) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.revokePermission(arg0, arg1, arg2);
    }

    public void confirmPermission(String arg0, User arg1) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.confirmPermission(arg0, arg1);
    }

    public void declinePermission(String arg0, User arg1) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.declinePermission(arg0, arg1);
    }

    public Permission getPermission(String arg0, User arg1, Boolean arg2) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getPermission(arg0, arg1, arg2);
    }

    public List<Permission> getPermissions(User arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getPermissions(arg0);
    }

    public List<Permission> getPermissionsPending(User arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getPermissionsPending(arg0);
    }

    public List<Permission> getPermissions(String arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getPermissions(arg0);
    }

    public List<Permission> getPermissionsPending(String arg0) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.getPermissionsPending(arg0);
    }

    public boolean hasRole(String roleName, String userName) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        return impl.hasRole(roleName, userName);
    }

    public void saveUser(User user) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.saveUser(user);
    }

    public void addUser(User user) throws UserManagementException {
        UserManagerImpl impl = new UserManagerImpl(strategy.getEntityManager(true));
        impl.addUser(user);
    }
}
