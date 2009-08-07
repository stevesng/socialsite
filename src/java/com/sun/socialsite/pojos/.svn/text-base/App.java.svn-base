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
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Represents a Google app.
 * See: http://code.google.com/apis/apps/docs/reference.html
 */
@Entity
@Table(name ="ss_app")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="App.getAll",
        query="SELECT a FROM App a ORDER BY a.title ASC"),

    @NamedQuery(name="App.getAllInDirectory",
        query="SELECT a FROM App a WHERE a.showInDirectory=true ORDER BY a.title ASC"),

    @NamedQuery(name="App.getOldest",
        query="SELECT a FROM App a ORDER BY a.created ASC"),

    @NamedQuery(name="App.getByURL",
        query="SELECT a FROM App a WHERE a.url=?1"),

    @NamedQuery(name="App.getByAuthor",
        query="SELECT a FROM App a WHERE a.author=?1")

})
public class App implements Serializable {

    private static Log log = LogFactory.getLog(App.class);

    public static final String ID = "id";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    private String author = null;

    private String authorEmail = null;

    private String authorLink = null;

    private String directoryTitle = null;

    private Boolean showInDirectory = Boolean.FALSE;

    @Lob
    private String description = null;

    private Integer height = null;

    private Boolean scrolling = null;

    private Boolean singleton = null;

    private String thumbnail = null;

    private String title = null;

    private String titleURL = null;

    private Integer width = null;

    private String url = null;

    @Transient
    private Map<String, URL> urls = new HashMap<String, URL>();


    // TODO: Where does this belong?
    public static App readFromURL(URL url) throws Exception {
        HttpURLConnection con = (HttpURLConnection)(url.openConnection());
        con.setDoOutput(false);
        // TODO: figure out why this is necessary for HTTPS URLs
        if (con instanceof HttpsURLConnection) {
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    if ("localhost".equals(urlHostName) && "127.0.0.1".equals(session.getPeerHost())) {
                        return true;
                    } else {
                        log.warn("URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                        return false;
                    }
                }
            };
            ((HttpsURLConnection)con).setDefaultHostnameVerifier(hv);
        }
        con.connect();
        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException(con.getResponseMessage());
        }
        InputStream in = con.getInputStream();
        return readFromStream(in, url);
    }

    // TODO: Where does this belong?
    public static App readFromStream(InputStream in, URL urlToStore) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(in);
        in.close();

        App app = new App();
        app.setURL(urlToStore);

        Element modulePrefsElement = (Element)(doc.getElementsByTagName("ModulePrefs").item(0));
        app.setAuthor(modulePrefsElement.getAttribute("author"));
        app.setAuthorEmail(modulePrefsElement.getAttribute("author_email"));
        if (! "".equals(modulePrefsElement.getAttribute("author_link"))) {
            app.setAuthorLink(new URL(urlToStore, modulePrefsElement.getAttribute("author_link")));
        }
        app.setDescription(modulePrefsElement.getAttribute("description"));
        app.setDirectoryTitle(modulePrefsElement.getAttribute("directory_title"));
        if (! "".equals(modulePrefsElement.getAttribute("height"))) {
            app.setHeight(Integer.parseInt(modulePrefsElement.getAttribute("height")));
        }
        if (! "".equals(modulePrefsElement.getAttribute("show_in_directory"))) {
            app.setShowInDirectory(Boolean.valueOf(modulePrefsElement.getAttribute("show_in_directory")));
        }
        if (! "".equals(modulePrefsElement.getAttribute("scrolling"))) {
            app.setScrolling(Boolean.valueOf(modulePrefsElement.getAttribute("scrolling")));
        }
        if (! "".equals(modulePrefsElement.getAttribute("singleton"))) {
            app.setSingleton(Boolean.valueOf(modulePrefsElement.getAttribute("singleton")));
        }
        if (! "".equals(modulePrefsElement.getAttribute("thumbnail"))) {
            app.setThumbnail(new URL(urlToStore, modulePrefsElement.getAttribute("thumbnail")));
        }
        app.setTitle(modulePrefsElement.getAttribute("title"));
        if (! "".equals(modulePrefsElement.getAttribute("title_url"))) {
            app.setTitleURL(new URL(urlToStore, modulePrefsElement.getAttribute("title_url")));
        }
        if (! "".equals(modulePrefsElement.getAttribute("width"))) {
            app.setWidth(Integer.parseInt(modulePrefsElement.getAttribute("width")));
        }

        return app;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public URL getAuthorLink() {
        return urls.get("authorLink");
    }

    public void setAuthorLink(URL authorLink) {
        urls.put("authorLink", authorLink);
    }

    public String getDirectoryTitle() {
        return directoryTitle;
    }

    public void setDirectoryTitle(String directoryTitle) {
        this.directoryTitle = directoryTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getScrolling() {
        return scrolling;
    }

    public void setScrolling(Boolean scrolling) {
        this.scrolling = scrolling;
    }

    public Boolean getSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public URL getThumbnail() {
        return urls.get("thumbnail");
    }

    public void setThumbnail(URL thumbnail) {
        urls.put("thumbnail", thumbnail);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getTitleURL() {
        return urls.get("titleURL");
    }

    public void setTitleURL(URL titleURL) {
        urls.put("titleURL", titleURL);
    }

    public URL getURL() {
        return urls.get("url");
    }

    public void setURL(URL url) {
        urls.put("url", url);
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
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

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put(ID, this.id);
            jo.put(URL, this.url);
            jo.put(TITLE, this.title);
            jo.put(DESCRIPTION, this.description);
        } catch (JSONException ex) {
            String msg = "Failed to create JSON for app: " + this.id;
            log.error(msg, ex);
        }
        return jo;
    }

    //-------------------------------------------------------

    @PrePersist
    @PreUpdate
    protected void doBeforeStorage() throws Exception {
        authorLink = ((urls.get("authorLink") != null) ? urls.get("authorLink").toExternalForm() : null);
        thumbnail = ((urls.get("thumbnail") != null) ? urls.get("thumbnail").toExternalForm() : null);
        titleURL = ((urls.get("titleURL") != null) ? urls.get("titleURL").toExternalForm() : null);
        url = ((urls.get("url") != null) ? urls.get("url").toExternalForm() : null);
    }

    @PostLoad
    protected void doAfterLoad() throws MalformedURLException {
        urls.put("authorLink", ((authorLink != null) ? new URL(authorLink) : null));
        urls.put("thumbnail", ((thumbnail != null) ? new URL(thumbnail) : null));
        urls.put("titleURL", ((titleURL != null) ? new URL(titleURL) : null));
        urls.put("url", ((url != null) ? new URL(url) : null));
    }

    //------------------------------------------------------- Good citizenship

    @Override
    public String toString() {
        return String.format("%s[url=%s]", getClass().getSimpleName(), this.getURL());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof App != true) return false;
        App o = (App)other;
        return new EqualsBuilder().append(getURL(), o.getURL()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getURL()).toHashCode();
    }

    /**
     * @return the showInDirectory
     */
    public Boolean getShowInDirectory() {
        return showInDirectory;
    }

    /**
     * @param showInDirectory the showInDirectory to set
     */
    public void setShowInDirectory(Boolean showInDirectory) {
        this.showInDirectory = showInDirectory;
    }

}
