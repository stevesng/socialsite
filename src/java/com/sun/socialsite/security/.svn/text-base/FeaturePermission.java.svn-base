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

package com.sun.socialsite.security;

import java.security.Permission;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Represents access to a specific "feature" in the gadgets API.
 * The name attribute should match the feature name (e.g. "socialsite-0.1").
 * A wildcard name ("*") represents access to all features.
 */
public class FeaturePermission extends Permission {

    private static Log log = LogFactory.getLog(FeaturePermission.class);


    /**
     * Creates a new FeaturePermission object with the specified name.
     * @param name the feature name (e.g. "socialsite-0.1")
     */
    public FeaturePermission(String name) {
        super(name);
    }


    /**
     * Returns the canonical string representation of the actions, which currently is
     * the empty string (""), since there are no actions for a FeaturePermission.
     */
    @Override
    public String getActions() {
        return "";
    }


    /**
     * Checks if the specified permission is "implied" by this object.  More specifically,
     * this method returns true if
     * <ul>
     *  <li>
     *   <code>p</code>'s class is the same as this object's class, and
     *  </li>
     *  <li>
     *   <code>p</code>'s name equals or (in the case of wildcards) is implied by this object's name.
     *   Currently, the only supported wildcard is a single asterisk ("*"), which implies any value.
     *  </li>
     * </ul>
     *
     * @param p the permission to check against
     * @return true if the specified permission is equal to or implied by this permission, false otherwise.
     */
    @Override
    public boolean implies(Permission p) {
        if (p instanceof FeaturePermission) {
            return (("*".equals(getName())) || (p.getName().equals(getName())));
        } else {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[name=%s]", getClass().getSimpleName(), this.getName());
    }


    /**
     * A FeaturePermission is equal only to another FeaturePermission which has
     * the same name.
     *
     * @param other the object to check against
     * @returns true if <code>other</code> is a FeaturePermission and has the same
     *  name as this object, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof FeaturePermission != true) return false;
        FeaturePermission o = (FeaturePermission)other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getName(), o.getName());
        eb.append(getActions(), o.getActions());
        return eb.isEquals();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).toHashCode();
    }

}
