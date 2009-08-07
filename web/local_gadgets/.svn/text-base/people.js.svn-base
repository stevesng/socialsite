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

var viewer =  null;
var baseImageUrl = '../local_gadgets/files';
var maxPeople = 10;
var msgBox = new gadgets.MiniMessage();

gadgets.util.registerOnLoadHandler(init);


// ****************************************************************************

function init() {
    os.Container.registerDocumentTemplates();
    socialsite.setTheming();
    fetchData(0);
}

function fetchData(offset) {

    // TODO: do we need a progress indicator?
    //document.getElementById('widgetBody').innerHTML = 'Requesting people...';

    var req = opensocial.newDataRequest();

    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, '@all');
    var params = {};
    if (offset) {
        params[opensocial.DataRequest.PeopleRequestFields.FIRST] = offset;
    }
    params[opensocial.DataRequest.PeopleRequestFields.MAX] = maxPeople;
    params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
        opensocial.Person.Field.AGE,
        opensocial.Person.Field.CURRENT_LOCATION,
        "viewerRelationship" // TODO: constants for SocialSite fields
    ];

    req.add(req.newFetchPeopleRequest(spec, params), 'allPeople');
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');

    req.send(renderUI);
}


// ****************************************************************************

function renderUI(dataResponse) {
    viewer =        dataResponse.get('viewer').getData();
    allPeople =     dataResponse.get('allPeople').getData();

    // make viewerRelationship visible in widget, its not standard field
    var people = allPeople.asArray();
    for (ip=0; ip<people.length; ip++) {
        people[ip].viewerRelationship = people[ip].getField("viewerRelationship");
    }

    // render people
    var template = os.getTemplate('peopleContent');
    template.renderInto(document.getElementById('peopleContent'), {
        "viewer"   : viewer,
        "baseImageUrl" : baseImageUrl,
        "people"   : people,
        "start"    : allPeople.getOffset(),
        "end"      : allPeople.getOffset() + allPeople.size(),
        "count"    : allPeople.size(),
        "total"    : allPeople.getTotalSize()
    });

    // render the footer next/prev controls
    var footerParams = {};
    if (allPeople.getOffset() > 0) {
        var prev = allPeople.getOffset() - maxPeople;
        footerParams.prev = prev < 0 ? 0 : prev;
    }
    if (allPeople.getOffset() + allPeople.size() < allPeople.getTotalSize()) {
        var next = allPeople.getOffset() + allPeople.size() + 1;
        footerParams.next = next;
    }
    var footerTemplate = os.getTemplate('footerContent');
    footerTemplate.renderInto(
        document.getElementById('footerContent'), footerParams);
}


// **********************************************************************

function sendUserMessage(profileId) {
    var replacement = 'compose_mail.xml&msgTo=' + profileId + '&msgToType=PERSON';
    var messageUrl = location.href.replace("people.xml", replacement);
    socialsite.showLightbox('Mail', messageUrl);
}

function friendConnect(id, name) {
    var replacement = "ss_requestrelationship.xml&personId=" + id + "&personName=" + name;
    var requestUrl = location.href.replace("people.xml", replacement);
    socialsite.showLightbox('Request Relationship', requestUrl);
}
