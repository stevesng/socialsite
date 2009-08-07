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

<s:if test="reviewComplete">
    <br />
    <br />
    <s:text name="GadgetRegistrationReview.done" />
    <s:url id="registration" value="GadgetRegistration"  />
    <a href="${registration}"><b><s:text name="GadgetRegistrationReview.returnToRegistration"/></b></a><br />
</s:if>

<s:else>
    
    <s:form theme="simple" action="GadgetRegistrationReview">
        <s:hidden name="id" />

        <s:if test="appRegistration.status == 'APPROVED'">
            <s:text name="GadgetRegistrationReview.removalPrompt" /><br /><br />
            <s:text name="GadgetRegistrationReview.removalComment" /><br />
            <s:textfield theme="simple" name="comment" size="60" maxLength="100" />
            <s:submit theme="simple" value="Remove" action="GadgetRegistrationReview!removeAppRegistration" />
        </s:if>

        <s:else>
            <s:text name="GadgetRegistrationReview.rejectionPrompt" /><br /><br />
            <s:text name="GadgetRegistrationReview.comment" />
            <s:textfield theme="simple" name="comment" size="60" maxLength="100" />
            <s:submit theme="simple" value="Approve" action="GadgetRegistrationReview!approveAppRegistration" />
            <s:submit theme="simple" value="Reject" action="GadgetRegistrationReview!rejectAppRegistration" />
        </s:else>

    </s:form>

    <h3><s:text name="GadgetRegistrationReview.gadgetDetails" /></h3>

    <s:if test="gadgetSpec">

        <table width="100%">
            <tr>
                <td width="15%"><b><s:text name="GadgetRegistrationReview.gadget_title" /></b></td>
                <td width="85%"><s:property value="gadgetSpec.getModulePrefs().getAttribute('title')" /></td>
            </tr>
            <tr>
                <td><b><s:text name="GadgetRegistrationReview.title_url" /></b></td>
                <td><s:property value="gadgetSpec.getModulePrefs().getAttribute('title_url')" /></td>
            </tr>
            <tr>
                <td><b><s:text name="GadgetRegistrationReview.description" /></b></td>
                <td><s:property value="gadgetSpec.getModulePrefs().getAttribute('description')" /></td>
            </tr>
            <tr>
                <td><b><s:text name="GadgetRegistrationReview.author" /></b></td>
                <td><s:property value="gadgetSpec.getModulePrefs().getAttribute('author')" /></td>
            </tr>
            <tr>
                <td><b><s:text name="GadgetRegistrationReview.author_email" /></b></td>
                <td><s:property value="gadgetSpec.getModulePrefs().getAttribute('author_email')" /></td>
            </tr>
            <s:if test="gadgetSpec.getModulePrefs().getAttribute('thumbnail') != null">
            <tr>
                <td valign="top"><b><s:text name="GadgetRegistrationReview.thumbnail" /></b></td>
                <td valign="top">
                    <img src='<s:property value="gadgetSpec.getModulePrefs().getAttribute('thumbnail')" />' alt="[Thumbnail]" />
                </td>
            </tr>
            </s:if>
            <s:if test="gadgetSpec.getModulePrefs().getAttribute('screenshot') != null">
            <tr>
                <td valign="top"><b><s:text name="GadgetRegistrationReview.screenshot" /></b></td>
                <td valign="top">
                    <img src='<s:property value="gadgetSpec.getModulePrefs().getAttribute('screenshot')" />' alt="[Screenshot]" />
                </td>
            </tr>
            </s:if>
        </table>

    </s:if>
    <s:else>
        
        <s:text name="GadgetRegistrationReview.errorReadingSpec" />

    </s:else>

</s:else>

