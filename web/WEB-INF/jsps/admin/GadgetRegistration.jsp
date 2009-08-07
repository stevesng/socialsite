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
<%@ taglib uri="http://jakarta.apache.org/taglibs/string-1.0.1" prefix="str" %>

<p><s:text name="GadgetRegistration.pageHelp" /></p>

<h2><s:text name="GadgetRegistration.pendingHeading"/></h2>

<s:if test="pendingRegistrations.size() > 0">

    <table id="appRegistrationTable" class="socialsiteTable">
        <tr>
            <th width="15%"><s:text name="GadgetRegistration.submitter"/></th>
            <th width="65%"><s:text name="GadgetRegistration.details"/></th>
            <th width="10%"><s:text name="GadgetRegistration.action"/></th>
        </tr>
        <s:iterator id="reg" value="pendingRegistrations" status="rowstatus">
        <tr>
            <td>
                <s:property value="profile.name" /><br />
                (<s:property value="profile.userId" />)
            </td>
            <td>
                <s:text name="GadgetRegistration.url"/><br />
                <span class="inactiveUrl">
                    <str:truncateNicely upper="80"><s:property value="appUrl" /></str:truncateNicely>
                </span>
            </td>
            <td>
                <s:url id="review" value="GadgetRegistrationReview"/>
                <a href="${review}?id=${id}"><s:text name="GadgetRegistration.review"/></a><br />
            </td>
        <tr>
        </s:iterator>
    </table>

</s:if>
<s:else>
    <s:text name="GadgetRegistration.noPending"/>
</s:else>


<h2><s:text name="GadgetRegistration.approvedHeading"/></h2>

<s:if test="approvedRegistrations.size() > 0">

    <table id="appRegistrationTable" class="socialsiteTable">
        <tr>
            <th width="15%"><s:text name="GadgetRegistration.submitter"/></th>
            <th width="65%"><s:text name="GadgetRegistration.details"/></th>
            <th width="10%"><s:text name="GadgetRegistration.action"/></th>
        </tr>
        <s:iterator id="reg" value="approvedRegistrations" status="rowstatus">
        <tr>
            <td>
                <s:property value="profile.name" /><br />
                (<s:property value="profile.userId" />)
            </td>
            <td>
                <s:text name="GadgetRegistration.url"/><br />
                <span class="inactiveUrl">
                    <str:truncateNicely upper="80"><s:property value="appUrl" /></str:truncateNicely>
                </span><br />
                <s:text name="GadgetRegistration.details"/><br />
                <div class="detail">
                    <b><s:text name="GadgetRegistration.submitted"/></b>: <s:property value="created"/><br />
                    <b><s:text name="GadgetRegistration.updated"/></b>: <s:property value="updated"/><br />
                    <s:if test="comment != null && comment != ''">
                        <b><s:text name="GadgetRegistration.comment"/></b>:
                        <str:truncateNicely upper="80"><s:property value="comment"/></str:truncateNicely>
                        <br />
                    </s:if>
                    <s:if test="consumerKey != null">
                        <b><s:text name="GadgetRegistration.consumerKey"/></b>: <s:property value="consumerKey"/>
                        <br />
                    </s:if>
                    <s:if test="consumerSecret != null">
                        <b><s:text name="GadgetRegistration.consumerSecret"/></b>: <s:property value="consumerSecret"/>
                    </s:if>
                </div>
            </td>
            <td>
                <s:url id="review" value="GadgetRegistrationReview"/>
                <a href="${review}?id=${id}"><s:text name="GadgetRegistration.review"/></a><br />
            </td>
        <tr>
        </s:iterator>
    </table>

</s:if>
<s:else>
    <s:text name="GadgetRegistration.noApproved"/>
</s:else>
