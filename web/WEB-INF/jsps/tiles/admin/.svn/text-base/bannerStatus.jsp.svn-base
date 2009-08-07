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

<form id="mastheadForm">
<div class="MstDiv_sun4">

    <table class="MstTblTop_sun4" cellpadding="0" cellspacing="0" width="100%">
        <tbody>
        <tr>
            <td>
                <div class="mastheadButton_4_sun4">
                    <a a class="Hyp_sun4" href='<s:url action="Config" namespace="/app-ui/admin" />'><s:text name="MenuBar.home" /></a>
                </div>
            </td>

            <s:if test="user != null">
                <td align="right">
                    <div class="mastheadButton_4_sun4"><a
                        href='<s:url action="logout" namespace="/app-ui" />'><s:text name="Logout.button"
                    /></a></div>
                </td>
            </s:if>

        </tr>
        <tbody>
    </table>

    <table class="MstTblBot_sun4" cellpadding="0" cellspacing="0" width="100%">
        <tbody>
        <tr>
            <td class="MstTdTtl_sun4">
              <div class="MstDivUsr_sun4">
                <span class="MstLbl_sun4"><s:text name="Banner.user" /></span>
                <span class="MstTxt_sun4"><s:property value="userId" /></span>
                <img width="14" height="14" border="0" alt=""
                  src="/app-ui/themes/admin/masthead/masthead-separator.gif"
                  id="propertyForm:Masthead:_userInfoSeparator"/>
                <span class="MstLbl_sun4"><s:text name="Banner.server" /></span>
                <span class="MstTxt_sun4">${pageContext.request.serverName}</span>
              </div>
            </td>
        </tr>
        <tr>
          <td  class="MstTdTtl_sun4">
            <span class="MstLbl_sun4">
              <h2><s:text name="Home.mainConsoleLink"/></h2>
            </span>
          </td>
        </tr>
        <tr><td><div class="hrule_sun4">
              <img src='<s:url value="/app-ui/themes/admin/other/dot.gif"/>' />
        </div></td></tr>
      </tbody>
   </table>

</div>
</form>
