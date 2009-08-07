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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * POJO deals with destination specific theme settings
 */
@Entity
@Table(name ="ss_themes")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="ThemeSettings.getAll",
        query="SELECT t FROM ThemeSettings t"),

    @NamedQuery(name="ThemeSettings.getByDestination",
        query="SELECT t FROM ThemeSettings t WHERE t.destination=?1")

})
public class ThemeSettings implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    private String destination = null;
    private String anchorcolor = null;
    private String bgcolor = null;
    private String bgimage = null;
    private String fontcolor = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return this.destination;
    }
    
    public void setDestination(String d) {
        this.destination = d;
    }

    public String getAnchorColor() {
        return this.anchorcolor;
    }
    
    public void setAnchorColor(String d) {
        this.anchorcolor = d;
    }

    public String getBackgroundColor() {
        return this.bgcolor;
    }
    
    public void setBackgroundColor(String d) {
        this.bgcolor = d;
    }

    public String getBackgroundImage() {
        return this.bgimage;
    }
    
    public void setBackgroundImage(String d) {
        this.bgimage = d;
    }

    public String getFontColor() {
        return this.fontcolor;
    }
    
    public void setFontColor(String d) {
        this.fontcolor = d;
    }
    
    @Override
    public String toString() {
        return String.format("%s[handle=%s]", getClass().getSimpleName(), this.destination);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ThemeSettings != true) {
            return false;
        }
        ThemeSettings o = (ThemeSettings) other;
        return new EqualsBuilder().append(getDestination(), o.getDestination()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getDestination()).toHashCode();
    }
}
