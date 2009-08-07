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
 * @fileoverview SocialSite Profile Editor Widget
 */

// TODO: don't hard code the baseURL!
var baseImageURL = '../local_gadgets/files';

// Set to true when save is needed
var dirty = false;

// Google Gadget Tabs object
var tabs = null;

// OpenSocial viewer object
var viewer =  null;

// Profile object, built from JSON profile data
var profile = null;

// ProfileDefinition object, built from JSON profile definition data
var profileDefinition = null;

// Collection of SectionPrivacy objects
var sectionPrivs = null;

// Only display privacy tab once
var privacyDisplayed = false;

// Only display mugshot tab once
var mugshotDisplayed = false;


//----------------------------------------------------------------------------

gadgets.util.registerOnLoadHandler(fetchData);

function fetchData() {
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'viewer');
    req.add(socialsite.newFetchProfileDefinitionRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'profileDef');
    req.add(socialsite.newFetchProfilePropertiesRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'profileProps');
    req.add(socialsite.newFetchSectionPrivaciesRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'sectionPrivs');
    req.send(receiveData);
}

function receiveData(dataResponse) {
    viewer =            dataResponse.get('viewer').getData();
    profile =           dataResponse.get('profileProps').getData();
    profileDefinition = dataResponse.get('profileDef').getData();
    sectionPrivs =      dataResponse.get('sectionPrivs').getData();
    renderUI();
}

/**
 * Render UI by creating a tab for each section defined in profile definition
 * data retrieved vi SocialSite API. Except for the first tab, tabs will not be
 * rendered until the user clicks on them, triggering a call to tabCallback().
 */
function renderUI() {
    socialsite.setTheming();

    tabs = new gadgets.TabSet(null, null, document.getElementById('tabs'));
    for (var si=0; si<profileDefinition.sections.length; si++) {
        tabs.addTab(profileDefinition.sections[si].local_name, {callback: tabCallback});
    }

    // TODO: finish mugshot update
    //tabs.addTab("Mugshot", {callback: tabCallbackMugshot});

    tabs.addTab("Privacy", {callback: tabCallbackPrivacy});
}


//----------------------------------------------------------------------------

/**
 * Clear tabs and all content, then force redisplay of UI.
 * Intended for use immediately after a save operation.
 */
function clearUI() {
    for (var ti=0; ti<tabs.getTabs().length - 1; ti++) {
        profileDefinition.sections[ti].display = false;
    }
    privacyDisplayed = false;
    while (tabs.getTabs().length > 0) {
        tabs.removeTab(0);
    }

    // workaround for apparent bug in Google Gadget Tab library
    $('#tabs_header').remove();

    fetchData();
}


//----------------------------------------------------------------------------

/**
 * Render UI within each tab with input element for each property.
 * Calls displayHolder() to do the recursive work of rendering properies,
 * property objects and property object collections.
 */
function tabCallback(tabId) {
    var out = '<div class="tabWrapper">';
    var tab = tabs.getSelectedTab();
    var sectionDef = profileDefinition.sections[tab.getIndex()];

    // Only do this once per tab
    if (!sectionDef.displayed) {
        out += '<form action=\'unused\'>';
        out += '<fieldset>';

        out += displayHolder(sectionDef.short_name, sectionDef, out);

        out += '</fieldset>';
        out += '</form></div>';
        $('#' + tabId).html(out);
        sectionDef.displayed = true;
        enableDirtyCheck();
    }
}


//----------------------------------------------------------------------------

/**
 * Render mugshot upload tab
 */
function tabCallbackMugshot(tabId) {
    if (!mugshotDisplayed) {
        var tab = tabs.getSelectedTab();

        var baseUrl = opensocial.Container.get().baseUrl_.replace('social/rest','');

        var out = '<div class="tabWrapper">';

        out += "<form id=\"mugshotForm\" name=\"edit/profile\" onsubmit=\"return true;\" action=\"" + baseUrl + "app-ui/core/edit/profile!save\" method=\"POST\" enctype=\"multipart/form-data\">";
        out += "    <table>";
        out += "        <tr>";
        out += "            <td valign=\"top\">";
        out += "                <img alt=\"mugshot\" height=\"150\" width=\"150\" hspace=\"10px\"";
        out += "                    vspace=\"10px\" src=\"" + baseUrl + "/images/person/" + viewer.getId() + "\" />";
        out += "            </td>";
        out += "            <td valign=\"top\">";
        out += "                That's your current profile image:<br />";
        out += "                Would you like to upload a new one?<br />";
        out += "                <input type=\"file\" name=\"image\" value=\"\" id=\"mugshotFormImage\"/><br />";
        out += "                <input type=\"submit\" id=\"mugshotFormSubmit\" name=\"Save new mugshot\" value=\"Save new mugshot\" />";
        out += "            </td>";
        out += "        </tr>";
        out += "    </table>";
        out += "</form>";

        out += "</div>";
        $('#' + tabId).html(out);
        mugshotDisplayed = true;
    }
}


//----------------------------------------------------------------------------

/**
 * Render section privacy tab
 */
function tabCallbackPrivacy(tabId) {
    if (!privacyDisplayed) {
        var tab = tabs.getSelectedTab();
        var out = '<div class="tabWrapper">';
        var privs = sectionPrivs.asArray();
        out += '<form action=\'unused\'>';
        out += '    <fieldset>';
        out += '        <table width="100%">';
        out += '<tr> <td>Section</td> <td>Privacy</td>';
        for (var i=0; i<privs.length; i++) {
            out += '<tr>';

            out += '<td>';
            out += privs[i].getField(socialsite.SectionPrivacy.Field.DISPLAY_NAME);
            out += '</td>';

            out += '<td valign=\'top\'>';
            out += '    <select class=\'formInput\' ';
            out += '        id=\'sp_visibility_' + privs[i].getField(socialsite.SectionPrivacy.Field.SECTION_NAME) + '\'>';

            var visibilities = ['PRIVATE', 'FRIENDS', 'ALLGROUPS', 'PUBLIC'];
            for (vis in visibilities) {
                if (privs[i].getField(socialsite.SectionPrivacy.Field.VISIBILITY) == visibilities[vis]) {
                    out += '<option selected=\'true\' value=\'' + visibilities[vis] + '\'>' + visibilities[vis] + '</option>';
                } else {
                    out += '<option value=\'' + visibilities[vis] + '\'>' + visibilities[vis] + '</option>';
                }
            }
            out += '    </select>';
            out += '    </td>';
            
            out += '</tr>';
        }
        out += '        </table>';
        out += '    </fieldset>';
        out += '</form>';
        out += "</div>";
        $('#' + tabId).html(out);
        privacyDisplayed = true;
    }
}

function selected(sectionPriv, visibility) {
    if (sectionPriv.getField(socialsite.SectionPrivacy.Field.VISIBILITY) == visibility) {
        return " selected=\'true\' ";
    }
    return "";
}


//----------------------------------------------------------------------------

/**
 * Save profile to server
 */
function saveProfile() {
    socialsite.statusMessage("Saving profile...","information");

    var req = opensocial.newDataRequest();

    // collect all profile properties and add to request
    var allProperties = new Object();
    for (si=0; si<profileDefinition.sections.length; si++) {
        collectProperties(profileDefinition.sections[si].short_name,
           profileDefinition.sections[si],
           allProperties);
    }
    req.add(socialsite.newUpdateProfilePropertiesRequest(
        opensocial.IdSpec.PersonId.VIEWER, allProperties), "dummy");

    // collect privacy settings and add to request
    var privs = sectionPrivs.asArray();
    for (var i=0; i<privs.length; i++) {
        var sectionName = privs[i].getField(socialsite.SectionPrivacy.Field.SECTION_NAME);
        var selectId = 'sp_visibility_' + sectionName;
        var selectElem = document.getElementById(selectId);
        if (selectElem) {
            privs[i].setField(socialsite.SectionPrivacy.Field.VISIBILITY, selectElem.value);
            req.add(socialsite.newUpdateSectionPrivacyRequest(
                opensocial.IdSpec.PersonId.VIEWER, sectionName, privs[i]), "dummy" + i);
        }
    }

    req.send(onProfileSaved);
}


function onProfileSaved(res) {
   if (res.hadError()) {
      socialsite.statusMessage("ERROR saving profile: " + res.getErrorMessage(),"error");
   } else {
      clearUI();
      setDirty(false);
      socialsite.statusMessage("Profile saved!","success");
      socialsite.hideLightbox(true); 
   }
}

//----------------------------------------------------------------------------

function cancelChanges() {
    clearUI();
    setDirty(false);
    socialsite.hideLightbox(false);
}

function onChangeEvent(e) {
   setDirty(true);
}

function enableDirtyCheck() {
    if (!dirty) {
        $("input").bind("change", onChangeEvent);
        $("input").bind("keypress", onChangeEvent);
    }
}

function setDirty(flag) {
    dirty = flag;
    if (dirty) {
        $("input").unbind("change", onChangeEvent);
        $("input").unbind("keypress", onChangeEvent);
        socialsite.statusMessage("Profile changed, save needed", "warning");
    } else {
        for (var zi=0; zi<profileDefinition.sections.length; zi++) {
            profileDefinition.sections[zi].displayed = false;
        }
        privacyDisplayed = false;
        enableDirtyCheck();
    }
}

