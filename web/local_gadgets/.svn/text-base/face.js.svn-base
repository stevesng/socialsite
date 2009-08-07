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
 * @fileoverview SocialSite Mugshot Widget
 */
var owner =  null;

// TODO: don't hard code the baseURL!
var baseImageURL = '../local_gadgets/files';


gadgets.util.registerOnLoadHandler(fetchData);
function fetchData() {
    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');
    req.send(receiveData);
}

function receiveData(dataResponse) {
    owner = dataResponse.get('owner').getData();
    renderUI();
}

function renderUI() {
    socialsite.setTheming();

    // TODO: constants for SocialSite fields
    var imageURL = owner.getField("imageUrl");
    var html = '';
    html += '<center>';
    html +=  '<div class="mugshot">';
    html +=   '<a href="'+owner.getField(opensocial.Person.Field.PROFILE_URL)+'" target="_parent">';
    html +=    '<img id="mugshotImage" src="' + imageURL + '" onload="onImageLoad()" border="0" />';
    html +=   '</a>';
    html +=  '</div>';
    html +=  owner.getDisplayName();
    html += '</center>';
    document.getElementById('content').innerHTML = html;

    displayFooterActions();
}

function onClickUploadImage() {
    var url = location.href.replace("face.xml", "face_upload.xml");
    socialsite.showLightbox("Upload Image", url);
}

function displayFooterActions() {
    var footer = '';
    if (owner.isViewer()) {
        footer += '<center><ul>';
        footer += '<li><a onclick="onClickUploadImage()">';
        footer += '    <img src="' + baseImageURL + '/vcard_edit.png" title="Upload Image" />';
        footer += '</a></li>';
        footer += '</ul></center>';
    }
    $("#footer").html(footer);
}

function onImageLoad() {
    var maxSide = 100;
    var imageElement = $("#mugshotImage")[0];
    var w = imageElement.width;
    var h = imageElement.height;
    if (h < maxSide && w < maxSide) return;
    var ratio = w / h;
    if (ratio == 1) {
        w = h = maxSide;
    } else if (ratio > 1 && w > maxSide) {
        w = maxSize;
        h = maxSide / ratio;
    } else if (ratio < 1 && h > maxSide) {
        h = maxSize;
        w = maxSide * ratio;
    }
    imageElement.width = w;
    imageElement.height = h;
}

