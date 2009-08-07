/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.sun.socialsite.web.rest.core;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.business.NotificationManager;
import com.sun.socialsite.pojos.Relationship;
import com.sun.socialsite.pojos.RelationshipRequest;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.service.PersonHandler;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Extend Shindig handler to add SocialSite features including requesting
 * relationships, accepting releationships, ignoring relationship requests and
 * removing relationships.
 *
 * <pre>
 * /people/{userId}/@friends
 *     GET - get friends (Person objects)
 *     POST - request or accept friendship (Person object)
 * 
 * /people/{userId}/@requests
 *     GET - get friendship requests (Person objects)
 * 
 * /people/{userId}/@requests/{personId}
 *     PUT - update/clarify friendship request
 *     DELETE - delete/ignore friendship request
 * 
 * /people/{userId}/@friends/{personId}
 *     DELETE - remove relationship 
 * </pre>
 */
@Service(name = "people", path = "/{userId}+/{groupId}/{personId}+")
public class PersonHandlerImpl extends PersonHandler {
    private static Log log = LogFactory.getLog(PersonHandlerImpl.class);
    private BeanJsonConverter jsonConverter;
    
    @Inject
    public PersonHandlerImpl(
            PersonService personService,
            ContainerConfig config,
            @Named("shindig.bean.converter.json") BeanConverter jsonConverter) {
        super(personService, config);
        this.jsonConverter = (BeanJsonConverter)jsonConverter;
    }

    @Operation(httpMethods="POST", bodyParam="person")
    public Future<?> post(SocialRequestItem request) throws SocialSpiException {

        RestrictedDataRequestHandler.authorizeRequest(request);

        //request.applyUrlTemplate(SOCIALSITE_PEOPLE_PATH);

        if ("@friends".equals(request.getParameter("groupId"))) {
            // A POST to a user's friend collection means that the user is
            // either requesting to add a new friend, pending approval, or
            // accepting a previously requested friendship.

            // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization

            try {
                // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc.
                JSONObject ret = new JSONObject();
                ResponseError error = null;
                String errorString = null;

                Person person = request.getTypedParameter("person", Person.class);
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();

                String howknow = request.getParameter("howknow");
                int level = 1;
                String levelString = request.getParameter("level");
                if (levelString != null) {
                    try {
                        level = Integer.parseInt(levelString);
                    } catch (NumberFormatException ignored) {}
                }

                // The person adding the friend
                Profile requestor = pmgr.getProfileByUserId(
                    request.getUsers().iterator().next()
                    .getUserId(request.getToken()));

                // The friend being added
                Profile requestee = pmgr.getProfileByUserId(person.getId());

                // Handle reqeust
                rmgr.requestRelationship(requestor, requestee, level, howknow);
                Factory.getSocialSite().flush();

            } catch (SocialSiteException ex) {
                log.debug("ERROR posting to friend collection", ex);
                throw new SocialSpiException(
                    ResponseError.BAD_REQUEST, ex.getMessage(), ex);
                
            } catch (Exception ex) {
                log.debug("ERROR posting to friend collection", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            }
        }

        return ImmediateFuture.newInstance(null);
    }

    @Operation(httpMethods="PUT")
    public Future<?> put(SocialRequestItem request) throws SocialSpiException {
        //request.applyUrlTemplate(SOCIALSITE_PEOPLE_PATH);

        RestrictedDataRequestHandler.authorizeRequest(request);

        if ("@requests".equals(request.getParameter("groupId"))) {
            // A PUT to an item in a user's @requests collection is an update,
            // specifically clarification of a relationhip request.

            // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization

            try {
                // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc.
                JSONObject ret = new JSONObject();
                ResponseError error = null;
                String errorString = null;

                Person person = request.getTypedParameter("person", Person.class);
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();

                String howknow = request.getParameter("howknow");
                int level = 1;
                String levelString = request.getParameter("level");
                if (levelString != null) {
                    try {
                        level = Integer.parseInt(levelString);
                    } catch (NumberFormatException ignored) {}
                }

                // The person making the clarification
                Profile requestor =
                    pmgr.getProfileByUserId(request.getToken().getViewerId());

                // The target of the relationship request
                Profile requestee = pmgr.getProfileByUserId(person.getId());

                RelationshipRequest rreq =
                    rmgr.getRelationshipRequest(/*from*/ requestee, /*to*/ requestor);

                if (requestor.equals(requestee)) {
                    error = ResponseError.BAD_REQUEST;
                    errorString = "ERROR - Requestor same as requestee";

                } else if (rreq == null) {
                    error = ResponseError.BAD_REQUEST;
                    errorString = "ERROR - No relationship request found";

                } else {
                    // Requestor clarifying request
                    rmgr.clarifyRelationshipRequest(rreq, level, howknow);
                    Factory.getSocialSite().flush();
                    ret = ret.put("code", 200).put("message",
                            "Request clarification requested");
                } 

                if (error != null) {
                    throw new SocialSpiException(error, errorString);
                }
                return ImmediateFuture.newInstance(ret);

            } catch (JSONException jex) {
                log.debug("ERROR clarifying relationship request", jex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, jex.getMessage(), jex);
            } catch (Exception ex) {
                log.debug("ERROR clarifying relationship request", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            }

        } else if ("@friends".equals(request.getParameter("groupId"))) {

            try {
                // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc.
                JSONObject ret = new JSONObject();
                ResponseError error = null;
                String errorString = null;

                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();

                int level = 1;
                String levelString = request.getParameter("level");
                if (levelString != null) {
                    try {
                        level = Integer.parseInt(levelString);
                    } catch (NumberFormatException ignored) {}
                }

                String viewerId = request.getToken().getViewerId();
                Profile requestor = pmgr.getProfileByUserId(viewerId);

                Profile target =
                    pmgr.getProfileByUserId(request.getParameter("personId"));

                Relationship fship = rmgr.getRelationship(requestor, target);
                if (fship != null) {
                    rmgr.adjustRelationship(requestor, target, level);
                    Factory.getSocialSite().flush();
                    ret = ret.put("code", 200).put("message", "SUCCESS: relationship removed");
                    
                } else {
                    error = ResponseError.BAD_REQUEST;
                    errorString = "ERROR: relationship not found";
                }
                if (error != null) {
                    throw new SocialSpiException(error, errorString);
                }               
                return ImmediateFuture.newInstance(ret);

            } catch (SocialSiteException ex) {
                log.debug("ERROR updating relationship", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            } catch (JSONException jex) {
                log.debug("ERROR updating relationship", jex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, jex.getMessage(), jex);
            }
        }
        return ImmediateFuture.newInstance(null);
    }

    
    @Override
    @Operation(httpMethods = "GET")
    public Future<?> get(SocialRequestItem request) throws SocialSpiException {

        //request.applyUrlTemplate(SOCIALSITE_PEOPLE_PATH);

        if ("@requests".equals(request.getParameter("groupId"))) {
            RestrictedDataRequestHandler.authorizeRequest(request);
            
            // GET all incoming friend request for the user

            try {                
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();
                
                String viewerId = request.getToken().getViewerId();
                Profile requestor = pmgr.getProfileByUserId(viewerId);

                List<Person> people = new ArrayList<Person>();
                int totalResults = 0;
        
                List<RelationshipRequest> requests = rmgr.getRelationshipRequestsByToProfile(
                    requestor, request.getStartIndex(), request.getCount());

                for (RelationshipRequest freq : requests) {
                    JSONObject jsonPerson = freq.getProfileFrom().toJSON(Profile.Format.OPENSOCIAL_MINIMAL);
                    Person person = jsonConverter.convertToObject(jsonPerson.toString(), Person.class);
                    people.add(person);                    
                }
                
                List<RelationshipRequest> allRequests = 
                    rmgr.getRelationshipRequestsByToProfile(requestor, 0, -1);
                totalResults = allRequests.size();
                
                RestfulCollection<Person> collection =
                    new RestfulCollection<Person>(people, request.getStartIndex(), totalResults);
                return ImmediateFuture.newInstance(collection);

            } catch (Exception ex) {
                log.debug("ERROR getting relationship requests", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            }
            
        } else if ("@union".equals(request.getParameter("groupId"))) {
            RestrictedDataRequestHandler.authorizeRequest(request);

            // URL is /{userid}/@union/{groupId1}/{groupId2}
            try {                
                String gid1 = request.getParameter("groupId1");
                String gid2 = request.getParameter("groupId2");
                
                GroupManager gmgr = Factory.getSocialSite().getGroupManager();
                Group thisGroup = gmgr.getGroupByHandle(gid1);
                Group thatGroup = gmgr.getGroupByHandle(gid2);      

                List<Profile> profiles = 
                        gmgr.getCommonMembersInGroups(thisGroup, thatGroup);

                SocialSiteToken token = (SocialSiteToken) request.getToken();
                List<Person> people = new ArrayList<Person>();
                RestfulCollection<?> collection = null;

                for (Profile profile : profiles) {
                     JSONObject json = profile.toJSON(
                        Profile.Format.OPENSOCIAL_MINIMAL, token.getViewerId());
                     Person person = getPerson(json, token);
                     people.add(person);
                }
                
                collection = new RestfulCollection<Person>(
                    people, request.getStartIndex(), profiles.size());
                
                return ImmediateFuture.newInstance(collection);
                
            } catch (Exception ex) {
                log.debug("ERROR getting union of groups", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            }
            
        } else if ("@all".equals(request.getParameter("groupId"))) {
            RestrictedDataRequestHandler.authorizeRequest(request);

            // URL is /{userId}+/@all - return all people
            
            // GET all incoming friend request for the user
            
            // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization

            try {                
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();
                
                String viewerId = request.getToken().getViewerId();

                List<Person> people = new ArrayList<Person>();
                int totalResults = 0;
        
                List<Profile> profiles = pmgr.getProfiles(
                    request.getStartIndex(), request.getCount());

                for (Profile profile : profiles) {
                    JSONObject jsonPerson = profile.toJSON(
                      Profile.Format.OPENSOCIAL, viewerId, request.getFields());
                    Person person = jsonConverter.convertToObject(
                      jsonPerson.toString(), Person.class);
                    people.add(person);                    
                }
                
                List<Profile> allRequests = pmgr.getProfiles(0, -1);
                totalResults = allRequests.size();

                RestfulCollection<Person> collection =
                    new RestfulCollection<Person>(people, request.getStartIndex(), totalResults);
                return ImmediateFuture.newInstance(collection);

            } catch (Exception ex) {
                log.debug("ERROR getting all people", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            }
        }
        
        return super.get(request);
    }
    
    /**
     * Obtains a Person object equivalent to the specified JSON (and
     * customized for the specified token context).
     */
    private Person getPerson(JSONObject json, SocialSiteToken token) 
            throws SocialSiteException {
        Person person = 
                jsonConverter.convertToObject(json.toString(), Person.class);
        person.setIsViewer(person.getId().equals(token.getViewerId()));
        person.setIsOwner(person.getId().equals(token.getOwnerId()));
        return person;
    }

    @Operation(httpMethods = "DELETE")
    public Future<?> delete(SocialRequestItem request) throws SocialSpiException {
        RestrictedDataRequestHandler.authorizeRequest(request);

        //request.applyUrlTemplate(SOCIALSITE_PEOPLE_PATH);

        if ("@friends".equals(request.getParameter("groupId"))) {
            
            // DELETE an existing friendship relationship
            
            try {                
                // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc. 
                JSONObject ret = new JSONObject();                
                ResponseError error = null;
                String errorString = null;
                
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();
                
                String viewerId = request.getToken().getViewerId();
                Profile requestor = pmgr.getProfileByUserId(viewerId);
                
                Profile target = 
                    pmgr.getProfileByUserId(request.getParameter("personId"));
                
                Relationship fship = rmgr.getRelationship(requestor, target);
                if (fship != null) {
                    rmgr.removeRelationship(requestor, target);
                    Factory.getSocialSite().flush();
                    ret = ret.put("code", 200).put("message", "SUCCESS: relationship removed");
                } else {
                    error = ResponseError.BAD_REQUEST;
                    errorString = "ERROR: relationship not found";
                }
                if (error != null) {
                    throw new SocialSpiException(error, errorString);
                }
                return ImmediateFuture.newInstance(ret);

            } catch (SocialSiteException ex) {
                log.debug("ERROR deleting from friend collection", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            } catch (JSONException jex) {
                log.debug("ERROR deleting from friend collection", jex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, jex.getMessage(), jex);
            }

        } else if ("@requests".equals(request.getParameter("groupId"))) {
            
            // DELETE an existing friendship request

            try {                
                // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc. 
                JSONObject ret = new JSONObject();                                
                ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
                RelationshipManager rmgr = Factory.getSocialSite().getRelationshipManager();
                NotificationManager nmgr = Factory.getSocialSite().getNotificationManager();
                ResponseError error = null;
                String errorString = null;
                
                String viewerId = request.getToken().getViewerId();
                Profile requestor = pmgr.getProfileByUserId(viewerId);
                
                Profile target = 
                    pmgr.getProfileByUserId(request.getParameter("personId"));
                
                RelationshipRequest freq = rmgr.getRelationshipRequest(target, requestor);
                if (freq != null) {
                    rmgr.removeRelationshipRequest(freq);
                    nmgr.recordNotification(
                        requestor, target, null,
                        MessageContent.NOTIFICATION, "Rejected friendship request",
                        requestor.getName() + " rejected your request for friendship", true);
                    Factory.getSocialSite().flush();
                    ret = ret.put("code", 200).put("message", "SUCCESS: request removed");
                } else {
                    error = ResponseError.BAD_REQUEST;
                    errorString = "ERROR: request not found";
                }
                if (error != null) {
                    throw new SocialSpiException(error, errorString);
                }
                return ImmediateFuture.newInstance(ret);

            } catch (SocialSiteException ex) {
                log.debug("ERROR deleting from friend request collection", ex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, ex.getMessage(), ex);
            } catch (JSONException jex) {
                log.debug("ERROR deleting from friend request collection", jex);
                throw new SocialSpiException(
                    ResponseError.INTERNAL_ERROR, jex.getMessage(), jex);
            }
        }
        
        return ImmediateFuture.newInstance(null);
    }

}
