
/**
 * @fileoverview SocialSite API based on OpenSocial/Shindig JSON-RPC protocol.
 */

// uses JSDoc annotations, see http://jsdoc.sourceforge.net


/**
 * @namespace SocialSite JavaScript API
 */
var socialsite = socialsite || {};

/**
 * Valid for methods that return collections
 */
socialsite.RequestParameter = {
    FIRST: "first",
    MAX: "max"
}

/**
 * Create request for SocialSite Profile Definition metadata which can be used
 * to display or provide an editor for raw SocialSite Profile Properties.
 *
 * @param {String} userId represents user ID of data being fetched
 * @returns {socialsite.JsonProfileDefinition} Profile Definition object 
 * @member socialsite
 */
socialsite.newFetchProfileDefinitionRequest = function(userId) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "profiledef.get" };
    rpc.params = this.translateIdSpec(idSpec);
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return new socialsite.ProfileDefinition(rawJson);
        }
    );
}


/**
 * Create request for SocialSite Profile Properties, a flat collection of
 * name and value pairs that use a special naming convention to represent
 * the OpenSocial person model hierarchy.
 *
 * @param {String} userId represents user ID of data being fetched
 * @returns {Map.&lt;String, String&gt;} map of name and value pairs
 * @member socialsite
 */
socialsite.newFetchProfilePropertiesRequest = function(userId) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "profiles.get" };
    rpc.params = this.translateIdSpec(idSpec);
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * Create request to update SocialSite Profile Properties, a flat collection
 * of name and value pairs that use a special naming convention to represent
 * the OpenSocial person model hierarchy.
 *
 * @param {String} userId id represents user ID of data being updated
 * @param {Map.&lt;String, String&gt;} profileProps profileProps map of name and value pairs
 * @member socialsite
 */
socialsite.newUpdateProfilePropertiesRequest = function(userId, profileProps) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "profiles.put" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.appId = "@app";
    rpc.params.profileProps = profileProps;
    return new JsonRpcRequestItem(rpc);
}


/**
 * Update user's status.
 *
 * @param {String} userId  id represents user ID of data being updated
 * @param {String} newStatus new status message
 * @member socialsite
 */
socialsite.newUpdateStatusRequest = function(userId, newStatus) {
    var profileProps = new Object();
    profileProps[opensocial.Person.Field.STATUS] = newStatus;

    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "profiles.put" };
    rpc.params = this.translateIdSpec(idSpec);  
    rpc.params.appId = "@app";
    rpc.params.profileProps = profileProps;
    return new JsonRpcRequestItem(rpc);
}


/**
 * Create request to fetch SocialSite Section Privacy data so that user can see
 * his/her privacy settings.
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId id represents user ID of data being updated
 * @returns {Array&lt;socialsite.SectionPrivacy&gt;} 
 * @member socialsite
 */
socialsite.newFetchSectionPrivaciesRequest = function(userId) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "sectionprivs.get" };
    rpc.params = this.translateIdSpec(idSpec);    
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonPrivs;
            if (rawJson['list']) {
                // For the array of items response
                jsonPrivs = rawJson['list'];
            } else {
                // For the single items response
                jsonPrivs = [rawJson];
            }

            var privs = [];
            for (var i = 0; i < jsonPrivs.length; i++) {
                privs.push(new socialsite.SectionPrivacy(jsonPrivs[i]));
            }
            return new opensocial.Collection(privs,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}


/**
 * Create request to fetch SocialSite Section Privacy data so that user can see
 * his/her privacy settings.
 * 
 * @param {String} userId represents user ID of data being updated
 * @param {String} sectionName name of section to be updated
 * @param {SectionPrivacy} sectionPrivacy privacy setting for section
 * @member socialsite
 */
socialsite.newUpdateSectionPrivacyRequest = function(userId, sectionName, sectionPrivacy) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "sectionprivs.put", "sectionName" : sectionName};
    rpc.params = this.translateIdSpec(idSpec);  
    rpc.params.appId = "@app";
    rpc.params.sectionPrivacy = sectionPrivacy.toJsonObject();
    return new JsonRpcRequestItem(rpc);
}

/**
 * Create request to send a message to a person or group. The recipient(s)
 * is contained within the message.
 *
 * @param {String} userId Represents the id of the user sending the message.
 * @param {Message} message The Opensocial message to be sent.
 * @member socialsite
 */
socialsite.newPostMessageRequest = function(userId, message) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "messages.post" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.message = message;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * Create request to fetch a specific message.
 *
 * TODO: use opensocial.jsonmessage if it becomes part of the API.
 *
 * @param {String} userId The ID of the user retrieving the message.
 * @param {String} msgId The ID of the message to be shown.
 * @member socialsite
 */
socialsite.newFetchMessageRequest = function(userId, msgId) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "messages.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.messageId = msgId;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return new JsonMessage(rawJson)
        }
    );
}


/**
 * Create request to fetch all messages for a user.
 *
 * TODO: use opensocial.jsonmessage if it becomes part of the API.
 * TODO: do we need userId here?
 *
 * @param {String} userId The ID of the user retrieving the message.
 * @param {String} box Currently 'inbox' or 'outbox'
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @member socialsite
 */
socialsite.newFetchMessagesRequest = function(userId, box, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "messages.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.box = box;
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonMessages;
            if (rawJson['list']) {
                // For the array of messages response
                jsonMessages = rawJson['list'];
            } else {
                // For the single message response
                jsonMessages = [rawJson];
            }

            var messages = [];
            for (var i = 0; i < jsonMessages.length; i++) {
                messages.push(new JsonMessage(jsonMessages[i]));
            }
            return new opensocial.Collection(messages,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}


/**
 * Create request to set the status of a message.
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId The ID of the user retrieving the message.
 * @param {String} messageId The ID of the message to be shown.
 * @param {String} status The new status for the message, e.g. "READ"
 * @member socialsite
 */
socialsite.newSetMessageStatusRequest = function(userId, messageId, status) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "messages.put" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.messageId = messageId;
    rpc.params.status = status;
    return new JsonRpcRequestItem(rpc);
}

/**
 * Create request to delete a message.
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId The ID of the user retrieving the message.
 * @param {String} messageId The ID of the message to be shown.
 * @member socialsite
 */
socialsite.newDeleteMessageRequest = function(userId, messageId) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "messages.delete" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.messageId = messageId;
    rpc.params.status = status;
    return new JsonRpcRequestItem(rpc);
}

/**
 * Create request for SocialSite Group Definition metadata which can be used
 * to display or provide an editor for raw SocialSite Group Properties.
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId represents group ID of data being fetched
 * @param {Array&lt;Object&gt;} opt_params Array of optional arguments (not used) 
 * @returns {socialsite.JsonProfileDefinition}Group Profile Definition object 
 * @member socialsite
 */
socialsite.newFetchGroupDefinitionRequest = function(userId, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groupdef.get" };
    rpc.params = this.translateIdSpec(idSpec);
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return new socialsite.ProfileDefinition(rawJson);
        }
    );
}

/**
 * Create request for getting all public groups in SocialSite
 * 
 * @param {String} userId  id represents user ID of data being updated
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @returns {socialsite.JsonProfileDefinition} 
 * @member socialsite
 */
socialsite.newFetchPublicGroupsRequest = function(userId, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groups.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.qualifier1 = "@public";
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonGroups;
            if (rawJson['list']) {
                // For the array of messages response
                jsonGroups = rawJson['list'];
            } else {
                // For the single message response
                jsonGroups = [rawJson];
            }

            var groups = [];
            for (var i = 0; i < jsonGroups.length; i++) {
                groups.push(new socialsite.Group(jsonGroups[i]));
            }
            return new opensocial.Collection(groups,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}


/**
 * Create request for getting the groups that a user's friends belong to in
 * SocialSite
 * 
 * @param {String} userId  id The user ID of the user whose friend's groups are to be returned
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @returns {socialsite.JsonProfileDefinition} 
 * @member socialsite
 */
socialsite.newFetchFriendsGroupsRequest = function(userId, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groups.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.qualifier2 = "@friends";
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonGroups;
            if (rawJson['list']) {
                // For the array of messages response
                jsonGroups = rawJson['list'];
            } else {
                // For the single message response
                jsonGroups = [rawJson];
            }

            var groups = [];
            for (var i = 0; i < jsonGroups.length; i++) {
                groups.push(new socialsite.Group(jsonGroups[i]));
            }
            return new opensocial.Collection(groups,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}

/**
 * Create request for getting the groups that the members of a group belong
 * to in SocialSite (used for filtering)
 *
 * TODO: do we need the userId argument here?
 *
 * @param {String} userId id represents user ID of data being updated
 * @param {String} groupId id of requested group
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @returns {socialsite.JsonProfileDefinition} 
 * @member socialsite
 */
socialsite.newFetchGroupMembersGroupsRequest = function(userId, groupId, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groups.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.qualifier1 = "@groups";
    rpc.params.qualifier2 = groupId;
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonGroups;
            if (rawJson['list']) {
                // For the array of messages response
                jsonGroups = rawJson['list'];
            } else {
                // For the single message response
                jsonGroups = [rawJson];
            }

            var groups = [];
            for (var i = 0; i < jsonGroups.length; i++) {
                groups.push(new socialsite.Group(jsonGroups[i]));
            }
            return new opensocial.Collection(groups,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}


/**
 * Create request for a public group in SocialSite
 *
 * TODO: do we need the userId argument here?
 *
 * @param {String} userId id represents user ID of data being updated
 * @param {String} groupId id of requested group
 * @returns {socialsite.JsonProfileDefinition}  
 * @member socialsite
 */
socialsite.newFetchGroupRequest = function(userId, groupId) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groups.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.qualifier1 = "@public";
    rpc.params.qualifier2 = groupId;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return new socialsite.Group(rawJson);
        }
    );
}

/**
 * Create request for getting a user's groups in SocialSite
 * 
 * @param {String} userId id represents user ID of data being updated
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @returns {socialsite.JsonProfileDefinition}  
 * @member socialsite
 */
socialsite.newFetchUsersGroupsRequest = function(userId, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groups.get" };
    rpc.params = this.translateIdSpec(idSpec);
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonGroups;
            if (rawJson['list']) {
                // For the array of messages response
                jsonGroups = rawJson['list'];
            } else {
                // For the single message response
                jsonGroups = [rawJson];
            }

            var groups = [];
            for (var i = 0; i < jsonGroups.length; i++) {
                groups.push(new socialsite.Group(jsonGroups[i]));
            }
            return new opensocial.Collection(groups,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}

/**
 * Create request for getting the members who belong to 2 groups
 * 
 * @param {String} userId   User ID of calling user,
 * @param {String} groupId1 First group id which members must belong to
 * @param {String} groupId2 Second group id which members must along belong to
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @member socialsite
 */
socialsite.newFetchCommonGroupMembersRequest = function(userId, groupId1, groupId2, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.get" };
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupId = "@union";
    rpc.params.groupId1 = groupId1;
    rpc.params.groupId2 = groupId2;
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * Create request to get SocialSite Group Profile Properties, a flat
 * collection of name and value pairs that use a special naming convention
 * to enable meta-data driven property editing.
 *
 * @param {String} groupId represents user ID of data being fetched
 * @returns {Map.&lt;String, String&gt;} map of name and value pairs
 * @member socialsite
 */
socialsite.newFetchGroupProfilePropertiesRequest = function(groupId) {
    var rpc = { method : "groupprofiles.get" };
    rpc.params = {};
    rpc.params.groupId = groupId;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 *Create request to remove a group
 *
 * @param {String} groupHandle The handle for the group to be deleted
 * @member socialsite
 */
socialsite.newDeleteGroupRequest = function(groupHandle) {
    var rpc = { method : "groupprofiles.delete" } ;
    rpc.params = {};
    rpc.params.groupId = groupHandle;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * Create request for group profile creation by posting SocialSite Group
 * Profile Properties, a flat collection of name and value pairs that use
 * a special naming convention to enable meta-data driven property editing.
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId represents user ID of group creator
 * @param {Map&lt;Stirng, String&gt;} groupDetails group details
 * @member socialsite
 */
socialsite.newCreateGroupProfileRequest = function(userId, groupDetails) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groupprofiles.post" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.qualifier1 = "@public";
    rpc.params.groupDetails = groupDetails;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * Create request to update SocialSite Group Profile Properties, a flat
 * collection of name and value pairs that use a special naming convention
 * to enable meta-data driven property editing.
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId id represents user ID of data being updated
 * @param {Map.&lt;String, String&gt;} profileProps profileProps map of name and value pairs
 * @member socialsite
 */
socialsite.newUpdateGroupProfilePropertiesRequest = function(userId, profileProps) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "groupprofiles.put" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.groupId = "@current";
    rpc.params.appId = "@app";
    rpc.params.profileProps = profileProps;
    return new JsonRpcRequestItem(rpc);
}

/**
 * New create-relationship request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user making relationship request
 * @param {Person} person Person being requested as friend
 * @param {int}    level  Relationship level
 * @param {String} howknow         How friend is known
 * @member socialsite
 */
socialsite.newCreateRelationshipRequest = function(userId, person, level, howknow) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.post" };
    if (!person.id) person.id = person.getId();
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupId = "@friends";    
    rpc.params.person = person;
    if (level) rpc.params.level = level;
    if (howknow) rpc.params.howknow = howknow;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * New accept-relationship request
 *
 * @param {String} userId User ID of calling user accepting request
 * @param {Person} person  Person being requested as friend
 * @param {int}    level      Relationship level
 * @member socialsite
 */
socialsite.newAcceptRelationshipRequest = function(userId, person, level) {
    if (!person.id) person.id = person.getId();
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.post" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.groupId = "@friends";
    rpc.params.person = person; // TODO: should probably be .toJsonObject()
    rpc.params.level = level;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * New clarify-relationship request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user clarifying request
 * @param {Person} person Other end of the relationship
 * @param {int}    level Relationship level
 * @param {String} howknow clarified how users know each other string
 *
 * @member socialsite
 */
socialsite.newClarifyRelationshipRequest = function(userId, person, level, howknow) {
    if (!person.id) person.id = person.getId();
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.put" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.groupId = "@requests";
    rpc.params.person = person; // TODO: should probably be .toJsonObject()
    rpc.params.howknow = howknow;
    rpc.params.level = level;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * New ignore-relationship request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user ignoring request
 * @param {Person} person Person being ignored
 * @member socialsite
 */
socialsite.newIgnoreRelationshipRequest = function(userId, person) {
    if (!person.id) person.id = person.getId();
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.delete" };
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupId = "@requests";
    rpc.params.personId = person.id;
    rpc.params.person = person;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * New remove-relationship request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user removing friend
 * @param {Person} person Person being removed
 * @member socialsite
 */
socialsite.newRemoveRelationshipRequest = function(userId, person) {
    if (!person.id) person.id = person.getId();
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.delete" };
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupId = "@friends";
    rpc.params.personId = person.id;
    rpc.params.person = person;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * New adjust-relationship request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user adjusting request
 * @param {Person} person Other end of the relationship
 * @param {int}    level  New relationship level for relationship
 * @member socialsite
 */
socialsite.newAdjustRelationshipRequest = function(userId, person, level) {
    if (!person.id) person.id = person.getId();
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "people.put" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.groupId = "@friends";
    rpc.params.personId = person.id;
    rpc.params.level = level;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * Create request to fetch members of a group
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user
 * @param {String} groupId id of requested group
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @returns {socialsite.JsonProfileDefinition}  
 * @member socialsite
 */
socialsite.newFetchGroupMembersRequest = function(userId, groupId, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "members.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.groupHandle = groupId;
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * New apply-for-group-membership request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user,
 * @param {Group}  group Group to which user is applying
 * @param {Person} person Person applying
 * @member socialsite
 */
socialsite.newGroupApplicationRequest = function(userId, group, person) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "members.post" };
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupHandle = group.id;    
    rpc.params.person = person;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * New invite-to-group request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user
 * @param {Person} person Person being invited to group
 * @param {Group}  group Group to which person is being invited
 * @member socialsite
 */
socialsite.newGroupInvitationRequest = function(userId, person, group) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "members.post" };
    if (!person.id) person.id = person.getId();
    if (!group.id) group.id = group.getId();
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupHandle = group.id;
    rpc.params.person = person;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * New accept-group-application request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user accepting request
 * @param {Group}  group Group to which person is being accepted
 * @param {Person} person Person being accepted into group
 * @member socialsite
 */
socialsite.newAcceptGroupApplicationRequest = function(userId, group, person) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "members.post" };
    rpc.params = this.translateIdSpec(idSpec);    
    rpc.params.groupHandle = group.id;
    rpc.params.person = person;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * New ignore-group-application request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user ignoring request
 * @param {Group}  group Group that is ignoring request
 * @param {Person} person Person being ignored
 * @member socialsite
 */
socialsite.newIgnoreGroupApplicationRequest = function(userId, group, person) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "members.delete" };
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupHandle = group.id;
    rpc.params.personId = "@requests";
    rpc.params.qualifier = person.id;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * New remove-group-member request
 *
 * TODO: do we need userId here?
 *
 * @param {String} userId User ID of calling user removing friend
 * @param {Group}  group Group that is removing request
 * @param {Person} person Person being removed
 * @member socialsite
 */
socialsite.newRemoveGroupMemberRequest = function(userId, group, person) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "members.delete" };
    rpc.params = this.translateIdSpec(idSpec); 
    rpc.params.groupHandle = group.id;
    rpc.params.personId = person.id;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * Search request.
 *
 * TODO: change search results to include total number of results, change method to
 * remove offset/length and have results specify uri for next/previous results.
 * 
 * @param {String} userId User ID of calling user
 * @param {String} searchString The string for which to search
 * @param {String} type The type of search: profile, group, or gadget
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @member socialsite
 */
socialsite.newSearchRequest = function(userId, searchString, type, opt_params) {
    var idSpec = this.makeIdSpec(userId);
    var rpc = { method : "search.get" };
    rpc.params = this.translateIdSpec(idSpec);
    rpc.params.searchString = searchString;
    rpc.params.type = type;
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonResults;
            if (rawJson['list']) {
                // For the array of messages response
                jsonResults = rawJson['list'];
            } else {
                // For the single message response
                jsonResults = [rawJson];
            }
            var results = [];
            for (var i = 0; i < jsonResults.length; i++) {
                if (type == 'profile') {
                    results.push(new JsonPerson(jsonResults[i]));
                } else if (type == 'group') {
                    results.push(new socialsite.Group(jsonResults[i]));
                } else {
                    results.push(jsonResults[i]);
                }
            }
            return new opensocial.Collection(results,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    ); 
}


/**
 * Create request for available SocialSite gadgets
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @member socialsite
 */
socialsite.newFetchAvailableGadgetsRequest = function(opt_params) {
    var rpc = { method : "gadgets.get" };
    rpc.params = {};
    rpc.params.gadgetId = "@all";
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonResults;
            if (rawJson['list']) {
                // For the array of gadgets response
                jsonResults = rawJson['list'];
            } else {
                // For the single gadgets response
                jsonResults = [rawJson];
            }
            var results = [];
            for (var i = 0; i < jsonResults.length; i++) {
                results.push(new socialsite.Gadget(jsonResults[i]));
            }
            return new opensocial.Collection(results,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}


/**
 * Get all app registrations associated with caller, approved and unapproved
 * @param {Array&lt;Object&gt;} opt_params specify optional paging request paramters FIRST and MAX
 * @member socialsite
 */
socialsite.newFetchRegisteredAppsRequest = function(opt_params) {
    var rpc = { method : "appregistry.get" };
    rpc.params = {};
    if (opt_params && opt_params[socialsite.RequestParameter.FIRST]) {
        rpc.params.startIndex = opt_params[socialsite.RequestParameter.FIRST];
    }
    if (opt_params && opt_params[socialsite.RequestParameter.MAX]) {
        rpc.params.count = opt_params[socialsite.RequestParameter.MAX];
    }
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            var jsonResults;
            if (rawJson['list']) {
                // For the array of registrations response
                jsonResults = rawJson['list'];
            } else {
                // For the single registrations response
                jsonResults = [rawJson];
            }
            var results = [];
            for (var i = 0; i < jsonResults.length; i++) {
                results.push(new socialsite.AppRegistration(jsonResults[i]));
            }
            return new opensocial.Collection(results,
                rawJson['startIndex'], rawJson['totalResults']);
        }
    );
}


/**
 * Register a new app on behalf of the caller
 * @param {String} appUrl URL of app to be registered
 * @param {String} consumerKey Consumer key for OAuth
 * @param {Array&lt;Object&gt;} opt_params Array of optional arguments (currently not used)
 * @member socialsite
 */
socialsite.newRegisterAppRequest = function(appUrl,
    serviceName, serviceConsumerKey, serviceConsumerSecret, serviceKeyType, opt_params) {
    var rpc = { method : "appregistry.post" };
    var appreg = new socialsite.AppRegistration({});
    appreg.setField(socialsite.AppRegistration.Field.APP_URL, appUrl);
    appreg.setField(socialsite.AppRegistration.Field.SERVICE_NAME, serviceName); 
    appreg.setField(socialsite.AppRegistration.Field.SERVICE_CONSUMER_KEY, serviceConsumerKey);
    appreg.setField(socialsite.AppRegistration.Field.SERVICE_CONSUMER_SECRET, serviceConsumerSecret);
    appreg.setField(socialsite.AppRegistration.Field.SERVICE_KEY_TYPE, serviceKeyType);
    rpc.params = {};
    rpc.params.registration = appreg.toJsonObject();
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * Remove one of the caller's apps from the registry
 * @param {String} id id of app to be unregistered
 * @param {Array&lt;Object&gt;} opt_params Array of optional arguments (currently not used)
 * @member socialsite
 */
socialsite.newUnregisterAppRequest = function(id, opt_params) {
    var rpc = { method : "appregistry.delete" };
    rpc.params = {"id" : id};
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


socialsite.newInstallUserGadgetRequest = function(collection, gadgetUrl, opt_params) {
    var rpc = { method : "gadgets.post" };
    rpc.params = {};
    rpc.collection = collection;
    rpc.params.gadgetUrl = gadgetUrl;
    rpc.params.subjectType = "@user";
    rpc.params.id = "@owner";
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


socialsite.newInstallGroupGadgetRequest = function(collection, gadgetUrl, opt_params) {
    var rpc = { method : "gadgets.post" };
    rpc.params = {};
    rpc.collection = collection;
    rpc.params.gadgetUrl = gadgetUrl;
    rpc.params.subjectType = "@group";
    rpc.params.id = "@current";
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}

/**
 * Remove gadget method for use within container.
 */
socialsite.newRemoveGadgetRequest = function(gadgetId) {
    var rpc = { method : "gadgets.delete" };
    rpc.params = {};
    rpc.params.gadgetId = gadgetId;
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * Gets the available SocialSite configuration properties.
 */
socialsite.newFetchPropertiesRequest = function() {
    var rpc = { method : "properties.get" };
    return new JsonRpcRequestItem(rpc,
        function (rawJson) {
            return rawJson;
        }
    );
}


/**
 * @class Gadget uninstaller to be called outside of container,
 * calls SocialSite REST API with security token.
 * 
 * @name socialsite.GadgetUninstaller
 * @description Creates a new GadgetInstaller
 */
socialsite.GadgetUninstaller = function(gadgetServerUrl, secureToken) {
    this.restURL = gadgetServerUrl;
    // TODO: remove this hardcoded context name
    this.baseURL = gadgetServerUrl.replace('socialsite/social','socialsite/gadgets/social/data');
    this.secureToken = (secureToken != null ? secureToken : gadgets.util.getUrlParameters().st);
}


/**
 * Remove gadget method for use outside of container.
 */
socialsite.GadgetUninstaller.prototype.removeInstalledGadgetById = function(id) {
    jQuery.ajax({type: "DELETE",
        url: this.restURL + "/gadgets?gadgetId=" + id + "&st=" + encodeURIComponent(this.secureToken),
        timeout: 5000, async: false,
        error: function(xhr, textStatus, errorThrown) {
            window.alert('ERROR removing installedgadget with id ' + id, false);
        }
    });
}


/**
 * Convenience method to create an ID spec from an ID
 *
 * @param {String} id represents user ID to be turned into an ID spec
 * @member socialsite
 * @private
 */
socialsite.makeIdSpec = function(id) {
    return new opensocial.IdSpec({"userId" : id});
};


/**
 * Translate an ID spec in to a string suitable for use in an URL
 *
 * @param {String} newIdSpec ID spec to be translated.
 * @member socialsite
 * @private
 */
socialsite.translateIdSpec = function(newIdSpec) {
    var userIds = newIdSpec.getField('userId');
    var groupId = newIdSpec.getField('groupId');

    // Upconvert to array for convenience
    if (!opensocial.Container.isArray(userIds)) {
        userIds = [userIds];
    }

    for (var i = 0; i < userIds.length; i++) {
        if (userIds[i] == 'OWNER') {
            userIds[i] = '@owner';
        } else if (userIds[i] == 'VIEWER') {
            userIds[i] = '@viewer';
        }
    }

    if (groupId == 'FRIENDS') {
        groupId = "@friends";
    } else if (groupId == 'SELF' || !groupId) {
        groupId = "@self";
    }

    return {
        userId : userIds,
        groupId : groupId
    };
};


/**
 * Show OpenSocial Gadget in lightbox.
 *
 * @param {String} title  Title of Gadget
 * @param {String} url    URL of Gadget specification file
 * @param {int}    width  Width of Gadget display area
 * @param {int}    height Height of Gadget display area
 * @member socialsite
 * @public
 */
socialsite.showLightbox = function(title, url, width, height) {
    var params = [title, url, width, height];
    gadgets.rpc.call(null, "socialsite_showLightbox", null, params);
}


/**
 * Hide/Close the currently displayed lightbox.
 *
 * @param {boolean} refresh True if Gadgets are to be refreshed
 * @member socialsite
 * @public
 */
socialsite.hideLightbox = function(refresh) {
    var reload = ((refresh != null) ? refresh : false);
    var params = [reload];
    gadgets.rpc.call(null, "socialsite_closeLightbox", null, params);
}


/**
 * Requests that the container page be reloaded.
 *
 * @member socialsite
 * @public
 */
socialsite.reloadPage = function() {
    var params = {};
    gadgets.rpc.call(null, "socialsite_reloadPage", null, params);
}


/**
 * Set skin properties for a gadget.
 *
 * @member socialsite
 * @public
 */
socialsite.setTheming = function() {
     var html = '';
     html += '<style type="text/css">';
     html += '#socialsiteWidget {';

     // bg color
     var bgColor = gadgets.skins.getProperty(gadgets.skins.Property.BG_COLOR);
     if (bgColor) {
         html += 'background-color:'+bgColor+';';
     }

     // bg image
     var bgImage = gadgets.skins.getProperty(gadgets.skins.Property.BG_IMAGE);
     if (bgImage) {
         html += 'background-image: url('+bgImage+');';
     }

     // font color
     var fontColor = gadgets.skins.getProperty(gadgets.skins.Property.FONT_COLOR);
     if (fontColor) {
         html += 'color:'+fontColor+';';
     }

     html += '}';

     // anchor color
     var anchorColor = gadgets.skins.getProperty(gadgets.skins.Property.ANCHOR_COLOR);
     if (anchorColor) {
         html += '#socialsiteWidget a, #socialsiteWidget a:hover, #socialsiteWidget a:visited { color:'+anchorColor+';}';
     }

     if (fontColor) {
         html += '#socialsiteWidget * { color:'+fontColor+';}';
     }

     html += '</style>';

     // NOTE: IE7 has problems using appendChild, so instead using innerHTML to append
     // style element
     var ssWidget = document.getElementById('socialsiteWidget');
     if (ssWidget) {
         var ssWidgetContent = ssWidget.innerHTML;
         ssWidget.innerHTML = ssWidgetContent + html;
     }

}

/**
 * Show standard Socialsite message in #messageBox with icon and color
 * appropriate for message type.
 * @param {String} message The message to display
 * @param {String} type Message type of 'information', 'succcess', 'warning' or 'error'
 */
socialsite.statusMessage = function(message, type) {
    $("#status-message").html(message);
    if (type == "information") {
        $("#status-icon").attr("src","../local_gadgets/files/information.png");
        $("#messageBox").css("background","#eee");
    } else if (type == "success") {
        $("#status-icon").attr("src","../local_gadgets/files/accept.png");
        $("#messageBox").css("background","#efe");
    } else if (type == "warning") {
        $("#status-icon").attr("src","../local_gadgets/files/error.png");
        $("#messageBox").css("background","#ffc");
    } else if (type == "error") {
        $("#status-icon").attr("src","../local_gadgets/files/error.png");
        $("#messageBox").css("background","#fee");
    }
}
