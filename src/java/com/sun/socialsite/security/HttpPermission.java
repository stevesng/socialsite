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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Represents HTTP(S) access to a URL through the SocialSite proxy.
 * The name attribute should match the allowed domain, for example sun.com.
 * A wildcard name ("*") represents access to all domains.  The actions attribute
 * matches the HTTP request type, for example GET.
 */
public class HttpPermission extends Permission {

    private static Log log = LogFactory.getLog(HttpPermission.class);

    private static List<String> allowedActions = new ArrayList<String>(5);
    static {
        allowedActions.add("DELETE");
        allowedActions.add("GET");
        allowedActions.add("HEAD");
        allowedActions.add("POST");
        allowedActions.add("PUT");
    }

    private Set<String> actionSet;


    /**
     * Creates a new HttpPermission object with the specified name.
     * @param name the domain name (e.g. "sun.com")
     * @param actions HTTP method names (e.g. "GET")
     */
    public HttpPermission(String name, String actions) {
        super(name);
        actionSet = new HashSet<String>();
        String[] actionsArray = actions.split(",");
        for (int i = 0; i < actionsArray.length; i++) {
            String actionName = actionsArray[i].trim().toUpperCase();
            if (allowedActions.contains(actionName)) {
                actionSet.add(actionName);
            } else {
                String msg = String.format("%s is not an allowed action name", actionName);
                throw new IllegalArgumentException(msg);
            }
        }
    }


    /**
     * Returns the "canonical string representation" of the actions.  Like
     * <code>java.security.FilePermission</code>, this method always returns
     * present actions in the following order: DELETE, GET, HEAD, POST, PUT.
     * For example, if this HttpPermission object allows both GET and POST 
     * actions, a call to getActions  will return the string "GET,POST".
     *
     * @return the canonical string representation of the actions.
     */
    @Override
    public String getActions() {
        StringBuilder sb = new StringBuilder();
        for (String actionName : allowedActions) {
            if (actionSet.contains(actionName)) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(actionName);
            }
        }
        return sb.toString();
    }


    /**
     * Checks if the specified permission is "implied" by this object.
     * More specifically, this method returns true if
     * <ul>
     *  <li>
     *   <code>p</code>'s class is the same as this object's class, and
     *  </li>
     *  <li>
     *   <code>p</code>'s name equals or (in the case of wildcards) is implied
     *   by this object's name. Currently, the only supported wildcard is
     *   a single asterisk ("*"), which implies any value.
     *  </li>
     * </ul>
     *
     * @param p the permission to check against
     * @return true if the specified permission is equal to or implied by
     *   this permission, false otherwise.
     */
    @Override
    public boolean implies(Permission p) {
        boolean result = false;
        if (p instanceof HttpPermission) {
            result = implies((HttpPermission)p);
        }
        log.debug("result="+result);
        return result;
    }


    private boolean implies(HttpPermission p) {
        if (!(("*".equals(getName())) || (p.getName().equals(getName())))) {
            log.debug("name mismatch");
            return false;
        }
        for (String actionName : p.actionSet) {
            if (!actionSet.contains(actionName)) {
                log.debug("action mismatch");
                return false;
            }
        }
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[name=%s,actions=%s]", getClass().getSimpleName(), getName(), getActions());
    }


    /**
     * A HttpPermission is equal only to another HttpPermission
     * which has the same name and actions.
     *
     * @param other the object to check against
     * @returns true if <code>other</code> is a HttpPermission and has the same
     *  name as this object, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof HttpPermission != true) {
            return false;
        }
        HttpPermission o = (HttpPermission)other;
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
