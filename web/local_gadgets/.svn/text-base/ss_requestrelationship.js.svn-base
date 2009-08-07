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

var baseImageUrl = '../local_gadgets/files';
var msg = new gadgets.MiniMessage();

var viewer;
var vrel;

var relationshipLevel = 1;

gadgets.util.registerOnLoadHandler(init);

var personId = gadgets.util.getUrlParameters().personId;
var personName = gadgets.util.getUrlParameters().personName;


// ****************************************************************************

function init() {
    os.Container.registerDocumentTemplates();
    socialsite.setTheming();

    $("body").addClass("yui-skin-sam");
    YAHOO.namespace("ssdialog");

    fetchData();
}

function fetchData() {
    var req = opensocial.newDataRequest();
    var params = {};
    params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
        opensocial.Person.Field.AGE,
        opensocial.Person.Field.CURRENT_LOCATION,
        "viewerRelationship"]; // TODO: constants for SocialSite fields

    req.add(req.newFetchPersonRequest(
        opensocial.IdSpec.PersonId.VIEWER, params), 'viewer');
    req.send(renderUI);
}

function renderUI(dataResponse) {
    viewer = dataResponse.get('viewer').getData();
    vrel = viewer.getField("viewerRelationship");

    var template = os.getTemplate('relationshipDialog');
    template.renderInto(document.getElementById('relationshipDialog'), {
        "personName" : personName,
        "viewer"     : viewer
    });

    $("#howknowLabel").hide();
    $("#howknowField").hide();

    YAHOO.ssdialog.relationshipSendButton = new YAHOO.widget.Button(
        { id: "relationshipSendButton", type: "button", label: "Send",
          container: "relationshipSendButton",
          onclick: {fn:submitCreateRelationshipDialog}});

    YAHOO.ssdialog.relationshipCancelButton = new YAHOO.widget.Button(
        { id: "relationshipCancelButton", type: "button", label: "Cancel",
          container: "relationshipCancelButton",
          onclick: {fn:cancelCreateRelationshipDialog} });

    var friendlevelMenu = [];
    for (var ni = 0; ni < vrel.relationshipLevelNames.length; ni++) {
        if (ni > 0) {
            friendlevelMenu[ni-1] = {
                text: vrel.relationshipLevelNames[ni],
                value: ni, onclick: {fn:onRelationshipLevelComboHandler} };
        }
    }
    YAHOO.ssdialog.relationshipLevelCombo = new YAHOO.widget.Button(
        { id: "relationshipLevelCombo", type: "split",
          label: vrel.relationshipLevelNames[1],
          menu: friendlevelMenu, container: "relationshipLevelCombo" });
}

function onRelationshipLevelComboHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdialog.relationshipLevelCombo.set(
        "label", p_oItem.cfg.getProperty("text"));

    relationshipLevel = p_oItem.value;

    if (relationshipLevel >= vrel.friendshipLevel) {
        $("#howknowLabel").show("slow");
        $("#howknowField").show("slow");
    } else {
        $("#howknowLabel").hide("slow");
        $("#howknowField").hide("slow");
    }
    onDialogChange();
}

function onDialogChange() {
    if ($("#relationshipHowknowText")[0].value == ""
        && relationshipLevel >= vrel.friendshipLevel) {
       YAHOO.ssdialog.relationshipSendButton.set("disabled", true, true);
    } else {
       YAHOO.ssdialog.relationshipSendButton.set("disabled", false, true);
    }
}

function cancelCreateRelationshipDialog() {
    socialsite.hideLightbox(false);
}

function submitCreateRelationshipDialog() {
    requestRelationship(
        {id: personId},
        relationshipLevel,
        $("#relationshipHowknowText")[0].value);
}

function requestRelationship(person, level, howknow) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newCreateRelationshipRequest(
        opensocial.IdSpec.PersonId.VIEWER, person, level, howknow), 'request');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("request")) {
                errorMessage = data.get("request").getErrorMessage();
                errorCode = data.get("request").getErrorCode();
            }
            if (errorCode == opensocial.ResponseItem.Error.LIMIT_EXCEEDED) {
                // duplicate request, no big deal but warn user
                msg.createDismissibleMessage(errorMessage);
            } else {
                // more serious problem
                window.alert("ERROR requesting/creating relationship: " + errorMessage);
            }
            return;
        } else {
            msg.createDismissibleMessage(
                "Requested/created relationship with " + personName);
            cancelCreateRelationshipDialog();
        }
    });
}
