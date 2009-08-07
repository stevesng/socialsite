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

<s:set name="tabMenu" value="menu"/>
<s:if test="#tabMenu != null">

<div id="menu">

<form id="propertyForm">

<div id="propertyForm:administrationTabs">
<div class="Tab1Div_sun4">

<table class="Tab1TblNew_sun4" cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
<s:iterator id="tab" value="#tabMenu.tabs" >

    <s:if test="#tab.selected">
        <s:set name="selectedTab" value="#tab" />
        <td class="Tab1TblSelTd_sun4">
           <div class="">
    </s:if>
    <s:else>
        <td>
    </s:else>
        <a class="Tab1Lnk_sun4" href='<s:url action="%{#tab.action}"><s:param name="weblog" value="actionWeblog.handle"/></s:url>'><s:text name="%{#tab.key}" /></a>

    <s:if test="#tab.selected">
           </div>
    </s:if>
        </td>

</s:iterator>
</tr>
</tbody>
</table>

<table class="Tab2TblNew_sun4" cellspacing="0" cellpadding="0" border="0" >
    <tr>
        <s:iterator id="tabItem" value="#selectedTab.items" status="stat">
            <s:if test="#tabItem.selected">
                <td class="Tab2TblSelTd_sun4">
                <div class="Tab2SelTxt_sun4">
                <a class="Tab2Lnk_sun4" href='<s:url action="%{#tabItem.action}"><s:param name="weblog" value="actionWeblog.handle"/></s:url>'><s:text name="%{#tabItem.key}" /></a>
                </div>
                </td>
            </s:if>
            <s:else>
                <td>
                <a class="Tab2Lnk_sun4" href='<s:url action="%{#tabItem.action}"><s:param name="weblog" value="actionWeblog.handle"/></s:url>'><s:text name="%{#tabItem.key}" /></a>
                </td>
            </s:else>
        </s:iterator>
    </tr>
</table>

</div>

</form>

</div>

</s:if>
