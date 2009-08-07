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
 * <p>
 *  Represents access to an <code>App</code>.  Each AppPermission object consists
 *  of a URL pattern (either "*" or a valid URL) and a set of actions which are
 *  valid for that URL apttern.  
 * </p>
 * <p>
 *  The URL pattern corresponds to the Spec URL for the App(s) covered by this
 *  permission.  Currently, the URL pattern must either be "*" (meaning that all
 *  apps are covered) or a valid and specific URL (meaning that only the app whose
 *  spec is available at that exact URL is covered).
 * </p>
 * <p>
 *  The actions to be granted are passed to the constructor in a string containing
 *  a list of one or more comma-separated keywords. The possible keywords are
 *  "read", "write", "execute", and "delete". Their meaning is defined as follows: 
 * </p>
 * <table>
 *  <thead>
 *   <tr><td>Action</td><td>Description</td></tr>
 *  </thead>
 *  <tbody>
 *   <tr><td>read</td><td>read permission</td></tr>
 *   <tr><td>write</td><td>write permission</td></tr>
 *   <tr><td>execute</td><td>execute permission</td></tr>
 *   <tr><td>delete</td><td>delete permission</td></tr>
 *  </tbody>
 * </table>
 */
public class AppPermission extends Permission {

    private static Log log = LogFactory.getLog(AppPermission.class);

    private static List<String> allowedActions = new ArrayList<String>(4);
    static {
        allowedActions.add("read");
        allowedActions.add("write");
        allowedActions.add("execute");
        allowedActions.add("delete");
    }

    private Set<String> actionSet;


    /**
     * Creates a new AppPermission object with the specified name.
     * @param name the feature name (e.g. "socialsite-0.1")
     */
    public AppPermission(String urlPattern, String actions) {
        super(urlPattern);
        actionSet = new HashSet<String>();
        String[] actionsArray = actions.split(",");
        for (int i = 0; i < actionsArray.length; i++) {
            String actionName = actionsArray[i].trim();
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
     * present actions in the following order: read, write, execute, delete.
     * For example, if this AppPermission object allows both write and read
     * actions, a call to getActions  will return the string "read,write". 
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
     * Checks if the specified permission is "implied" by this object.  More specifically,
     * this method returns true only if all of the following are true:
     * <ul>
     *  <li>
     *   <code>p</code>'s class is the same as this object's class.
     *  </li>
     *  <li>
     *   <code>p</code>'s name equals or (in the case of wildcards) is implied by this object's name.
     *   Currently, the only supported wildcard is a single asterisk ("*"), which implies any value.
     *  </li>
     *  <li>
     *   All of <code>p</code>'s actions are present in this object's actions list.
     *  </li>
     * </ul>
     *
     * @param p the permission to check against
     * @return true if the specified permission is equal to or implied by this permission, false otherwise.
     */
    @Override
    public boolean implies(Permission p) {
        if (p instanceof AppPermission) {
            return implies((AppPermission)p);
        } else {
            return false;
        }
    }


    private boolean implies(AppPermission p) {
        if (!(("*".equals(getName())) || (p.getName().equals(getName())))) {
            return false;
        }
        for (String actionName : p.actionSet) {
            if (!actionSet.contains(actionName)) {
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
     * A AppPermission is equal only to another AppPermission which has
     * the same name.
     *
     * @param other the object to check against
     * @returns true if <code>other</code> is a AppPermission and has the same
     *  name as this object, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other instanceof AppPermission != true) return false;
        AppPermission o = (AppPermission)other;
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
