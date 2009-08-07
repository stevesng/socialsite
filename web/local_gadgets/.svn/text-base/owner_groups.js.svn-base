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

/**
 * Request for group information when the page loads.
 */
function getData() {
  document.getElementById('widgetBody').innerHTML = 'Requesting groups...';
  var req = opensocial.newDataRequest();
  req.add(socialsite.newFetchUsersGroupsRequest(opensocial.IdSpec.PersonId.OWNER), 'groups');
  req.send(onLoadGroups);
}

/**
 * Parses the response to the group information request and generates
 * html to list the groups along with the group's name and picture.
 *
 * @param {Object} dataResponse Group information that was requested.
 */
function onLoadGroups(dataResponse) {
  socialsite.setTheming();

  var html = '';
  var groups = "";

  groups = dataResponse.get('groups').getData().asArray();

  var group;
  for (var i=0; i < groups.length; i++)
  {
    group = groups[i];
    html += '<div class="group clearfloat">';

    // add image
    if (group.getField(socialsite.Group.Field.THUMBNAIL_URL) != null)
    {
        html += '<div class="icon"><img src="' + group.getField(socialsite.Group.Field.THUMBNAIL_URL) + '" width="50" height="50"/></div>';
    }

    // add group text
    html += '<div class="text"><h2>';
    if (group.getField(socialsite.Group.Field.VIEW_URL) != null)
    {
        html += '<a href="' + group.getField(socialsite.Group.Field.VIEW_URL) + '" target="_parent">';
        html += group.getField(socialsite.Group.Field.NAME);
        html += '</a>';
    }
    else
    {
        html += group.getField(socialsite.Group.Field.NAME);
    }
    html += '</h2>';

    // add description
    if (group.getField(socialsite.Group.Field.DESCRIPTION) != null) {
        html += '<div class="status">' + group.getField(socialsite.Group.Field.DESCRIPTION) + '</div>';
    }
    html += '</div>';

    html += getGroupInviteHtml(group);

    // hr not needed at bottom of list
    if (i < groups.length - 1) {
        html += '<hr class="clearfloat"/>';
    }

    html += '</div>';

  }

  if (groups.length <= 0) {
      html += '<div class="nogroups">You are a not a member of a group.</div>';
  }

  document.getElementById('widgetBody').innerHTML = html;
}


/**
 * Gets the HTML to show a "invite a user to join this group" option.
 */
function getGroupInviteHtml(toGroup) {
    var result = '';
    var groupId = toGroup.handle;

    var inviteAltText = "Invite user to join " + toGroup.getField(socialsite.Group.Field.NAME);

    result += '<div class="joinGroup"><a href="javascript:composeGroupInvite(\'' + groupId + '\')">'
             +  '<img src="' + baseImageURL + '/group_invite.png" alt="' + inviteAltText + '" title="' + inviteAltText + '" /></a>'
             + '</div>';

    return result;
}


function composeGroupInvite(toGroupId) {
   var replacement = 'group_invite.xml&inviteGroup=' + gadgets.util.escapeString(toGroupId);

   var groupInviteUrl = location.href.replace("owner_groups.xml", replacement);
   socialsite.showLightbox('Invite User to Join a Group', groupInviteUrl, 400, 180);
}


gadgets.util.registerOnLoadHandler(getData);
