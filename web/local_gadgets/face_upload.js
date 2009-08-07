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
var viewer =  null;
var uploadUrl = null;

// Where to find image resources
var baseImageUrl = '../local_gadgets/files';

// True if new image has been uploaded
var dirty = false;


gadgets.util.registerOnLoadHandler(fetchData);

function fetchData() {
    socialsite.setTheming();
    os.Container.registerDocumentTemplates();

    var req = opensocial.newDataRequest();
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.OWNER), 'owner');
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER), 'viewer');
    req.send(receiveData);
}

function receiveData(dataResponse) {
    owner = dataResponse.get('owner').getData();
    viewer = dataResponse.get('viewer').getData();

    var baseUrl = opensocial.Container.get().baseUrl_;
    var st = encodeURIComponent(shindig.auth.getSecurityToken());

    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
    uploadUrl = baseUrl + "/uploads/profile/" + owner.getId() + "?st=" + st;

    var template = os.getTemplate('uploadFormContent');
    template.renderInto(document.getElementById('uploadDiv'), {
        "viewer": viewer,
        "action": uploadUrl
    });
}

function onSubmit() {
    var fileName = trim(document.uploadForm.mugshot.value);
    if (fileName == "") {
        alert("Please specify a file");
        return false;
    }
    if (!isImage(fileName)) {
        alert("Please specify a JPG, GIF or PNG image");
        return false;
    }

    // we'll be uploading new images
    dirty = true;

    // show progress spinners in place of images
    $("#profileImage").html("<img src='" + baseImageUrl + "/progress.gif'/>");
    $("#thumbnailImage").html("<img src='" + baseImageUrl + "/progress.gif'/>");

    // wait 2 seconds then show images, which should be updated
    window.setTimeout(
        function() {
            // Append some unique param to ensure we don't hit the browser's cache
            var hackParam = "?" + new Date().getTime();
            $("#profileImage").html("<img src='" + viewer.getField("imageUrl") + hackParam + "'/>");
            $("#thumbnailImage").html("<img src='" + viewer.getField("thumbnailUrl") + hackParam + "'/>");
        }, 2000
    );

    return true;
}

function onDone() {
    socialsite.hideLightbox(dirty);
}

function trim(s) {
    return s.replace(/^\s+|\s+$/g, '');
}

function isImage(s) {
    if (s == s.match(".*\\.jpg$|.*\\.png$|.*\\.gif$")) {
        return true;
    }
    return false;
}
