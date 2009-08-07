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

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.MapKey;
import javax.persistence.CascadeType;

import java.sql.Timestamp;

import java.io.Serializable;

import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.socialsite.business.impl.JPAListenerManagerImpl;

/**
 * Logs one activity performed by user.
 */
@Entity
@Table(name = "ss_activity")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries ({
    @NamedQuery(name="Activity.getAll",
        query="SELECT a FROM SocialSiteActivity a ORDER BY a.updated DESC"),

    @NamedQuery(name="Activity.getByProfile",
        query="SELECT a FROM SocialSiteActivity a WHERE a.profile = ?1 ORDER BY a.updated DESC"),

    @NamedQuery(name="Activity.getByProfileAndType",
        query="SELECT a FROM SocialSiteActivity a WHERE a.profile = ?1 " +
        "AND a.type = ?2 ORDER BY a.updated DESC"),

    @NamedQuery(name="Activity.getFriendsActivityByProfile",
        query="SELECT a FROM SocialSiteActivity a WHERE a.profile = ?1 OR "
        +"a.profile.id IN (SELECT r.profileTo.id FROM Relationship r WHERE r.profileFrom = ?1) "
        +"ORDER BY a.updated DESC"),
        
    @NamedQuery(name="Activity.getByGroup",
        query="SELECT a FROM SocialSiteActivity a WHERE a.group = ?1 ORDER BY a.updated DESC"),
    
    @NamedQuery(name="Activity.getByGadget",
        query="SELECT a FROM SocialSiteActivity a WHERE a.appId = ?1 ORDER BY a.updated DESC")
})
public class SocialSiteActivity implements Serializable {

    private static Log log = LogFactory.getLog(SocialSiteActivity.class);

    // Activity descType definitions
    public transient static final String NEW_MEMBERSHIP  = "NEW_MEMBERSHIP";
    public transient static final String NEW_ADMIN       = "NEW_ADMIN";
    public transient static final String NEW_FRIENDSHIP  = "NEW_FRIENDSHIP";
    public transient static final String EDITED_PROFILE  = "EDITED_PROFILE";
    public transient static final String CREATED_GROUP   = "CREATED_GROUP";
    public transient static final String LEFT_GROUP      = "LEFT_GROUP";
    public transient static final String STATUS          = "STATUS";
    public transient static final String APP_MESSAGE     = "APP_MESSAGE";

    @Id
    @Column(nullable = false, updatable = false)
    private String id = UUID.randomUUID().toString();
    private String appId = null;
    private String type = null;
    private String body = null;
    private String bodyId = null;
    private String title = null;
    private String titleId = null;
    private String externalId = null;

    @ManyToOne
    @JoinColumn(name = "profileid")
    protected Profile profile = null;

    @ManyToOne
    @JoinColumn(name = "groupid")
    protected Group group = null;

    @OneToMany(mappedBy = "activity", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SocialSiteMediaItem> mediaItems;

    @OneToMany(mappedBy = "activity", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @MapKey(name = "name")
    private Map<String, TemplateParameter> templateParameters;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    public SocialSiteActivity() {
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    public String getAppId() {
        return this.appId;
    }
        
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return this.type;
    }    
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return this.title;
    }    
    
    public void setTitleId(String titleId) {
        this.titleId = titleId;
    }
    
    public String getTitleId() {
        return this.titleId;
    }    
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getBody() {
        return this.body;
    }    

    public void setBodyId(String bodyId) {
        this.bodyId = bodyId;
    }
    
    public String getBodyId() {
        return this.bodyId;
    }    

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getExternalId() {
        return this.externalId;
    }    

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    public Profile getProfile() {
        return this.profile;
    }    

    public void setGroup(Group group) {
        this.group = group;
    }
    
    public Group getGroup() {
        return this.group;
    }    

    public void addMediaItems(SocialSiteMediaItem m) {
        m.setActivity(this);
        this.mediaItems.add(m);
    }
    
    public List<SocialSiteMediaItem> getMediaItems() {
        return this.mediaItems;
    }
    
    public void addTemplateParameters(TemplateParameter t) {
        t.setActivity(this);
        this.templateParameters.put(t.getName(), t);
    }
    
    public Map<String, TemplateParameter> getTemplateParameters() {
        return this.templateParameters;
    }
    
    public TemplateParameter getATemplateParameter(String name) {
        return this.templateParameters.get(name);
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
}
