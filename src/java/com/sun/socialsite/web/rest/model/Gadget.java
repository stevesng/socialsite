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
package com.sun.socialsite.web.rest.model;

import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.AppInstance;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.opensocial.service.PersonServiceImpl;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONObject;


/**
 * Represents an available (and App) or installed OpenSocial Gadget (and AppInstance).
 */
public class Gadget {
    private String       appId = null;
    private Long         moduleId = 0L;
    private String       url = null;

    private Date         created = null;
    private Date         updated = null;
    private Date         installed = null;

    private String       title = null;
    private String       titleUrl = null;
    private String       directoryTitle = null;
    private String       description = null;
    private String       thumbnailUrl = null;

    private String       author = null;
    private String       authorEmail = null;
    private String       authorLink = null;

    private String       collection;
    private String       position;

    private Boolean      scrolling = null;
    private Boolean      singleton = null;
    private Integer      height = null;
    private Integer      width = null;

    private Person       person = null;
    private GroupWrapper group = null;

    /**
     * Default constructor needed for testing?
     *
    public Gadget() {
        super();
    }*/

    /**
     * Construct Gadget to wrap a SocailSite App, which represents a Gadget
     * available for use by the end-user.
     * @param app
     */
    public Gadget(App app) {
        init(null, app);
    }

    /**
     * Construct Gadget to wrap a SocailSite AppInstance, which represents a
     * Gadget which is installed in a user or groups collection.
     * @param app
     */
    public Gadget(AppInstance appInstance) {
        init(appInstance, appInstance.getApp());
    }

    private void init(AppInstance appInstance, App app) {

        if (app != null) {
            this.setAppId(          app.getId());
            this.setAuthor(         app.getAuthor());
            this.setAuthorEmail(    app.getAuthorEmail());
            this.setCreated(        app.getCreated());
            this.setDescription(    app.getDescription());
            this.setDirectoryTitle( app.getDirectoryTitle());
            this.setHeight(         app.getHeight());
            this.setWidth(          app.getWidth());
            this.setScrolling(      app.getScrolling());
            this.setSingleton(      app.getSingleton());
            this.setTitle(          app.getTitle());

            if (app.getURL() != null) {
                this.setUrl( app.getURL().toString());
            }
            if (app.getThumbnail() != null) {
                this.setThumbnailUrl(app.getThumbnail().toString());
            }
            if (app.getTitleURL() != null) {
                this.setTitleUrl(app.getTitleURL().toString());
            }
            if (app.getAuthorLink() != null) {
                this.setAuthorUrl(app.getAuthorLink().toString());
            }
        }

        if (appInstance != null) {
            this.setModuleId(appInstance.getId());
            this.setPosition(appInstance.getPosition());
            this.setCollection(appInstance.getCollection());

            if (appInstance.getGroup() != null) {
                this.setGroup(new GroupWrapper(appInstance.getGroup()));
            }
            if (appInstance.getProfile() != null) {
                BeanJsonConverter conv = PersonServiceImpl.getJsonConverter();
                JSONObject jp = appInstance.getProfile().toJSON(Profile.Format.OPENSOCIAL_MINIMAL);
                this.setPerson(conv.convertToObject(jp.toString(), PersonEx.class));
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        Gadget otherGadget = (Gadget)other;
        if (this.moduleId != null && otherGadget.moduleId != null) {
            return this.moduleId.equals(otherGadget.moduleId);
        }
        if (this.appId != null && otherGadget.appId != null) {
            return this.appId.equals(otherGadget.appId);
        }
        return false;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the moduleId
     */
    public Long getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId the moduleId to set
     */
    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the updated
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the titleURL
     */
    public String getTitleUrl() {
        return titleUrl;
    }

    /**
     * @param titleURL the titleURL to set
     */
    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    /**
     * @return the directoryTitle
     */
    public String getDirectoryTitle() {
        return directoryTitle;
    }

    /**
     * @param directoryTitle the directoryTitle to set
     */
    public void setDirectoryTitle(String directoryTitle) {
        this.directoryTitle = directoryTitle;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the thumbnail
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnailUrl(String thumbnail) {
        this.thumbnailUrl = thumbnail;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the authorEmail
     */
    public String getAuthorEmail() {
        return authorEmail;
    }

    /**
     * @param authorEmail the authorEmail to set
     */
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    /**
     * @return the authorLink
     */
    public String getAuthorUrl() {
        return authorLink;
    }

    /**
     * @param authorLink the authorLink to set
     */
    public void setAuthorUrl(String authorLink) {
        this.authorLink = authorLink;
    }

    /**
     * @return the collection
     */
    public String getCollection() {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the scrolling
     */
    public Boolean getScrolling() {
        return scrolling;
    }

    /**
     * @param scrolling the scrolling to set
     */
    public void setScrolling(Boolean scrolling) {
        this.scrolling = scrolling;
    }

    /**
     * @return the singleton
     */
    public Boolean getSingleton() {
        return singleton;
    }

    /**
     * @param singleton the singleton to set
     */
    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * @return the group
     */
    public GroupWrapper getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(GroupWrapper group) {
        this.group = group;
    }

    /**
     * @return the installed
     */
    public Date getInstalled() {
        return installed;
    }

    /**
     * @param installed the installed to set
     */
    public void setInstalled(Date installed) {
        this.installed = installed;
    }

}
