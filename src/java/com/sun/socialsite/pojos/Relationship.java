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
import java.util.Locale;
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

/**
 * Relationship request from one user to another.
 */
@Entity
@Table(name="ss_userrel")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="Relationship.getAll",
        query="SELECT f FROM Relationship f ORDER BY f.profileTo.lastName,f.profileTo.firstName ASC"),

    @NamedQuery(name="Relationship.getByProfileFrom",
        query="SELECT f FROM Relationship f WHERE f.profileFrom=?1 ORDER BY f.profileTo.lastName,f.profileTo.firstName ASC"),

    @NamedQuery(name="Relationship.getByProfileTo",
        query="SELECT f FROM Relationship f WHERE f.profileTo=?1  ORDER BY f.profileFrom.lastName,f.profileFrom.firstName ASC"),

    @NamedQuery(name="Relationship.getByProfileFromAndProfileTo",
        query="SELECT f FROM Relationship f WHERE f.profileFrom=?1 AND f.profileTo=?2 ORDER BY f.profileTo.lastName,f.profileTo.firstName ASC")
})
public class Relationship implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name="fromprofileid")
    private Profile profileFrom;

    @ManyToOne
    @JoinColumn(name="toprofileid")
    private Profile profileTo;

    private int level;

    private String howknow;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /** TODO: level name */
    public String getLevelName() {
        return "";
    }

    /** TODO: level name */
    public String getLevelName(Locale locale) {
        return "";
    }

    public String getHowknow() {
        return howknow;
    }

    public void setHowknow(String howknow) {
        this.howknow = howknow;
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

    public Profile getProfileTo() {
        return profileTo;
    }

    public void setProfileTo(Profile p) {
        this.profileTo = p;
    }

    public Profile getProfileFrom() {
        return profileFrom;
    }

    public void setProfileFrom(Profile p) {
        this.profileFrom = p;
    }

}
