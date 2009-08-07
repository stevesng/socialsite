/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.socialsite.pojos;

import com.sun.socialsite.business.impl.JPAListenerManagerImpl;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * An PermissionGrant can be used to control access to restricted services.
 */
@Entity
@Table(name ="ss_permissiongrant")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="PermissionGrant.getAll",
        query="SELECT pg FROM PermissionGrant pg ORDER BY pg.created ASC"),

    @NamedQuery(name="PermissionGrant.getById",
        query="SELECT pg FROM PermissionGrant pg WHERE pg.id=?1"),

    @NamedQuery(name="PermissionGrant.getByApp",
        query="SELECT pg FROM PermissionGrant pg WHERE pg.app=?1"),

    @NamedQuery(name="PermissionGrant.getByGadgetDomain",
        query="SELECT pg FROM PermissionGrant pg WHERE pg.gadgetDomain=?1 OR pg.gadgetDomain='*'"),

    @NamedQuery(name="PermissionGrant.getByProfileId",
        query="SELECT pg FROM PermissionGrant pg WHERE pg.profileId=?1 OR pg.profileId='*'")
})
public class PermissionGrant implements Serializable {

    private static Log log = LogFactory.getLog(PermissionGrant.class);

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    private String profileId;

    private String groupId;

    @ManyToOne
    @JoinColumn(name="appid")
    private App app;

    private String gadgetDomain = null;

    private String type = null;

    private String name = null;

    private String actions = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }
 
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getGroupId() {
        return groupId;
    }
 
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public App getApp() {
        return app;
    }
 
    public void setApp(App app) {
        this.app = app;
    }

    public String getGadgetDomain() {
        return gadgetDomain;
    }

    public void setGadgetDomain(String gadgetDomain) {
        this.gadgetDomain = gadgetDomain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Timestamp(created.getTime());
    }

    public Date getUpdated() {
        return new Date(updated.getTime());
    }

    public void setUpdated(Date updated) {
        this.updated = new Timestamp(updated.getTime());
    }

    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return String.format("%s[id=(%s),type=(%s),name=(%s),actions=(%s)]", getClass().getSimpleName(), getId(), getType(), getName(), getActions());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof PermissionGrant != true) return false;
        PermissionGrant o = (PermissionGrant)other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getId(), o.getId());
        eb.append(getType(), o.getType());
        eb.append(getName(), o.getName());
        eb.append(getActions(), o.getActions());
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

}
