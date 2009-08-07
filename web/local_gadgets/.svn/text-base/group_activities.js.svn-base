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

/**
  * Request for activity information when the page loads.
  */
function getData() {
    document.getElementById('widgetBody').innerHTML = 'Requesting group activities...';
    var req = opensocial.newDataRequest();

    req.add(req.newFetchPersonRequest(
        opensocial.IdSpec.PersonId.VIEWER), 'viewer');

    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, "@current");

    req.add(req.newFetchActivitiesRequest(spec), 'activities');

    req.add(socialsite.newFetchGroupRequest(
        opensocial.IdSpec.PersonId.VIEWER, "@current"), 'groupdetails');

    req.send(onLoadActivities);
}


/**
  * Parses the response to the activity information request and generates
  * html to list the activities.
  * @param {Object} dataResponse information that was requested.
  */
function onLoadActivities(dataResponse) {
    var viewer = dataResponse.get('viewer').getData();

    socialsite.setTheming();
    var html = '';

    var group = dataResponse.get('groupdetails').getData();
    if (group == null) {
        document.getElementById('widgetBody').innerHTML = 'ERROR: A group needs to be specified in order to show the group activities.';
        return;
    }

    var activities = dataResponse.get("activities").getData().asArray();

    var activity;
    for (var i=0; i < activities.length; i++) {
        activity = activities[i];
        var a = gadgets.util.unescapeString(activity.getField(opensocial.Activity.Field.BODY));
        html += markupActivity(a);

        // hr not needed at bottom of list
        if (i < activities.length - 1) {
            html += '<hr class="clearfloat"/>';
        }
        html += '</div>';
    }

    if (activities.length <= 0) {
        html += '<div class="noactivity">This group has no activities yet.</div>';
    }

    document.getElementById('widgetBody').innerHTML = html;
}


function markupActivity(s) {
    if (s == null) {
        return null
    }
    var a1 = s.replace('<img', '<div class="icon"><img');
    var a2 = a1.replace('<a', '</div><div class="text"><h2><a');
    var a3 = a2.replace(/<a/g, '<a target="_parent"');

    var activity = a3 + '</h2></div>';
    return activity;
}

gadgets.util.registerOnLoadHandler(getData);
