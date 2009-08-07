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

package com.sun.socialsite.web.rest.core;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.GroupRequest;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.RequestItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.json.JSONObject;

import static com.sun.socialsite.pojos.GroupRelationship.Relationship.ADMIN;
import static com.sun.socialsite.pojos.GroupRelationship.Relationship.MEMBER;
import static com.sun.socialsite.pojos.GroupRelationship.Relationship.PENDING;

/**
 * Extend Shindig handler to add SocialSite features including requesting
 * relationships, accepting releationships, ignoring relationship requests and
 * removing relationships.
 *
 * <pre>
 * /members/{groupId}
 *     GET - get list of members in group (Person objects)
 *     POST - request or accept membership in group (Person object)
 *
 * /members/{groupId}/{userId}
 *     DELETE - delete a member in group
 *
 * /members/{groupId}/@admins
 *     GET - get list of admins of group (Person objects)
 *     POST - make existing group member into a a group admin (Person objects)
 *
 * /members/{groupId}/@requests
 *     GET - get group membership requests
 *
 * /members/{groupId}/@requests/{personId}
 *     DELETE - remove (i.e. ignore/reject) a membership request
 * </pre>
 */
@Service(name = "members", path="/{groupHandle}/{personId}/{qualifier}")
public class GroupMemberHandler extends RestrictedDataRequestHandler {

    private static Log log = LogFactory.getLog(GroupMemberHandler.class);

    private BeanJsonConverter jsonConverter;
    private static final String SOCIALSITE_GROUPMEMBER_PATH =
        "/members/{groupHandle}/{personId}/{qualifier}";


    @Inject
    public GroupMemberHandler(
            @Named("shindig.bean.converter.json") BeanConverter jsonConverter) {
        this.jsonConverter = (BeanJsonConverter)jsonConverter;
    }


    @Operation(httpMethods="POST", bodyParam="person")
    public Future<?> post(SocialRequestItem request) throws SocialSpiException {
        authorizeRequest(request);

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        GroupManager gmgr = Factory.getSocialSite().getGroupManager();

        // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc.
        JSONObject ret = null;

        Group group = null;       // Group identified by request
        Profile requestor = null; // Person making post
        Profile requestee = null; // Person being posted
        GroupRequest requesteeRequest = null;
        boolean requestorIsAdmin = false;
        GroupRelationship.Relationship requesteeRel = null;

        try {
            //request.applyUrlTemplate(SOCIALSITE_GROUPMEMBER_PATH);
            logParameters(request);
            Person person = request.getTypedParameter("person", Person.class);
            requestor    = pmgr.getProfileByUserId(request.getToken().getViewerId());
            requestee    = pmgr.getProfileByUserId(person.getId());
            group        = gmgr.getGroupByHandle(request.getParameter("groupHandle"));
            if (gmgr.getMembership(group, requestor) != null) {
                requestorIsAdmin = gmgr.isAdmin(group, requestor);
            }
            if (gmgr.getMembership(group, requestee) != null) {
                requesteeRel = gmgr.getMembership(group, requestee).getRelcode();
            }
            requesteeRequest = gmgr.getMembershipRequest(group, requestee);
        } catch (Exception ex) {
            return ImmediateFuture.errorInstance(ex);
        }

        if ("@admins".equals(request.getParameter("personId"))
           && requestorIsAdmin
           && MEMBER.equals(requesteeRel)) {
            // If personId == @admins, requestor is group ADMIN and requestee is group member
               // Then make requestee into an admin
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                ret = new JSONObject();
                GroupRelationship grel = gmgr.getMembership(group, requestee);
                grel.setRelcode(ADMIN);
                gmgr.saveGroupRelationship(grel);
                Factory.getSocialSite().flush();
                ret.put("code", 200).put("message", "SUCCESS: member is now a group admin");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if (requestee.equals(requestor) && requesteeRel == null) {
            // If requestor == requestee and not group member
               // Add group request for requestee
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                ret = new JSONObject();
                gmgr.requestMembership(group, requestor);
                Factory.getSocialSite().flush();
                ret.put("code", 202).put("message", "SUCCESS: Membership requested");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if (requestorIsAdmin && requesteeRequest != null) {
            // Else if requestor is group ADMIN, requestee has requested membership
               // Accept request
            try {
                ret = new JSONObject();
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                gmgr.acceptMembership(requesteeRequest);
                Factory.getSocialSite().flush();
                ret.put("code", 200).put("message", "SUCCESS: Membership accepted");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if (requestorIsAdmin && requesteeRequest == null) {
            // Else if requestor is group ADMIN and requestee is not group member
               // invite user to join group

            try {
                ret = new JSONObject();
                Factory.getSocialSite().getNotificationManager().recordNotification(
                    requestor, requestee, group,
                    MessageContent.GROUP_INVITE, "Invitation to join group",
                    requestor.getName() + " would like you to consider joining group " + request.getParameter("groupHandle"), true);
                Factory.getSocialSite().flush();
                ret.put("code", 202).put("message", "SUCCESS: Invitation sent");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else {
            try {
                ret = new JSONObject();
                ret.put("code", 400).put("message", "ERROR: Bad request");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }
        }

        return ImmediateFuture.newInstance(ret);
    }

    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem request) throws SocialSpiException {
        authorizeRequest(request);

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        GroupManager gmgr = Factory.getSocialSite().getGroupManager();

        RestfulCollection<?> collection = null;

        Group group = null;       // Group identified by request
        String groupId = null;
        Profile requestor = null; // Person making get
        boolean requestorIsAdmin = false;
        GroupRelationship.Relationship requestorRel = null;

        try {
            //request.applyUrlTemplate(SOCIALSITE_GROUPMEMBER_PATH);
            logParameters(request);
            requestor    = pmgr.getProfileByUserId(request.getToken().getViewerId());
            groupId      = request.getParameter("groupHandle");
            if ("@current".equals(groupId)) {
                SocialSiteToken tok = (SocialSiteToken) request.getToken();
                groupId = tok.getGroupHandle();
            }

            group        = gmgr.getGroupByHandle(groupId);
            if (gmgr.getMembership(group, requestor) != null) {
                requestorIsAdmin = gmgr.isAdmin(group, requestor);
                requestorRel = gmgr.getMembership(group, requestor).getRelcode();
            }
        } catch (Exception ex) {
            return ImmediateFuture.errorInstance(ex);
        }

        if ("@requests".equals(request.getParameter("personId")) && (group != null) && requestorIsAdmin) {
            log.debug("in pending requests case");
            // If person == @requests and group is valid then
                // Return all pending group membership requests for the group
            try {
                List<GroupRequest> groupRequests = gmgr.getMembershipRequestsByGroup(group, request.getStartIndex(), request.getCount());
                int totalResults = gmgr.getMembershipRequestsByGroup(group, 0, -1).size();
                List<Person> people = new ArrayList<Person>(groupRequests.size());
                for (GroupRequest groupRequest : groupRequests) {
                    JSONObject jsonPerson = groupRequest.getProfileFrom().toJSON(Profile.Format.OPENSOCIAL_MINIMAL);
                    Person person = jsonConverter.convertToObject(jsonPerson.toString(), Person.class);
                    people.add(person);
                }
                collection = new RestfulCollection<Person>(people, request.getStartIndex(), totalResults);
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if ("@admins".equals(request.getParameter("personId")) && group != null) {
            // Else if groupId is valid an personId == @admins
               // Return all group ADMINs
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                List<GroupRelationship> rels = gmgr.getAdminsOfGroup(group, request.getStartIndex(), request.getCount());
                int totalResults = gmgr.getAdminsOfGroup(group, 0, -1).size();
                List<Person> people = new ArrayList<Person>(rels.size());
                for (GroupRelationship rel : rels) {
                    JSONObject jsonPerson = rel.getUserProfile().toJSON(Profile.Format.OPENSOCIAL_MINIMAL);
                    Person person = jsonConverter.convertToObject(jsonPerson.toString(), Person.class);
                    people.add(person);
                }
                collection = new RestfulCollection<Person>(people, request.getStartIndex(), totalResults);

            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if (group != null) {
            // Else if groupId is valid then
               // Return person objects of group members
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                List<GroupRelationship> rels = gmgr.getMembershipsByGroup(group, request.getStartIndex(), request.getCount());
                int totalResults = gmgr.getMembershipsByGroup(group, 0, -1).size();
                List<Person> people = new ArrayList<Person>(rels.size());
                for (GroupRelationship rel : rels) {
                    JSONObject jsonPerson = rel.getUserProfile().toJSON(Profile.Format.OPENSOCIAL_MINIMAL);
                    Person person = jsonConverter.convertToObject(jsonPerson.toString(), Person.class);
                    people.add(person);
                }
                collection = new RestfulCollection<Person>(people, request.getStartIndex(), totalResults);

            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else {
            return ImmediateFuture.errorInstance(new Exception("TODO: error object!"));
        }

        return ImmediateFuture.newInstance(collection);
    }

    @Operation(httpMethods="DELETE")
    public Future<?> delete(SocialRequestItem request) throws SocialSpiException {
        authorizeRequest(request);

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        GroupManager gmgr = Factory.getSocialSite().getGroupManager();

        // TODO: use an success/error object that can be converted to XML, JSON, Atom, etc.
        JSONObject ret = null;

        Group group = null;       // Group identified by request
        Profile requestor = null; // Person making delete
        Profile requestee = null; // Person being deleted
        boolean requestorIsAdmin = false;
        GroupRelationship.Relationship requesteeRel = null;

        try {
            //request.applyUrlTemplate(SOCIALSITE_GROUPMEMBER_PATH);
            logParameters(request);
            group        = gmgr.getGroupByHandle(request.getParameter("groupHandle"));
            requestor    = pmgr.getProfileByUserId(request.getToken().getViewerId());
            requestee    = pmgr.getProfileByUserId(request.getParameter("personId"));
            if (requestee == null) {
                requestee = pmgr.getProfileByUserId(request.getParameter("qualifier"));
            }
            if (gmgr.getMembership(group, requestor) != null) {
                requestorIsAdmin = gmgr.isAdmin(group, requestor);
            }
            if (gmgr.getMembership(group, requestee) != null) {
                requesteeRel = gmgr.getMembership(group, requestee).getRelcode();
            }
            else if (gmgr.getMembershipRequest(group, requestee) != null) {
                requesteeRel = PENDING;
            }
        } catch (Exception ex) {
            return ImmediateFuture.errorInstance(ex);
        }

        if (requestorIsAdmin && MEMBER.equals(requesteeRel)) {
            // If requestor is group ADMIN and requestee is group member
               // remove requestee from group
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                ret = new JSONObject();
                gmgr.removeMembership(group, requestee);
                Factory.getSocialSite().flush();
                ret.put("code", 200).put("message", "SUCCESS: member removed");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if (requestorIsAdmin && PENDING.equals(requesteeRel)) {
            // Else if requestor is group ADMIN and requestee has requested membership
            // remove requestee's membership request
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                ret = new JSONObject();
                GroupRequest requesteeReq = gmgr.getMembershipRequest(group, requestee);
                gmgr.removeGroupRequest(requesteeReq);
                Factory.getSocialSite().flush();
                ret.put("code", 200).put("message", "SUCCESS: member removed");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if (requestee.equals(requestor)) {
            // Else requestee == requestor
               // remove requestee from group
            try {
                // TODO: verify that 1) viewer is same as requestor 2) caller has appropriate authorization
                ret = new JSONObject();
                gmgr.removeMembership(group, requestor);
                Factory.getSocialSite().flush();
                ret.put("code", 200).put("message", "SUCCESS: member removed");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else if ("@requests".equals(request.getParameter("personId")) && requestorIsAdmin) {
            try {
                ret = new JSONObject();
                requestee = pmgr.getProfileByUserId(request.getParameter("qualifier"));
                GroupRequest greq = gmgr.getMembershipRequest(group, requestee);
                gmgr.removeGroupRequest(greq);
                ret.put("code", 200).put("", "SUCCESS: request remove");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }

        } else {
            try {
                ret = new JSONObject();
                ret.put("code", 400).put("", "ERROR: bad request");
            } catch (Exception ex) {
                return ImmediateFuture.errorInstance(ex);
            }
        }

        return ImmediateFuture.newInstance(ret);
    }

    private void logParameters(RequestItem request) {
        if (log.isDebugEnabled()) {
            log.debug("groupHandle="+request.getParameter("groupHandle"));
            log.debug("personId="+request.getParameter("personId"));
            log.debug("qualifier="+request.getParameter("qualifier"));
        }
    }

}
