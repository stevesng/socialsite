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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:hidden name="groupid" value="%{groupid}"/>

<c:choose>
  <c:when test='${groupid != null}'>
    <c:if test='${(members != null) && (fn:length(members) > 0)}'>
      <s:form action="GroupMemberDetails" method="GET">
        <p><s:text name="Group.currentMembers" /></p>
        <s:hidden name="groupid" value="%{groupid}"/>
        <select name="userid" size="5">
          <s:iterator id="mem" value="members">
            <c:if test='${members != null}'>
              <option value="${mem.userProfile.userId}">
                <c:out value="${mem.userProfile.userId}"/>
              </option>
            </c:if>
          </s:iterator>
        </select>
        <s:submit value="%{getText('GroupMembers.administer')}"/>
      </s:form>

      <hr/>
    </c:if>

    <s:form action="GroupMemberDetailsSave" method="POST"
            enctype="multipart/form-data">
      <s:hidden name="groupid" value="%{groupid}"/>
      <s:textfield key="Group.newMember" name="userid" />
      <s:submit />
    </s:form>
  </c:when>
  <c:otherwise>
    <p><s:text name="Groups.noGroupFound" /></p>
  </c:otherwise>
</c:choose>

