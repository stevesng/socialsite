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
 * @fileoverview SocialSite Profile Widget
 */

// TODO: don't hard code the baseURL!
var baseImageURL = '../local_gadgets/files';

// OpenSocial owner object
var owner =  null;

// Profile object, built from JSON profile data
var profile = null;

// ProfileDefinition object, built from JSON profile definition data
var profileDefinition = null;

// Google Gadget TabSet object
var tabSet = null;


// **********************************************************************

// On load we fetch standard OWNER information via OpenSocial API

gadgets.util.registerOnLoadHandler(fetchData);
function fetchData() {
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');
    req.add(socialsite.newFetchProfileDefinitionRequest(opensocial.IdSpec.PersonId.OWNER), 'profileDef');
    req.add(socialsite.newFetchProfilePropertiesRequest(opensocial.IdSpec.PersonId.OWNER), 'profileProps');
    req.send(receiveData);
}

// As soon as we have data we render the UI
function receiveData(dataResponse) {
    owner = dataResponse.get('owner').getData();
    profileDefinition = dataResponse.get('profileDef').getData();
    profile = dataResponse.get('profileProps').getData();
    renderUI();
}

// Render UI by creating a tab for each section defined in profile definition
// data retrieved vi SocialSite API. Except for the first tab, tabs will not be
// rendered until the user clicks on them, triggering a call to tabCallback().
function renderUI() {
    socialsite.setTheming();

    tabSet = new gadgets.TabSet(null, null, document.getElementById('tabs'));
    tabSet.alignTabs('left');
    for (var si=0; si<profileDefinition.sections.length; si++) {
        var sectionDef = profileDefinition.sections[si];
        if (hasContents(sectionDef.short_name, sectionDef)) {
            var tabId = tabSet.addTab(profileDefinition.sections[si].local_name, {callback: tabCallback});
        }
    }

    if (owner.isViewer()) {

        // Show edit profile and manage gadget buttons
        displayFooterActions();
    }
}


// ****************************************************************************

// Render UI within each tab with input element for each property.
// Calls displayHolder() to do the recursive work of rendering properies,
// property objects and property object collections.
function tabCallback(tabId) {

    var tab = tabSet.getSelectedTab();
    var currentTabsLabel = tab.getName();
    var sectionDef = null;
    for (var i=0; i<profileDefinition.sections.length; i++) { 
        var currentSectionName = profileDefinition.sections[i].local_name;
        if(currentSectionName == currentTabsLabel) {
            sectionDef = profileDefinition.sections[i];
            break;
        }
    }

    if(sectionDef == null) {
        return;
    }

    // Only do this once per tab
    if (!sectionDef.displayed) {
        var height = window.innerHeight - 194;
        $('#'+tabId).html(
            '<div class="tabWrapper" style="height: '+height+'px;">'
          + ' <form action="unused">'
          + '  <fieldset>'
          +     displayHolder(sectionDef.short_name, sectionDef)
          + '  </fieldset>'
          + ' </form>'
          + '</div>'
        );
    }

    /*
    var tabElement = document.getElementById(tabId);
    alert('window.innerHeight='+window.innerHeight
         +'\ntabElement\='+tabElement
         +'\ntabElement.offsetTop='+tabElement.offsetTop);
    */
}


// ****************************************************************************
function hasContents(basePath, holder) {
    // First check if any property has a content
    if (hasProperties(basePath, holder)) {
        return true;
    }

    // Check if property object collections have contents
    if (holder.propertyObjectCollections) {
        for(var k=0; k<holder.propertyObjectCollections.length; k++) {
            var collection = holder.propertyObjectCollections[k];
            var indexOfUnderScore = collection.short_name.indexOf('_');
            var shortNameToLookFor = collection.short_name.substring(0,indexOfUnderScore);

            // just check for property #1
            if (hasProperties(basePath+"_"+shortNameToLookFor+"_1", collection)) {
                return true;
            }
        }
    }

    // Check if property objects have content
    if (holder.propertyObjects) {
        for (var l=0; l<holder.propertyObjects.length; l++) {
            var propObject = holder.propertyObjects[l];
            if (hasProperties(basePath+"_"+propObject.short_name, propObject)) {
                return true;
            }
        }
    }
    return false;
}

function hasProperties(basePath, holder) {
    if (holder.properties) {
        for (var j=0; j<holder.properties.length; j++) {
            var propertyDef = holder.properties[j];
            var fullname = basePath + "_" + propertyDef.short_name;
            var propertyValue = profile[fullname];
            if (propertyValue && trim(propertyValue).length > 0) {
                return true;
            }
        }
    }
    return false;
}


// ****************************************************************************

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
                out +=  '    <td class="propertyLabel">' + propertyDef.local_name + '</td>';
                out +=  '    <td class="propertyValue">' + propertyValue + '</td>';
                out +=  '</tr>';
            }
        }
        out += '</table>';
    }

    // Loop through section PROPERTY OBJECT definitions
    if (holder.propertyObjects) {
        for (var k=0; k<holder.propertyObjects.length; k++) {
            var objectDef = holder.propertyObjects[k];
            var name = basePath + "_" + objectDef.short_name;
            if (hasProperties(name, objectDef)) {
                out += '<div>'
                out +=     '<div id="' + objectDef.name + '" class="propertyObjectTitle">';
                out +=         objectDef.local_name;
                out +=     '</div>';
                out +=     '<div class="propertyObject">'
                out +=         displayHolder(name, objectDef);
                out +=     '</div>'
                out += '</div>'
            }
        }
    }

    // Loop through PROPERTY OBJECT COLLECTION definitions
    if (holder.propertyObjectCollections) {
        for (var m=0; m<holder.propertyObjectCollections.length; m++) {
            var collectionDef = holder.propertyObjectCollections[m];

            var empty = true;
            for (var poci=1; poci<20; poci++) {
                var pocBasePath = basePath + "_" + holder.propertyObjectCollections[m].short_name.replace('{n}', poci);
                if (hasProperties(pocBasePath, collectionDef)) {
                    empty = false;
                    break;
                }
            }
            if (empty) continue;

            var tmpout = '<div style="margin: 0; border: 0; padding: 0; overflow: hidden;">'
            tmpout +=     '<div id="' + collectionDef.name + '" class="propertyObjectCollectionTitle">';
            tmpout +=         collectionDef.local_name;
            tmpout +=     '</div>';

            // Show the property object collection objects matching current definition
            var emptyCollection = true;
            for (poci=1; poci<20; poci++) {
                var pocBasePath = basePath + "_" + holder.propertyObjectCollections[m].short_name.replace('{n}', poci);
                if (hasProperties(pocBasePath, collectionDef)) {
                    emptyCollection = false;
                    tmpout += '<div class="propertyObject">'
                    tmpout += displayHolder(pocBasePath, collectionDef);
                    tmpout += '</div>'
                }
            }
            tmpout += '</div>'
            if (!emptyCollection) {
                out += tmpout;
            }
        }
    }
    return out;
}

function onClickEditProfile() {
    var url = location.href.replace("profile.xml", "profileedit.xml");
    socialsite.showLightbox("Edit Profile", url, 700, 700);
}

function onClickManageGadgets() {
    var url = location.href.replace("profile.xml", "gadget_directory.xml");
    socialsite.showLightbox("Gadget Directory", url, 700, 700);
}

function trim(s) {
    return s.replace(/^\s+|\s+$/g, '');
}

// **********************************************************************

function displayFooterActions() {
    var footer = '';
    footer += '<center><ul>';

    footer += '<li><a onclick="onClickEditProfile()">';
    footer += '    <img src="' + baseImageURL + '/vcard_edit.png" title="Edit Profile" />';
    footer += '</a></li>';

    footer += '<li><a onclick="onClickManageGadgets()">';
    footer += '    <img src="' + baseImageURL + '/plugin_edit.png" title="Manage Gadgets" />';
    footer += '</a></li>';

    footer += '</ul></center>';

    $("#footer").html(footer);
}
