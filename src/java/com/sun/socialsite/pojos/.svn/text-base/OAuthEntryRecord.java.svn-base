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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;


/**
 * For persisting an OAuthEntry.
 * Part of SocialSite OAuth Provider implementation.
 */
@Entity
@Table(name="ss_oauthentry")
public class OAuthEntryRecord implements Serializable {

    @Id
    private String token;

    private String consumerKey;
    private String tokenSecret;
    private String domain;
    private String container;
    private String appId;
    private String callbackUrl;
    private String userId;
    private OAuthEntry.Type type;
    private boolean authorized;

    private Timestamp updated;
    private Timestamp issueTime;

    public OAuthEntryRecord() {}

    public OAuthEntryRecord(OAuthEntry entry) {

        this.token       = entry.token;
        this.consumerKey = entry.consumerKey;
        this.tokenSecret = entry.tokenSecret;
        this.domain      = entry.domain;
        this.container   = entry.container;
        this.appId       = entry.appId;
        this.callbackUrl = entry.callbackUrl;
        this.userId      = entry.userId;
        this.authorized  = entry.authorized;
        this.type        = entry.type;

        this.updated = new Timestamp(new Date().getTime());

        if (entry.issueTime != null) {
            this.issueTime = new Timestamp(entry.issueTime.getTime());
        } else {
            this.issueTime = new Timestamp(System.currentTimeMillis());
        }
    }

    public OAuthEntry getOAuthEntry() {

        OAuthEntry entry = new OAuthEntry();
        entry.token       = this.token;
        entry.consumerKey = this.consumerKey;
        entry.tokenSecret = this.tokenSecret;
        entry.domain      = this.domain;
        entry.container   = this.container;
        entry.appId       = this.appId;
        entry.callbackUrl = this.callbackUrl;
        entry.userId      = this.userId;
        entry.authorized  = this.authorized;
        entry.type        = this.type;

        entry.issueTime = new Date(issueTime.getTime());

        return entry;

    }

    public void update(OAuthEntry entry) {

        this.consumerKey = entry.consumerKey;
        this.tokenSecret = entry.tokenSecret;
        this.domain      = entry.domain;
        this.container   = entry.container;
        this.appId       = entry.appId;
        this.callbackUrl = entry.callbackUrl;
        this.userId      = entry.userId;
        this.authorized  = entry.authorized;
        this.type        = entry.type;
        
        if (entry.issueTime != null) {
            this.issueTime = new Timestamp(entry.issueTime.getTime());
        } else {
            this.issueTime = updated;
        }
    }
}

