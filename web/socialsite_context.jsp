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

<%@ page language="java" %>
<%@ page contentType="application/json" %>
<%@ page import="com.sun.socialsite.business.EmfProvider" %>
<%@ page import="com.sun.socialsite.userapi.User" %>
<%@ page import="com.sun.socialsite.userapi.UserManager" %>
<%@ page import="com.sun.socialsite.userapi.UserManagerImpl" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="org.json.JSONObject" %>
<%!
  private static JSONObject getPersonJSON(String userId) throws Exception {
      if (StringUtils.isEmpty(userId)) {
          return null;
      } else {
          EntityManager em = EmfProvider.getEmf().createEntityManager();
          UserManager userManager = new UserManagerImpl(em);
          User user = userManager.getUserByUserId(userId);
          em.close();
          JSONObject json = new JSONObject();
          json.put("id", userId);
          if (user != null) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                  json.put("displayName", user.getFullName());
                  json.put("name", new JSONObject().put("unstructured", user.getFullName()));
              } else {
                  json.put("displayName", userId);
              }
              if (StringUtils.isNotEmpty(user.getEmailAddress())) {
                  json.append("emails", new JSONObject().put("value", user.getEmailAddress()).put("primary", true));
              }
          }
          return json;
      }
  }
%>
<%
  JSONObject viewerJson = getPersonJSON(request.getRemoteUser());
  JSONObject ownerJson = getPersonJSON(request.getParameter("owner"));
%>

{
  'timeout': <%=request.getSession().getMaxInactiveInterval()%>,
  'assertions': {
    'containerId': 'socialsite',
    <% if (viewerJson != null) { %>
      'viewer': <%=viewerJson.toString()%>,
    <% } %>
    <% if (ownerJson != null) { %>
      'owner': <%=ownerJson.toString()%>,
    <% } %>
  }
}
