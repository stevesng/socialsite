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

var viewer;

function onLoad() {
    socialsite.setTheming();
   os.Container.registerDocumentTemplates();
   init();
}

gadgets.util.registerOnLoadHandler(onLoad);

function init() {
    var req = opensocial.newDataRequest();
    var params = {};
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, params), 'viewer');
    req.add(socialsite.newFetchRegisteredAppsRequest(), "registrations");

    req.send(function(response) {
        viewer = response.get("viewer").getData();
        viewer.id = viewer.getId();
        var registrations = response.get("registrations").getData();
        var template = os.getTemplate('registrations');
        template.renderInto(document.getElementById('registrations'), {
            "registrations": registrations.asArray(),
            "size": registrations.size()
        });

        socialsite.statusMessage(
           "Enter a valid Gadget Spec URL above",
           "information");
        $("#registerButton").attr("disabled", "true");
    });
}

function onGadgetURLChange() {
    var gadgetURL = jQuery.trim($("#gadgetURL").val());
    if (gadgetURL.match(/http:\/\/([\w.-:]+)\/(\S*)/) ) {
        $("#registerButton").removeAttr("disabled");
        socialsite.statusMessage(
            "Looks good so far, click the register button when you are done typing.","success");
    } else if (gadgetURL == "") {
        socialsite.statusMessage("Enter a valid Gadget Spec URL above","information");
        $("#registerButton").attr("disabled", "true");
    } else {
        $("#registerButton").attr("disabled", "true");
        socialsite.statusMessage("That does not look like a valid Gadget Spec URL.","warning");
    }
}

function registerApp() {
    var gadgetURL             = jQuery.trim($("#gadgetURL").val());
    var serviceName           = jQuery.trim($("#serviceName").val());
    var serviceConsumerKey    = jQuery.trim($("#serviceConsumerKey").val());
    var serviceConsumerSecret = jQuery.trim($("#serviceConsumerSecret").val());
    var serviceKeyType        = jQuery.trim($("#serviceKeyType").val());
    var req = opensocial.newDataRequest();
    req.add(socialsite.newRegisterAppRequest(gadgetURL,
       serviceName, serviceConsumerKey, serviceConsumerSecret, serviceKeyType), 'response');
    req.send(function() {
        init();
    });
}

function removeApp(id) {
    if (window.confirm("Are you sure you want to delete the Gadget Registration?")) {
        var req = opensocial.newDataRequest();
        req.add(socialsite.newUnregisterAppRequest(id), 'response');
        req.send(function() {
            init();
        });
    }
}

