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
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerIndex;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret;


/**
 * For persisting a BasicOAuthStoreConsumerKeyAndSecret.
 * Part of SocialSite OAuth Consumer implementation.
 */
@Entity
@Table(name="ss_oauthconsumer")
public class OAuthConsumerRecord implements Serializable {

    @Id
    private int id;
    private String keyName;
    private String consumerKey;
    private String consumerSecret;
    private BasicOAuthStoreConsumerKeyAndSecret.KeyType keyType;
    private Timestamp updated;

    public OAuthConsumerRecord() {}

    public OAuthConsumerRecord(
        BasicOAuthStoreConsumerIndex index, BasicOAuthStoreConsumerKeyAndSecret data) {
        this.id = index.hashCode();
        update(data);
    }

    public BasicOAuthStoreConsumerKeyAndSecret getBasicOAuthStoreConsumerKeyAndSecret() {
        return new BasicOAuthStoreConsumerKeyAndSecret(consumerKey, consumerSecret, keyType, keyName);
    }

    public void update(BasicOAuthStoreConsumerKeyAndSecret data) {
        this.keyName        = data.getKeyName();
        this.consumerKey    = data.getConsumerKey();
        this.consumerSecret = data.getConsumerSecret();
        this.keyType        = data.getKeyType();
        this.updated = new Timestamp(new Date().getTime());
    }
}

