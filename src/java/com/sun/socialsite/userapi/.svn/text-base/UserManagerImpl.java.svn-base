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

package com.sun.socialsite.userapi;

import com.sun.socialsite.config.Config;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements the interface UserManager.
 * It is important to note that this implementation expects
 * the caller will start appropriate transaction if necessary.
 *
 * @author Shing Wai Chan
 * @author David M Johnson
 */
public class UserManagerImpl implements UserManager {
    private EntityManager em = null;
    private static Log log = LogFactory.getLog(UserManagerImpl.class);

    // Only used when we need to perform some action in a seprate EntityManager
    private static EntityManagerFactory emf = null;

    public static void setEmf(EntityManagerFactory emf) {
        UserManagerImpl.emf = emf;
    }

    private static EntityManagerFactory getEmf() {
        synchronized(UserManagerImpl.class) {
            if (emf == null) {
                String puName = Config.getProperty("usermanager.puname", "SocialSite_PU");
                emf = Persistence.createEntityManagerFactory(puName);
            }
            return emf;
        }
    }

    public UserManagerImpl(EntityManager em) {
        this.em = em;
    }

    // Register users, update extended user profile data, etc.
    public void registerUser(User user) throws UserManagementException {
        if (user == null) {
            throw new UserManagementException("Cannot register null user");
        }
        createPersistentObject(user);
    }

    public void removeUser(User user) throws UserManagementException {
        if (user == null) {
            throw new UserManagementException("Cannot remove null user");
        }
        removePersistentObject(User.class, user.getId());
    }

    public long getUserCount() throws UserManagementException {
        Query query = getNamedQuery("User.count");
	Long count = (Long)query.getSingleResult();
        return count;
    }

    public User getUserByUserId(String UserId) throws UserManagementException {
        return getUserByUserId(UserId, null);
    }

    public User getUserByUserId(String UserId, Boolean enabled) throws UserManagementException {
        if (UserId == null) {
            throw new UserManagementException("Cannot get user with null UserId");
        }

        Query query = null;
        if (enabled == null) {
            query =  getNamedQuery("User.findByUserId");
            query.setParameter("userId", UserId);
        } else {
            query = getNamedQuery("User.findByUserId&Enabled");
            query.setParameter("userId", UserId);
            query.setParameter("enabled", enabled);
        }
        User user = null;
        try {
            user = (User)query.getSingleResult();
        } catch(NoResultException ex) {
            user = null;
        }
        return user;
    }

    public User getUserByActivationCode(String activationCode) throws UserManagementException {
        if (activationCode == null) {
            throw new UserManagementException("Cannot get user with null activationCode");
        }
        Query query = getNamedQuery("User.findByActivationCode");
        query.setParameter("activationCode", activationCode);
        User user = null;
        try {
            user = (User)query.getSingleResult();
        } catch(NoResultException ex) {
            user = null;
        }
        return user;
    }

    // Query by enabled status, creation date and with offset/length paging
    public List<User> getUsers(Boolean enabled, Date startDate, Date endDate, int offset, int length) throws UserManagementException {
        if (endDate == null) {
            endDate = new Date();
        }

        List<User> users = new ArrayList<User>();
        Query query = null;
        if (enabled != null) {
            if (startDate != null) {
                query = getNamedQuery("User.findByEnabled&StartDate&EndDateOrderByCreationDateDESC");
                query.setParameter("enabled", enabled);
                query.setParameter("startDate", startDate, TemporalType.DATE);
                query.setParameter("endDate", endDate, TemporalType.DATE);
            } else {
                query = getNamedQuery("User.findByEnabled&EndDateOrderByCreationDateDESC");
                query.setParameter("enabled", enabled);
                query.setParameter("endDate", endDate, TemporalType.DATE);
            }
        } else {
            if (startDate != null) {
                query = getNamedQuery("User.findByStartDate&EndDateOrderByCreationDateDESC");
                query.setParameter("startDate", startDate, TemporalType.DATE);
                query.setParameter("endDate", endDate, TemporalType.DATE);
            } else {
                query = getNamedQuery("User.findByEndDateOrderByCreationDateDESC");
                query.setParameter("endDate", endDate, TemporalType.DATE);
            }
        }
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                users.add((User)obj);
            }
        }
        return users;
    }

    public List<User> getUsersStartingWith(String startsWith, Boolean enabled, int offset, int length) throws UserManagementException {
        List<User> users = new ArrayList<User>();
        Query query = null;
        if (enabled != null) {
            if (startsWith != null) {
                query = getNamedQuery(
                        "User.findByUserIdOrEmailAddressPattern&Enabled");
                query.setParameter("enabled", enabled);
                query.setParameter("pattern", startsWith + '%');
            } else {
                query = getNamedQuery("User.findByEnabled");
                query.setParameter("enabled", enabled);
            }
        } else {
            if (startsWith != null) {
                query = getNamedQuery(
                        "User.findByUserIdOrEmailAddressPattern");
                query.setParameter("pattern", startsWith + '%');
            } else {
                query = getNamedQuery("User.getAll");
            }
        }

        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                users.add((User)obj);
            }
        }
        return users;
    }

    public List<User> getUsersByLetter(char letter, int offset, int length) throws UserManagementException {
        List<User> users = new ArrayList<User>();
        Query query = getNamedQuery("User.findByUserIdPatternOrderByUserId");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter("pattern", letter + "%");
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                users.add((User)obj);
            }
        }
        return users;
    }

    public Map<String, Long> getUserIdLetterMap() throws UserManagementException {
        String lc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Map<String, Long> results = new TreeMap<String, Long>();
        Query query = getNamedQuery("User.countWithUserIdPattern");
        for (int i = 0; i < lc.length(); i++) {
            char currentChar = lc.charAt(i);
            query.setParameter("pattern", currentChar + "%");
            List row = query.getResultList();
            Long count = (Long) row.get(0);
            results.put(String.valueOf(currentChar), count);
        }
        return results;
    }

    // Grant and revoke roles because roles imply permissions
    // "is user in role" is provided by the container
    // role is granted to user but not save to database
    public void grantRole(String roleName, User user) throws UserManagementException {
        if (roleName == null) {
            throw new UserManagementException("Cannot grant role with null roleName");
        }
        if (user == null) {
            throw new UserManagementException("Cannot grant role with null user");
        }

        UserRole role = null;
        try {
            role = getOrCreateRole(roleName);
            Set<UserRole> userRoles = user.getRoles();
            userRoles.add(role);
            user.setRoles(userRoles);
        } catch (Throwable t) {
            throw new UserManagementException(t);
        }
    }

    private UserRole getOrCreateRole(String roleName) throws PersistenceException {
        UserRole role = null;
        try {
            Query query = getNamedQuery("UserRole.findByRoleName");
            query.setParameter("roleName", roleName);
            role = (UserRole)query.getSingleResult();
        } catch(NoResultException nre) {
            // create the role in database
            EntityManager em2 = null;
            try {
                em2 = getEmf().createEntityManager();
                em2.getTransaction().begin();
                UserRole newRole = new UserRole();
                newRole.setRoleName(roleName);
                em2.persist(newRole);
                em2.flush();
                em2.getTransaction().commit();
            } catch (PersistenceException pe) {
                if (em2 == null) {
                    // If we couldn't even create an EntityManager, something is clearly wrong
                    throw pe;
                } else {
                    // Otherwise, ignore exception for now; the role may have been created in another thread
                    if (em2.getTransaction().isActive()) em2.getTransaction().rollback();
                }
            } finally {
                if (em2 != null) em2.close();
            }
        }

        // If role is null, try again (since it _should_ now exist in the DB).
        if (role == null) {
            Query query = getNamedQuery("UserRole.findByRoleName");
            query.setParameter("roleName", roleName);
            role = (UserRole)query.getSingleResult();
        }

        return role;
    }

    // role is revoked to user but not save to database
    public void revokeRole(String roleName, User user) throws UserManagementException {
        if (roleName == null) {
            throw new UserManagementException("Cannot revoke role with null roleName");
        }
        if (user == null) {
            throw new UserManagementException("Cannot revoke role with null user");
        }

        Query query = getNamedQuery("UserRole.findByRoleName");
        query.setParameter("roleName", roleName);

        try {
            UserRole role = (UserRole)query.getSingleResult();
            Set<UserRole> userRoles = user.getRoles();
            userRoles.remove(role);
            user.setRoles(userRoles);
        } catch(NoResultException ex) {
            // no op
        }
    }

    // and to display the roles and permissions associated with each user
    public List<String> getRoles(String userId) throws UserManagementException {
        if (userId == null) {
            throw new UserManagementException("Cannot get roles for null UserId");
        }

        List<String> roleStrings = new ArrayList<String>();
        Query query = getNamedQuery("UserRole.findByUserId");
        query.setParameter("userId", userId);

        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                roleStrings.add(((UserRole)obj).getRoleName());
            }
        }
        return roleStrings;
    }

    // Grant and revoke SF object permissions
    public void grantPermission(String objectId, String objectType,
            User user, List<String> actions) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot grant Permission with null objectId");
        }
        if (objectType == null) {
            throw new UserManagementException("Cannot grant Permission with null objectType");
        }
        if (user == null) {
            throw new UserManagementException("Cannot grant Permission with null user");
        }

        // first, see if user already has a permission for the specified object
        Permission existingPerm = getPermission(objectId, user, null);

        // permission already exists, so add any actions specified in perm argument
        if (existingPerm != null) {
            existingPerm.addActions(actions);
        } else {
            // it's a new permission, so store it
            Permission perm = new Permission(objectId, objectType,
                    user, actions);
            createPersistentObject(perm);
        }
    }

    public void grantPermissionPending(String objectId, String objectType,
            User user, List<String> actions) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot grant Permission with null objectId");
        }
        if (objectType == null) {
            throw new UserManagementException("Cannot grant Permission with null objectType");
        }
        if (user == null) {
            throw new UserManagementException("Cannot grant Permission with null user");
        }

        // first, see if user already has a permission for the specified object
        Permission existingPerm = getPermission(objectId, user, null);

        // permission already exists, so complain
        if (existingPerm != null) {
            throw new UserManagementException("Cannot make existing permission into pending");
        } else {
            // it's a new permission, so store it
            Permission perm = new Permission(objectId, objectType,
                    user, actions);
            perm.setPending(true);
            createPersistentObject(perm);
        }
    }

    public void revokePermission(String objectId, User user, List<String> actions) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot revoke Permission with null objectId");
        }
        if (user == null) {
            throw new UserManagementException("Cannot revoke Permission with null user");
        }

        // first, see if user already has a permission for the specified object
        Permission oldPerm = getPermission(objectId, user, null);

        if (oldPerm == null) {
            throw new UserManagementException("Permission not found");
        } else {
            // remove actions specified in perm agument
            oldPerm.removeActions(actions);
            if (oldPerm.isEmpty()) {
                // no actions left in permission so remove it
                user.getPermissions().remove(oldPerm);
                removePersistentObject(Permission.class, oldPerm.getId());
            } else {
                // otherwise save it
            }
        }
    }


    /**
     * Confirm a permission that is currently in pending state.
     * If user already has a permission record for the specified object, then
     * actions specified in argument perm will be added to that record.
     */
    public void confirmPermission(String objectId, User user) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot confirm Permission with null objectId");
        }
        if (user == null) {
            throw new UserManagementException("Cannot confirm Permission with null user");
        }

        // first, see if user already has a permission for the specified object
        Permission existingPerm = getPermission(objectId, user, null);

        if (existingPerm == null) {
            throw new UserManagementException("permission not found");
        } else {
            existingPerm.setPending(false);
        }
    }

    /**
     * Decline a permission that is currently in pending state.
     * Causes permission record to be deleted.
     */
    public void declinePermission(String objectId, User user) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot confirm Permission with null objectId");
        }
        if (user == null) {
            throw new UserManagementException("Cannot confirm Permission with null user");
        }

        // first, see if user already has a permission for the specified object
        Permission existingPerm = getPermission(objectId, user, null);

        if (existingPerm == null) {
            throw new UserManagementException("permission not found");
        } else {
            user.getPermissions().remove(existingPerm);
            removePersistentObject(Permission.class, existingPerm.getId());
        }
    }

    /**
     * Retrieve Permission by objectId, userId and pending.
     * return null when the result is not found
     * @param objectId
     * @param user
     * @param pending
     */
    public Permission getPermission(String objectId, User user, Boolean pending) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot find Permission with null objectId");
        }
        if (user == null) {
            throw new UserManagementException("Cannot find Permission with null user");
        }
        Query query = null;
        if (pending == null) {
            query = getNamedQuery("Permission.findByUserId&ObjectId");
            query.setParameter("objectId", objectId);
            query.setParameter("userId", user.getId());
        } else {
            query = getNamedQuery("Permission.findByUserId&ObjectId&Pending");
            query.setParameter("objectId", objectId);
            query.setParameter("userId", user.getId());
            query.setParameter("pending", pending);
        }
        Permission perm = null;
        try {
            perm = (Permission)query.getSingleResult();
        } catch (NoResultException ignored) {
            // ignored
        }
        return perm;
    }

    public List<Permission> getPermissions(User user) throws UserManagementException {
        if (user == null) {
            throw new UserManagementException("Cannot get Permissions for null user");
        }

        List<Permission> permissions = new ArrayList<Permission>();
        Query query = getNamedQuery("Permission.findByUserId");
        query.setParameter("userId", user.getId());
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                permissions.add((Permission)obj);
            }
        }
        return permissions;
    }

    /**
     * Get all pending permissions associated with an object .
     */
    public List<Permission> getPermissionsPending(User user) throws UserManagementException {
        if (user == null) {
            throw new UserManagementException("Cannot get pending Permissions for null user");
        }

        List<Permission> permissions = new ArrayList<Permission>();
        Query query = getNamedQuery("Permission.findByUserId&Pending");
        query.setParameter("userId", user.getId());
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                permissions.add((Permission)obj);
            }
        }
        return permissions;
    }

    public List<Permission> getPermissions(String objectId) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot get Permissions for null objectId");
        }

        List<Permission> permissions = new ArrayList<Permission>();
        Query query = getNamedQuery("Permission.findByObjectId");
        query.setParameter("objectId", objectId);
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                permissions.add((Permission)obj);
            }
        }
        return permissions;
    }

    /**
     * Get all of user's pending permissions.
     */
    public List<Permission> getPermissionsPending(String objectId) throws UserManagementException {
        if (objectId == null) {
            throw new UserManagementException("Cannot get pending Permissions for null objectId");
        }

        List<Permission> permissions = new ArrayList<Permission>();
        Query query = getNamedQuery("Permission.findByObjectId&Pending");
        query.setParameter("objectId", objectId);
        List results = query.getResultList();
        if (results != null) {
            for (Object obj : results) {
                permissions.add((Permission)obj);
            }
        }
        return permissions;
    }

    //----- private methods -----

    /**
     * Store object in an existing transaction.
     * @param obj the object to persist
     * @return the object persisted
     */
    private Object createPersistentObject(Object obj) {
        if (!em.contains(obj)) {
            em.persist(obj);
        }
        return obj;
    }

    /**
     * Remove object in an existing transaction
     * @param clazz the class of object to remove
     * @param id the id of the object to remove
     */
    private void removePersistentObject(Class<?> clazz, String id) {
        Object po = em.find(clazz, id);
        em.remove(po);
    }

    /**
     * Get named query with FlushModeType.COMMIT
     * @param clazz the class of instances to find
     * @param queryName the name of the query
     */
    private Query getNamedQuery(String queryName) {
        Query q = em.createNamedQuery(queryName);
        return q;
    }

    public void saveUser(User user) {
        user.setUpdateDate(new java.util.Date());
        user.setAccessDate(new java.util.Date());
        if (!em.contains(user)) {
            // If entity is not managed we can assume it is new
            em.persist(user);
        }
    }

    public void addUser(User user) throws UserManagementException {
        // for now, we just save the user
        saveUser(user);
    }

    public boolean hasRole(String roleName, String UserId) throws UserManagementException {
        if (UserId == null) {
            throw new UserManagementException("Cannot get role for null UserId");
        }
        if (roleName == null) {
            throw new UserManagementException("Cannot get role for null roleName");
        }

        List<String> roleStrings = new ArrayList<String>();
        Query query = getNamedQuery("UserRole.findByUserIdAndRoleName");
        query.setParameter("userId", UserId);
        query.setParameter("roleName", roleName);

        List results = query.getResultList();
        return results == null ? false : (results.size() > 0 ? true : false);
    }
}
