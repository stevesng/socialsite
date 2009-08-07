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
 * @fileoverview SocialSite Gadget Directory and Installer; this is similar
 *  to the "gadget_directory" gadget, but it actually makes the gadget
 *  installation calls directly (instead of making an RPC request for the
 *  container page).
 */

var MSG_NORESULTS = "No results match your input.";


/**
 * Removes whitespace from the beginning and end of the specified string.
 */
String.prototype.trim = function() {
    return this.replace(/^\s+|\s+$/g, '');
}


/**
 * Returns true if the current string starts with s, false otherwise.
 */
String.prototype.startsWith = function(s) {
    return (this.match('^'+s) == s);
}


/**
 * Returns true if the current string ends with s, false otherwise.
 */
String.prototype.endsWith = function(s) {
    return (this.match(s+'$') == s);
}


function updateLayout() {
    var widgetBody = document.getElementById('widgetBody');
    widgetBody.style.height = window.innerHeight - widgetBody.offsetTop;
}


function clearDisplay() {
    var widgetBody = document.getElementById('widgetBody');
    while (widgetBody.childNodes.length >= 1) {
        widgetBody.removeChild(widgetBody.firstChild);
    }
}


/**
 * Perform tasks when the gadget is loaded.
 */
function onLoad() {
    socialsite.setTheming();
}


function addGadget(gadgetUrl) {
    var req = opensocial.newDataRequest();
    var params = {};
    req.add(socialsite.newInstallUserGadgetRequest('PROFILE', gadgetUrl, params, 'install'));
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


function handleInput(input) {

    clearDisplay();

    // See if input is empty
    var trimmedInput = input.trim();
    if (trimmedInput == '') {
        return;
    }

    // See if input looks like a gadget spec URL
    var loweredInput = trimmedInput.toLowerCase();
    if (loweredInput.startsWith('http://') || loweredInput.startsWith('https://')) {
        addGadget(trimmedInput);
        return;
    }

    // Finally, assume input is just a search string
    doSearch(trimmedInput);

}


function doSearch(searchString) {
    var widgetBody = document.getElementById('widgetBody');
    var req = opensocial.newDataRequest();
    req.add(
        socialsite.newSearchRequest(
            opensocial.IdSpec.PersonId.VIEWER,
            searchString, 'gadget', 0, 10
        ),
        'gadgets'
    );
    req.send(doSearchCallback);
}


function doSearchCallback(response) {
    // TODO: why is this asArray bit needed?
    var results = response.get('gadgets').getData().asArray()[0];
    var widgetBody = document.getElementById('widgetBody');
    var html = '';
    if (!results || results.totalApps <= 0) {
        html += '<ul><li><div class="text"><h2>'+MSG_NORESULTS+'</h2></div></li></ul>';
        widgetBody.innerHTML = html;
    } else if (results.error) {
        html += '<ul><li><div class="text"><h2>'+results.errorMessage+'</h2></div></li></ul>';
        widgetBody.innerHTML = html;
    } else {
        var ul = document.createElement('ul');
        for (var i in results.Gadgets) {
            var gadget = results.Gadgets[i];
            var li = document.createElement('li');
            var a = document.createElement('a');
            a.setAttribute('href', gadget.url);
            a.onclick = function() {
                addGadget(this.getAttribute('href'));
                return false;
            };
            a.appendChild(document.createTextNode(gadget.title));
            li.appendChild(a);
            ul.appendChild(li);
        }
        widgetBody.appendChild(ul);
    }
}


gadgets.util.registerOnLoadHandler(onLoad);
