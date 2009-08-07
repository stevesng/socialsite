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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Logs a message for a user.
 */
@Entity
@DiscriminatorValue("MESSAGE")
@NamedQueries ({
    @NamedQuery(name="MessageContent.getAll",
        query="SELECT a FROM MessageContent a ORDER BY a.updated DESC"),

        // Used for admin
    @NamedQuery(name="MessageContent.getGroupNotification",
        query="SELECT a FROM MessageContent a WHERE a.group = ?1 " +
        "ORDER BY a.updated DESC"),

        // Used for admin
    @NamedQuery(name="MessageContent.getByToProfileAndLabel",
        query="SELECT a FROM MessageContent a WHERE a.toprofileId = ?1 " +
        "AND a.catlabel = ?2 ORDER BY a.updated DESC"),

    @NamedQuery(name="MessageContent.getByProfileTypeLabel",
        query="SELECT a FROM MessageContent a WHERE a.profile = ?1 " +
        "AND a.desctype = ?2 AND a.catlabel = ?3 ORDER BY a.updated DESC"),
        
    @NamedQuery(name="MessageContent.getByToProfileTypeLabel",
        query="SELECT a FROM MessageContent a WHERE a.toprofileId = ?1 " +
        "AND a.desctype = ?2 AND a.catlabel = ?3 ORDER BY a.updated DESC"),
    
    @NamedQuery(name="MessageContent.getSystemNotifications",
        query="SELECT a FROM MessageContent a WHERE a.catlabel = ?1 " +
        "ORDER BY a.updated DESC"),
        
    @NamedQuery(name="MessageContent.getGroupNotificationsForUser",
        query="SELECT a FROM MessageContent a, GroupRelationship b WHERE "+
        "a.group = b.group AND b.userProfile = ?1 ORDER BY a.updated DESC")
})
public class MessageContent extends Content {

    /** Types of messages */
    public transient static final String NOTIFICATION = "NOTIFICATION";
    public transient static final String GROUP_INVITE = "INVITATION";
    public final static String SYS_NOTIFICATIONS = "SYSTEM_NOTIFICATIONS";
    public final static String GROUP_NOTIFICATIONS = "GROUP_NOTIFICATIONS";

    /** Status of messages */
    public transient static final String READ = "READ";
    public transient static final String UNREAD = "UNREAD";
    public transient static final String DELETED = "DELETED";
    
    public transient static final String CAT_SCHEME = "MESSAGE";

    public MessageContent() {
        super.setCatScheme(this.CAT_SCHEME);
        super.setContentType("text/plain");
    }
}
