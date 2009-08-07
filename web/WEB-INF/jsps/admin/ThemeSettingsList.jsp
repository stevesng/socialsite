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

<p><s:text name="ThemeSettings.pageHelp" /></p>

<p><a href='<s:url action="ThemeSettingsCreate"/>'><s:text name="ThemeSettings.createAction"/></a></p>

<s:form action="ThemeSettingsList!remove"
    method="POST" enctype="multipart/form-data">

  <table class="socialsiteTable">
  <tr>
    <th class="socialsiteTable"><s:text name="ThemeSettings.destination" /></th>
    <th class="socialsiteTable"><s:text name="ThemeSettings.anchorColor" /></th>
    <th class="socialsiteTable"><s:text name="ThemeSettings.backgroundColor" /></th>
    <th class="socialsiteTable"><s:text name="ThemeSettings.backgroundImage" /></th>
    <th class="socialsiteTable"><s:text name="ThemeSettings.fontColor" /></th>
    <th class="socialsiteTable" width="5%"><s:text name="ThemeSettings.remove" /></th>
  </tr>
  <s:iterator id="ts" value="settings" status="rowstatus">
    <s:if test="#rowstatus.odd == true">
      <tr class="socialsiteTable_odd">
    </s:if>
    <s:else>
      <tr class="socialsiteTable_even">
    </s:else>
      <td><a href='<s:url action="ThemeSettingsCreate">
          <s:param name="dest" value="#ts.destination" />
        </s:url>'><s:property value="#ts.destination" /></a></td>
      <td><s:property value="#ts.anchorColor" /></td>
      <td><s:property value="#ts.backgroundColor" /></td>
      <td><s:property value="#ts.backgroundImage" /></td>
      <td><s:property value="#ts.fontColor" /></td>
      <td align="center"><input type="checkbox" name='<s:property value="#ts.destination"/>'></td>
    </tr>
  </s:iterator>
  </table>

  <p>&nbsp;</p>
  
  <div class="control">
    <input class="buttonBox" type="submit" value="<s:text name="ThemeSettings.removeSettings"/>"/>
  </div>
</s:form>
