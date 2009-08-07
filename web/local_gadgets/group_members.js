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

 var groupHandle = null;

 // Array to keep track of all members - needed for filtering support
 var allMembers = new Array();

 // Current value of filter option menu
 var filterValue = "all";

 // Current index of filter option menu
 var filterIndex = 0;

 /**
* Request for group's member information when the page loads.
*/
function getData()
{
  document.getElementById('widgetBody').innerHTML = 'Requesting group members...';
  var req = opensocial.newDataRequest();
  req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');
  /* Potentially may need to replace the previous line with the next lines.
  var opt_params = {};
  opt_params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] =
      [opensocial.Person.Field.CURRENT_LOCATION, opensocial.Person.Field.EMAILS];
  req.add(req.newFetchPeopleRequest(opensocial.IdSpec.PersonId.VIEWER, opt_params), 'viewer');
  */

  req.add(socialsite.newFetchGroupRequest(
      opensocial.IdSpec.PersonId.VIEWER, "@current"), 'groupdetails');
  req.add(socialsite.newFetchGroupMembersRequest(
      opensocial.IdSpec.PersonId.VIEWER, "@current"), 'groupmembers');
  req.add(socialsite.newFetchGroupMembersGroupsRequest(
      opensocial.IdSpec.PersonId.VIEWER, "@current"), 'membersGroups');

  req.send(onLoadGroupMembers);
};


 /**
* Parses the response to the group's member information request and generates
* html to list the members along with their display name and picture.
*
* @param {Object} dataResponse Group member information that was requested.
*/
function onLoadGroupMembers(dataResponse)
{
  var html = '';

  socialsite.setTheming();

  var viewer = dataResponse.get('viewer').getData();

  var group = dataResponse.get('groupdetails').getData();
  if (group == null) {
      document.getElementById('widgetBody').innerHTML = 'ERROR: A group needs to be specified in order to show the group members.';
      return;
  }

  groupHandle = group.getId();

  // filtering
  var groupData = dataResponse.get('membersGroups').getData().asArray();

  var headerHtml = getFilteringHtml(groupData);
  document.getElementById('widgetHeading').innerHTML = headerHtml;

  var memberData = dataResponse.get('groupmembers').getData();

  var members = "";
  if (memberData && memberData['list']) {
      members = memberData['list'];
 }

  var member;
  for (var i=0; i < members.length; i++)
  {
    member = members[i];
    allMembers[i] = member.id;

    var memberId = "member_" + member.id;
    html += '<div class="friend clearfloat" id=' + memberId + '>';

    // add image
    if (member.thumbnailUrl != null)
    {
        html += '<div class="icon"><img src="' + member.thumbnailUrl + '"/></div>';

    }

    // add name
    html += '<div class="text"><h2>';
    var profileURL = member.profileUrl;

    if (profileURL != null)
    {
        html += '<a href="' + profileURL + '" target="_parent">';
        html += member.displayName;
        html += '</a>';
    }
    else
    {
        html += member.displayName;
    }
    html += '</h2>';

    // add location
    var currLoc = member.currentLocation;
    if (currLoc != null) {
        var city = currLoc.locality;
        var state = currLoc.region;
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
    if (member.status != null) {
        html += '<div class="status">' + member.status + '</div>';
    }
    html += '</div>';

    // add mail option
    html += getEmailHtml(member);

    // hr not needed at bottom of list
    if (i < members.length - 1) {
        html += '<hr class="clearfloat"/>';
    }

    html += '</div>';
  }

  if (members.length <= 0) {
      html += '<div class="nofriends">There are no members to view for this group.</div>';
  }

  document.getElementById('widgetBody').innerHTML = html;
};


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
function filterGroup() {
   var sel = document.getElementById("filterOptions");
   filterIndex = sel.selectedIndex;
   filterValue = sel.options[sel.selectedIndex].value;

   if (filterValue != "all" && filterIndex != 0) {
       // data request to get members who also belong to second group.
       var req = opensocial.newDataRequest();

       req.add(socialsite.newFetchCommonGroupMembersRequest(opensocial.IdSpec.PersonId.VIEWER, groupHandle, filterValue), 'groupsUnion');

       req.send(requestedMembersInGroups);
   } else {
       var membersToDisplay = new Array();
       filterMembersShown(membersToDisplay);
   }
}

/**
 * Obtains response which has the members who belong to 2 groups.
 */
function requestedMembersInGroups(dataResponse) {
   var membersToDisplay = new Array();
   var memberData = dataResponse.get('groupsUnion').getData();

   var members = "";
   if (memberData && memberData['list']) {
       members = memberData['list'];
   }

   var member;
   for (var i=0; i < members.length; i++)
   {
     member = members[i];
     membersToDisplay[member.id] = true;
   }

   filterMembersShown(membersToDisplay);
}

/**
 * Filters the members shown in the UI.
 */
function filterMembersShown(membersToDisplay) {
   // Loop through all members and decide if they will be shown or not.
   for (var i=0; i < allMembers.length; i++) {
      var show = false;
      if (filterValue == "all" && filterIndex == 0) {
         show = true;
      } else if (membersToDisplay[allMembers[i]]) {
         show = true;
      }

      showElement("member_" + allMembers[i], show);
   }
}


/**
 * Gets the HTML to show the "filter" group members option.
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
function getEmailHtml(member) {

    var result = '';
    var recipientId = member.id;

    var emailAltText = "Email " +  member.displayName;

    result += '<div class="mailFriend"><a href="javascript:composeMail(\'' + recipientId + '\')">'
             +  '<img src="' + baseImageURL + '/mail.png" alt="' + emailAltText + '" title="' + emailAltText + '" /></a>'
             + '</div>';

    return result;
}


function composeMail(toUserId) {
   var replacement = 'compose_mail.xml&msgTo=' + gadgets.util.escapeString(toUserId);
   replacement += '&msgToType=PERSON';

   var messageUrl = location.href.replace("group_members.xml", replacement);
   socialsite.showLightbox('Mail', messageUrl);
}

gadgets.util.registerOnLoadHandler(getData);
