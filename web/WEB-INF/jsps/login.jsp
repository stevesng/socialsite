<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%--
  Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
--%>
<%--
  Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  The ASF licenses this file to You
  under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License.
  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.  For additional information regarding
  copyright in this work, please see the NOTICE file in the top level
  directory of this distribution.
--%>

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script type="text/javascript">
  <% // Auto-focus the username field of the login form %>
  window.onload = function() { document.getElementById('j_username').focus(); }
</script>

<h1><s:text name="Login.pageTitle" /></h1>
<s:text name="Login.extraText"><span style="display:none;"></span></s:text>

<c:if test="${failed}">
  <p class="error"><s:text name="Login.tryAgain" /></p>
</c:if>
<form id="loginForm" method="post" action="j_security_check">
  <table>
    <tr>
      <td align="right"><s:text name="Login.username" />:</td>
      <td><input type="text" name="j_username" id="j_username" /></td>
    </tr>
    <tr>
      <td align="right"><s:text name="Login.password" />:</td>
      <td><input type="password" name="j_password" id="j_password" /></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td><button type="submit"><s:text name="Login.button" /></button></td>
    </tr>
    <c:if test="${selfRegistrationEnabled}">
      <tr>
        <td>&nbsp;</td>
        <td>
          <br />
          <a href='<s:url namespace="/selfregistration" action="SelfRegistration" />'>
            <s:text name="Login.selfRegistration"/>
          </a>
        </td>
      </tr>
    </c:if>
  </table>
</form>
