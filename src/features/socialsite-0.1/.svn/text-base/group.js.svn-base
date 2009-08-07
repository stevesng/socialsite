
/**
 * @fileoverview Group object
 */

/**
 * @class
 * Represents a public group of users, managed by ADMINs
 * @name socialsite.Group
 */
socialsite.Group = function(data) {
    this.fields_ = data;
}

/**
 * @static
 * @class
 * All of the fields that an group has. These are the supported keys for the
 * <a href="socialsite.Group.html#getField">Group.getField()</a> method.
 *
 * @name socialsite.Group.Field
 */
socialsite.Group.Field = {
    
    /**
     * @member socialsite.Group.Field
     */
    HANDLE: "handle",

    /**
     * @member socialsite.Group.Field
     */
    NAME: "name",
    
    /**
     * @member socialsite.Group.Field
     */
    DESCRIPTION: "description",

    /**
     * @member socialsite.Group.Field
     */
    THUMBNAIL_URL: "thumbnailUrl",
    
    /**
     * @member socialsite.Group.Field
     */
    VIEW_URL: "viewUrl",

    /**
     * @member socialsite.Group.Field
     */
    VIEWER_RELATIONSHIP: "viewerRelationship"
};

/**
 * Gets the section privacy data that's associated with the specified key.
 *
 * @param {String} key The key to get data for;
 *   see the <a href="socialsite.Group.Field.html">Field</a> class
 * for possible values
 * @param {Map.&lt;opensocial.DataRequest.DataRequestFields, Object&gt;}
 *  opt_params Additional
 *    <a href="opensocial.DataRequest.DataRequestFields.html">params</a>
 *    to pass to the request.
 * @return {String} The data
 * @member socialsite.Group
 */
socialsite.Group.prototype.getField = function(key, opt_params) {
  return opensocial.Container.getField(this.fields_, key, opt_params);
};


/**
 * Sets data for this Group associated with the given key.
 *
 * @param {String} key The key to set data for
 * @param {String} data The data to set
 */
socialsite.Group.prototype.setField = function(key, data) {
  return this.fields_[key] = data;
};


socialsite.Group.prototype.toJsonObject = function() {
    var jsonObject = socialsite.Group.copyFields(this.fields_);
    return jsonObject;
}
    
socialsite.Group.copyFields = function(oldObject) {
    var newObject = {};
    for (var field in oldObject) {
        newObject[field] = oldObject[field];
    }
    return newObject;
}

socialsite.Group.prototype.getId = function() {
    return this.fields_["id"];
}

