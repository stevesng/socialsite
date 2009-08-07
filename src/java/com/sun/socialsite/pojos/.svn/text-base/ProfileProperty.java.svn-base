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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
 * Represents a single profile property.
 */
@Entity
@Table(name="ss_profileprop", uniqueConstraints={@UniqueConstraint(columnNames={"name", "profileid"})})
@NamedQueries({
    @NamedQuery(name="ProfileProperty.getByName",
        query="SELECT p FROM ProfileProperty p WHERE p.name = ?1"),

    @NamedQuery(name="ProfileProperty.getByProfile",
        query="SELECT p FROM ProfileProperty p WHERE p.name = ?1")
})
public class ProfileProperty implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    private String name = null;

    @ManyToOne
    @JoinColumn(name="profileid")
    private Profile profile;

    /** I18N key for looking up localized name of propery */
    private String namekey = null;

    /** Property value */
    private String value = null;

    private String type = "string";
    
    @Enumerated(EnumType.STRING)
    private Profile.VisibilityType vistype = null;

    /** For visibility type of FRIENDS, minimum relationship level requied to view property */
    private int vislevel = 0;
    
    /** For visibility type of SOMEGROUPS, list of group handles */
    private String someGroups = null;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Profile.VisibilityType getVisibility() {
        return vistype;
    }

    public void setVisibility(Profile.VisibilityType visibility) {
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

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    public List<String> getSomeGroups() {
        if (someGroups == null) return Collections.emptyList();
        return Arrays.asList(someGroups.split(","));
    }
    
    public void setSomeGroups(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() > 0) sb.append(",");
            sb.append(s);
        }
        someGroups = sb.toString();
    }

    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return (this.profile.getUserId() + ", " + this.name + "=" + this.value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof ProfileProperty != true) return false;
        ProfileProperty o = (ProfileProperty)other;
        return new EqualsBuilder().append(id, o.id).isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder().append(id);
        if (profile != null) hcb.append(profile.getUserId());
        else hcb.append("null");
        return hcb.toHashCode();
    }

}
