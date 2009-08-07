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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author dave
 */
@Entity
@Table(name = "userapi_permission")
@NamedQueries(
{
    @NamedQuery(name = "Permission.findByUserId",
        query = "SELECT p FROM Permission p WHERE p.user.id = :userId"),

    @NamedQuery(name = "Permission.findByUserId&Pending",
        query = "SELECT p FROM Permission p WHERE p.user.id = :userId AND p.pending = TRUE"),

    @NamedQuery(name = "Permission.findByObjectId",
        query = "SELECT p FROM Permission p WHERE p.objectId = :objectId"),

    @NamedQuery(name = "Permission.findByObjectId&Pending",
        query = "SELECT p FROM Permission p WHERE p.objectId = :objectId AND p.pending = TRUE"),

    @NamedQuery(name = "Permission.findByUserId&ObjectId",
        query = "SELECT p FROM Permission p WHERE p.user.id = :userId AND p.objectId = :objectId"),

    @NamedQuery(name = "Permission.findByUserId&ObjectId&Pending",
        query = "SELECT p FROM Permission p WHERE p.user.id = :userId AND p.objectId = :objectId AND p.pending = :pending")
})

public class Permission implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    private String id = UUID.randomUUID().toString();

    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private User user;

    @Column(name = "actions")
    private String actions;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "pending")
    private boolean pending = false;

    @Column(name = "creation_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date creationDate = new Date();

    public Permission() {
    }

    public Permission(String objectId, String objectType,
            User user, List<String> la) {
        this.objectId = objectId;
        this.objectType = objectType;
        this.user = user;
        Set<Permission> perms = user.getPermissions();
        if (perms == null) {
            perms = new HashSet<Permission>();
            user.setPermissions(perms);
        }
        perms.add(this);
        actions = listToString(la);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user= user;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void addActions(List<String> addList) {
        if (addList != null && addList.size() > 0) {
            List<String> stringList = getActionsAsList(actions);
            for (String s : addList) {
                String st = s.trim();
                if (!stringList.contains(st)) {
                    stringList.add(st);
                }
            }
            actions = listToString(stringList);
        }
    }

    public void removeActions(List<String> rmList) {
        if (rmList != null && rmList.size() > 0) {
            List<String> stringList = getActionsAsList(actions);
            for (String s : rmList) {
                String st = s.trim();
                if (stringList.contains(st)) {
                    stringList.remove(st);
                }
            }
            actions = listToString(stringList);
        }
    }

    public boolean hasAction(String action) {
        if (action == null || action.trim().length() == 0) {
            return false;
        }
        List<String> actionList = getActionsAsList(actions);
        return actionList.contains(action.trim());
    }

    public boolean hasActions(List<String> actionsToCheck) {
        List<String> actionList = getActionsAsList(actions);
        for (String actionToCheck : actionsToCheck) {
            actionToCheck = actionToCheck.trim();
            if (!actionList.contains(actionToCheck)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        return (actions == null || actions.trim().length() == 0);
    }

    public List<String> getActionsAsList(String s) {
        List<String> list = null;
        if (s != null && s.trim().length() > 0) {
            String[] ss = s.trim().split(" *, *");
            list = new ArrayList<String>(Arrays.asList(ss));
        } else {
            list = new ArrayList<String>();
        }
        return list;
    }

    private String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i).trim());
            if (i != list.size() - 1) {
                sb.append(",");
            }
        }
        String result = sb.toString();
        return (result.length() > 0)? result : null;
    }
}
