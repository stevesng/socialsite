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

var baseImageURL = '../local_gadgets/files';

var groupData = null;
var viewer = null;

var msgBox = new gadgets.MiniMessage();

function statusMessage(msg, fade) {
    $("#widgetBody").html(msg);
    $("#widgetBody").show("fast");
    if (fade) $("#widgetBody").fadeOut(2000);
}

// **********************************************************************

function joinGroup(groupId) {
    var req = opensocial.newDataRequest();
    var group = {};
    group.id = groupId;
    var person = {};
    person.id = viewer.getId();
    req.add(socialsite.newGroupApplicationRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'joinGroupRequest');
    req.send(requestedToJoinGroup);
}

function requestedToJoinGroup(dataResponse) {
    var res = dataResponse.get('joinGroupRequest');

    if (res.hadError()) {
        msgBox.createDismissibleMessage("Error while requesting group membership: " + res.getErrorMessage());
    } else {
        msgBox.createDismissibleMessage("Group request sent");
    }

}

// **********************************************************************

function leaveGroup(groupId) {
    var req = opensocial.newDataRequest();
    var group = {};
    group.id = groupId;
    var person = {};
    person.id = viewer.getId();
    req.add(socialsite.newRemoveGroupMemberRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'leaveGroupRequest');
    req.send(leftGroup);
}

function leftGroup(dataResponse) {
    var res = dataResponse.get('leaveGroupRequest');

    // TODO: have nicer alert that still allows the information to be updated properly.
    // TODO: add the specific group name to these messages.
    if (res.hadError()) {
        alert("Error while leaving group: " + res.getErrorMessage());
    } else {
        alert("You have been removed from the group");
    }

    // reload to update contents
    window.location.reload();
}

// **********************************************************************

/**
 * Request for group information when the page loads.
 */
function getData() {
    document.getElementById('widgetBody').innerHTML = 'Requesting groups...';
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');
    req.add(socialsite.newFetchUsersGroupsRequest(opensocial.IdSpec.PersonId.VIEWER), 'mygroups');
    req.add(socialsite.newFetchPublicGroupsRequest(opensocial.IdSpec.PersonId.VIEWER), 'publicgroups');
    req.send(onLoadGroups);
 }

/**
 * Parses the response to the group information request and generates
 * html to list the groups along with the group's name and picture.
 *
 * @param {Object} dataResponse Group information that was requested.
 */
function onLoadGroups(dataResponse) {
    viewer = dataResponse.get('viewer').getData();

    socialsite.setTheming();

    var html = '';

    // retrieve groups user belongs to, so as to determine if they have option to join group.
    var myGroups = "";
    var myRoles="";

    myGroups = dataResponse.get('mygroups').getData().asArray();

    var myGroupList = new Array();
    var myRolesList = new Array();
    // Put group ids in associative array to easily determine if user is a member of a
    // group.
    for (var i=0; i < myGroups.length; i++) {
         var id = myGroups[i].handle;
         myGroupList[id] = true;
         myRolesList[id] = myRoles[i];
    }

    // retrieve all group data
    var groups = dataResponse.get('publicgroups').getData().asArray();

    var group;
    for (var i=0; i < groups.length; i++) {

      group = groups[i];
      html += '<div class="group clearfloat">';

      // image
      if (group.getField(socialsite.Group.Field.THUMBNAIL_URL) != null) {
          html += '<div class="icon"><img src="' + group.getField(socialsite.Group.Field.THUMBNAIL_URL) + '" width="50" height="50"/></div>';
      }

      // group text
      html += '<div class="text"><h2>';
      if (group.getField(socialsite.Group.Field.VIEW_URL) != null) {
          html += '<a href="' + group.getField(socialsite.Group.Field.VIEW_URL) + '" target="_parent">';
          html += group.getField(socialsite.Group.Field.NAME);
          html += '</a>';
      } else {
          html += group.getField(socialsite.Group.Field.NAME);
      }

      html += '</h2>';

      // description
      if (group.getField(socialsite.Group.Field.DESCRIPTION) != null) {
          html += '<div class="status">' + group.getField(socialsite.Group.Field.DESCRIPTION) + '</div>';
      }

      html += '</div>';

      // group requests
      var groupId = group.getId();

      if (myGroupList[groupId]) {
          if (group.getField(socialsite.Group.Field.VIEWER_RELATIONSHIP).relationship == "MEMBER") {
              var altText = "Leave group";
              html += '<div class="joinGroup"><a href="javascript:;"><img src="' + baseImageURL + '/remove.png" alt="' + altText + '" title="' + altText + '" onclick="leaveGroup(\'' + groupId + '\')"/></a></div>';
          }
      } else {
          var altText = "Request group membership";
          html += '<div class="joinGroup"><a href="javascript:;"><img src="' + baseImageURL + '/add.png" alt="' + altText + '" title="' + altText + '" onclick="joinGroup(\'' + groupId + '\')"/></a></div>';
      }

      // hr not needed at bottom of list
      if (i < groups.length - 1) {
          html += '<hr class="clearfloat"/>';
      }

      html += '</div>';

    }

    if (groups.length <= 0) {
        html += '<div class="nogroups">There are no groups.</div>';
    }

    document.getElementById('widgetBody').innerHTML = html;
}

gadgets.util.registerOnLoadHandler(getData);
