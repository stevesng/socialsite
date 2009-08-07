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

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Represents a user with a unique unchanging ID, a unique but changeable
 * username and a minimal set of user-profile properties.
 */
@Entity
@Table(name = "userapi_user")
@NamedQueries(
{
    @NamedQuery(name = "User.getAll", query = "SELECT u FROM User u"),

    @NamedQuery(name = "User.findByUserId",
        query = "SELECT u FROM User u WHERE u.userId = :userId"),

    @NamedQuery(name = "User.findByUserId&Enabled",
        query = "SELECT u FROM User u WHERE u.userId = :userId AND u.enabled = :enabled"),

    @NamedQuery(name = "User.findByEnabled",
        query = "SELECT u FROM User u WHERE u.enabled = :enabled"),

    @NamedQuery(name = "User.findByEnabled&StartDate&EndDateOrderByCreationDateDESC",
        query = "SELECT u FROM User u WHERE u.enabled = :enabled AND u.creationDate BETWEEN :startDate AND :endDate ORDER By u.creationDate DESC"),

    @NamedQuery(name = "User.findByEnabled&EndDateOrderByCreationDateDESC",
        query = "SELECT u FROM User u WHERE u.enabled = :enabled AND u.creationDate < :endDate ORDER By u.creationDate DESC"),

    @NamedQuery(name = "User.findByStartDate&EndDateOrderByCreationDateDESC",
        query = "SELECT u FROM User u WHERE u.creationDate BETWEEN :startDate AND :endDate ORDER BY u.creationDate DESC"),

    @NamedQuery(name = "User.findByEndDateOrderByCreationDateDESC",
        query = "SELECT u FROM User u WHERE u.creationDate < :endDate ORDER BY u.creationDate DESC"),

    @NamedQuery(name = "User.findByUserIdOrEmailAddressPattern",
        query = "SELECT u FROM User u WHERE u.userId LIKE :pattern OR u.emailAddress LIKE :pattern OR u.userName LIKE :pattern"),

    @NamedQuery(name = "User.findByUserIdOrEmailAddressPattern&Enabled",
        query = "SELECT u FROM User u WHERE u.enabled = :enabled AND (u.userId LIKE :pattern OR u.userName LIKE :pattern OR u.emailAddress LIKE :pattern)"),

    @NamedQuery(name = "User.findByUserIdPattern&Enabled",
        query = "SELECT u FROM User u WHERE (u.userId LIKE :pattern OR u.userName LIKE :pattern) AND u.enabled = :enabled"),

    @NamedQuery(name = "User.findByActivationCode",
        query = "SELECT u FROM User u WHERE u.activationCode = :activationCode"),

    @NamedQuery(name = "User.findByUserIdPatternOrderByUserId",
        query = "SELECT u FROM User u WHERE u.userId LIKE :pattern ORDER BY u.userId"),

    @NamedQuery(name = "User.count",
        query = "SELECT count(u) FROM User u"),

    @NamedQuery(name = "User.countWithUserIdPattern",
        query = "SELECT COUNT(u) FROM User u WHERE UPPER(u.userId) LIKE :pattern")
})

public class User implements Serializable {
    private static final char[] HEXADECIMAL = { '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    @Id
    @Column(name = "id", nullable = false)
    private String id = UUID.randomUUID().toString();

    /** Unique user ID that will never ever change, employee ID for example */
    @Column(name = "userId", nullable = false, unique = true)
    private String userId;

    @Column(name = "passphrase", nullable = false)
    private String password;

    /** Unique user name that is allowed to change */
    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "creation_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    @Column(name = "update_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date updateDate;

    @Column(name = "access_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date accessDate;

    @Column(name = "locale")
    private String locale;

    @Column(name = "timezone")
    private String timeZone;

    @Column(name = "isenabled", nullable = false)
    private boolean enabled;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "security_answer")
    private String securityAnswer;

    @Column(name = "bio")
    private String bio;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "userapi_user_userrole",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<UserRole> roles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Permission> permissions;

    public User() {
        creationDate = updateDate = accessDate = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Reset this user's password and handles encryption.
     *
     * @param newPassword The new password to be set.
     * @exception NoSuchAlgorithmException
     */
    public void resetPassword(String newPassword, String algorithm)
            throws UserManagementException {

        byte[] bytes = newPassword.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            md.update(bytes);
            byte[] digest = md.digest();
            password = hexEncode(digest);
        } catch(Exception ex) {
            throw new UserManagementException(ex);
        }
    }

    private String hexEncode(byte[] bytes) {
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int low = (int)(bytes[i] & 0x0f);
            int high = (int)((bytes[i] & 0xf0) >> 4);
            sb.append(HEXADECIMAL[high]);
            sb.append(HEXADECIMAL[low]);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof User) {
            if (((User)arg0).getId() .equals(getId())) {
                return true;
            }
        }
        return false;
    }
}
