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

// OpenSocial owner object
var owner = null;

// Array to keep track of all friends - needed for filtering support
var allFriends = new Array();

// Current value of filter option menu
var filterValue = "all";

// Current index of filter option menu
var filterIndex = 0;

/**
 * Request for friend information when the page loads.
 */
function getData() {

  document.getElementById('widgetBody').innerHTML = 'Requesting friends...';
  var req = opensocial.newDataRequest();

  // owner data request
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');

  // friends' data request
  var spec = new opensocial.IdSpec();
  spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.OWNER);
  spec.setField(opensocial.IdSpec.Field.GROUP_ID, 'FRIENDS');
  spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);

  var opt_params = {};
  //opt_params[opensocial.DataRequest.PeopleRequestFields.MAX] = 3;
  //opt_params[opensocial.DataRequest.PeopleRequestFields.FIRST] = 3;
  opt_params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] =
      [opensocial.Person.Field.PROFILE_URL,
       opensocial.Person.Field.CURRENT_LOCATION,
       opensocial.Person.Field.STATUS];
  req.add(req.newFetchPeopleRequest(spec, opt_params), 'ownerFriends');

  req.add(socialsite.newFetchFriendsGroupsRequest(opensocial.IdSpec.PersonId.OWNER), 'friendsGroups');

  req.send(onLoadFriends);
}

/**
 * Parses the response to the friend information request and generates
 * html to list the friends along with their display name and picture.
 *
 * @param {Object} dataResponse Friend information that was requested.
 */
function onLoadFriends(dataResponse) {
  owner = dataResponse.get('owner').getData();

  socialsite.setTheming();
  var html = '';

  var ownerFriends = dataResponse.get('ownerFriends').getData();
  var numItems = ownerFriends.size();
  var item = 0;

  // TODO: update this to use collection syntax
  var groupData = dataResponse.get('friendsGroups').getData().asArray();

  var headerHtml = getFilteringHtml(groupData);
  document.getElementById('widgetHeading').innerHTML = headerHtml;


  ownerFriends.each(function(person) {
    allFriends[item] = person.getId();

    item++;

    var friendId = "friend_" + person.getId();
    html += '<div class="friend clearfloat" id=' + friendId + '>';

    var thumbnailURL = person.getField(opensocial.Person.Field.THUMBNAIL_URL);
    if (thumbnailURL) {
      html += '<div class="icon"><img src="' + thumbnailURL + '"/></div>';
    }

    html += '<div class="text"><h2>';
    var profileURL = person.getField(opensocial.Person.Field.PROFILE_URL);
    if (profileURL) {
      html += '<a href="' + profileURL + '" target="_parent">';
      html += person.getDisplayName();
      html += '</a>';
    } else {
      html += person.getDisplayName();
    }

    html += '</h2>';

    // add location
    var currLoc = person.getField(opensocial.Person.Field.CURRENT_LOCATION);
    if (currLoc != null) {
        var city = currLoc.getField(opensocial.Address.Field.LOCALITY);
        var state = currLoc.getField(opensocial.Address.Field.REGION);
        var location = '';
        if (city != null && state != null && city != '' & state != '') {
            location = city + ', ' + state;
        } else if (state != null && state != '') {
            location = state;
        } else if (city != null && city != '') {
            location = city;
        }
        html += '<div class="location">' + location + '</div>';
    }

    // add status
    if (person.getField(opensocial.Person.Field.STATUS) != null) {
        html += '<div class="status">' + person.getField(opensocial.Person.Field.STATUS) + '</div>';
    }
    html += '</div>';

    html += getEmailHtml(person);
    html += getGroupInviteHtml(person);

    // hr not needed at bottom of list
    if (item < numItems) {
        html += '<hr class="clearfloat"/>';
    }

    html += '</div>';
  });

  if (item == 0) {
    html += '<div class="nofriends">You currently have no friend connections.</div>';
  }
  document.getElementById('widgetBody').innerHTML = html;
}


/**
 * shows or hides an element depending on the "show" boolean variable
 */
function showElement(id, show) {
   if (document.getElementById(id)) {
       var displayType = "none";
       if (show) {
           displayType = "block";
       }

       document.getElementById(id).style.display = displayType;
   }
}

/**
 * Gets the selected group to filter on and makes any necessary requests
 */
function filterGroup()
{
   var sel = document.getElementById("filterOptions");
   filterIndex = sel.selectedIndex;
   filterValue = sel.options[sel.selectedIndex].value;

   if (filterValue != "all" && filterIndex != 0) {
       // data request to get friends who are part of this group.
       var req = opensocial.newDataRequest();

       var groupSpec = new opensocial.IdSpec();
       groupSpec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.OWNER);
       groupSpec.setField(opensocial.IdSpec.Field.GROUP_ID, filterValue);
       groupSpec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);

       req.add(req.newFetchPeopleRequest(groupSpec), 'groupFriends');

       req.send(requestedFriendsInGroup);
   } else {
       var friends = new Array();
       filterFriendsShown(friends);
   }
}

/**
 * Obtains response which has the friends who are members of a group.
 */
function requestedFriendsInGroup(dataResponse) {
   var friends = new Array();
   var groupFriends = dataResponse.get('groupFriends').getData();

   groupFriends.each(function(person) {
     var id = person.getId();
     friends[id] = true;
   });

   filterFriendsShown(friends);
}

/**
 * Filters the friends shown in the UI.
 */
function filterFriendsShown(friends) {
   // Loop through all friends and decide if they will be shown or not.
   for (var i=0; i < allFriends.length; i++) {
      var show = false;
      if (filterValue == "all" && filterIndex == 0) {
         show = true;
      } else if (friends[allFriends[i]]) {
         show = true;
      }

      showElement("friend_" + allFriends[i], show);
   }
}


/**
 * Gets the HTML to show the "filter" friends option.
 */
function getFilteringHtml(groups) {
   var result = '';

   // only show filtering option if there are groups
   if (groups.length < 1) {
       return result;
   }

   result += '<div class="filter">';

   result += '<span class="filterText">Show: </span>';
   result += '<select id="filterOptions" onchange="filterGroup()">';
   result += '<option selected value="all">All</option>';

   var group;
   for (var i=0; i < groups.length; i++)
   {
     group = groups[i];
     var gid = group.getId();
     var gname = group.getField(socialsite.Group.Field.NAME);

     // FOR NOW: temporary fix for getting the group id and group name
     if (!gid) gid = group.fields_["handle"];
     if (!gname) gname = group.fields_["identification_name"];

     if (gid && gname) {
       result += '<option value=' + gid + '>' + gname + '</option>';
     }
   }

   result += '</select>';
   result += '</div>';
   result += '<div class="clearfloat"></div>';

   return result;
}

/**
 * Gets the HTML to show a "mail this user" option.
 */
function getEmailHtml(toUser) {
    var result = '';
    var recipientId = toUser.getId();

    var emailAltText = "Email " + toUser.getDisplayName();
    result += '<div class="mailFriend"><a href="javascript:composeMail(\'' + recipientId + '\')">'
             +  '<img src="' + baseImageURL + '/mail.png" alt="' + emailAltText + '" title="' + emailAltText + '" /></a>'
             + '</div>';

    return result;
}


 /**
 * Gets the HTML to show a "invite this user to join a group" option.
 */
function getGroupInviteHtml(toUser) {
    var result = '';
    var recipientId = toUser.getId();

    var inviteAltText = "Invite " + toUser.getDisplayName() + " to join a group";

    result += '<div class="mailFriend"><a href="javascript:composeGroupInvite(\'' + recipientId + '\')">'
             +  '<img src="' + baseImageURL + '/group_invite.png" alt="' + inviteAltText + '" title="' + inviteAltText + '" /></a>'
             + '</div>';

    return result;
}


function composeMail(toUserId) {
   var replacement = 'compose_mail.xml&msgTo=' + gadgets.util.escapeString(toUserId);
   replacement += '&msgToType=PERSON';

   var messageUrl = location.href.replace("owner_friends.xml", replacement);
   socialsite.showLightbox('Mail', messageUrl);
}

function composeGroupInvite(toUserId) {
   var replacement = 'group_invite.xml&inviteTo=' + gadgets.util.escapeString(toUserId);

   var groupInviteUrl = location.href.replace("owner_friends.xml", replacement);
   socialsite.showLightbox('Invite User to Join a Group', groupInviteUrl, 400, 180);
}

gadgets.util.registerOnLoadHandler(getData);
