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
 * @fileoverview SocialSite Gadget Directory and Installer
 */

// Person object represents viewer
var viewer =  null;
var owner = null;
var group = null;

// Google Gadgets Tab control
var tabSet = null;

// Keep track of which tabs have been initialized
var tabInitMap = {};

// Maximum number of items to show in each tab
var maxGadgets = 6;

// Callbacks to refresh each of the tabs
var refreshAvailableGadgets = function() {}
function noCallback() {}


// Where to find image resources
var baseImageUrl = '../local_gadgets/files';

// Google Gadgets Mini Message facility
var msg = new gadgets.MiniMessage(0, "minimessages");


///////////////////////////////////////////////////////////////////////////////
//
//                    --------- Initialization ---------
//
///////////////////////////////////////////////////////////////////////////////


function init() {

    $("body").addClass("yui-skin-sam");
    YAHOO.namespace("ssgadgetdir");

    // Show progress indicator while gadget loads data
    YAHOO.ssgadgetdir.wait =
        new YAHOO.widget.Panel("wait", {
            width: "240px",
            fixedcenter: true,
            close: false,
            draggable: false,
            zindex:4,
            modal: true,
            visible: false
        }
    );

    YAHOO.ssgadgetdir.wait.setHeader("Loading, please wait...");
    YAHOO.ssgadgetdir.wait.setBody(
        '<img src="../app-ui/images/rel_interstitial_loading.gif" />');
    YAHOO.ssgadgetdir.wait.render(document.body);
    YAHOO.ssgadgetdir.wait.show();

    socialsite.setTheming();
    os.Container.registerDocumentTemplates();

    var req = opensocial.newDataRequest();
    var params = {};
    params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
        "viewerRelationship"]; // TODO: constants for SocialSite fields
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER, params), 'owner');
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, params), 'viewer');
    req.add(socialsite.newFetchGroupRequest(
        opensocial.IdSpec.PersonId.VIEWER, "@current"), 'group');

    req.send(function(data) {
        owner = data.get("owner").getData();
        group = data.get("group").getData();

        viewer = data.get("viewer").getData();
        viewer.id = viewer.getId();

        initYUI_AvailableGadgets();

        tabSet = new gadgets.TabSet(null, null, document.getElementById('tabs'));
        tabSet.alignTabs('left');

        tabSet.addTab("Available Gadgets", {
            contentContainer: document.getElementById("availableGadgetsTab"),
            callback: availableGadgetsTabCallback});

        if (group) {
            $("#prompt").html("Pick a Gadget to add for group: <b>" + group.getId() + "</b>");
        } else if (owner) {
            $("#prompt").html("Pick a Gadget to add to your profile page");
        }

        YAHOO.ssgadgetdir.wait.hide();
    });
}

///////////////////////////////////////////////////////////////////////////////
//
//                     --------- Messages Tab ---------
//
///////////////////////////////////////////////////////////////////////////////


function availableGadgetsTabCallback(tabId) {
    if (!tabInitMap[tabId]) {
        tabInitMap[tabId] = "initialized";
        showAvailableGadgets(null, 0);
    }
}

function initYUI_AvailableGadgets() {
    YAHOO.ssgadgetdir.prevAvailableGadgetsButton = new YAHOO.widget.Button(
        { id: "prevAvailableGadgetsButton", type: "button", label: "Prev",
          container: "prevAvailableGadgetsButton"});
    YAHOO.ssgadgetdir.prevAvailableGadgetsButton.set("disabled", true, true);

    YAHOO.ssgadgetdir.nextAvailableGadgetsButton = new YAHOO.widget.Button(
        { id: "nextAvailableGadgetsButton", type: "button", label: "Next",
          container: "nextAvailableGadgetsButton"});
    YAHOO.ssgadgetdir.nextAvailableGadgetsButton.set("disabled", true, true);
}

function showAvailableGadgets(event, offset) {
    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params.first = offset;
    params.max = maxGadgets;

    req.add(socialsite.newFetchAvailableGadgetsRequest(params), 'gadgets');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("gadgets")) {
                errorMessage = data.get("gadgets").getErrorMessage();
            }
            window.alert("ERROR fetching gadgets: " + errorMessage);
            return;
        }
        renderAvailableGadgetsContent("Showing Available Gadgets",
            showAvailableGadgets, data.get("gadgets").getData());
    });
    refreshAvailableGadgets = function() {
        showAvailableGadgets(event, offset);
    }
}

function renderAvailableGadgetsContent(title, callback, gadgets) {
    
    // enable/disable next/prev buttons
    syncNextPrevButtons(gadgets, callback,
        YAHOO.ssgadgetdir.nextAvailableGadgetsButton,
        YAHOO.ssgadgetdir.prevAvailableGadgetsButton);

    var template = os.getTemplate('availableGadgetsTemplate');
    template.renderInto(document.getElementById('availableGadgetsContent'), {
        "title"    : title,
        "viewer"   : viewer,
        "baseImageUrl" : baseImageUrl,
        "gadgets"  : gadgets.asArray(),
        "start"    : gadgets.getOffset() + 1,
        "end"      : gadgets.getOffset() + gadgets.size(),
        "count"    : gadgets.size(),
        "total"    : gadgets.getTotalSize()
    });

    $("tr.itemRow").hover(
        function () {
            $(this).css("background","#eee");
        },
        function () {
            $(this).css("background","#fff");
        }
    );

    // put an Add Gadget button on each row
    for (i in gadgets.asArray()) {
        var gadget = gadgets.asArray()[i];
        YAHOO.ssgadgetdir.gadgetButton = new YAHOO.widget.Button({
            type: "push", label: "Add",
            name: gadget.getId()+"_gadgetButton",
            container: gadget.getId()+"_gadgetButton",
            onclick: {fn:addGadgetHandler, obj:gadget }});
    }
}

function addGadgetHandler(event, gadget) {
    var req = opensocial.newDataRequest();
    var params = {};
    try {
        if (owner) {
            req.add(socialsite.newInstallUserGadgetRequest('PROFILE',
                gadget.getField(socialsite.Gadget.Field.URL), params), "install");
        } else {
            req.add(socialsite.newInstallGroupGadgetRequest('GROUP',
                gadget.getField(socialsite.Gadget.Field.URL), params), "install");
        }
    } catch (e) {window.alert(e);}

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("install")) {
                errorMessage = data.get("install").getErrorMessage();
            }
            window.alert("ERROR installing gadget: " + errorMessage);
            return;
        } else {
            socialsite.hideLightbox(true);
        }
    });
}


//------------------------------------------------------------------- Utilities

/**
 * Setup next and previous buttons for any collection of items.
 */
function syncNextPrevButtons(items, callback, nextButton, prevButton) {
    var next = items.getOffset() + items.size() < items.getTotalSize();
    var prev = items.getOffset() > 0;
    if (next) {
        nextButton.set("disabled", false, true);
        nextButton.set("onclick",
            {fn: callback, obj: items.getOffset() + items.size()}, true)
    } else {
        nextButton.set("disabled", true, true);
        nextButton.set("onclick", {fn: noCallback}, true)
    }
    if (prev) {
        var prevOffset = items.getOffset() - maxGadgets;
        prevOffset = prevOffset >= 0 ? prevOffset : 0;
        prevButton.set("disabled", false, true);
        prevButton.set("onclick",
            {fn: callback, obj: prevOffset }, true)
    } else {
        prevButton.set("disabled", true, true);
        prevButton.set("onclick", {fn: noCallback}, true)
    }
}

//-----------------------------------------------------------------------------

// Go go go!
YAHOO.util.Event.onDOMReady(init);

