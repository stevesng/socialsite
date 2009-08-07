/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
 * or legal/LICENSE.txt.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at legal/LICENSE.txt.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided by Sun
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

var viewer = null;
var viewerFriends = null;
var toUser = null;
var toGroup = null;

var toId = null;
var toType = null;

//var defaultUserValue = "type friends ID here";

var msgBox = new gadgets.MiniMessage();

/*
 * Removes whitespace from the beginning and end of the specified string.
 */
function trim(s) {
  return s.replace(/^\s+|\s+$/g, '');
}

/*
 * Method called when the value in the "To" field is changed.
 */
function updateToField() {
  var sel = document.getElementById("to_field");
  toId = sel.options[sel.selectedIndex].value;
  toName = sel.options[sel.selectedIndex].label;

/* May use later if support user typing in name
    var toName = trim(document.getElementById('to_field').value);
    // TODO: add support to allow user to specify someone other than the initial person/group.
    var initToName = "";
    if (toType == "GROUP") {
        initToName = toGroup.getField(socialsite.Group.Field.NAME);
    } else {
        initToName = toUser.getDisplayName();
    }

    if (toName != initToName) {
        var warnTxt = "FOR NOW you cannot change the recipient of a message... your change will be reverted";
        alert(warnTxt);
        document.getElementById('to_field').value = initToName;
        return;
    }
    */
}

/*
 * Method called when user hits the select button of the form.
 */
function sendMessage() {
  /* The below functionality is only needed if the "To" field is editable (which it currently isn't
    var toName = trim(document.getElementById('to_field').value);
    if (toName == "") {
        msgBox.createDismissibleMessage('Please specify a recipient in the "To" field.');
        return;
    }
    */

  var subject = trim(document.getElementById('subject_field').value);

  var msg = trim(document.getElementById('message_field').value);
  if (msg == "") {
    msgBox.createDismissibleMessage('Please specify text in the "Message" field.');
    return;
  }

  var tempToId = "";
  if (toType == "GROUP") {
    tempToId = 'group_' + toId;
  } else {
    tempToId = 'person_' + toId;
  }
  var req = opensocial.newDataRequest();
  var message = opensocial.newMessage(msg);
  message.setField(opensocial.Message.Field.TYPE,
    opensocial.Message.Type.PRIVATE_MESSAGE);
  message.setField(opensocial.Message.Field.TITLE, subject);

  // not part of api, but in RPC proposal
  var recipients = [ tempToId ];
  message.setField("recipients", recipients);

  req.add(socialsite.newPostMessageRequest(
    opensocial.IdSpec.PersonId.VIEWER, message),
    'resp');
  req.send(handleResponse);
}

// callback method for sent messages
function handleResponse(response) {
  var data = response.get('resp').getData();
  if (data && data['errorMessage']) {
    msgBox.createDismissibleMessage(data['errorMessage']);
  } else {
    msgBox.createDismissibleMessage("Message sent");
  }
}

/**
  * Request for sender and recipient information when the page loads.
  */
function getData() {
  document.getElementById('widgetBody').innerHTML = 'Requesting sender information...';
  toId = gadgets.util.getUrlParameters().msgTo;
  // FOR NOW: determine recipient type by url parameter.
  toType = gadgets.util.getUrlParameters().msgToType;
  if (toType == 'undefined') {
    toType = "PERSON";
  }
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');

  var spec = new opensocial.IdSpec();
  spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
  spec.setField(opensocial.IdSpec.Field.GROUP_ID, 'FRIENDS');
  spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);
  req.add(req.newFetchPeopleRequest(spec), 'viewerFriends');

  if (toType == "PERSON" && toId != 'undefined') {
    req.add(req.newFetchPersonRequest(toId), 'toUser');
  } else if (toType == "GROUP" && toId != 'undefined') {
    req.add(socialsite.newFetchGroupRequest(opensocial.IdSpec.PersonId.VIEWER, toId), 'groupdetails');
  }

  req.send(renderUI);
}

/**
  * Parses the response to the sender and recipient information request and generates
  * html for sending a mail message.
  *
  * @param {Object} dataResponse information that was requested.
  */
function renderUI(dataResponse) {
  viewer = dataResponse.get('viewer').getData();
  viewerFriends = dataResponse.get('viewerFriends').getData();

  socialsite.setTheming();

  var toName = ""
  if (toType == "GROUP") {
    toGroup = dataResponse.get('groupdetails').getData();
    toName = toGroup.getField(socialsite.Group.Field.NAME);
  } else if (toType == "PERSON") {
    toUser = dataResponse.get('toUser').getData();
    toName = toUser.getDisplayName();
  }


  var html = '';

  html += '<div id="lightboxDiv" class="clearfloat">';
  html += '<form onsubmit="sendMessage(); return false" action="" id="compose_message" name="msgForm">';

  // TO line
  html += '<table class="clearfloat">';
  html += '<tr>';
  html += '<td><label for="to_field" class="propLabel">To:</label></td>';

  if (toName == "") {
    // use what's commented out if we want to support changing the "To" field by typing
    //html += '<td><input type="text" size="40" onchange="updateToField()" name="to" id="to_field" value="' + defaultUserValue + '" onFocus="if (this.value==\'' + defaultUserValue + '\') this.value=\'\'"/></td>';

    html += '<td><select id="to_field" onChange="updateToField()" >';

    var firstItem = true;
    viewerFriends.each(function(person) {
      var tName = person.getDisplayName();
      var tId = person.getId();

      if (firstItem) {
        toId = tId;
        toName = tName;

        firstItem = false;
      }

      html += '<option value="' + tId + '"label="' + tName + '">' + tName + '</option>';
    });

    html += '</select></td>';

    toType = "PERSON";
  } else {
    html += '<td><div name="to" id="to_field" class="propValue">' + toName + '</div></td>';
  }
  html += '</tr>';

  // SUBJECT line
  html += '<tr>';
  html += '<td><label for="subject_field" class="propLabel">Subject:</label></td>';
  html += '<td><input type="text" size="40" value="" name="subject" id="subject_field"/></td>';
  html += '</tr>';

  // MESSAGE area
  html += '<tr>';
  html += '<td valign="top"><label for="message_field" class="propLabel">Message:</label></td>';
  html += '<td><textarea name="message" id="message_field" rows="8" cols="40"></textarea></td>';
  html += '</tr>';

  // SEND and CANCEL buttons
  html += '<tr>';
  html += '<td> </td>';

  html += '<td><div><input type="submit" value="Send" name="send_button" id="send_button"/>';

  html += '<input type="button" value="Cancel" id="exit_button" onclick="socialsite.hideLightbox()"/></div></td>';
  html += '</tr>';

  html += '</table>';

  html += '</form>';

  html += '</div>';

  document.getElementById('widgetBody').innerHTML = html;
}

gadgets.util.registerOnLoadHandler(getData);
