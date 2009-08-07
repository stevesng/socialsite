<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title><s:property value="getProp('site.name')"/>: <s:property value="pageTitle" /></title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

        <tiles:insertAttribute name="head" />
        <style type="text/css">
            <tiles:insertAttribute name="styles" />
        </style>
    </head>
    <body>

        <div id="banner">
            <tiles:insertAttribute name="bannerStatus" />
        </div>

        <div id="menu1">
            <tiles:insertAttribute name="menu" />
        </div>

        <div id="content">
            <div id="leftcontent_wrap">
                <div id="leftcontent">
                </div>
            </div>

            <div id="centercontent_wrap">
                <div id="centercontent">
                    <h1><s:property value="pageTitle" /></h1>
                    <tiles:insertAttribute name="messages" />
                    <tiles:insertAttribute name="content" />
                </div>
            </div>

            <div id="rightcontent_wrap">
                <div id="rightcontent">
                    <tiles:insertAttribute name="sidebar" />
                </div>
            </div>
        </div>

        <div id="footer">
            <tiles:insertAttribute name="footer" />
        </div>

        <div id="datetagdiv"
             style="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;">
        </div>
    </body>
</html>
