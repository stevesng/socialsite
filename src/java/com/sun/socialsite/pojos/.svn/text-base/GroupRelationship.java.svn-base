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

package com.sun.socialsite.pojos;

import com.sun.socialsite.business.impl.JPAListenerManagerImpl;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Group relationship.
 */
@Entity
@Table(name="ss_grouprel")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="GroupRelationship.getAll",
        query="SELECT gr FROM GroupRelationship gr"),

    @NamedQuery(name="GroupRelationship.getByUserProfile",
        query="SELECT gr FROM GroupRelationship gr WHERE gr.userProfile=?1"),

    @NamedQuery(name="GroupRelationship.getByGroup",
        query="SELECT gr FROM GroupRelationship gr WHERE gr.group=?1"),

    @NamedQuery(name="GroupRelationship.getAdminsOfGroup",
        query="SELECT gr FROM GroupRelationship gr WHERE gr.group=?1 AND gr.relcode=?2"),

    @NamedQuery(name="GroupRelationship.getByGroupAndProfile",
        query="SELECT gr FROM GroupRelationship gr WHERE gr.group=?1 AND gr.userProfile=?2"),

    @NamedQuery(name="GroupRelationship.getByGroupAndUserProfile",
        query="SELECT gr FROM GroupRelationship gr WHERE gr.group=?1 AND gr.userProfile=?2"),

    @NamedQuery(name="GroupRelationship.getFriendsGroups",
        query="SELECT gr.group FROM GroupRelationship gr, Relationship r "
        + "WHERE gr.userProfile = r.profileTo AND r.profileFrom = ?1"),
    
    @NamedQuery(name="GroupRelationship.getCommonMembers",
        query="SELECT gr1.userProfile FROM GroupRelationship gr1, GroupRelationship gr2 "
        + "WHERE gr1.userProfile = gr2.userProfile AND gr1.group = ?1 "
        + "AND gr2.group = ?2"),
        
    /*
     * TBD : this QL is not working; need to fix it
    @NamedQuery(name="GroupRelationship.getGroupMembersGroups",
        query="SELECT gr1.group FROM GroupRelationship gr1, GroupRelationship gr2 "
        + "WHERE gr1.userProfile = gr2.userProfile AND gr2.group = ?1"),
      */  
    @NamedQuery(name="GroupRelationship.getPopularGroups",
    // TBD - fix the query
//            query="SELECT gr.group FROM GroupRelationship gr GROUP BY gr.group")
           query="SELECT gr.group FROM GroupRelationship gr")       
})
public class GroupRelationship implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    public enum Relationship  {MEMBER, FOUNDER, ADMIN, PENDING}
    @Enumerated(EnumType.STRING)
    private Relationship relcode = null;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="groupid")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profileid")
    private Profile userProfile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Profile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(Profile userProfile) {
        this.userProfile = userProfile;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Relationship getRelcode() {
        return relcode;
    }

    public void setRelcode(Relationship relcode) {
        this.relcode = relcode;
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

    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof GroupRelationship != true) return false;
        GroupRelationship o = (GroupRelationship)other;
        return new EqualsBuilder().append(getGroup().getHandle(), o.getGroup().getHandle()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getGroup().getHandle()).toHashCode();
    }
}
