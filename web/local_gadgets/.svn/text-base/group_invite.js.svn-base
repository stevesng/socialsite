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
var toUser = null;
var toGroup = null;

var toId = null;
var toGroupId = null;

var defaultUserValue = "type friends ID here";

var msgBox = new gadgets.MiniMessage();

/*
 * Removes whitespace from the beginning and end of the specified string.
 */
function trim(s) {
  return s.replace(/^\s+|\s+$/g, '');
}

/*
 * Method called when the value in the "Invite" field is changed.
 */
function updateInviteeField() {
  var toName = trim(document.getElementById('invitee_field').value);

  // FOR NOW: toName will be the toId
  toId = toName;

  // TODO: add support to allow user to specify a person's name instead of just userid.
  //var initToName = toUser.getDisplayName();
}

/*
 * Method called when the value in the "Group" field is changed.
 */
function updateGroupField() {
   var sel = document.getElementById("group_field");
   toGroupId = sel.options[sel.selectedIndex].value;
   toGroup = sel.options[sel.selectedIndex].label;
}

/*
 * Method called when user hits the submit button of the form.
 */
function sendInvite() {
  // invitee
  if (document.getElementById('invitee_field').value != null) {
      var toName = trim(document.getElementById('invitee_field').value);
      if (toName == "" || toName == defaultUserValue) {
          alert('Please specify the invitee in the "Invite" field.');
          return;
      }
  }

  // TODO: need to do error checking on invitee field

  // make request
  var req = opensocial.newDataRequest();
  var group = {};
  group.id = toGroupId;
  var person = {};
  person.id = toId;

  req.add(socialsite.newGroupInvitationRequest(opensocial.IdSpec.PersonId.VIEWER, person, group), 'inviteGroupRequest');
  req.send(requestedGroupInvite);
}

function requestedGroupInvite(dataResponse) {
  var res = dataResponse.get('inviteGroupRequest');
  if (res.hadError()) {
      msgBox.createDismissibleMessage("Error inviting " + toId + " to group " + toGroupId + ": " + res.getErrorMessage());
  } else {
      msgBox.createDismissibleMessage("Your invitation has been sent to " + toId);
  }
}

/**
 * Request for inviter and invitee information when the page loads.
 */
function getData() {
  document.getElementById('widgetBody').innerHTML = 'Requesting invite information...';
  toId = gadgets.util.getUrlParameters().inviteTo;
  toGroupId = gadgets.util.getUrlParameters().inviteGroup;

  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');

  if (toId != null && toId != 'undefined') {
      req.add(req.newFetchPersonRequest(toId), 'toUser');
  }

  if (toGroupId != null && toGroupId != 'undefined') {
      req.add(socialsite.newFetchGroupRequest(opensocial.IdSpec.PersonId.VIEWER, toGroupId), 'groupdetails');
  } else {
      req.add(socialsite.newFetchUsersGroupsRequest(opensocial.IdSpec.PersonId.VIEWER), 'groups');
  }

  req.send(renderUI);
}

 /**
* Parses the response to the inviter and invitee information request and generates
* html for sending a mail message.
*
* @param {Object} dataResponse information that was requested.
*/
function renderUI(dataResponse) {
  viewer = dataResponse.get('viewer').getData();

  socialsite.setTheming();

  var html = '';

  html += '<div id="lightboxDiv" class="clearfloat">';
  html += '<form onsubmit="sendInvite(); return false" action="" id="send_invite" name="inviteForm">';
  html += '<table class="clearfloat">';


  // TO line
  html += '<tr>';
  html += '<td colspan="2"><label for="invitee_field" class="propLabel">Invite</label>';

  var toName = "";
  if (toId != null && toId != 'undefined') {
      toUser = dataResponse.get('toUser').getData();
      toName = toUser.getDisplayName();

      html += '<span name="to" id="invitee_field" class="propValue">' + toName + '</span></td>';
  } else {
      html += '&nbsp;<input type="text" size="25" onchange="updateInviteeField()" name="to" id="invitee_field" value="' + defaultUserValue + '" onFocus="if (this.value==\'' + defaultUserValue + '\') this.value=\'\'"/></td>';
  }

  html += '</tr>';


  // GROUP line
  html += '<tr>';
  html += '<td colspan="2"><label for="group_field" class="propLabel">To Join the Group</label>';

  var toGroupName = "";
  if (toGroupId != null && toGroupId != 'undefined') {
      toGroup = dataResponse.get('groupdetails').getData();
      toGroupName = toGroup.getField(socialsite.Group.Field.NAME);
      html += '<span name="group" id="group_field" class="propValue">' + toGroupName + '</span></td>';
  } else {
      html += '&nbsp;<select id="group_field" onChange="updateGroupField()" >';

      // get user's groups
      var groups = dataResponse.get('groups').getData().asArray();

      var group;
      for (var i=0; i < groups.length; i++)
      {
        group = groups[i];
        var gName = group.getField(socialsite.Group.Field.NAME);
        var gId = group.getId();

        if (i == 0) {
            toGroupId = gId;
            toGroup = gName;
        }

        html += '<option value="' + gId + '"label="' + gName + '">' + gName + '</option>';

      }

      html += '</select></td>';
  }

  html += '</tr>';

  // Spacing between rows
  html += '<tr><td colspan="2">&nbsp;</td></tr>';

  // SUBMIT and CANCEL buttons
  html += '<tr>';
  html += '<td> </td>';

  html += '<td align="right"><div><input type="submit" value="Send Invitation" name="send_button" id="send_button"/>';

  html += '<input type="button" value="Cancel" id="exit_button" onclick="socialsite.hideLightbox()"/></div></td>';
  html += '</tr>';

  html += '</table>';

  html += '</form>';

  html += '</div>';

  document.getElementById('widgetBody').innerHTML = html;
}


gadgets.util.registerOnLoadHandler(getData);
