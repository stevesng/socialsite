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

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.impl.JPAListenerManagerImpl;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.spec.GadgetSpec;


/**
 * Represents specific instance of an App (belong to a user or group).
 */
@Entity
@Table(name ="ss_appinstance")
@EntityListeners({ JPAListenerManagerImpl.Listener.class })
@NamedQueries({
    @NamedQuery(name="AppInstance.getAll",
        query="SELECT ai FROM AppInstance ai"
    ),
    @NamedQuery(name="AppInstance.getById",
        query="SELECT ai FROM AppInstance ai WHERE ai.id=?1"
    ),
    @NamedQuery(name="AppInstance.getByAppAndGroup",
        query="SELECT ai FROM AppInstance ai WHERE ai.app=?1 AND ai.group=?2 AND ai.collection IS NULL ORDER BY ai.created"
    ),
    @NamedQuery(name="AppInstance.getByAppAndProfile",
        query="SELECT ai FROM AppInstance ai WHERE ai.app=?1 AND ai.profile=?2 AND ai.collection IS NULL ORDER BY ai.created"
    ),
    @NamedQuery(name="AppInstance.getByGroupAndCollection",
        query="SELECT ai FROM AppInstance ai WHERE ai.group=?1 AND ai.collection=?2 ORDER BY ai.position, ai.created"
    ),
    @NamedQuery(name="AppInstance.getByProfileAndCollection",
        query="SELECT ai FROM AppInstance ai WHERE ai.profile=?1 AND ai.collection=?2 ORDER BY ai.position, ai.created"
    )
})
public class AppInstance implements Serializable {

    private static Log log = LogFactory.getLog(AppInstance.class);

    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="appinstance")
    @TableGenerator(name="appinstance", table="ss_ids", pkColumnName="name", valueColumnName="value", pkColumnValue="appinstance")
    @Column(nullable=false,updatable=false)
    private Long id;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    @ManyToOne
    @JoinColumn(name="appid")
    private App app;

    @ManyToOne
    @JoinColumn(name="profileid")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name="groupid")
    private Group group;

    private String collection;

    private String position;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
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

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public GadgetSpec getGadgetSpec() {
        GadgetSpec result = null;
        try {
            result = Factory.getSocialSite().getAppManager().getGadgetSpecByURL(app.getURL());
        } catch (SocialSiteException e) {
            log.error(e);
        }
        return result;
    }

    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return String.format("AppInstance[user=%s,group=%s,app.url=%s]", getUserName(), getGroupName(), getApp().getURL());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof AppInstance != true) return false;
        AppInstance o = (AppInstance)other;
        return new EqualsBuilder().append(getId(), o.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

    //------------------------------------------------------- Private Utility Methods

    private String getUserName() {
        Profile profile = getProfile();
        return ((profile != null) ? getProfile().getUserId() : null);
    }

    private String getGroupName() {
        Group group = getGroup();
        return ((group != null) ? getGroup().getHandle() : null);
    }

}
