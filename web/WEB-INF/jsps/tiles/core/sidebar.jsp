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
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="logo">
    <h1><a href='<s:url value="/" />'><s:property value="getProp('socialsite.sampleapp.site.name')"/></a></h1>
    <h2><s:property value="getProp('socialsite.sampleapp.site.description')"/></h2>
    <s:text name="Sidebar.extraText"><span style="display:none;"></span></s:text>
</div>

<p style="text-align:center">
    <img alt="icon" src='<c:url value="/app-ui/images/socialfish-pair.png"/>' width="120" />
</p>

<div class="box">
    <h3>Social Networking options</h3>
    <ul class="bottom">
        <s:if test="%{userId != null}">
            <li><a href='<s:url value="/" />'><s:text name="MenuBar.home" /></a></li>
            <li><a href='<s:url namespace="/app-ui/core" action="profile" />'><s:text name="MenuBar.profile" /></a></li>
            <li>Logged in as [<s:property value="userId" />]</li>
            <li><a href='<s:url namespace="/app-ui/core" action="logout" />'><s:text name="MenuBar.logout" /></a></li>
        </s:if>
        <s:else>
            <li>Welcome to SocialSite!</li>
        </s:else>
    </ul>
</div>
