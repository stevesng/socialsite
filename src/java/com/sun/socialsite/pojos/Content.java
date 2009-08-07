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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Any content associated with a profile. e.g. photos, uploads, activities,
 * messages etc.
 */
@Entity
@Table(name = "ss_content")
@Inheritance
@DiscriminatorColumn(name = "description")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
public class Content implements Serializable {

    public static final String FROM = "content_from";
    public static final String FROM_PRIMARYEMAIL = "content_from_email";
    public static final String TO = "content_to";
    public static final String SUBJECT = "content_subject";
    public static final String CONTENT = "content_content";
    public static final String SUMMARY = "content_summary";
    public static final String CREATED = "content_created";

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    @ManyToOne
    @JoinColumn(name = "profileid")
    protected Profile profile = null;

    @ManyToOne
    @JoinColumn(name = "groupid")
    protected Group group = null;

    // App that created or uploaded content
    protected String appId = null;

    protected String toprofileId = null;

    protected String replytoId = null;

    protected String title = null;

    protected String desctype = null;

    @Column(name = "CONTENTTYPE")
    protected String contentType = null;

    protected String content = null;

    // foreign key
    protected String visibility = null;

    // true if user has hidden this content
    protected boolean hidden = false;

    protected String status = null;

    // URI of category scheme
    protected String catscheme = null;

    // category label
    protected String catlabel = null;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    protected String summary = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Group getGroup() {
        return group;
    }
    
    public void setGroup(Group g) {
        this.group = g;
    }
    
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToProfileId() {
        return toprofileId;
    }

    public void setToProfileId(String toprofileId) {
        this.toprofileId = toprofileId;
    }

    public String getReplyToId() {
        return replytoId;
    }

    public void setReplyToId(String replytoId) {
        this.replytoId = replytoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescType() {
        return desctype;
    }

    public void setDescType(String desctype) {
        this.desctype = desctype;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCatScheme() {
        return catscheme;
    }

    public void setCatScheme(String catscheme) {
        this.catscheme = catscheme;
    }

    public String getCatLabel() {
        return catlabel;
    }

    public void setCatLabel(String catlabel) {
        this.catlabel = catlabel;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String stat) {
        this.status = stat;
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

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(FROM, this.profile.getDisplayName());
            jo.put(TO, this.toprofileId);
            jo.put(SUBJECT, this.summary);
            jo.put(CONTENT, this.content);
            jo.put(SUMMARY, this.summary);
            jo.put(CREATED, this.created.toString());
            jo.put(FROM_PRIMARYEMAIL, this.profile.getPrimaryEmail());

        } catch (JSONException ex) {
            String msg = "ERROR creating JSON for profile: " + id;
        }
        return jo;
    }

    //------------------------------------------------------- Good citizenship
    @Override
    public String toString() {
        return String.format("%s[id=%s,descType=%s]", getClass().getSimpleName(), getId(), getDescType());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof Content != true) {
            return false;
        }
        Content o = (Content) other;
        return new EqualsBuilder().append(getId(), o.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }
}
