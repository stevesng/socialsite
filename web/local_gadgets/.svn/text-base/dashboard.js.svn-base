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
 * @fileoverview Dashboard with support for people browsing and friending,
 * group management, activity viewing and messaging. Displays activities,
 * people, groups and messages each in its own tab. Uses YUI components
 * for buttons and menus.
 */

// Person object represents viewer
var viewer =  null;

// Google Gadgets Tab control
var tabSet = null;

// Keep track of which tabs have been initialized
var tabInitMap = {};

// Maximum number of items to show in each tab
var maxPeople = 6;
var maxMessages = 7;
var maxGroups = 7;
var maxActivities = 10;

// Callbacks to refresh each of the tabs
var refreshMessages = function() {}
var refreshPeople = function() {}
var refreshActivities = function() {}
var refreshGroups = function() {}
function noCallback() {}

// Relationship level names for person menu
var relationshipLevelNames = [];

// Where to find image resources
var baseImageUrl = '../local_gadgets/files';

// Google Gadgets Mini Message facility
var msg = new gadgets.MiniMessage(0, "minimessages");

// Pass recipient to create/send message methods, TODO: use listener instead
var recipient = null;


///////////////////////////////////////////////////////////////////////////////
//
//                    --------- Initialization ---------
//
///////////////////////////////////////////////////////////////////////////////


function init() {

    $("body").addClass("yui-skin-sam");
    YAHOO.namespace("ssdashboard");

    // Show progress indicator while gadget loads data
    YAHOO.ssdashboard.wait =
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

    YAHOO.ssdashboard.wait.setHeader("Loading, please wait...");
    YAHOO.ssdashboard.wait.setBody(
        '<img src="../app-ui/images/rel_interstitial_loading.gif" />');
    YAHOO.ssdashboard.wait.render(document.body);
    YAHOO.ssdashboard.wait.show();

    socialsite.setTheming();
    os.Container.registerDocumentTemplates();

    var req = opensocial.newDataRequest();
    var params = {};
    params[opensocial.DataRequest.PeopleRequestFields.PROFILE_DETAILS] = [
        "viewerRelationship"]; // TODO: constants for SocialSite fields
    req.add(req.newFetchPersonRequest(opensocial.IdSpec.PersonId.VIEWER, params), 'viewer');

    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, 'FRIENDS');
    spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);

    req.send(function(data) {
        viewer = data.get("viewer").getData();
        viewer.id = viewer.getId();

        var vrel = viewer.getField("viewerRelationship");
        relationshipLevelNames = vrel.relationshipLevelNames;

        initYUI_CommonDialogs();
        initYUI_People();
        initYUI_Activities();
        initYUI_Messages();
        initYUI_Groups();

        tabSet = new gadgets.TabSet(null, null, document.getElementById('tabs'));
        tabSet.alignTabs('left');

        tabSet.addTab("Activities", {
            contentContainer: document.getElementById("activitiesTab"),
            callback: activitiesTabCallback});

        tabSet.addTab("People", {
            contentContainer: document.getElementById("peopleTab"),
            callback: peopleTabCallback});

        tabSet.addTab("Groups", {
            contentContainer: document.getElementById("groupsTab"),
            callback: groupsTabCallback});

        tabSet.addTab("Messages", {
            contentContainer: document.getElementById("messagesTab"),
            callback: messagesTabCallback});

        YAHOO.ssdashboard.wait.hide();
    });
}


///////////////////////////////////////////////////////////////////////////////
//
//                     --------- Messages Tab ---------
//
///////////////////////////////////////////////////////////////////////////////


function messagesTabCallback(tabId) {
    if (!tabInitMap[tabId]) {
        tabInitMap[tabId] = "initialized";
        showMessages(null, {offset:0, obj:"inbox"});
    }
}

function initYUI_Messages() {
    var vrel = viewer.getField("viewerRelationship");

    //--- messages buttons

    var messagesFilterMenuData = [
        { text: "Inbox", value: "inbox", onclick: { fn: onMessagesFilterMenuItemClickHandler } },
        { text: "Sent", value: "sent", onclick: { fn: onMessagesFilterMenuItemClickHandler } }
    ];
    YAHOO.ssdashboard.messagesFilterButton = new YAHOO.widget.Button({
        id: "messagesFilterButton", type: "menu", label: "Inbox",
        menu: messagesFilterMenuData, container: "messagesFilterButton" });

    YAHOO.ssdashboard.prevMessagesButton = new YAHOO.widget.Button(
        { id: "prevMessagesButton", type: "button", label: "Prev",
          container: "prevMessagesButton"});
    YAHOO.ssdashboard.prevMessagesButton.set("disabled", true, true);

    YAHOO.ssdashboard.nextMessagesButton = new YAHOO.widget.Button(
        { id: "nextMessagesButton", type: "button", label: "Next",
          container: "nextMessagesButton"});
    YAHOO.ssdashboard.nextMessagesButton.set("disabled", true, true);


    //--- message view dialog

    YAHOO.ssdashboard.messageViewDialog = new YAHOO.widget.Dialog(
        "messageViewDialog",
        { modal: true, width:"500px",  x: 10, y: 10, close: false,
          visible: false, lazyloadmenu: true,
          fixedcenter: false, constraintoviewport: true } );

    YAHOO.ssdashboard.messageViewDeleteButton = new YAHOO.widget.Button(
        { id: "messageViewDeleteButton", type: "button", label: "Delete",
          container: "messageViewDeleteButton"});

    YAHOO.ssdashboard.messageViewCancelButton = new YAHOO.widget.Button(
        { id: "messageViewCancelButton", type: "button", label: "Cancel",
          container: "messageViewCancelButton",
          onclick: {fn:onViewMessageCancelHandler}});

    YAHOO.ssdashboard.messageViewDialog.render(document.body);


    //--- message create dialog

    YAHOO.ssdashboard.messageCreateDialog = new YAHOO.widget.Dialog(
        "messageCreateDialog",
        { modal: true, width:"500px",  x: 10, y: 10, close: false,
          visible: false, lazyloadmenu: true,
          fixedcenter: false, constraintoviewport: true } );

    YAHOO.ssdashboard.messageCreateSendButton = new YAHOO.widget.Button(
        { id: "messageCreateSendButton", type: "push", label: "Send",
          container: "messageCreateSendButton",
          onclick: {fn:onMessageSend}});
    YAHOO.ssdashboard.messageCreateSendButton.set("disabled", true, true);

    YAHOO.ssdashboard.messageCreateCancelButton = new YAHOO.widget.Button(
        { id: "messageCreateCancelButton", type: "button", label: "Cancel",
          container: "messageCreateCancelButton",
          onclick: {fn:onCancelCreateMessage}});

    YAHOO.ssdashboard.messageEditor = new YAHOO.widget.SimpleEditor('messageEditorContent',
        { toolbar: {
              titlebar: false,
              buttons: [
                { group: 'textstyle', label: 'Font Style',
                    buttons: [
                        { type: 'push', label: 'Bold', value: 'bold' },
                        { type: 'push', label: 'Italic', value: 'italic' },
                        { type: 'push', label: 'Underline', value: 'underline' }
                    ]
                }
            ]
        }
    });
    YAHOO.ssdashboard.messageEditor.addListener("htmlChange", onCreateMessageDialogChange);
    YAHOO.ssdashboard.messageEditor.render();

    // needed for IE7
    $("#messageEditorWrapper").hide();

    YAHOO.ssdashboard.messageCreateDialog.render(document.body);
}


//------------------------------------------------------------- Message display

function showMessages(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;
    var mailbox = pageSpec ? pageSpec.obj : null;

    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params.first = offset;
    params.max = maxMessages;

    req.add(socialsite.newFetchMessagesRequest(
        opensocial.IdSpec.PersonId.VIEWER, mailbox, params), 'messages');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("messages")) {
                errorMessage = data.get("messages").getErrorMessage();
            }
            window.alert("ERROR fetching messages: " + errorMessage);
            return;
        }
        renderMessagesContent("Showing " + mailbox,
            {fn: showMessages, obj:mailbox}, data.get("messages").getData());
    });
    refreshMessages = function() {
        showMessages(event, {offset: offset, obj: mailbox});
    }
}

function renderMessagesContent(title, callback, messages) {

    // enable/disable next/prev buttons
    syncNextPrevButtons(messages, maxMessages, callback,
        YAHOO.ssdashboard.nextMessagesButton, YAHOO.ssdashboard.prevMessagesButton);

    var template = os.getTemplate('messagesContent');
    template.renderInto(document.getElementById('messagesContent'), {
        "title"    : title,
        "viewer"   : viewer,
        "baseImageUrl" : baseImageUrl,
        "messages" : messages.asArray(),
        "start"    : messages.getOffset() + 1,
        "end"      : messages.getOffset() + messages.size(),
        "count"    : messages.size(),
        "total"    : messages.getTotalSize()
    });

    $("tr.itemRow").hover(
        function () {
            $(this).css("background","#eee");
        },
        function () {
            $(this).css("background","#fff");
        }
    );

    // add a split button for each message with appropriate options
    for (i in messages.asArray()) {
        var message = messages.asArray()[i];

        // add click listener so we can launch viewmessage dialogs
        var mlink = document.getElementById(message.getId() + "_messageLink");
        YAHOO.util.Event.addListener(mlink, "click", onViewMessage, message);

        var menuValue = {
            person:  message.getField("sender"),
            level:   message.getField("level"),
            howknow: message.getField("howknow"),
            group:   message.getField("group"),
            message: message};

        // TODO: define constants for extendedType field
        if (message.getField("extendedType") == "RELATIONSHIP_REQUEST") {
            var rrMenu = [
              { text: "Accept", value: "accept",
                onclick: { fn: onAcceptRelationship, obj: menuValue }},
              { text: "Ignore", value: "ignore",
                onclick: { fn: onIgnoreRelationship, obj: menuValue }}];
            new YAHOO.widget.Button({
                type: "split", label: "Action",
                name: message.getId()+"_messageControl",
                menu: rrMenu,
                container: message.getId()+"_messageControl" });
        }
        else if (message.getField("extendedType") == "GROUP_INVITE") {
            var giMenu = [
              { text: "Join", value: "accept",
                onclick: { fn: onAcceptGroupInvite, obj: menuValue }},
              { text: "Ignore", value: "ignore",
                onclick: { fn: onIgnoreGroupInvite, obj: menuValue }}];
            new YAHOO.widget.Button({
                type: "split", label: "Action",
                name: message.getId()+"_messageControl",
                menu: giMenu,
                container: message.getId()+"_messageControl" });
        }
        else if (message.getField("extendedType") == "GROUP_MEMBERSHIP_REQUEST") {
            var grMenu = [
              { text: "Accept", value: "accept",
                onclick: { fn: onAcceptGroupMember, obj: menuValue }},
              { text: "Ignore", value: "ignore",
                onclick: { fn: onIgnoreGroupMember, obj: menuValue }}];
            new YAHOO.widget.Button({
                type: "split", label: "Action",
                name: message.getId()+"_messageControl",
                menu: grMenu,
                container: message.getId()+"_messageControl" });
        }
    }
}


//---------------------------------------------------------------- Message view

function onViewMessage(event, message) {
    if (message.getField("sender")) {
        $("#messageFrom").html(message.getField("sender").getDisplayName());
    } else {
        $("#messageFrom").html("System Administrator");
    }
    $("#messageTitle").html(message.getField(opensocial.Message.Field.TITLE));
    $("#messageContent").html(message.getField(opensocial.Message.Field.BODY, {escapeType:"none"}));

    YAHOO.ssdashboard.messageViewDeleteButton.addListener(
        "click", onViewMessageDeleteHandler, message);

    YAHOO.ssdashboard.messageViewDialog.show();
}

function onViewMessageCancelHandler(event) {
    YAHOO.ssdashboard.messageViewDialog.hide();
}

function onViewMessageDeleteHandler(event, message) {
    YAHOO.ssdashboard.messageViewDialog.hide();
    var req = opensocial.newDataRequest();

    req.add(socialsite.newDeleteMessageRequest(
        opensocial.IdSpec.PersonId.VIEWER, message.getId()), 'delete');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("delete")) {
                errorMessage = data.get("delete").getErrorMessage();
            }
            window.alert("ERROR deleting message: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage("Deleted message");
            refreshMessages();
        }
    });
}


//-------------------------------------------------------------- Message create

function onCreateMessageToPerson(p_sType, p_aArgs, person) {
    $("#messageTo").html(person.getDisplayName() + " (" + person.getId() + ")");

    //try { // remove group listener, if there is one
    //    YAHOO.ssdashboard.messageCreateSendButton.removeListener(
    //        "click", onSendMessageToGroup);
    //} catch (intentionallyIgnored) {}
    //YAHOO.ssdashboard.messageCreateSendButton.addListener(
    //    "click", onSendMessageToPerson, person);

    // would have preferred to use listener (like above) rather than this global
    recipient = "person_" + person.getId();

    YAHOO.ssdashboard.messageCreateDialog.show();

    // needed for IE7
    $("#messageEditorWrapper").show();
}

function onCreateMessageToGroup(p_sType, p_aArgs, group) {
    $("#messageTo").html(group.getId() == null ? group.id : group.getId());

    //try { // remove person listener, if there is one
    //    YAHOO.ssdashboard.messageCreateSendButton.removeListener(
    //        "click", onSendMessageToPerson);
    //} catch (intentionallyIgnored) {}
    //YAHOO.ssdashboard.messageCreateSendButton.addListener(
    //    "click", onSendMessageToGroup, group);

    // would have preferred to use listener (like above) rather than this global
    recipient = "group_" + group.getId();

    YAHOO.ssdashboard.messageCreateDialog.show();

    // needed for IE7
    $("#messageEditorWrapper").show();
}

//function onSendMessageToPerson(p_sType, p_aArgs, person) {
//    sendMessage("person_" + person.getId());
//}
//function onSendMessageToGroup(p_sType, p_aArgs, group) {
//    sendMessage("group_" + group.getId());
//}

function onCancelCreateMessage(event) {
    closeMessageCreateDialog();
}

function onCreateMessageDialogChange() {
    var title =   trim(createMessageForm.title.value);
    var content = trim(YAHOO.ssdashboard.messageEditor.getEditorHTML());

    var disabled = true;
    if (title != "" && content != "") {
        disabled = false;
    }
    YAHOO.ssdashboard.messageCreateSendButton.set("disabled", disabled, true);
}

function closeMessageCreateDialog() {
    createMessageForm.title.value = "";
    YAHOO.ssdashboard.messageEditor.setEditorHTML("");
    YAHOO.ssdashboard.messageCreateDialog.hide();

    // needed for IE7
    $("#messageEditorWrapper").hide();
}

function onMessageSend(p_sType, p_aArgs) {

    var title =   trim(createMessageForm.title.value);
    var content = trim(YAHOO.ssdashboard.messageEditor.getEditorHTML());

    closeMessageCreateDialog();

    var req = opensocial.newDataRequest();
    var message = opensocial.newMessage(msg);
    message.setField(opensocial.Message.Field.TYPE,
        opensocial.Message.Type.PRIVATE_MESSAGE);
    message.setField(opensocial.Message.Field.TITLE, title);
    message.setField(opensocial.Message.Field.BODY, content);

    var recipients = [ recipient ]; // TODO: align with OpenSocial
    message.setField("recipients", recipients);

    req.add(socialsite.newPostMessageRequest(
        opensocial.IdSpec.PersonId.VIEWER, message), 'send');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("send")) {
                errorMessage = data.get("send").getErrorMessage();
                errorCode = data.get("send").getErrorCode();
            }
            window.alert("ERROR sending message: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage("Sent message to " + recipient);
            refreshMessages();
        }
    });
}


//------------------------------------------------- Other message menu handlers

function onMessagesFilterMenuItemClickHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdashboard.messagesFilterButton.set("label", p_oItem.cfg.getProperty("text"));
    if ("inbox" == p_oItem.value) {
        showMessages(null, {offset:0, obj: "inbox"});
    } else {
        showMessages(null, {offset:0, obj:"outbox"});
    }
}

function onIgnoreRelationship(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newIgnoreRelationshipRequest(
        opensocial.IdSpec.PersonId.VIEWER, menuValue.person), 'ignore');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("ignore")) {
                errorMessage = data.get("ignore").getErrorMessage();
            }
            window.alert("ERROR ignoring relationship: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Ignored relationship with " + menuValue.person.getDisplayName());
            refreshPeople();
            refreshMessages();
        }
    });
}

function onAcceptGroupInvite(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();
    var group = {id: menuValue.group.handle}; // TODO: use group object
    var person = {id: viewer.getId()};

    req.add(socialsite.newGroupApplicationRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'apply');
    req.add(socialsite.newDeleteMessageRequest(
        opensocial.IdSpec.PersonId.VIEWER, menuValue.message.getId(), 'remove'));

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("apply")) {
                errorMessage = data.get("apply").getErrorMessage();
            } else if (data.get("remove")) {
                errorMessage = data.get("remove").getErrorMessage();
            }
            window.alert("ERROR requesting membership in group: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(  // TODO: use group object
                "Requested membership in group: " + menuValue.group.name);
            refreshMessages();
        }
    });
}

function onIgnoreGroupInvite(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newDeleteMessageRequest(
        opensocial.IdSpec.PersonId.VIEWER, menuValue.message.getId()), 'remove');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("remove")) {
                errorMessage = data.get("remove").getErrorMessage();
            }
            window.alert("ERROR ignoring group invite: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage( // TODO: use group object
                "Ignored invitation to join group: " + menuValue.group.name);
            refreshMessages();
        }
    });
}

function onAcceptGroupMember(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();
    var group = {id: menuValue.group.handle}; // TODO: use group object
    var person = {id: menuValue.person.getId()};

    req.add(socialsite.newAcceptGroupApplicationRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'accept');
    req.add(socialsite.newDeleteMessageRequest(
        opensocial.IdSpec.PersonId.VIEWER, menuValue.message.getId(), 'remove'));

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("accept")) {
                errorMessage = data.get("accept").getErrorMessage();
            }
            window.alert("ERROR accepting new group member: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Accepted group membership request from: " + menuValue.person.getDisplayName());
            refreshMessages();
        }
    });
}

function onIgnoreGroupMember(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();
    var group = {id: menuValue.group.handle}; // TODO: use group object
    var person = {id: menuValue.person.getId()};

    req.add(socialsite.newIgnoreGroupApplicationRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'ignoreGroupRequest');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("remove")) {
                errorMessage = data.get("remove").getErrorMessage();
            } else if (data.get("remove")) {
                errorMessage = data.get("remove").getErrorMessage();
            }
            window.alert("ERROR ignoring group membership request: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Ignored group membership request from: " + menuValue.person.getDisplayName());
            refreshMessages();
        }
    });
}


///////////////////////////////////////////////////////////////////////////////
//
//                     --------- People Tab ---------
//
///////////////////////////////////////////////////////////////////////////////


function peopleTabCallback(tabId) {
    if (!tabInitMap[tabId]) {
        tabInitMap[tabId] = "initialized";
        showPeopleFriends(null, {offset:0});
    }
}

function initYUI_People() {
    var vrel = viewer.getField("viewerRelationship");

    //--- people buttons

    YAHOO.ssdashboard.peopleSearchButton = new YAHOO.widget.Button(
        { id: "peopleSearchButton", type: "button", label: "Search",
          container: "peopleSearchButtonContainer", onclick: {fn: onSearchPeople}});
    YAHOO.ssdashboard.peopleSearchButton.set("disabled", true, true);

    var peopleFilterMenuData = [
        { text: "All", value: "all", onclick: { fn: onPeopleFilterMenuItemClickHandler } },
        { text: "Friends", value: "friends", onclick: { fn: onPeopleFilterMenuItemClickHandler } }
    ];
    for (var i=0; i<vrel.groups.length; i++) {
        var menuItem = {};
        menuItem.text = "Group " + vrel.groups[i].displayName;
        menuItem.value = vrel.groups[i].handle;
        menuItem.onclick = {fn: onPeopleGroupFilterMenuItemClickHandler, obj: vrel.groups[i]};
        peopleFilterMenuData.push(menuItem);
    }
    YAHOO.ssdashboard.peopleFilterButton = new YAHOO.widget.Button({
        id: "peopleFilterButton", type: "menu", label: "Friends",
        menu: peopleFilterMenuData, container: "peopleFilterButton" });

    YAHOO.ssdashboard.prevPeopleButton = new YAHOO.widget.Button(
        { id: "prevPeopleButton", type: "button", label: "Prev",
          container: "prevPeopleButton"});
    YAHOO.ssdashboard.prevPeopleButton.set("disabled", true, true);

    YAHOO.ssdashboard.nextPeopleButton = new YAHOO.widget.Button(
        { id: "nextPeopleButton", type: "button", label: "Next",
          container: "nextPeopleButton"});
    YAHOO.ssdashboard.nextPeopleButton.set("disabled", true, true);
}


//-------------------------------------------------------------- People display

function showPeopleAll(event, pageSpec) {
    var offset = pageSpec.offset;

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
        "viewerRelationship" // TODO: contants for SocialSite fields
    ];

    req.add(req.newFetchPeopleRequest(spec, params), 'people');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("people")) {
                errorMessage = data.get("people").getErrorMessage();
            }
            window.alert("ERROR fetching all users: " + errorMessage);
            return;
        }
        renderPeopleContent("Everybody", {fn: showPeopleAll}, data.get("people").getData());
    });
    refreshPeople = function() {
        showPeopleAll(event, {offset: offset});
    }
}

function showPeopleFriends(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;

    var req = opensocial.newDataRequest();
    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, 'FRIENDS');
    spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);
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

    req.add(req.newFetchPeopleRequest(spec, params), 'people');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("people")) {
                errorMessage = data.get("people").getErrorMessage();
            }
            window.alert("ERROR fetching friend users: " + errorMessage);
            return;
        }
        renderPeopleContent("Friends", {fn: showPeopleFriends}, data.get("people").getData());
    });
    refreshPeople = function() {
        showPeopleFriends(event, {offset: offset});
    }
}

function showPeopleSearch(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;
    var searchString = pageSpec ? pageSpec.obj : null;

    var req = opensocial.newDataRequest();

    if (!searchString) searchString = trim(document.getElementById('peopleSearchField').value);

    var params = {};
    if (offset) {
        params[opensocial.DataRequest.PeopleRequestFields.FIRST] = offset;
    }
    params[opensocial.DataRequest.PeopleRequestFields.MAX] = maxPeople;

    req.add(socialsite.newSearchRequest(opensocial.IdSpec.PersonId.VIEWER,
        searchString, 'profile', params), 'search');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("search")) {
                errorMessage = data.get("search").getErrorMessage();
            }
            window.alert("ERROR searching people: " + errorMessage);
            return;
        }
        renderPeopleContent("Search", {fn: showPeopleSearch}, data.get("search").getData());
    });
    refreshPeople = function() {
        showPeopleSearch(event, {offset: offset, obj:searchString});
    }
}

function showPeopleInGroup(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;
    var groupId = pageSpec ? pageSpec.obj : null;

    var req = opensocial.newDataRequest();
    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, groupId);
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

    req.add(req.newFetchPeopleRequest(spec, params), 'search');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("search")) {
                errorMessage = data.get("search").getErrorMessage();
            }
            window.alert("ERROR searching people: " + errorMessage);
            return;
        }
        renderPeopleContent("Group: " + groupId, 
            {fn:showPeopleInGroup, obj:groupId}, data.get("search").getData());
    });
    refreshPeople = function() {
        showPeopleInGroup(event, {offset: offset, obj:groupId});
    }
}

function renderPeopleContent(title, callback, people) {

    // enable/disable next/prev buttons
    syncNextPrevButtons(people, maxPeople, callback,
        YAHOO.ssdashboard.nextPeopleButton, YAHOO.ssdashboard.prevPeopleButton);

    // render the people content template
    var template = os.getTemplate('peopleContent');
    template.renderInto(document.getElementById('peopleContent'), {
        "title"  : title,
        "viewer" : viewer,
        "people" : people.asArray(),
        "start"  : people.getOffset() + 1,
        "end"    : people.getOffset() + people.size(),
        "count"  : people.size(),
        "total"  : people.getTotalSize()
    });

    // add hover hander to turn row grey on hover
    $("tr.itemRow").hover(
        function () {
            $(this).css("background","#eee");
        },
        function () {
            $(this).css("background","#fff");
        }
    );

    // add a split button for each person with appropriate options
    for (i in people.asArray()) {
        var person = people.asArray()[i];
        var vrel = person.getField("viewerRelationship");

        // No action if this person is viewer
        if (person.getId() == viewer.getId()) {
            $("#" + person.getId()+"_relationshipControl").html("This is you!");
        }

        // If the person is waiting for you to either accept or ignore a
        // relationship then show the accept/ignore menu and nothing else
        else if (vrel.status == "PENDING_VIEWER") {
            var menuValue = {
                person:  person,
                level:   vrel.level,
                howknow: vrel.howknow
            };

            var menuPendingData = [
              { text: "Accept", value: "acceptRelationship",
                onclick: { fn: onAcceptRelationship, obj: menuValue } },
              { text: "Ignore", value: "ignoreRelationship",
                onclick: { fn: onIgnoreRelationship, obj: menuValue } }];
            new YAHOO.widget.Button({
                type: "split", label: "Pending",
                name: person.getId()+"_relationshipControl",
                menu: menuPendingData,
                container: person.getId()+"_relationshipControl" });
        }


        else {

            // Show menu so viewer can change their relationship with the
            // person, send message or invite them to join a group
            var personMenuData = [];

            // Show Relationship Levels in menu and indicate which level
            // is the current level in effect for viewer to person
            var friendMenuData = [];
            for (j in vrel.relationshipLevelNames) {
                var levelName = vrel.relationshipLevelNames[j];
                var menuValue = { person: person, level: j};

                if (j == vrel.level) {
                    // this is current level, no action on this item
                    friendMenuData[j] = {text: levelName + " \u276e\u276e"};
                }
                else if (j == 0) {
                    // this is item 0, it means end relationship
                    friendMenuData[j] = {
                        text: levelName, value: "endRelationship",
                        onclick: { fn: onEndRelationship, obj: person }
                    };
                }
                else if (j <  vrel.friendshipLevel && vrel.status == "NONE") {
                    // to go from no relationship to less than friendship we
                    // create non-friend-level relationship, no dialog needed
                    friendMenuData[j] = {
                        text: levelName, value: "createRelationship",
                        onclick: { fn: onCreateRelationship, obj: menuValue }};
                }
                else if (j >=  vrel.friendshipLevel
                      &&  vrel.level <  vrel.friendshipLevel
                      && vrel.status != "MUTUAL"
                      && vrel.status != "PENDING") {
                    // to upgrade to friendship level, when not already mutual
                    // or pending we must request friend-level relationship
                    friendMenuData[j] = {
                        text: levelName, value: "requestRelationship",
                        onclick: { fn: onCreateRelatonship, obj: menuValue }};
                }
                else {
                    // otherwise we're just adjusting relationship
                    friendMenuData[j] = {
                        text: levelName, value: "adjustRelationship",
                        onclick: { fn: onAdjustRelationship, obj: menuValue }};
                }
            }
            personMenuData.push(friendMenuData);

            // Menu item to allow viewer to send message to person
            var messageMenuData = [];
            messageMenuData.push({
                text:  "Send message",
                onclick: { fn: onCreateMessageToPerson, obj: person }});
            personMenuData.push(messageMenuData);

            // Menu items to invite person to suitable viewer's groups
            var groupMenuData = [];
            if (vrel.suggestedGroups && vrel.suggestedGroups.length > 0) {
                for (var si=0; si<vrel.suggestedGroups.length; si++) {
                    var inviteMenuValue = {
                       person: menuValue.person,
                       group: vrel.suggestedGroups[si]
                    };
                    groupMenuData.push({
                        text:  "Invite to group " + vrel.suggestedGroups[si].displayName,
                        value: vrel.suggestedGroups[si].displayName,
                        onclick: { fn: onInviteToGroup, obj: inviteMenuValue}});
                }
            }
            personMenuData.push(groupMenuData);

            new YAHOO.widget.Button({
                type: "split", label: vrel.relationshipLevelNames[vrel.level],
                name: person.getId()+"_relationshipControl",
                menu: personMenuData,
                container: person.getId()+"_relationshipControl" });
        }
    }
}


//-------------------------------------------------------------- People actions

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
                "Requested/created relationship with " + person.getDisplayName());
            refreshPeople();
        }
    });
}

function clarifyRelationship(person, level, howknow) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newClarifyRelationshipRequest(
        opensocial.IdSpec.PersonId.VIEWER, person, level, howknow), 'clarify');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("clarify")) {
                errorMessage = data.get("clarify").getErrorMessage();
                errorCode = data.get("clarify").getErrorCode();
            }
            window.alert("ERROR clarifying relationship: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Clarifying relationship with " + person.getDisplayName());
            refreshPeople();
        }
    });
}

function acceptRelationship(person, level, howknow) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newAcceptRelationshipRequest(
        opensocial.IdSpec.PersonId.VIEWER, person, level), 'accept');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("accept")) {
                errorMessage = data.get("accept").getErrorMessage();
                errorCode = data.get("accept").getErrorCode();
            }
            window.alert("ERROR accepting relationship: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Accepting relationship with " + person.getDisplayName());
            refreshPeople();
            refreshMessages();
        }
    });
}

//------------------------------------------------------------- People handlers

function onPeopleFilterMenuItemClickHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdashboard.peopleFilterButton.set("label", p_oItem.cfg.getProperty("text"));
    if ("all" == p_oItem.value) {
        showPeopleAll(null, {offset:0});
    } else {
        showPeopleFriends(null, {offset:0});
    }
}

function onPeopleGroupFilterMenuItemClickHandler(p_sType, p_aArgs, group) {
    YAHOO.ssdashboard.peopleFilterButton.set("label", "Group");
    showPeopleInGroup(null, {offset:0, obj:group.handle}); // TODO: use group object
}

function onPeopleSearchFieldChange() {
    if (trim(document.getElementById('peopleSearchField').value) != "") {
        YAHOO.ssdashboard.peopleSearchButton.set("disabled", false, true);
    } else {
        YAHOO.ssdashboard.peopleSearchButton.set("disabled", true, true);
    }
}

function onSearchPeople(p_sType, p_aArgs, value) {
    var searchString = trim(document.getElementById('peopleSearchField').value);
    if (searchString && searchString != "") {
        showPeopleSearch(null, {offset:0, obj: searchString} );
    }
    // we never ever want to actually submit the form
    return false;
}

function onCreateRelationship(p_sType, p_aArgs, menuValue) {
    requestRelationship(menuValue.person, menuValue.level, menuValue.howknow);
}

function onAdjustRelationship(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newAdjustRelationshipRequest(
        opensocial.IdSpec.PersonId.VIEWER, menuValue.person, menuValue.level), 'adjust');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("adjust")) {
                errorMessage = data.get("adjust").getErrorMessage();
            }
            window.alert("ERROR adjusting relationship: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Adjusted relationship with " + menuValue.person.getDisplayName());
            refreshPeople();
        }
    });
}

function onEndRelationship(p_sType, p_aArgs, person) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newRemoveRelationshipRequest(
        opensocial.IdSpec.PersonId.VIEWER, person), 'remove');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("remove")) {
                errorMessage = data.get("remove").getErrorMessage();
            }
            window.alert("ERROR ending relationship: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Ended relationship with " + person.getDisplayName());
            refreshPeople();
        }
    });
}

function onInviteToGroup(p_sType, p_aArgs, menuValue) {
    var req = opensocial.newDataRequest();

    req.add(socialsite.newGroupInvitationRequest(
        opensocial.IdSpec.PersonId.VIEWER, menuValue.person, menuValue.group), 'invite');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("invite")) {
                errorMessage = data.get("invite").getErrorMessage();
            }
            window.alert("ERROR inviting user: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage(
                "Invited user " + menuValue.person.getDisplayName());
            refreshPeople();
        }
    });
}

function onSendMessageToPerson(p_sType, p_aArgs, person) {

}


///////////////////////////////////////////////////////////////////////////////
//
//                     --------- Activities Tab ---------
//
///////////////////////////////////////////////////////////////////////////////


function activitiesTabCallback(tabId) {
    if (!tabInitMap[tabId]) {
        tabInitMap[tabId] = "initialized";
        showGroupActivities(null, {offset:0, obj:"FRIENDS"});
    }
}

function initYUI_Activities() {
    var vrel = viewer.getField("viewerRelationship");

    //--- activities buttons

    var activitiesFilterMenuData = [
        { text: "Yours", value: "yours", onclick: { fn: onActivitiesFilterMenuItemClickHandler } },
        { text: "Friends", value: "friends", onclick: { fn: onActivitiesFilterMenuItemClickHandler } }
    ];
    for (i=0; i<vrel.groups.length; i++) {
        menuItem = {};
        menuItem.text = "Group " + vrel.groups[i].displayName;
        menuItem.value = vrel.groups[i].handle;
        menuItem.onclick = {fn: onActivitiesGroupFilterMenuItemClickHandler, obj: vrel.groups[i]};
        activitiesFilterMenuData.push(menuItem);
    }
    YAHOO.ssdashboard.activitiesFilterButton = new YAHOO.widget.Button({
        id: "activitiesFilterButton", type: "menu", label: "Friends",
        menu: activitiesFilterMenuData, container: "activitiesFilterButton" });

    YAHOO.ssdashboard.prevActivitiesButton = new YAHOO.widget.Button(
        { id: "prevActivitiesButton", type: "button", label: "Prev",
          container: "prevActivitiesButton"});
    YAHOO.ssdashboard.prevActivitiesButton.set("disabled", true, true);

    YAHOO.ssdashboard.nextActivitiesButton = new YAHOO.widget.Button(
        { id: "nextActivitiesButton", type: "button", label: "Next",
          container: "nextActivitiesButton"});
    YAHOO.ssdashboard.nextActivitiesButton.set("disabled", true, true);
}


//---------------------------------------------------------- Activities display

function showOwnerActivities(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;

    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params["first"] = offset;
    params["max"] = maxActivities;

    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.OWNER);

    req.add(req.newFetchActivitiesRequest(spec, params), 'activities');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("activities")) {
                errorMessage = data.get("activities").getErrorMessage();
            }
            window.alert("ERROR fetching activities: " + errorMessage);
            return;
        }
        renderActivitiesContent("Your activities",
            {fn: showOwnerActivities}, data.get("activities").getData());
    });
    refreshActivities = function() {
        showOwnerActivities(event, {offset: offset});
    }
}

function showGroupActivities(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;
    var groupId = pageSpec? pageSpec.obj : null;

    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params["first"] = offset;
    params["max"] = maxActivities;

    var spec = new opensocial.IdSpec();
    spec.setField(opensocial.IdSpec.Field.USER_ID, opensocial.IdSpec.PersonId.VIEWER);
    spec.setField(opensocial.IdSpec.Field.GROUP_ID, groupId);
    spec.setField(opensocial.IdSpec.Field.NETWORK_DISTANCE, 1);

    req.add(req.newFetchActivitiesRequest(spec, params), 'activities');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("activities")) {
                errorMessage = data.get("activities").getErrorMessage();
            }
            window.alert("ERROR fetching activities: " + errorMessage);
            return;
        }
        var title = groupId == "FRIENDS"
            ? "Friends activities" : "Activities for group: " + groupId;
        renderActivitiesContent(title,
            {fn: showGroupActivities, obj:groupId}, data.get("activities").getData());
    });
    refreshActivities = function() {
        showGroupActivities(event, {offset: offset, obj: groupId});
    }
}

function renderActivitiesContent(title, callback, activities) {

    // enable/disable next/prev buttons
    syncNextPrevButtons(activities, maxActivities, callback,
        YAHOO.ssdashboard.nextActivitiesButton, YAHOO.ssdashboard.prevActivitiesButton);

    // Work around an OS templates problem: all OpenSocial fields are escaped
    var activitiesArray = activities.asArray();
    for (var ai=0; ai<activitiesArray.length; ai++) {
        activitiesArray[ai].unescapedBody =  activitiesArray[ai].getField(
            opensocial.Activity.Field.BODY, {escapeType:'none'});
        activitiesArray[ai].unescapedTitle = activitiesArray[ai].getField(
            opensocial.Activity.Field.TITLE, {escapeType:'none'});
    }

    var template = os.getTemplate('activitiesContent');
    template.renderInto(document.getElementById('activitiesContent'), {
        "title"    : title,
        "viewer"   : viewer,
        "baseImageUrl" : baseImageUrl,
        "activities"   : activitiesArray,
        "start"    : activities.getOffset() + 1,
        "end"      : activities.getOffset() + activities.size(),
        "count"    : activities.size(),
        "total"    : activities.getTotalSize()
    });

    $("tr.itemRow").hover(
        function () {
            $(this).css("background","#eee");
        },
        function () {
            $(this).css("background","#fff");
        }
    );

    $("a").attr("target","_parent");
}

function onActivitiesFilterMenuItemClickHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdashboard.activitiesFilterButton.set("label", p_oItem.cfg.getProperty("text"));
    if ("yours" == p_oItem.value) {
        showOwnerActivities(null, {offset:0});
    } else {
        showGroupActivities(null, {offset:0, obj:"FRIENDS"});
    }
}

function onActivitiesGroupFilterMenuItemClickHandler(p_sType, p_aArgs, group) {
    YAHOO.ssdashboard.activitiesFilterButton.set("label", "Group");
    showGroupActivities(null, {offset:0, obj:group.handle});  // TODO: use group object
}


///////////////////////////////////////////////////////////////////////////////
//
//                     --------- Groups Tab ---------
//
///////////////////////////////////////////////////////////////////////////////


function groupsTabCallback(tabId) {
    if (!tabInitMap[tabId]) {
        tabInitMap[tabId] = "initialized";
        showViewerGroups(null, {offset: 0});
    }
}

function initYUI_Groups() {
    var vrel = viewer.getField("viewerRelationship");

    //--- create group dialog

    YAHOO.ssdashboard.createGroupDialog = new YAHOO.widget.Dialog(
        "createGroupDialog",
        { modal: true, width:"500px",  x: 10, y: 10, close: false,
          visible: false, lazyloadmenu: true,
          fixedcenter: false, constraintoviewport: true } );

    YAHOO.ssdashboard.submitGroupButton = new YAHOO.widget.Button(
        { id: "submitGroupButton", container: "submitGroupButton",
          type: "button", label: "Create Group",
          onclick: { fn: onSaveGroup }});
    YAHOO.ssdashboard.submitGroupButton.set("disabled", true, true);

    YAHOO.ssdashboard.cancelCreateGroupButton = new YAHOO.widget.Button(
        { id: "cancelCreateGroupButton", container: "cancelCreateGroupButton",
          type: "button", label: "Cancel",
          onclick: { fn: onCancelCreateGroup }});

    YAHOO.ssdashboard.createGroupDialog.render(document.body);

    //--- groups buttons

    YAHOO.ssdashboard.groupsSearchButton = new YAHOO.widget.Button(
        { id: "groupsSearchButton", type: "button", label: "Search",
          container: "groupsSearchButton", onclick: {fn: onSearchGroups}});
    YAHOO.ssdashboard.groupsSearchButton.set("disabled", true, true);

    YAHOO.ssdashboard.createGroupButton = new YAHOO.widget.Button(
        { id: "createGroupButton", type: "button", label: "Create Group",
          container: "createGroupButton", onclick: { fn: onCreateGroup }});

    var groupsFilterMenuData = [
        { text: "All", value: "all", onclick: { fn: onGroupsFilterMenuItemClickHandler } },
        { text: "Yours", value: "yours", onclick: { fn: onGroupsFilterMenuItemClickHandler } }
    ];
    YAHOO.ssdashboard.groupsFilterButton = new YAHOO.widget.Button({
        id: "groupsFilterButton", type: "menu", label: "Yours",
        menu: groupsFilterMenuData, container: "groupsFilterButton" });

    YAHOO.ssdashboard.prevGroupsButton = new YAHOO.widget.Button(
        { id: "prevGroupsButton", type: "button", label: "Prev",
          container: "prevGroupsButton"});
    YAHOO.ssdashboard.prevGroupsButton.set("disabled", true, true);

    YAHOO.ssdashboard.nextGroupsButton = new YAHOO.widget.Button(
        { id: "nextGroupsButton", type: "button", label: "Next",
          container: "nextGroupsButton"});
    YAHOO.ssdashboard.prevGroupsButton.set("disabled", true, true);
}


//------------------------------------------------------------- Groups display

function showAllGroups(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;

    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params["first"] = offset;
    params["max"] = maxGroups;

    req.add(socialsite.newFetchPublicGroupsRequest(
        opensocial.IdSpec.PersonId.VIEWER, params), 'allgroups');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("allgroups")) {
                errorMessage = data.get("allgroups").getErrorMessage();
            }
            window.alert("ERROR fetching all groups: " + errorMessage);
            return;
        }
        renderGroupsContent("All Groups",
            {fn:showAllGroups}, data.get("allgroups").getData());
    });
    refreshGroups = function() {
        showAllGroups(event, {offset:offset});
    }
}

function showViewerGroups(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;

    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params["first"] = offset;
    params["max"] = maxGroups;

    req.add(socialsite.newFetchUsersGroupsRequest(
        opensocial.IdSpec.PersonId.VIEWER, params), 'viewerGroups');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("viewerGroups")) {
                errorMessage = data.get("viewerGroups").getErrorMessage();
            }
            window.alert("ERROR fetching viewer groups: " + errorMessage);
            return;
        }
        renderGroupsContent("Your Groups",
            {fn:showViewerGroups}, data.get("viewerGroups").getData());
    });
    refreshGroups = function() {
        showViewerGroups(event, {offset:offset});
    }
}

function showGroupsSearch(event, pageSpec) {
    var offset = pageSpec ? pageSpec.offset : null;
    var searchString = pageSpec ? pageSpec.obj : null;
    
    var req = opensocial.newDataRequest();
    var params = {};
    if (offset) params["first"] = offset;
    params["max"] = maxGroups;

    if (!searchString) searchString = trim(groupsControlForm.query.value);

    req.add(socialsite.newSearchRequest(
        opensocial.IdSpec.PersonId.VIEWER, searchString, 'group', params), 'searchGroups');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            if (data.get("searchGroups")) {
                errorMessage = data.get("searchGroups").getErrorMessage();
            }
            window.alert("ERROR searching groups: " + errorMessage);
            return;
        }
        renderGroupsContent("Groups matching: " + searchString,
            {fn:showGroupsSearch, obj:searchString}, data.get("searchGroups").getData());
    });
    refreshGroups = function() {
        showGroupsSearch(event, {offset:offset, obj:searchString});
    }
}

function renderGroupsContent(title, callback, groups) {

    // enable/disable next/prev buttons
    syncNextPrevButtons(groups, maxGroups, callback,
        YAHOO.ssdashboard.nextGroupsButton, YAHOO.ssdashboard.prevGroupsButton);

    var template = os.getTemplate('groupsContent');
    template.renderInto(document.getElementById('groupsContent'), {
        "title"    : title,
        "viewer"   : viewer,
        "baseImageUrl" : baseImageUrl,
        "groups"   : groups.asArray(),
        "start"    : groups.getOffset() + 1,
        "end"      : groups.getOffset() + groups.size(),
        "count"    : groups.size(),
        "total"    : groups.getTotalSize()
    });

    $("tr.itemRow").hover(
        function () {
            $(this).css("background","#eee");
        },
        function () {
            $(this).css("background","#fff");
        }
    );

    // add a split button for each group with appropriate options
    for (i in groups.asArray()) {
        var group = groups.asArray()[i];
        var vrel = group.getField("viewerRelationship");
        if (vrel.relationship == "ADMIN" || vrel.relationship == "FOUNDER") {
            continue;
        }
        var menuRequestData = [];
        if (vrel.relationship == "NONE") {
            menuRequestData.push(
              { text: "Join Group", value: "joinGroup",
                onclick: { fn: onJoinGroup, obj:group }});
        } else if (vrel.relationship == "MEMBER") {
            menuRequestData.push(
              { text: "Leave Group", value: "leaveGroup",
                onclick: { fn: onLeaveGroup, obj:group }});
        }
        menuRequestData.push(
              { text: "Message to group", value: "messageGroup",
                onclick: { fn: onCreateMessageToGroup, obj:group }});

        new YAHOO.widget.Button({
            type: "split", label: "Request",
            name: group.getId()+"_membershipControl",
            menu: menuRequestData,
            container: group.getId()+"_membershipControl" });
    }

}

//------------------------------------------------------------- Groups handlers

function onCreateGroup(p_sType, p_aArgs, menuValue) {
    YAHOO.ssdashboard.createGroupDialog.show();
}

function onGroupSearchFieldChange() {
    if (trim(document.getElementById('groupsSearchField').value) != "") {
        YAHOO.ssdashboard.groupsSearchButton.set("disabled", false, true);
    } else {
        YAHOO.ssdashboard.groupsSearchButton.set("disabled", true, true);
    }
}

function onGroupsFilterMenuItemClickHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdashboard.groupsFilterButton.set("label", p_oItem.cfg.getProperty("text"));
    if ("all" == p_oItem.value) {
        showAllGroups(null, {offset:0});
    } else {
        showViewerGroups(null, {offset:0});
    }
}

function onSearchGroups(p_sType, p_aArgs, menuValue) {
    var searchString = trim(document.getElementById('groupsSearchField').value);
    if (searchString && searchString != "") {
        showGroupsSearch(null, {offset:0, obj: searchString} );
    }
    // we never ever want to actually submit the form
    return false;
}

function onJoinGroup(p_sType, p_aArgs, group) {
    var req = opensocial.newDataRequest();
    group.id = group.getId();
    var person = {};
    person.id = viewer.getId();

    req.add(socialsite.newGroupApplicationRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'join');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("join")) {
                errorMessage = data.get("join").getErrorMessage();
                errorCode = data.get("join").getErrorCode();
            }
            window.alert("ERROR requesting to join group: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage("Requested membership in group "
                + group.getField(socialsite.Group.Field.NAME));
            refreshGroups();
        }
    });
}

function onLeaveGroup(p_sType, p_aArgs, group) {
    var req = opensocial.newDataRequest();
    group.id = group.getId();
    var person = {};
    person.id = viewer.getId();

    req.add(socialsite.newRemoveGroupMemberRequest(
        opensocial.IdSpec.PersonId.VIEWER, group, person), 'leave');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("leave")) {
                errorMessage = data.get("join").getErrorMessage();
                errorCode = data.get("join").getErrorCode();
            }
            window.alert("ERROR leaving group: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage("Left group "
                + group.getField(socialsite.Group.Field.NAME));
            refreshGroups();
        }
    });
}

function onSaveGroup(p_sType, p_aArgs, menuValue) {
    YAHOO.ssdashboard.createGroupDialog.hide();

    var group = {};
    group.name =        trim(createGroupForm.name.value);
    group.handle =      trim(createGroupForm.handle.value);
    group.description = trim(createGroupForm.description.value);
    group.imageUrl =    trim(createGroupForm.iconUrl.value);

    // make sure handle contains no special characters.
    var encodedHandle = encodeURIComponent(group.handle);

    if (encodedHandle != group.handle) {
        window.alert('Please specify a group handle which does not contain special characters');
        YAHOO.ssdashboard.createGroupDialog.show();
        return;
    }

    var req = opensocial.newDataRequest();

    req.add(socialsite.newCreateGroupProfileRequest(
        opensocial.IdSpec.PersonId.VIEWER, group), 'create');

    req.send(function(data) {
        if (data.hadError()) {
            var errorMessage = data.getErrorMessage();
            var errorCode = 0;
            if (data.get("create")) {
                errorMessage = data.get("create").getErrorMessage();
                errorCode = data.get("create").getErrorCode();
            }
            window.alert("ERROR creating group: " + errorMessage);
            return;
        } else {
            msg.createDismissibleMessage("Created new group " + group.name);
            refreshGroups();
        }
    });
}

function onGroupDialogChange() {
    var name =        trim(createGroupForm.name.value);
    var handle =      trim(createGroupForm.handle.value);
    var description = trim(createGroupForm.description.value);
    var imageUrl =    trim(createGroupForm.iconUrl.value);
    var disabled = true;
    if (name != "" && handle != "" && description != "") {
        if (imageUrl != "" && validateUrl(imageUrl)) {
            disabled = false;
        }
        if (imageUrl == "") {
            disabled = false;
        }
     }
     YAHOO.ssdashboard.submitGroupButton.set("disabled", disabled, true);
}

function onCancelCreateGroup(p_sType, p_aArgs, ignored) {
    YAHOO.ssdashboard.createGroupDialog.hide();
    
    createGroupForm.name.value = "";
    createGroupForm.handle.value = "";
    createGroupForm.description.value = "";
    createGroupForm.iconUrl.value = "";
}


///////////////////////////////////////////////////////////////////////////////
//
//                    --------- Common code ---------
//
///////////////////////////////////////////////////////////////////////////////

function initYUI_CommonDialogs() {
    var vrel = viewer.getField("viewerRelationship");

    //--- relationship dialog

    YAHOO.ssdashboard.relationshipDialog = new YAHOO.widget.Dialog(
        "relationshipDialog",
        { modal: true, width:"500px",  x: 10, y: 10, close: false,
          visible: false, lazyloadmenu: true,
          fixedcenter: false, constraintoviewport: true } );

    YAHOO.ssdashboard.relationshipSendButton = new YAHOO.widget.Button(
        { id: "relationshipSendButton", type: "button", label: "Send",
          container: "relationshipSendButton",
          onclick: {fn:onSubmitCreateRelationship}});

    YAHOO.ssdashboard.relationshipCancelButton = new YAHOO.widget.Button(
        { id: "relationshipCancelButton", type: "button", label: "Cancel",
          container: "relationshipCancelButton",
          onclick: {fn:onCancelCreateRelationship} });

    YAHOO.ssdashboard.relationshipDialog.render(document.body);

    var friendlevelMenu = [];
    var count = 0;
    for (var ni = 0; ni < vrel.relationshipLevelNames.length; ni++) {
        if (ni >= vrel.friendshipLevel) {
            friendlevelMenu[count++] = { text: vrel.relationshipLevelNames[ni],
                value: ni, onclick: {fn:onRelationshipLevelComboHandler}};
        }
    }
    YAHOO.ssdashboard.relationshipLevelCombo = new YAHOO.widget.Button(
        { id: "relationshipLevelCombo", type: "split", label: "Level",
          menu: friendlevelMenu, container: "relationshipLevelCombo" });
    if (count == 1) {
        // there's only one friendship level and we don't send requests
        // when the level is less than friendship level so disable combo
        YAHOO.ssdashboard.relationshipLevelCombo.set("disabled", true);
    }


    //--- accept relationship dialog

    YAHOO.ssdashboard.acceptRelationshipDialog = new YAHOO.widget.Dialog(
        "acceptRelationshipDialog",
        { modal: true, width:"500px",  x: 10, y: 10, close: false,
          visible: false, lazyloadmenu: true,
          fixedcenter: false, constraintoviewport: true } );

    YAHOO.ssdashboard.acceptRelationshipButton = new YAHOO.widget.Button(
        { id: "acceptRelationshipButton", type: "button", label: "Accept",
          container: "acceptRelationshipButton",
          onclick: {fn:onSubmitAcceptRelationship}});

    YAHOO.ssdashboard.clarifyRelationshipButton = new YAHOO.widget.Button(
        { id: "clarifyRelationshipButton", type: "button", label: "Clarify",
          container: "clarifyRelationshipButton",
          onclick: {fn:onSubmitClarifyRelationship} });

    YAHOO.ssdashboard.acceptRelationshipDialog.render(document.body);

    count = 0;
    var levelMenu = [];
    for (var mi = 0; mi < vrel.relationshipLevelNames.length; mi++) {
        if (mi > 0) {
            levelMenu[count++] = { text: vrel.relationshipLevelNames[mi],
                value: mi, onclick: {fn:onAcceptRelationshipLevelComboHandler}};
        }
    }
    YAHOO.ssdashboard.acceptRelationshipLevelCombo = new YAHOO.widget.Button(
        { id: "acceptRelationshipLevelCombo", type: "split", label: "Level",
          menu: levelMenu, container: "acceptRelationshipLevelCombo" });
}


//------------------------------------------ Create Relationship dialog methods

function onCreateRelatonship(p_sType, p_aArgs, menuValue) {
    $("#relationshipHowknowText")[0].value = "";

    YAHOO.ssdashboard.relationshipLevelCombo.set(
        "label", relationshipLevelNames[menuValue.level]);

    YAHOO.ssdashboard.relationshipDialog.show();
    YAHOO.ssdashboard.relationshipDialog.menuValue = menuValue;
}

function onRelationshipLevelComboHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdashboard.relationshipLevelCombo.set("label", p_oItem.cfg.getProperty("text"));
}

function onCancelCreateRelationship() {
    YAHOO.ssdashboard.relationshipDialog.hide();
}

function onSubmitCreateRelationship() {
    YAHOO.ssdashboard.relationshipDialog.hide();
    requestRelationship(
        YAHOO.ssdashboard.relationshipDialog.menuValue.person,
        YAHOO.ssdashboard.relationshipDialog.menuValue.level,
        $("#relationshipHowknowText")[0].value);
}


//------------------------------------------ Accept Relationship dialog methods

function onAcceptRelationship(p_sType, p_aArgs, menuValue) {
    $("#acceptRelationshipHowknowText")[0].value = menuValue.howknow;

    YAHOO.ssdashboard.acceptRelationshipLevelCombo.set(
        "label", relationshipLevelNames[menuValue.level]);

    YAHOO.ssdashboard.acceptRelationshipDialog.show();
    YAHOO.ssdashboard.acceptRelationshipDialog.menuValue = menuValue;
}

function onAcceptRelationshipLevelComboHandler(p_sType, p_aArgs, p_oItem) {
    YAHOO.ssdashboard.acceptRelationshipLevelCombo.set("label", p_oItem.cfg.getProperty("text"));
    YAHOO.ssdashboard.acceptRelationshipDialog.menuValue.level = p_oItem.value;
}

function onSubmitAcceptRelationship() {
    YAHOO.ssdashboard.acceptRelationshipDialog.hide();

    acceptRelationship(
        YAHOO.ssdashboard.acceptRelationshipDialog.menuValue.person,
        YAHOO.ssdashboard.acceptRelationshipDialog.menuValue.level,
        $("#acceptRelationshipHowknowText")[0].value);
}

function onSubmitClarifyRelationship() {
    YAHOO.ssdashboard.acceptRelationshipDialog.hide();

    clarifyRelationship(
        YAHOO.ssdashboard.acceptRelationshipDialog.menuValue.person,
        YAHOO.ssdashboard.acceptRelationshipDialog.menuValue.level,
        $("#acceptRelationshipHowknowText")[0].value);
}


//------------------------------------------------------------------- Utilities

/**
 * Setup next and previous buttons for any collection of items.
 */
function syncNextPrevButtons(items, maxItems, callback, nextButton, prevButton) {
    var next = items.getOffset() + items.size() < items.getTotalSize();
    var prev = items.getOffset() > 0;
    if (next) {
        nextButton.set("disabled", false, true);
        nextButton.set("onclick",
            {fn: callback.fn, obj: {obj: callback.obj, offset: items.getOffset() + items.size()} }, true);
    } else {
        nextButton.set("disabled", true, true);
        nextButton.set("onclick", {fn: noCallback}, true)
    }
    if (prev) {
        var prevOffset = items.getOffset() - maxItems;
        prevOffset = prevOffset >= 0 ? prevOffset : 0;
        prevButton.set("disabled", false, true);
        prevButton.set("onclick",
            {fn: callback.fn, obj: {obj: callback.obj, offset:prevOffset} }, true);
    } else {
        prevButton.set("disabled", true, true);
        prevButton.set("onclick", {fn: noCallback}, true)
    }
}

function trim(s) {
    return s.replace(/^\s+|\s+$/g, '');
}

function validateUrl(s) {
    var regex = new RegExp();
    regex.compile("^[A-Za-z]+://.");
    if (!regex.test(s)) {
        return false;
    }
    return true;
}


//-----------------------------------------------------------------------------

// Go go go!
YAHOO.util.Event.onDOMReady(init);
