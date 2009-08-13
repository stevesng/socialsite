/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Base interface for json-based message objects.
 *
 * @private
 * @constructor
 */
var JsonMessage = function(opt_params) {
  opt_params = opt_params || {};
  
  JsonMessage.constructObject(opt_params, "sender", JsonPerson);
  
  // TODO: fix this when the Message construtor is no longer broken
  //opensocial.Message.call(this, opt_params);
  this.fields_ = opt_params || {};
};
JsonMessage.inherits(opensocial.Message);

// Converts the fieldName into an instance of the specified object
JsonMessage.constructObject = function(map, fieldName, className) {
  var fieldValue = map[fieldName];
  if (fieldValue) {
    map[fieldName] = new className(fieldValue);
  }
}

JsonMessage.prototype.toJsonObject = function() {
  return JsonMessage.copyFields(this.fields_);
}

// TODO: Pull this method into a common class, it is from jsonperson.js
//JsonMessage.constructArrayObject = function(map, fieldName, className) {
//  var fieldValue = map[fieldName];
//  if (fieldValue) {
//    for (var i = 0; i < fieldValue.length; i++) {
//      fieldValue[i] = new className(fieldValue[i]);
//    }
//  }
//}

// TODO: Pull into common class as well
JsonMessage.copyFields = function(oldObject) {
  var newObject = {};
  for (var field in oldObject) {
    newObject[field] = oldObject[field];
  }
  return newObject;
}

JsonMessage.prototype.getId = function() {
    return this.fields_["id"];
}


