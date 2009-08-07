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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Social request: group or relationship.
 */
@Entity
@Table(name = "ss_socialrequest")
@Inheritance
@DiscriminatorColumn(name = "requesttype")
public abstract class SocialRequest implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    protected String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "fromprofileid")
    protected Profile profileFrom = null;

    @Column(name = "fromlevel")
    protected int levelFrom = 2; // default to friend level

    @ManyToOne
    @JoinColumn(name = "toprofileid")
    protected Profile profileTo = null;

    @Column(name = "tolevel")
    protected int levelTo = 2; // default to friend level

    protected String howknow;

    @ManyToOne
    @JoinColumn(name = "groupid")
    protected Group group = null;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    public enum Status {
        PENDING, ACCEPTED, IGNORED, REJECTED
    }
    @Enumerated(EnumType.STRING)
    protected Status status = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Profile getProfileFrom() {
        return profileFrom;
    }

    public void setProfileFrom(Profile fromuser) {
        this.profileFrom = fromuser;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = new Timestamp(created.getTime());
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
