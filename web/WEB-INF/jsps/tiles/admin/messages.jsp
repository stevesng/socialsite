<%--
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 2 only ("GPL") or the Common Development
 and Distribution License("CDDL") (collectively, the "License").  You
 may not use this file except in compliance with the License. You can obtain
 a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
 or legal/LICENSE.txt.  See the License for the specific language governing
 permissions and limitations under the License.

 When distributing the software, include this License Header Notice in each
 file and include the License file at legal/LICENSE.txt.  Sun designates this
 particular file as subject to the "Classpath" exception as provided by Sun
 in the GPL Version 2 section of the License file that accompanied this code.
 If applicable, add the following below the License Header, with the fields
 enclosed by brackets [] replaced by your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 Contributor(s):

 If you wish your version of this file to be governed by only the CDDL or
 only the GPL Version 2, indicate your decision by adding "[Contributor]
 elects to include this software in this distribution under the [CDDL or GPL
 Version 2] license."  If you don't indicate a single choice of license, a
 recipient has the option to distribute your version of this file under
 either the CDDL, the GPL Version 2 or to extend the choice of license to
 its licensees as provided above.  However, if you add GPL Version 2 code
 and therefore, elected the GPL Version 2 license, then the option applies
 only if the new code is made subject to such option by the copyright
 holder.
--%>

<%@ taglib prefix="s" uri="/struts-tags" %>


<s:if test="error != null">
    <div class="error"><b><s:text name="error"/></b> <s:property value="error"/></div>
</s:if>

<s:elseif test="warning != null">
    <div class="warning"><b><s:text name="warning"/></b> <s:property value="warning"/></div>
</s:elseif>

<s:elseif test="success != null">
    <div class="success"><b><s:text name="success"/></b> <s:property value="success"/></div>
</s:elseif>


<%-- standard Struts errors and messages --%>

<s:if test="!actionErrors.isEmpty || !fieldErrors.isEmpty">
    <div class="actionErrors">
        <ul>
            <s:iterator id="actionError" value="actionErrors">
                <li><s:property value="#actionError" /></li>
            </s:iterator>
            <s:iterator id="fieldErrorName" value="fieldErrors.keySet()">
                <s:iterator id="fieldErrorValue" value="fieldErrors[#fieldErrorName]">
                    <li><s:property value="#fieldErrorValue" /></li>
                </s:iterator>
            </s:iterator>
        </ul>
    </div>
</s:if>

<s:if test="!actionMessages.isEmpty">
    <div class="actionMessages">
        <ul>
            <s:iterator id="actionMessage" value="actionMessages">
                <li><s:property value="#actionMessage" /></li>
            </s:iterator>
        </ul>
    </div>
</s:if>

