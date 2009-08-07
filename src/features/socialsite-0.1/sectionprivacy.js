
/**
 * @fileoverview Section Privacy object
 */

/**
 * @class
 * Represent privacy setting for one section of a personal profile.
 * @name socialsite.SectionPrivacy
 */
socialsite.SectionPrivacy = function(data) {
    this.fields_ = data;
}

/**
 * @static
 * @class
 * All of the fields that an section privacy has. These are the supported keys for the
 * <a href="socialsite.SectionPrivacy.html#getField">SectionPrivacy.getField()</a> method.
 *
 * @name socialsite.SectionPrivacy.Field
 */
socialsite.SectionPrivacy.Field = {
    
    /**
     * Section name unique identifies section
     * @member socialsite.SectionPrivacy.Field
     */
    SECTION_NAME: "sectionName",

    /**
     * Localized display name for section
     * @member socialsite.SectionPrivacy.Field
     */
    DISPLAY_NAME: "localizedName",

    /**
     * Visibility of type string (PUBLIC, PRIVATE, SOMEGROUPS, ALLGROUPS, FRIENDS)
     * @member socialsite.SectionPrivacy.Field
     */
    VISIBILITY: "visibility",
    
    /**
     * Array of strings, the group handles for the some groups visibility level
     * @member socialsite.SectionPrivacy.Field
     */
    GROUPS: "groups",

    /**
     * Friend level, an integer
     * @member socialsite.SectionPrivacy.Field
     */
    FRIEND_LEVEL: "friendLevel"
};

/**
 * Gets the section privacy data that's associated with the specified key.
 *
 * @param {String} key The key to get data for;
 *   see the <a href="socialsite.SectionPrivacy.Field.html">Field</a> class
 * for possible values
 * @param {Map.&lt;opensocial.DataRequest.DataRequestFields, Object&gt;}
 *  opt_params Additional
 *    <a href="opensocial.DataRequest.DataRequestFields.html">params</a>
 *    to pass to the request.
 * @return {String} The data
 * @member socialsite.SectionPrivacy
 */
socialsite.SectionPrivacy.prototype.getField = function(key, opt_params) {
  return opensocial.Container.getField(this.fields_, key, opt_params);
};


/**
 * Sets data for this section privacy associated with the given key.
 *
 * @param {String} key The key to set data for
 * @param {String} data The data to set
 */
socialsite.SectionPrivacy.prototype.setField = function(key, data) {
  return this.fields_[key] = data;
};


socialsite.SectionPrivacy.prototype.toJsonObject = function() {
    var jsonObject = socialsite.SectionPrivacy.copyFields(this.fields_);
    return jsonObject;
}
    
socialsite.SectionPrivacy.copyFields = function(oldObject) {
    var newObject = {};
    for (var field in oldObject) {
        newObject[field] = oldObject[field];
    }
    return newObject;
}
