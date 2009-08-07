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
  * Request for friend information when the page loads.
  */
function getData() {
    document.getElementById('widgetBody').innerHTML = 'Requesting activities...';
    var req = opensocial.newDataRequest();

    // TODO: figure out if there is a way to get the viewer's and friend's activities
    // together.  FOR NOW: get these separately as was done when using 0.7 API

    // viewer's activities
    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    req.add(req.newFetchActivitiesRequest(spec), 'viewerActivities');

    // friends' data request
    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, 'FRIENDS');
    spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);
    req.add(req.newFetchActivitiesRequest(spec), 'friendsActivities');

    req.send(onLoadActivities);
}


/**
  * Parses the response to the activity information request and generates
  * html to list the activities.
  *
  * @param {Object} dataResponse information that was requested.
  */
function onLoadActivities(dataResponse) {
    socialsite.setTheming();

    var html = '';
    var activities;

    activities = dataResponse.get('friendsActivities').getData().asArray();
    activities = activities.concat(dataResponse.get('viewerActivities').getData().asArray());

    if (!activities || activities.length == 0) {
        html += '<div class="noactivity">You and your friends have no activities yet.</div>';
    } else {

        for (var i = 0; i < activities.length; i++) {
            var activity = activities[i];

            // add activity text
            html += '<div class="activity clearfloat">';
            var a = gadgets.util.unescapeString(activity.getField(opensocial.Activity.Field.BODY));
            html += markupActivity(a);

            if (i < activities.length - 1) {
                html += '<hr class="clearfloat"/>';
            }

            html += '</div>';

        }
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
