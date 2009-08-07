
/**
 * @fileoverview AppRegistration object
 */

/**
 * @class
 * Represents an available or installed AppRegistration.
 */
socialsite.AppRegistration = function(data) {
    this.fields_ = data;
}

/**
 * @static
 * @class
 * All of the fields that an AppRegistration has. These are the supported keys for the
 * <a href="socialsite.AppRegistration.html#getField">AppRegistration.getField()</a> method.
 *
 * @name socialsite.AppRegistration.Field
 */
socialsite.AppRegistration.Field = {

    /**
     * @member socialsite.AppRegistration.Field
     */
    APP_URL: "appUrl",

    /**
     * @member socialsite.AppRegistration.Field
     */
    CREATED: "created",

    /**
     * @member socialsite.AppRegistration.Field
     */
    UPDATED: "updated",

    /**
     * @member socialsite.AppRegistration.Field
     */
    COMMENT: "comment",

    /**
     * @member socialsite.AppRegistration.Field
     */
    CONSUMER_SECRET: "consumerSecret",

    /**
     * @member socialsite.AppRegistration.Field
     */
    CONSUMER_KEY: "consumerKey",

    /**
     * @member socialsite.AppRegistration.Field
     */
    SERVICE_NAME: "serviceName",

    /**
     * @member socialsite.AppRegistration.Field
     */
    SERVICE_CONSUMER_KEY: "serviceConsumerKey",

    /**
     * @member socialsite.AppRegistration.Field
     */
    SERVICE_CONSUMER_SECRET: "serviceConsumerSecret",

    /**
     * @member socialsite.AppRegistration.Field
     */
    SERVICE_KEY_TYPE: "serviceKeyType"
};

/**
 * Gets the AppRegistration data that's associated with the specified key.
 *
 * @param {String} key The key to get data for;
 *   see the <a href="socialsite.AppRegistration.Field.html">Field</a> class
 * for possible values
 * @param {Map.&lt;opensocial.DataRequest.DataRequestFields, Object&gt;}
 *  opt_params Additional
 *    <a href="opensocial.DataRequest.DataRequestFields.html">params</a>
 *    to pass to the request.
 * @return {String} The data
 * @member socialsite.AppRegistration
 */
socialsite.AppRegistration.prototype.getField = function(key, opt_params) {
  return opensocial.Container.getField(this.fields_, key, opt_params);
};


/**
 * Sets data for this Group associated with the given key.
 *
 * @param {String} key The key to set data for
 * @param {String} data The data to set
 */
socialsite.AppRegistration.prototype.setField = function(key, data) {
  return this.fields_[key] = data;
};


socialsite.AppRegistration.prototype.toJsonObject = function() {
    var jsonObject = socialsite.AppRegistration.copyFields(this.fields_);
    return jsonObject;
}
    
socialsite.AppRegistration.copyFields = function(oldObject) {
    var newObject = {};
    for (var field in oldObject) {
        newObject[field] = oldObject[field];
    }
    return newObject;
}

socialsite.AppRegistration.prototype.getId = function() {
    return this.fields_["id"] ?
        this.fields_["id"] : this.fields_["id"];
}

