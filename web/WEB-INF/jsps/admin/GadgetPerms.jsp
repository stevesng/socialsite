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

<p><s:text name="GadgetPermsCreate.pageHelp" /></p>

<s:form action="GadgetPermissions!remove"
    method="POST" enctype="multipart/form-data">

  <%-- Application Permissions --%>
  <h3><s:text name="GadgetPerms.appPerms" /></h3>
  <p>
    <a href='<s:url action="GadgetPermissionCreateApp"/>'><s:text name="GadgetPerms.createAppAction"/></a>
  </p>
  <p><s:text name="GadgetPerms.appPermByProfileId" /></p>
  <table class="socialsiteTable">
  <tr>
    <th class="socialsiteTable"><s:text name="GadgetPerms.profile" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionName" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionActions" /></th>
    <th class="socialsiteTable" width="5%"><s:text name="GadgetPerms.remove" /></th>
  </tr>
  <s:iterator id="p" value="appPermsByUser" status="rowstatus">
    <s:if test="#rowstatus.odd == true">
      <tr class="socialsiteTable_odd">
    </s:if>
    <s:else>
      <tr class="socialsiteTable_even">
    </s:else>
      <td><s:property value="profileId" /></td>
      <td><s:property value="name" /></td>
      <td><s:property value="actions" /></td>
      <td align="center"><input type="checkbox" name='<s:property value="#p.id"/>'></td>
    </tr>
  </s:iterator>
  </table>

  <p><s:text name="GadgetPerms.appPermByGroupHandle" /></p>
  <table class="socialsiteTable">
  <tr>
    <th class="socialsiteTable"><s:text name="GadgetPerms.group" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionName" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionActions" /></th>
    <th class="socialsiteTable" width="5%"><s:text name="GadgetPerms.remove" /></th>
  </tr>
  <s:iterator id="p" value="appPermsByGroup" status="rowstatus">
    <s:if test="#rowstatus.odd == true">
      <tr class="socialsiteTable_odd">
    </s:if>
    <s:else>
      <tr class="socialsiteTable_even">
    </s:else>
      <td><s:property value="groupId" /></td>
      <td><s:property value="name" /></td>
      <td><s:property value="actions" /></td>
      <td align="center"><input type="checkbox" name='<s:property value="#p.id"/>'></td>
    </tr>
  </s:iterator>
  </table>

  <%-- Feature Permissions --%>
  <h3><s:text name="GadgetPerms.featurePerms" /></h3>
  <p>
    <s:text name="GadgetPerms.featurePermHelp" />
    <a href='<s:url action="GadgetPermissionCreateFeature"/>'><s:text name="GadgetPerms.createFeatureAction"/></a>
  </p>
  <table class="socialsiteTable">
  <tr>
    <th class="socialsiteTable"><s:text name="GadgetPerms.appTitle" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.appUrl" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionName" /></th>
    <th class="socialsiteTable" width="5%"><s:text name="GadgetPerms.remove" /></th>
  </tr>
  <s:iterator id="p" value="featurePerms" status="rowstatus">
    <s:if test="#rowstatus.odd == true">
      <tr class="socialsiteTable_odd">
    </s:if>
    <s:else>
      <tr class="socialsiteTable_even">
    </s:else>
    <s:if test="app == null">
      <td>*</td><td>*</td>
    </s:if>
    <s:else>
      <td><s:property value="app.title" /></td>
      <td><s:property value="app.URL.path" /></td>
    </s:else>
      <td><s:property value="name" /></td>
      <td align="center"><input type="checkbox" name='<s:property value="#p.id"/>'></td>
    </tr>
  </s:iterator>
  </table>

  <%-- Http Permissions --%>
  <h3><s:text name="GadgetPerms.httpPerms" /></h3>
  <p>
    <s:text name="GadgetPerms.httpPermsHelp" />
    <a href='<s:url action="GadgetPermissionCreateHttp"/>'><s:text name="GadgetPerms.createHttpAction"/></a>
  </p>
  <p><s:text name="GadgetPerms.httpPermsByApp" /></p>
  <table class="socialsiteTable">
  <tr>
    <th class="socialsiteTable"><s:text name="GadgetPerms.appTitle" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionUrl" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionActions" /></th>
    <th class="socialsiteTable" width="5%"><s:text name="GadgetPerms.remove" /></th>
  </tr>
  <s:iterator id="p" value="httpPermsByApp" status="rowstatus">
    <s:if test="#rowstatus.odd == true">
      <tr class="socialsiteTable_odd">
    </s:if>
    <s:else>
      <tr class="socialsiteTable_even">
    </s:else>
      <td><s:property value="app.title" /></td>
      <td><s:property value="name" /></td>
      <td><s:property value="actions" /></td>
      <td align="center"><input type="checkbox" name='<s:property value="#p.id"/>'></td>
    </tr>
  </s:iterator>
  </table>

  <p><s:text name="GadgetPerms.httpPermsByDomain" /></p>
  <table class="socialsiteTable">
  <tr>
    <th class="socialsiteTable"><s:text name="GadgetPerms.gadgetDomain" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionUrl" /></th>
    <th class="socialsiteTable"><s:text name="GadgetPerms.permissionActions" /></th>
    <th class="socialsiteTable" width="5%"><s:text name="GadgetPerms.remove" /></th>
  </tr>
  <s:iterator id="p" value="httpPermsByDomain" status="rowstatus">
    <s:if test="#rowstatus.odd == true">
      <tr class="socialsiteTable_odd">
    </s:if>
    <s:else>
      <tr class="socialsiteTable_even">
    </s:else>
      <td><s:property value="gadgetDomain" /></td>
      <td><s:property value="name" /></td>
      <td><s:property value="actions" /></td>
      <td align="center"><input type="checkbox" name='<s:property value="#p.id"/>'></td>
    </tr>
  </s:iterator>
  </table>

  <p>&nbsp;</p>
  
  <div class="control">
    <input class="buttonBox" type="submit" value="<s:text name="GadgetPerms.removeGrants"/>"/>
  </div>
</s:form>
