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

/**
 * @fileoverview SocialSite Invite Widget
 */

var owner = null;

var groups = null;

var msgBox = new gadgets.MiniMessage();

gadgets.util.registerOnLoadHandler(getData);


function isFriend(friends, person) {
    for (var i = 0; i < friends.length; i++) {
        if (friends[i].getId() == person.getId()) {
            return true;
        }
    }

    return false;
}


/**
 * Request for owner information when the page loads.
 */
function getData() {
    document.getElementById('widgetBody').innerHTML = 'Requesting user information...';

    var req = opensocial.newDataRequest();

    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');

    // friends' data request
    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, 'FRIENDS');
    spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);
    req.add(req.newFetchPeopleRequest(spec), 'viewerFriends');

    // viewer's group request
    req.add(socialsite.newFetchUsersGroupsRequest(opensocial.IdSpec.PersonId.VIEWER), 'groups');

    req.send(onLoadInvite);
}


/**
 * Parses the response to the owner's information request and generates
 * html to show the status.
 *
 * @param {Object} dataResponse information that was requested.
 */
function onLoadInvite(dataResponse) {
    owner = dataResponse.get('owner').getData();

    groups = dataResponse.get('groups').getData().asArray();


    socialsite.setTheming();

    var viewerFriends = dataResponse.get('viewerFriends').getData().asArray();

    var html = '';

    html += '<div id="lightboxDiv" class="clearfloat">';

    // show the appropriate html depending on the relationship between the owner and viewer
    if (owner.isViewer()) {
        html += getSelfHtml();
    } else if (isFriend(viewerFriends, owner)) {
        html += getFriendHtml();
    } else {
        html += getNonFriendHtml();
    }

    html += '</div>';

    document.getElementById('widgetBody').innerHTML = html;
}


/**
 * Returns the the best available informal name for the specified person.
 */
function getInformalName(person) {
    var name = owner.getField(opensocial.Person.Field.NAME);
    var firstName = name.getField(opensocial.Name.Field.GIVEN_NAME);
    return ((firstName != null) ? firstName : person.getDisplayName());
}


/**
 * Gets the HTML when the person viewing this is the owner.
 */
function getSelfHtml() {
    var result = '';
    result += '<div class="text">Hello ' + getInformalName(owner) + '!</div>';
    return result;
}


/**
 * Gets the HTML when the person viewing this is a friend of the owner.
 */
function getFriendHtml() {
    var result = '';
    result += '<div style="width: 60%" class="text">' + getInformalName(owner) + ' is one of your friends.</div>';
    result += '<div class="inviteOption">' + getGroupInviteHtml(owner) + '</div>';
    return result;
}


/**
 * Gets the HTML when the person viewing this is not a friend of the owner.
 */
function getNonFriendHtml() {
    var result = '';

    result += '<div style="width: 60%" class="text">Do you know ' + getInformalName(owner) + '?</div>';

    // invite to be a friend
    result += '<div class="inviteOption">' + getFriendConnectHtml(owner) + '</div>';

    // invite to a group
    result += '<div class="inviteOption">' + getGroupInviteHtml(owner) + '</div>';

    return result;
}


/**
 * Gets the HTML to show a "add as a friend" option.
 */
function getFriendConnectHtml(toUser) {
    var result = '';
    var recipientId = toUser.getId();
    var recipientName = toUser.getDisplayName();

    var inviteText = "Add as friend";

    result += '<a href="javascript:friendConnect(\''
             + recipientId + '\', \'' + recipientName + '\')">'
             + inviteText + '</a>';

    return result;
}

function friendConnect(id, name) {
    var replacement = "ss_requestrelationship.xml&personId=" + id + "&personName=" + name;
    var requestUrl = location.href.replace("invite.xml", replacement);
    socialsite.showLightbox('Request Relationship', requestUrl);
}

function friendConnectCallback(responseItem) {
    alert("Your request has been sent: " + responseItem.get("requestFriendship").getData().message);
}


 /**
  * Gets the HTML to show a "invite this user to join a group" option.
  */
function getGroupInviteHtml(toUser) {
    var result = '';

    if (groups.length > 0) {
        var recipientId = toUser.getId();

        var inviteText = "Invite to join group";

        result += '<a href="javascript:composeGroupInvite(\'' + recipientId + '\')">'
               +  inviteText + '</a>';
    }

    return result;
}

function composeGroupInvite(toUserId) {
   var replacement = 'group_invite.xml&inviteTo=' + gadgets.util.escapeString(toUserId);

   var groupInviteUrl = location.href.replace("invite.xml", replacement);
   socialsite.showLightbox('Invite User to Join a Group', groupInviteUrl, 400, 180);
}
