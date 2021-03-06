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

<style type="text/css">
<!--
  .gadget-spacer, .gadget-spacer tbody, .gadget-spacer tr, .gadget-spacer td {
    border-collapse: collapse;
    margin: 0;
    padding: 0;
    border: 0;
  }
  .gadget-spacer .gadgets-gadget-chrome {
    margin: 0;
    padding: 0;
  }
-->
</style>

<script type="text/javascript" src="<%=request.getScheme()%>://<%=request.getServerName()%>:<%=request.getServerPort()%>/socialsite/js/consumer.jsp"></script>
<script type="text/javascript">
 socialsite.setContext({
   'delegate': {
     'method': 'GET',
     'url': '<%=request.getScheme()%>://<%=request.getServerName()%>:<%=request.getServerPort()%><%=request.getContextPath()%>/socialsite_context.jsp?owner=${ownerId}',
     'headers': {
       'cookie': document.cookie
     }
   }
 });
</script>

<% /**** BEGIN: Temporary Workaround for Gadget Creation (TODO: Remove) ****/ %>
<script type="text/javascript">
 function showGadgetDirectory() {
   var params = new Object();
   params.spec = '/local_gadgets/app_installer.xml';
   params.useLightbox = true;
   params.includeChrome = false;
   socialsite.addGadget(params);
 }
</script>
<s:if test="owner">
  Would you like to <a href="#" onclick="showGadgetDirectory(); return false;">add more gadgets</a>?
</s:if>
<% /**** END: Temporary Workaround for Gadget Creation ****/ %>

<div style="padding: 5px 0;">
  <script type="text/javascript">
    socialsite.addGadget({'spec':'/local_gadgets/invite.xml', 'includeChrome':false, 'removable':false});
  </script>
</div>

<table class="gadget-spacer">
  <tbody>
    <tr>
      <td width="25%" style="padding-right: 5px;">
        <script type="text/javascript">
          socialsite.addGadget({'spec':'/local_gadgets/face.xml', 'removable':false, 'height':150});
        </script>
      </td>
      <td width="75%">
        <script type="text/javascript">
          socialsite.addGadget({'spec':'/local_gadgets/status.xml', 'removable':false, 'height':150});
        </script>
      </td>
    </tr>
  </tbody>
</table>

<script type="text/javascript">
 socialsite.addGadget({'spec':'/local_gadgets/profile.xml', 'removable':false});
 socialsite.addGadget({'spec':'/local_gadgets/owner_friends.xml', 'removable':false});
 socialsite.addGadget({'spec':'/local_gadgets/owner_activities.xml', 'removable':false});
 socialsite.addGadgets({'collection':'PROFILE'});
</script>
