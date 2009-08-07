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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h1>Test Page for SocialSite Gadgets</h1>

<c:set var="viewerId" value="${pageContext.request.remoteUser}"/>
<c:choose>
  <c:when test="${empty param.ownerId}">
    <c:set var="ownerId" value="${viewerId}"/>
    <p>
      <b>Note:</b> use "<code>?ownerId=&lt;id&gt;</code>" in the page url to
      try a different user.
    </p>
  </c:when>
  <c:otherwise>
    <c:set var="ownerId" value="${param.ownerId}"/>
  </c:otherwise>
</c:choose>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/consumer.jsp"></script>
<script type="text/javascript">
 socialsite.setContext({
   'attributes': {
     'ownerId': '${ownerId}',
     'groupHandle': 'test'
   },
   'delegate': {
     'method': 'GET',
     'url': '<%=request.getContextPath()%>/socialsite_context.jsp',
     'headers': {
       'cookie': document.cookie
     }
   }
 });
</script>

<p>
  <strong>ownerId:</strong> ${ownerId}
</p>

<p>
  <strong>viewerId:</strong> ${viewerId}
</p>

<hr/>

<!-- *********************************************************************** -->
<h1>Dashboard and Profile Widgets</h1>

<h3>Status Gadget for ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/status.xml', 'removable':false});
</script>

<h3>Dashboard Gadget</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/dashboard.xml', 'removable':false});
</script>

<h3>Profile Gadget (mini) for ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/profilemini.xml', 'removable':false});
</script>

<h3>Profile Gadget for ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/profile.xml', 'removable':false});
</script>

<h3>Profile Editor Gadget for ${viewerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/profileedit.xml', 'removable':false});
</script>

<h3>Compose Mail Gadget for ${viewerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/compose_mail.xml', 'removable':false});
</script>


<!-- *********************************************************************** -->
<hr/>
<h1>People Widgets</h1>

<h3>People Gadget for ${viewerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/people.xml', 'removable':false});
</script>

<h3>Friends Gadget for ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/owner_friends.xml', 'removable':false});
</script>

<!-- *********************************************************************** -->
<h1>Activity Widgets</h1>

<h3>Activities Gadget for owner ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/face.xml', 'removable':false});
</script>

<h3>Activities Gadget for owner ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/owner_activities.xml', 'removable':false});
</script>

<h3>Activities Gadget for group </h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/group_activities.xml', 'removable':false});
</script>

<h3>Activities Gadget for ${viewerId} and friends</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/viewerandfriends_activities.xml', 'removable':false});
</script>

<!-- *********************************************************************** -->
<hr/>
<h1>Group Widgets</h1>

<h3>Groups Gadget for ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/owner_groups.xml', 'removable':false});
</script>

<h3>Groups Gadget for ${ownerId}</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/viewer_groups.xml', 'removable':false});
</script>

<h3>All Groups Gadget</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/groups.xml', 'removable':false});
</script>

<h3>Group Members Gadget</h3>
<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/group_members.xml', 'removable':false});
</script>

