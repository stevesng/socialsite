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

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserManager {

    // Register users, update extended user profile data, etc.
    /**
     * Add a new user to datastore.
     * @param user
     * @exception UserManagementException
     */
    public void registerUser(User user) throws UserManagementException;

    /**
     * Remove the user from datastore.
     * @param user
     * @exception UserManagementException
     */
    public void removeUser(User user) throws UserManagementException;

    /**
     * Return the number of user in datastore.
     * @exception UserManagementException
     */
    public long getUserCount() throws UserManagementException;

    /**
     * Get the user with the given userId independent of their enabled status.
     * @param userId
     * @exception UserManagementException
     */
    public User getUserByUserId(String userId) throws UserManagementException;

    /**
     * Get the user with the given userId and enabled status.
     * @param userId
     * @param enabled (if null means independent of status)
     * @exception UserManagementException
     */
    public User getUserByUserId(String userId, Boolean enabled) throws UserManagementException;

    /**
     * Get the user with the given activationCode.
     * @param activationCode
     * @exception UserManagementException
     */
    public User getUserByActivationCode(String activationCode) throws UserManagementException;

    // Query by enabled status, creation date and with offset/length paging
    /**
     * Get the user with the given enabled status, startDate, endDate, offset,
     * length. The result is ordered by creationDate in descending order.
     * @param enabled (null means regardless of enabled status)
     * @param startDate (null means regardless of startDate)
     * @param endDate (default current date if null)
     * @param offset
     * @param length (-1 means unlimited)
     * @exception UserManagementException
     */
    public List<User> getUsers(Boolean enabled, Date startDate, Date endDate, int offset, int length) throws UserManagementException;

    /**
     * Get the list of users with username or email address starting
     * with given startsWtih, given enabled status, offset and length.
     * @param startsWith (null means regardless of username and emailAddress)
     * @param enabled (null means regardless of enabled status)
     * @param offset
     * @param length (-1 means unlimited)
     * @exception UserManagementException
     */
    public List<User> getUsersStartingWith(String startsWith, Boolean enabled, int offset, int length) throws UserManagementException;

    /**
     * Get the list of users starting with the given letter, offset
     * and length and the result is ordered by userId.
     * @param letter
     * @param offset
     * @param length (-1 means unlimited)
     * @exception UserManagementException
     */
    public List<User> getUsersByLetter(char letter, int offset, int length) throws UserManagementException;

    /**
     * Return a Map&lt;String, Long&gt; with key a given character String
     * and value number of users with name starting with the corresponding
     * character.
     * @exception UserManagementException
     */
    public Map<String, Long> getUserIdLetterMap() throws UserManagementException;

    // Grant and revoke roles because roles imply permissions
    // "is user in role" is provided by the container
    /**
     * Grant a role with given roleName to the user.
     * @param roleName
     * @param user
     * @exception UserManagementException
     */
    public void grantRole(String roleName, User user) throws UserManagementException;

    /**
     * Revoke a role with given roleName to the user.
     * @param roleName
     * @param user
     * @exception UserManagementException
     */
    public void revokeRole(String roleName, User user) throws UserManagementException;

    // and to display the roles and permissions associated with each user:
    /**
     * Get the list of roles for a user with given userId.
     * @param userId
     * @exception UserManagementException
     */
    public List<String> getRoles(String userId) throws UserManagementException;

    // Grant and revoke SF object permissions
    /**
     * Grant a Permission with given objectId, objectType, user and actions.
     * @param objectId
     * @param objectType
     * @param user
     * @param actions
     * @exception UserManagementException
     */
    public void grantPermission(String objectId, String objectType, User user, List<String> actions) throws UserManagementException;

    /**
     * Grant a Permission with given objectId, objectType, user and actions
     * and set the pending status to true.
     * @param objectId
     * @param objectType
     * @param user
     * @param actions
     * @exception UserManagementException
     */
    public void grantPermissionPending(String objectId, String objectType, User user, List<String> actions) throws UserManagementException;

    /**
     * Revoke a Permission with given objectId, objectType, user and actions.
     * @param objectId
     * @param objectType
     * @param user
     * @param actions
     * @exception UserManagementException
     */
    public void revokePermission(String objectId, User user, List<String> actions) throws UserManagementException;

    /**
     * Confirm a permission that is currently in pending state.
     * In other words, it will set the pending to false.
     * If user already has a permission record for the specified object, then
     * actions specified in argument perm will be added to that record.
     */
    public void confirmPermission(String objectId, User user) throws UserManagementException;

    /**
     * Decline a permission that is currently in pending state.
     * Causes permission record to be deleted.
     */
    public void declinePermission(String objectId, User user) throws UserManagementException;

    /**
     * Retrieve the Permission by objectId, userId and pending status.
     * return null when the result is not found
     * @param objectId
     * @param user
     * @param pending (null means regardless of pending status)
     * @param UserManagementException
     */
    public Permission getPermission(String objectId, User user, Boolean pending) throws UserManagementException;

    /**
     * Return the list of Permission by user.
     * return null when the result is not found
     * @param objectId
     * @param user
     * @param UserManagementException
     */
    public List<Permission> getPermissions(User user) throws UserManagementException;

    /**
     * Get all of user's pending permissions.
     * @param user
     * @param UserManagementException
     */
    public List<Permission> getPermissionsPending(User user) throws UserManagementException;

    /**
     * Get all of user's permissions.
     * @param objectId
     * @param UserManagementException
     */
    public List<Permission> getPermissions(String objectId) throws UserManagementException;

    /**
     * Get all pending permissions associated with given objectId.
     * @param objectId
     * @param UserManagementException
     */
    public List<Permission> getPermissionsPending(String objectId) throws UserManagementException;

    public boolean hasRole(String roleName, String userId) throws UserManagementException;

    public void saveUser(User user) throws UserManagementException;

    public void addUser(User testUser) throws UserManagementException;
}
