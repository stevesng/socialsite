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

// TODO: don't hard code the baseURL!
var baseImageURL = '../local_gadgets/files';

// Google Gadget Tabs object
var tabs = null;

// OpenSocial viewer object
var viewer =  null;

// Profile object, built from JSON profile data
var profile = null;

// ProfileDefinition object, built from JSON profile definition data
var profileDefinition = null;

var groupHandle = null;
var userType = null;
var group = null;

// Google Gadget Tabs object
var tabs = null;

var msgBox = new gadgets.MiniMessage();

var userType = null;


// **********************************************************************

// On load we fetch standard VIEWER information via OpenSocial API

gadgets.util.registerOnLoadHandler(fetchData);
function fetchData() {
    var req = opensocial.newDataRequest();

    req.add(req.newFetchPersonRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'viewer');
    req.add(socialsite.newFetchGroupProfilePropertiesRequest(
        "@current"), 'groupdetails');
    req.add(socialsite.newFetchGroupRequest(
        opensocial.IdSpec.PersonId.VIEWER, "@current"), 'group');
    req.add(socialsite.newFetchGroupDefinitionRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'groupdef');
    req.add(socialsite.newFetchUsersGroupsRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'mygroups');

    req.send(receiveData);
}

// As soon as we have data we render the UI
function receiveData(dataResponse) {
    viewer = dataResponse.get('viewer').getData();
    profile = dataResponse.get('groupdetails').getData();
    profileDefinition = dataResponse.get('groupdef').getData();

    group = dataResponse.get('group').getData();
    groupHandle = group.getId();
    userType = group.getField(socialsite.Group.Field.VIEWER_RELATIONSHIP).relationship;

    myGroups = dataResponse.get('mygroups').getData().asArray();

    renderUI();
}

// Render UI by creating a tab for each section defined in profile definition
// data retrieved vi SocialSite API. Except for the first tab, tabs will not be
// rendered until the user clicks on them, triggering a call to tabCallback().
function renderUI() {
    socialsite.setTheming();

    // display top summary area (which includes editing capability)
    displaySummary(viewer);

    tabs = new gadgets.TabSet(null, null, document.getElementById('tabs'));
    for (var si=0; si<profileDefinition.sections.length; si++) {
        tabs.addTab(profileDefinition.sections[si].local_name, {callback: tabCallback});
    }

    // update footer
    displayFooterActions(viewer);
}


// **********************************************************************

// Render UI within each tab with input element for each property.
// Calls displayHolder() to do the recursive work of rendering properies,
// property objects and property object collections.
function tabCallback(tabId) {
    var out = '<div class="tabWrapper">';
    var tab = tabs.getSelectedTab();
    var sectionDef = profileDefinition.sections[tab.getIndex()];

    // Only do this once per tab
    if (!sectionDef.displayed) {
        out += '<form action=\'unused\'>';
        out += '<fieldset>';

        // if first tab and have image then formatting is a little different
        // Note: was using profile.image_url
        if (tab.getIndex() == 0 && group.getField(socialsite.Group.Field.THUMBNAIL_URL) != null) {
            out += '<div class="icon"><img src="' + group.getField(socialsite.Group.Field.THUMBNAIL_URL) + '" width="150" height="150"/></div>';
            out += '<div class="groupProperties">';
            out += displayHolder(sectionDef.short_name, sectionDef, out);
            out += '</div>';
        } else {
            out += displayHolder(sectionDef.short_name, sectionDef, out);
        }

        out += '</fieldset>';
        out += '</form></div>';
        $('#' + tabId).html(out);
        sectionDef.displayed = true;
    }
}


// **********************************************************************

function hasProperties(basePath, holder) {
    if (holder.properties) {
        for (var j=0; j<holder.properties.length; j++) {
            var propertyDef = holder.properties[j];
            var fullname = basePath + "_" + propertyDef.short_name;
            var propertyValue = profile[fullname];
            if (propertyValue) return true;
        }
    }
    return false;
}


// **********************************************************************

// Returns HTML markup to display one property holder, which may have
// properties, property objects, property object collections and nesting of
// objects and collections. This is a recursive method.
function displayHolder(basePath, holder) {
    var out = "";

    // Loop though PROPERTIES, display each with appropriate UI control
    if (holder.properties) {
        out += '<table class="propertyTable">';
        for (var j=0; j<holder.properties.length; j++) {
            var propertyDef = holder.properties[j];
            var fullname = basePath + "_" + propertyDef.short_name;
            var propertyValue = profile[fullname];
            if (propertyValue) {
                out +=  '<tr>';
                out +=  '    <td valign="top" align="left" class="propertyLabel">' + propertyDef.local_name + '</td>';
                out +=  '    <td valign="top" align="left" class="propertyValue">' + propertyValue + '</td>';
                out +=  '</tr>';
            }
        }
        out += '</table>';
    }

    // Loop through section PROPERTY OBJECT definitions
    if (holder.propertyObjects) {
        for (var k=0; k<holder.propertyObjects.length; k++) {
            var objectDef = holder.propertyObjects[k];
            if (hasProperties(basePath, holder)) break;
            var name = basePath + "_" + objectDef.short_name;
            out += '<div id="' + objectDef.name + '" class="propertyObjectTitle">';
            out +=     objectDef.local_name;
            out += '</div>';
            out += '<div class="propertyObject">'
            out += displayHolder(name, objectDef);
            out += '</div>'
        }
    }

    // Loop through PROPERTY OBJECT COLLECTION definitions
    if (holder.propertyObjectCollections) {
        for (var m=0; m<holder.propertyObjectCollections.length; m++) {
            var collectionDef = holder.propertyObjectCollections[m];
            out +=     '<div id="' + collectionDef.name + '" class="propertyObjectCollectionTitle">';
            out +=         collectionDef.local_name;
            out +=     '</div>';

            // Show the property object collection objects matching current definition
            var emptyCollection = true;
            for (var poci=1; poci<20; poci++) {
                var pocBasePath = basePath + "_" + holder.propertyObjectCollections[m].short_name.replace('{n}', poci);
                if (hasProperties(pocBasePath, collectionDef)) {
                    emptyCollection = false;
                    out += '<div class="propertyObject">'
                    out += displayHolder(pocBasePath, collectionDef);
                    out += '</div>'
                }
            }
            if (emptyCollection) {
                out += '<span class="propertyObjectCollectionMessage">None specified</span>';
            }
        }
    }
    return out;
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

function editGroup() {
    var url = location.href.replace("group_profile.xml", "group_profileedit.xml");
    socialsite.showLightbox("Edit Group Profile", url, 600, 520);
 }

// **********************************************************************

function displaySummary(viewer) {
    var summary = '';

    summary += '<table width="100%"><tr>';

    // group name
    var groupName = group.getField(socialsite.Group.Field.NAME);
    summary += '  <td width="75%" align="center">';
    summary += '   <div class="groupName">' + groupName + '</div>';
    summary += '  </td>';

    // edit option (if admin of group)
    summary += '  <td width="25%" align="center">';

    // determine if user can edit group
    if (userType == "ADMIN") {
        summary += '   <input type="button" value="Edit" onclick="editGroup()"></input>';
    }
    summary += '  </td>';

    summary += '</tr></table>';

    document.getElementById('summary').innerHTML = summary;
}

// **********************************************************************

function displayFooterActions(viewer) {
    var footer = '';
    footer += '<center><ul>';

    // support to mail a group
    footer += '<li>' + getEmailHtml(group) + '</li>';

    if (userType == "ADMIN" || userType == "FOUNDER") {
        footer += '<li>' + getEditGroupProfileHtml()   + '</li>';
        footer += '<li>' + getManageGroupGadgetsHtml() + '</li>';
        footer += '<li>' + getRemoveGroupHtml()   + '</li>';
    }

    if (userType == "MEMBER") {
        var altText = "Leave group";
        footer += '<li><img src="' + baseImageURL
            + '/remove.png" alt="' + altText + '" title="' + altText
            + '"  onclick="leaveGroup(\'' + groupHandle + '\')"/></li>';

    } else if (userType == "NONE") {
        var altText = "Request group membership";
        footer += '<li><img src="' + baseImageURL
            + '/add.png" alt="' + altText + '" title="' + altText
            + '" onclick="joinGroup(\'' + groupHandle + '\')"/></li>';
    }

    footer += '</ul></center>';
    document.getElementById('widgetFooter').innerHTML = footer;
}

// **********************************************************************

/**
 * Gets the HTML to show a "mail this group" option.
 */
function getEmailHtml(toGroup) {

    var result = '';
    var urlParams = '';

    var recipientId = toGroup.getId();

    if (recipientId) {
        urlParams += "&msgTo=" + gadgets.util.escapeString(recipientId);
    }

    urlParams += "&msgToType=GROUP";

    var emailAltText = "Email Group";
    var replacement = "compose_mail.xml" + urlParams;
    var emailUrl = location.href.replace("group_profile.xml", replacement);

    result += '<a onclick="socialsite.showLightbox(\'Mail\', \'' + emailUrl + '\')">'
           +  '<img src="' + baseImageURL + '/mail.png" alt="' + emailAltText + '" title="' + emailAltText + '" /></a>';
    return result;

}


// **********************************************************************

function getEditGroupProfileHtml(toGroup) {
    var result = "";
    result += '<a onclick="onClickEditProfile()" >';
    result += '<img src="'+baseImageURL+'/vcard_edit.png" title="Edit Group Profile" />';
    result += '</a>';
    return result;
}

function onClickEditProfile() {
    var url = location.href.replace("group_profile.xml", "group_profileedit.xml");
    socialsite.showLightbox("Edit Group Profile", url);
}

// **********************************************************************

function getRemoveGroupHtml(toGroup) {
    var result = "";
    result += '<a onclick="onClickRemoveGroup(\''+groupHandle+'\')" >';
    result += '<img src="'+baseImageURL+'/cancel.png" title="Remove group" />';
    result += '</a>';
    return result;
}

function onClickRemoveGroup(handle) {
    var req = opensocial.newDataRequest();
    req.add(socialsite.newDeleteGroupRequest(handle), 'requestResult');
    req.send(requestedToRemoveGroup);
}

function requestedToRemoveGroup(dataResponse) {
    var res = dataResponse.get('requestResult');

    if (res.hadError()) {
        msgBox.createDismissibleMessage("Error while removing group : " + res.getErrorMessage());
    } else {
        msgBox.createDismissibleMessage("Group removed");
    }

}

// **********************************************************************

function getManageGroupGadgetsHtml(toGroup) {
    var result = "";
    result += '<a onclick="onClickManageGadgets()">';
    result += '<img src="'+baseImageURL+'/plugin_edit.png" title="Manage Gadgets" ';
    result += '</a>';
    return result;
}

function onClickManageGadgets() {
    var url = location.href.replace("group_profile.xml", "gadget_directory.xml");
    socialsite.showLightbox("Gadget Directory", url, 700, 700); 
}
