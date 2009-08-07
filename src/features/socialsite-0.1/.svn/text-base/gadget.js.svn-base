
/**
 * @fileoverview Gadget object
 */

/**
 * @class
 * Represents an available or installed Gadget.
 */
socialsite.Gadget = function(data) {
    this.fields_ = data;
    if (this.fields_[socialsite.Gadget.Field.OWNING_PERSON]) {
        var person = new JsonPerson(this.fields_[socialsite.Gadget.Field.OWNING_PERSON]);
        this.fields_[socialsite.Gadget.Field.OWNING_PERSON] = person;
    }
    if (this.fields_[socialsite.Gadget.Field.OWNING_GROUP]) {
        var group = new Group(this.fields_[socialsite.Gadget.Field.OWNING_GROUP]);
        this.fields_[socialsite.Gadget.Field.OWNING_GROUP] = group;
    }
}

/**
 * @static
 * @class
 * All of the fields that an gadget has. These are the supported keys for the
 * <a href="socialsite.Gadget.html#getField">Gadget.getField()</a> method.
 *
 * @name socialsite.Gadget.Field
 */
socialsite.Gadget.Field = {

    /**
     * @member socialsite.Gadget.Field
     */
    APP_ID: "appId",

    /**
     * @member socialsite.Gadget.Field
     */
    MODULE_ID: "moduleId",

    /**
     * @member socialsite.Gadget.Field
     */
    URL: "url",

    /**
     * @member socialsite.Gadget.Field
     */
    CREATED: "created",

    /**
     * @member socialsite.Gadget.Field
     */
    UPDATED: "updated",

    /**
     * @member socialsite.Gadget.Field
     */
    INSTALLED: "installed",

    /**
     * @member socialsite.Gadget.Field
     */
    TITLE: "title",

    /**
     * @member socialsite.Gadget.Field
     */
    TITLE_URL: "titleUrl",

    /**
     * @member socialsite.Gadget.Field
     */
    DIRECTORY_TITLE: "directoryTitle",

    /**
     * @member socialsite.Gadget.Field
     */
    DESCRIPTION:"description",

    /**
     * @member socialsite.Gadget.Field
     */
    THUMBNAIL_URL: "thumbnailUrl",

    /**
     * @member socialsite.Gadget.Field
     */
    AUTHOR: "author",

    /**
     * @member socialsite.Gadget.Field
     */
    AUTHOR_EMAIL: "authorEmail",

    /**
     * @member socialsite.Gadget.Field
     */
    AUTHOR_URL: "authorUrl",

    /**
     * @member socialsite.Gadget.Field
     */
    COLLECTION: "collection",

    /**
     * @member socialsite.Gadget.Field
     */
    POSITION: "position",

    /**
     * @member socialsite.Gadget.Field
     */
    SCROLLING: "scrolling",

    /**
     * @member socialsite.Gadget.Field
     */
    SINGLETON: "singleton",

    /**
     * @member socialsite.Gadget.Field
     */
    HEIGHT: "height",

    /**
     * @member socialsite.Gadget.Field
     */
    WIDTH: "width",

    /**
     * @member socialsite.Gadget.Field
     */
    OWNING_PERSON: "person",

    /**
     * @member socialsite.Gadget.Field
     */
    OWNING_GROUP: "group"
};

/**
 * Gets the Gadget data that's associated with the specified key.
 *
 * @param {String} key The key to get data for;
 *   see the <a href="socialsite.Gadget.Field.html">Field</a> class
 * for possible values
 * @param {Map.&lt;opensocial.DataRequest.DataRequestFields, Object&gt;}
 *  opt_params Additional
 *    <a href="opensocial.DataRequest.DataRequestFields.html">params</a>
 *    to pass to the request.
 * @return {String} The data
 * @member socialsite.Gadget
 */
socialsite.Gadget.prototype.getField = function(key, opt_params) {
  return opensocial.Container.getField(this.fields_, key, opt_params);
};


/**
 * Sets data for this Group associated with the given key.
 *
 * @param {String} key The key to set data for
 * @param {String} data The data to set
 */
socialsite.Gadget.prototype.setField = function(key, data) {
  return this.fields_[key] = data;
};


socialsite.Gadget.prototype.toJsonObject = function() {
    var jsonObject = socialsite.Gadget.copyFields(this.fields_);
    return jsonObject;
}
    
socialsite.Gadget.copyFields = function(oldObject) {
    var newObject = {};
    for (var field in oldObject) {
        newObject[field] = oldObject[field];
    }
    return newObject;
}

socialsite.Gadget.prototype.getId = function() {
    return this.fields_["moduleId"] ?
        this.fields_["moduleId"] : this.fields_["appId"];
}
