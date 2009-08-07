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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Represents a single group property.
 */
@Entity
@Table(name="ss_groupprop", uniqueConstraints={@UniqueConstraint(columnNames={"name", "groupid"})})
@NamedQueries({
    @NamedQuery(name="GroupProperty.getByName",
        query="SELECT gp FROM GroupProperty gp WHERE gp.name = ?1"),

    @NamedQuery(name="GroupProperty.getByGroup",
        query="SELECT gp FROM GroupProperty gp WHERE gp.name = ?1")
})
public class GroupProperty implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    private String name = null;

    @ManyToOne
    @JoinColumn(name="groupid")
    private Group group;

    /** I18N key for looking up localized name of propery */
    private String namekey = null;

    /** Property value */
    private String value = null;

    // TBD: refine visibility settings
    public enum VisibilityType  {PRIVATE, SOMEGROUPS, ALLGROUPS, PUBLIC}
    @Enumerated(EnumType.STRING)
    private VisibilityType vistype = null;

    /** Minimum level requied to view property */
    private int vislevel = 0;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKey() {
        return namekey;
    }

    public void setNameKey(String namekey) {
        this.namekey = namekey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public VisibilityType getVisibility() {
        return vistype;
    }

    public void setVisibility(VisibilityType visibility) {
        this.vistype = visibility;
    }

    public int getVisibilityLevel() {
        return vislevel;
    }

    public void setVisibilityLevel(int vislevel) {
        this.vislevel = vislevel;
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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return (this.group.getHandle() + ", " + this.name + "=" + this.value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof GroupProperty != true) return false;
        GroupProperty o = (GroupProperty)other;
        return new EqualsBuilder().append(id, o.id).isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder().append(id);
        if (group != null) hcb.append(group.getId());
        else hcb.append("null");
        return hcb.toHashCode();
    }

}
