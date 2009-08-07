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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Pojo for context rules used by ConsumerContextValidator.
 * Each entry is a source for a rule. Assertions are returned
 * and set as List<String>.
 *
 * This is part of a fix for issue:
 * https://socialsite.dev.java.net/issues/show_bug.cgi?id=336
 * Please see the issue for more information (and remove this
 * text when the issue is fixed).
 */
@Entity
@Table(name="ss_contextrule")
@EntityListeners({JPAListenerManagerImpl.Listener.class})
@NamedQueries({
    @NamedQuery(name="ContextRule.getAll",
        query="SELECT cr FROM ContextRule cr"),
    @NamedQuery(name="ContextRule.getById",
        query="SELECT cr FROM ContextRule cr WHERE cr.id=?1")
// no test yet
//    @NamedQuery(name="ContextRule.getBySource",
//        query="SELECT cr FROM ContextRule cr WHERE cr.source=?1")
})
public class ContextRule implements Serializable {

    @Id
    @Column(nullable=false,updatable=false)
    private String id = UUID.randomUUID().toString();

    private String source;

    private boolean direct;

    // Assertions to accept
    private String accept;

    // Assertions to reject
    private String reject;

    private Timestamp created = new Timestamp(System.currentTimeMillis());

    @Version
    private Timestamp updated = new Timestamp(created.getTime());

    @Transient
    private static final String SEP = ",";

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getAccept() {
        if (accept == null || accept.length() == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(accept.split(SEP));
    }

    public void setAccept(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() > 0) {
                sb.append(SEP);
            }
            sb.append(s);
        }
        accept = sb.toString();
    }

    public List<String> getReject() {
        if (reject == null || reject.length() == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(reject.split(SEP));
    }

    public void setReject(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() > 0) {
                sb.append(SEP);
            }
            sb.append(s);
        }
        reject = sb.toString();
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

    public String getId() {
        return id;
    }

    //-------- Good citizenship --------//

    @Override
    public String toString() {
        return String.format("%s[source=%s, direct=%s]",
            getClass().getSimpleName(), this.source, this.direct);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        final ContextRule cRule = (ContextRule) other;
        return new EqualsBuilder().append(id, cRule.id).isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder().append(id);
        hcb.append(source);
        return hcb.toHashCode();
    }

}
