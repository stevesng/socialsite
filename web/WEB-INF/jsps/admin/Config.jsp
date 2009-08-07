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

<p><s:text name="ConfigForm.pageHelp" /></p>

<s:form action="Config!save" method="POST" enctype="multipart/form-data">

    <table class="formtableNoDesc">

    <s:iterator id="dg" value="configDef.displayGroups">

        <tr>
            <td colspan="3"><h3><s:text name="%{#dg.key}" /></h3></td>
        </tr>

        <s:iterator id="pd" value="#dg.propertyDefs">

            <tr>
                <td class="label"><s:text name="%{#pd.key}" />:</td>

                  <%-- special condition for comment plugins --%>
                  <s:if test="#pd.name == 'users.comments.plugins'">
                      <td class="field"><s:checkboxlist list="pluginsList" name="commentPlugins" listKey="id" listValue="name" /></td>
                  </s:if>

                  <%-- "string" type means use a simple textbox --%>
                  <s:elseif test="#pd.type == 'string'">
                    <td class="field">
                      <input type="text" name="<s:property value='#pd.name'/>" value="<s:property value='properties[#pd.name].value'/>" size="35" />
                    </td>
                  </s:elseif>

                  <%-- "text" type means use a full textarea --%>
                  <s:elseif test="#pd.type == 'text'">
                    <td class="field">
                      <textarea name="<s:property value='#pd.name'/>" rows="<s:property value='#pd.rows'/>" cols="<s:property value='#pd.cols'/>"><s:property value='properties[#pd.name].value'/></textarea>
                    </td>
                  </s:elseif>

                  <%-- "boolean" type means use a checkbox --%>
                  <s:elseif test="#pd.type == 'boolean'">
                      <s:if test="properties[#pd.name].value == 'true'">
                          <td class="field"><input type="checkbox" name="<s:property value='#pd.name'/>" CHECKED></td>
                      </s:if>
                      <s:else>
                          <td class="field"><input type="checkbox" name="<s:property value='#pd.name'/>"></td>
                      </s:else>
                  </s:elseif>

                  <%-- if it's something we don't understand then use textbox --%>
                  <s:else>
                    <td class="field"><input type="text" name="<s:property value='#pd.name'/>" size="50" /></td>
                  </s:else>

                <td class="description"><%-- <s:text name="" /> --%></td>
            </tr>

        </s:iterator>

        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>

    </s:iterator>

    </table>

    <div class="control">
        <input class="buttonBox" type="submit" value="<s:text name="ConfigForm.save"/>"/>
    </div>

</s:form>
