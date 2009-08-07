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
 * @fileoverview SocialSite Group Profile Editor Widget
 */

// Set to true when save is needed
var dirty = false;

// TODO: don't hard code the baseURL!
var baseImageURL = '../local_gadgets/files';

// Google Gadget Tabs object
var tabs = null;

// OpenSocial owner object
var viewer =  null;

// Profile object, built from JSON profile data
var profile = null;

// ProfileDefinition object, built from JSON profile definition data
var profileDefinition = null;

var groupHandle = null;

// Google Gadget Tabs object
var tabs = null;


// ****************************************************************************

// On load we fetch standard VIEWER information via OpenSocial API

gadgets.util.registerOnLoadHandler(fetchData);
function fetchData() {
    var req = opensocial.newDataRequest();

    req.add(req.newFetchPersonRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'viewer');
    req.add(socialsite.newFetchGroupProfilePropertiesRequest(
        "@current"), 'groupdetails');
    req.add(socialsite.newFetchGroupDefinitionRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'groupdef');

    req.send(receiveData);
}

// As soon as we have data we render the UI
function receiveData(dataResponse) {
    viewer = dataResponse.get('viewer').getData();
    profile = dataResponse.get('groupdetails').getData();
    groupHandle = profile.handle;
    profileDefinition = dataResponse.get('groupdef').getData();
    renderUI();
}

// Render UI by creating a tab for each section defined in profile definition
// data retrieved vi SocialSite API. Except for the first tab, tabs will not be
// rendered until the user clicks on them, triggering a call to tabCallback().
function renderUI() {
    socialsite.setTheming();

    tabs = new gadgets.TabSet(null, null, document.getElementById('tabs'));
    for (var si=0; si<profileDefinition.sections.length; si++) {
        tabs.addTab(profileDefinition.sections[si].local_name, {callback: tabCallback});
    }
}


//----------------------------------------------------------------------------

// Clear tabs and all content, then force redisplay of UI.
// Intended for use immediately after a save operation.
function clearUI() {
    for (var ti=0; ti<tabs.getTabs().length; ti++) {
        profileDefinition.sections[ti].display = false;
    }
    while (tabs.getTabs().length > 0) {
        tabs.removeTab(0);
    }

    // workaround for apparent bug in Google Gadget Tab library
    $('#tabs_header').remove();

    fetchData();
}


//----------------------------------------------------------------------------

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

        out += displayHolder(sectionDef.short_name, sectionDef, out);

        out += '</fieldset>';
        out += '</form></div>';
        $('#' + tabId).html(out);
        sectionDef.displayed = true;
        enableDirtyCheck();
    }
}

//----------------------------------------------------------------------------

// Save profile to server
function saveProfile() {
   statusMessage("Saving group profile...","information");

   var allProperties = new Object();
   for (si=0; si<profileDefinition.sections.length; si++) {
       collectProperties(profileDefinition.sections[si].short_name,
                         profileDefinition.sections[si],
                         allProperties);
   }
   var req = opensocial.newDataRequest();
   req.add(socialsite.newUpdateGroupProfilePropertiesRequest(
        opensocial.IdSpec.PersonId.VIEWER, allProperties), "returnData");
   req.send(onProfileSaved);
}

function onProfileSaved(response) {
   if (response.hadError()) {
      statusMessage("ERROR saving group profile: " + response.getErrorMessage(),"error");
   } else {
      clearUI();
      setDirty(false);
      statusMessage("Group profile saved!","success");
      socialsite.hideLightbox(true); 
   }
}

//----------------------------------------------------------------------------

function collectProperties(basePath, holder, allProperties) {
   var foundProps = false;
   if (holder.properties) {
       for (var pi=0; pi<holder.properties.length; pi++) {
           var name = basePath + "_" + holder.properties[pi].short_name;
           if (document.getElementById(name)) {
               allProperties[name] = document.getElementById(name).value;
               foundProps = true;
           }
       }
   }
   if (holder.propertyObjectCollections) {
       for (var pci=0; pci<holder.propertyObjectCollections.length; pci++) {
           for (var pcj=1; pcj<20; pcj++) {
               var name = basePath + "_" + holder.propertyObjectCollections[pci].short_name;
               name = name.replace('{n}',pcj);
               if (!collectProperties(name, holder.propertyObjectCollections[pci], allProperties)) break;
           }
       }
   }
   if (holder.propertyObjects) {
       for (var poi=0; poi<holder.propertyObjects.length; poi++) {
           var name = basePath + "_" + holder.propertyObjects[poi].short_name;
           if (!collectProperties(name, holder.propertyObjects[poi], allProperties)) break;
       }
   }
   return foundProps;
}


//----------------------------------------------------------------------------

function cancelChanges() {
    clearUI();
    setDirty(false);
    socialsite.hideLightbox(true);
}


//----------------------------------------------------------------------------

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
        statusMessage("Group profile changed, save needed", "warning");
    } else {
        enableDirtyCheck();
    }
}

function onChangeEvent(e) {
   setDirty(true);
}

function statusMessage(message, type) {
    $("#status-message").html(message);
    if (type == "information") {
        $("#status-icon").attr("src","../local_gadgets/files/information.png");
        $("#messageBox").css("background","#eee");
    } else if (type == "success") {
        $("#status-icon").attr("src","../local_gadgets/files/accept.png");
        $("#messageBox").css("background","#efe");
    } else if (type == "warning") {
        $("#status-icon").attr("src","../local_gadgets/files/error.png");
        $("#messageBox").css("background","#ffc");
    }
}
