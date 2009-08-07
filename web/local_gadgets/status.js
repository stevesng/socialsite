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
 * @fileoverview SocialSite Status Widget
 */

var owner = null;

var msgBox = new gadgets.MiniMessage();

var currentStatus = "";

var statusUpdated = false;

gadgets.util.registerOnLoadHandler(getData);


/*
 * Removes whitespace from the beginning and end of the specified string.
 */
function trim(s) {
    return s.replace(/^\s+|\s+$/g, '');
}


/**
 * Request for owner information when the page loads.
 */
function getData() {
    document.getElementById('widgetBody').innerHTML = 'Requesting status information...';

    var req = opensocial.newDataRequest();
    var opt_params = {};
    opt_params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] =
        [opensocial.Person.Field.STATUS];
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER, opt_params), 'owner');

    req.send(onLoadStatus);
}


/**
 * Parses the response to the owner's information request and generates
 * html to show the status.
 *
 * @param {Object} dataResponse information that was requested.
 */
function onLoadStatus(dataResponse) {
    owner = dataResponse.get('owner').getData();

    socialsite.setTheming();

    var html = '';

    html += '<div id="lightboxDiv" class="clearfloat">';

    // determine if the viewer can edit the status by seeing if they are the owner
    if (owner.isViewer()) {
        html += getEditStatusHtml();
    } else {
        html += getViewStatusHtml();
    }

    html += '</div>';

    document.getElementById('widgetBody').innerHTML = html;

    if (statusUpdated) {
        msgBox.createTimerMessage('Your status has been updated.', 5);
        statusUpdated = false;
    }
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
 * Gets the HTML to allow the user to edit their status.
 */
function getEditStatusHtml() {
    var result = '';

    result += '<form onsubmit="updateStatus(); return false" action="" id="show_status" name="statusForm">';

    // Status intro
    result += '<div class="text">What are you doing right now?</div>';

    // get current status
    if (owner.getField(opensocial.Person.Field.STATUS) != null) {
        currentStatus = owner.getField(opensocial.Person.Field.STATUS);
    }

    // status text field
    result += '<div class="text">';
    result += '<input type="text" size="35" name="status" id="status_field" value="' + currentStatus + '"/> ';

    // Update button
    result += '<input type="submit" value="Update Status" name="update_button" id="update_button"/>';
    result += '</div>';

    result += '</form>';

    return result;
}


/**
 * Gets the HTML to allow the user to view the owner's status.
 */
function getViewStatusHtml() {
    var result = '';

    result += '<span class="propLabel">' + getInformalName(owner) + '\'s current status:</span>';

    // get current status
    if (owner.getField(opensocial.Person.Field.STATUS) != null) {
        currentStatus = owner.getField(opensocial.Person.Field.STATUS);
    }

    // status text field
    result += '<span class="propValue">' + currentStatus + '</span>';

    return result;
}


/*
 * Method called when user tries to update their status in the form.
 */
function updateStatus() {
    var status = trim(document.getElementById('status_field').value);
    if (status == "") {
        msgBox.createTimerMessage('Please specify a status', 5);
        return;
    } else if (status == currentStatus) {
        msgBox.createTimerMessage('Status unchanged', 5);
        return;
    }
    var req = opensocial.newDataRequest();
    req.add(socialsite.newUpdateStatusRequest(
      opensocial.IdSpec.PersonId.OWNER, status), "dummy");

    statusUpdated = true;
    req.send(getData);
}
