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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * Relationship request from one profile to another.
 */
@Entity
@DiscriminatorValue("RELATIONSHIP")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="RelationshipRequest.getAll",
        query="SELECT n FROM RelationshipRequest n"),

    @NamedQuery(name="RelationshipRequest.getByProfileTo",
        query="SELECT n FROM RelationshipRequest n WHERE n.profileTo=?1"),

    @NamedQuery(name="RelationshipRequest.getByProfileFrom",
        query="SELECT n FROM RelationshipRequest n WHERE n.profileFrom=?1"),

    @NamedQuery(name="RelationshipRequest.getByProfileFromAndProfileTo",
        query="SELECT n FROM RelationshipRequest n WHERE n.profileFrom=?1 and n.profileTo=?2")
})
public class RelationshipRequest extends SocialRequest {

    public Profile getProfileTo() {
        return profileTo;
    }

    public void setProfileTo(Profile touser) {
        this.profileTo = touser;
    }

    public int getLevelFrom() {
        return levelFrom;
    }

    public void setLevelFrom(int levelFrom) {
        this.levelFrom = levelFrom;
    }

    public int getLevelTo() {
        return levelTo;
    }

    public void setLevelTo(int levelTo) {
        this.levelTo = levelTo;
    }

    public String getHowknow() {
        return howknow;
    }

    public void setHowknow(String howknow) {
        this.howknow = howknow;
    }
    
    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return String.format("%s[id=%s,fromUser.userId=%s,toUser.userId=%s]",
            getClass().getSimpleName(), getId(), getProfileFrom().getUserId(), getProfileTo().getUserId());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof RelationshipRequest != true) return false;
        RelationshipRequest o = (RelationshipRequest)other;
        return new EqualsBuilder().append(getId(), o.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

}
