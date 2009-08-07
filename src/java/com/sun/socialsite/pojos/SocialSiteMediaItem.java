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
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Represents one media item associated with a given activity
 */
@Entity
@Table(name="ss_mediaitem")
public class SocialSiteMediaItem implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();
    @ManyToOne
    @JoinColumn(name="activityid")
    private SocialSiteActivity activity;
    private String mimetype = null;
    private String type = null;
    private String url = null;

    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }

    public void setActivity(SocialSiteActivity id) {
        this.activity = id;
    }
    
    public SocialSiteActivity getActivity() {
        return this.activity;
    }

    public void setMimetype(String id) {
        this.mimetype = id;
    }
    
    public String getMimetype() {
        return this.mimetype;
    }

    public void setType(String id) {
        this.type = id;
    }
    
    public String getType() {
        return this.type;
    }

    public void setUrl(String id) {
        this.url = id;
    }
    
    public String getUrl() {
        return this.url;
    }
}
