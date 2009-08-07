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

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.NotificationManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.RelationshipRequest;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRelationship;
import com.sun.socialsite.pojos.GroupRequest;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.model.MessageImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.Operation;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.ResponseItem;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.Service;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.service.SocialRequestItem;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.sun.socialsite.pojos.MessageContent.READ;

/**
 * <p>Handles message requests.</p>
 * 
 * <p>Supports these URIs and HTTP methods:</p>
 *    /messages/{userId}/outbox<br/>
 *        - POST to create a new message.<br/>
 *        - GET to get sent messages for a user.<br/>
 *    /messages/{userId}/inbox - GET to get messages for a user.<br/>
 *    /messages/{userId}/inbox/{messageId}<br/>
 *        - GET to get an individual message.<br/>
 *        - PUT to update a message (i.e., mark it read).<br/>
 *        - DELETE to delete a message.<br/>
 */
@Service(name = "messages", path="/{userId}/{box}")
public class MessageHandler extends RestrictedDataRequestHandler {
    
    private static Log log = LogFactory.getLog(MessageHandler.class);
    private static final String POST_MESSAGE_PATH = "/messages/{userId}/outbox";

    // box = 'inbox' or 'outbox'
    private static final String MESSAGE_PATH =
        "/messages/{userId}/{box}/{messageId}";
    private static final String MESSAGE_STATUS_PATH =
        "/messages/{userId}/inbox/{messageId}/{status}";

    private static final String GROUP_PREFIX = "group_";
    private static final String PERSON_PREFIX = "person_";

    private static final String INBOX = "inbox";
    private static final String OUTBOX = "outbox";

    /*
     * Either the 'box' param must be non-null for retrieving
     * a collection of messages, or the messageId param must
     * be non-null for retrieving a single message (in which
     * case in/outbox is ignored).
     */
    @Operation(httpMethods="GET")
    public Future<?> get(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(MESSAGE_PATH);
        String userId = null;
        String box = reqItem.getParameter("box");
        String messageId = reqItem.getParameter("messageId");
        if (box == null && messageId == null) {
            String msg = "Request must specify either in/outbox or message id";
            log.error(msg);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
        try {
            userId = getUserId(reqItem);

            if (box != null) {
                return fetchMessages(userId, box, reqItem.getStartIndex(), reqItem.getCount());
            } else {
                NotificationManager notificationManager =
                    Factory.getSocialSite().getNotificationManager();
                MessageContent mc =
                    notificationManager.getNotification(messageId);

                Message message = new MessageImpl(mc);
                log.trace("END");
                return ImmediateFuture.newInstance(message);
            }
        } catch (SocialSiteException sse) {
            log.error(sse.getMessage(), sse);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, sse.getMessage()));
        } catch (Exception e) {
            String msg = String.format(
                "Failed to retrieve message with id=%s for userId=%s",
                messageId, userId);
            log.error(msg, e);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }

    
    @Operation(httpMethods="PUT")
    public Future<? extends ResponseItem> put(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(MESSAGE_STATUS_PATH);
        String userId = null;
        String messageId = reqItem.getParameter("messageId");
        String status = reqItem.getParameter("status");
        try {
            userId = getUserId(reqItem);

            // only handling "READ" currently
            if (! MessageContent.READ.equals(status)) {
                String msg = String.format(
                    "Failed to mark message with id=%s for userId=%s status=%s",
                    messageId, userId, status);
                log.error(msg);
                return ImmediateFuture.newInstance(
                    new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }

            NotificationManager notificationManager =
                Factory.getSocialSite().getNotificationManager();
            notificationManager.setNotifcationStatus(messageId, status);
            log.trace("END");
            return ImmediateFuture.newInstance(null);
        } catch (SocialSiteException sse) {
            log.error(sse.getMessage(), sse);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, sse.getMessage()));
        } catch (Exception e) {
            String msg = String.format(
                "Failed mark message with id=%s for userId=%s status=%s",
                messageId, userId, status);
            log.error(msg, e);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }

    
    @Operation(httpMethods="DELETE")
    public Future<? extends ResponseItem> delete(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(MESSAGE_PATH);
        String messageId = reqItem.getParameter("messageId");
        try {
            
            String userId = getUserId(reqItem);
            ProfileManager profileManager =
                Factory.getSocialSite().getProfileManager();
            Profile fromProfile = profileManager.getProfileByUserId(userId);
            if (fromProfile == null) {
                return ImmediateFuture.newInstance(noProfile(userId));
            }        
            try {
                NotificationManager notificationManager =
                    Factory.getSocialSite().getNotificationManager();
                notificationManager.removeNotification(messageId, fromProfile);
                Factory.getSocialSite().flush();
            } catch(SocialSiteException socex) {                
                return ImmediateFuture.newInstance(
                        new ResponseItem(ResponseError.BAD_REQUEST, socex.getMessage()));
            }
            log.trace("END");
            return ImmediateFuture.newInstance(null);
        } catch (SocialSiteException sse) {
            log.error(sse.getMessage(), sse);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, sse.getMessage()));
        } catch (Exception e) {
            String msg = String.format(
                "Failed to delete message with id=%s", messageId);
            log.error(msg, e);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }

    
    @Operation(httpMethods="POST")
    public Future<? extends ResponseItem> post(SocialRequestItem reqItem) {
        log.trace("BEGIN");
        authorizeRequest(reqItem);

        //reqItem.applyUrlTemplate(POST_MESSAGE_PATH);
        String userId = null;
        try {
            userId = getUserId(reqItem);

            ProfileManager profileManager =
                Factory.getSocialSite().getProfileManager();
            Profile fromProfile = profileManager.getProfileByUserId(userId);
            if (fromProfile == null) {
                return ImmediateFuture.newInstance(noProfile(userId));
            }
            
            // bug in client code causing request to be malformed
            //Message incomingMessage = reqitem.getTypedParameter("message", Message.class);

            // workaround to use JSONObject instead.
            // see https://socialsite.dev.java.net/issues/show_bug.cgi?id=103
            String postData =
                reqItem.getTypedParameter("message", String.class);
            if (log.isDebugEnabled()) {
                log.debug(postData);
            }
            JSONObject incomingMessage = new JSONObject(postData);
            incomingMessage = incomingMessage.getJSONObject("fields_");
            // end workaround

            if ( !(incomingMessage.has("body")
                && incomingMessage.has("recipients")
                && incomingMessage.has("title")
                && incomingMessage.has("type"))) {
                String msg =
                    "Request must specify body, title, type, and recipients";
                return ImmediateFuture.newInstance(
                    new ResponseItem(ResponseError.BAD_REQUEST, msg));
            }
            String body = incomingMessage.getString("body");
            String title = incomingMessage.getString("title");
            
            // not currently used
            String type = incomingMessage.getString("type");
           
            List<Profile> profiles =
                getToProfiles(incomingMessage, profileManager);
            sendMessageToProfiles(fromProfile, profiles, title, body);
            log.trace("END");
            return ImmediateFuture.newInstance(null);
        } catch (SocialSiteException sse) {
            log.error(sse.getMessage(), sse);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, sse.getMessage()));
        } catch (Exception e) {
            String msg = "Failed to send message for userId=" + userId;
            log.error(msg, e);
            return ImmediateFuture.newInstance(
                new ResponseItem(ResponseError.BAD_REQUEST, msg));
        }
    }

    /*
     * Fetch all messages from specified mailbox.
     *
     * @param userId The id of the user
     * @param box The string 'inbox' or 'outbox'
     */
    private Future<?> fetchMessages(String userId, String box, int offset, int length)
            throws SocialSiteException {
        assert(box != null);
        List<Message> messages = new ArrayList<Message>();

        
        ProfileManager profileManager =
            Factory.getSocialSite().getProfileManager();
        Profile profile = profileManager.getProfileByUserId(userId);
        if (profile == null) {
            return ImmediateFuture.newInstance(noProfile(userId));
        }
        
        NotificationManager notificationManager =
            Factory.getSocialSite().getNotificationManager();
        List<MessageContent> mcs = new ArrayList<MessageContent>();
        List<MessageContent> readMsgs =
            new ArrayList<MessageContent>();

        if (INBOX.equals(box)) {
            mcs = notificationManager.getUserInbox(profile, 0, -1);
        } else if (OUTBOX.equals(box)) {
            mcs = notificationManager.getUserSentBox(profile, 0, -1);
        } else {
            String msg = String.format(
                "Cannot retrieve messages from unknown box=%s", box);
            log.warn(msg);
            throw new SocialSiteException(msg);
        }
        
        // read messages after unread
        for (MessageContent mc : mcs) {
            if (READ.equals(mc.getStatus())) {
                readMsgs.add(mc);
                continue;
            }
            messages.add(new MessageImpl(mc));
        }
        for (MessageContent mc : readMsgs) {
            messages.add(new MessageImpl(mc));
        }

        // now add social requests if this is inbox
        if (INBOX.equals(box)) {
            
            // friend requests
            RelationshipManager friendManager =
                Factory.getSocialSite().getRelationshipManager();
            List<RelationshipRequest> frs =
                friendManager.getRelationshipRequestsByToProfile(profile, 0, -1);
            for (RelationshipRequest fr : frs) {
                messages.add(new MessageImpl(fr));
            }
            
            // group invites
            mcs = notificationManager.getUserInvitations(profile, 0, -1);
            for (MessageContent mc : mcs) {
                messages.add(new MessageImpl(mc, true));
            }
            
            // group requests
            GroupManager groupManager =
                Factory.getSocialSite().getGroupManager();
            List<GroupRelationship> grels =
                groupManager.getMembershipsByProfile(profile, 0, -1);
            for (GroupRelationship grel : grels) {
                // if user is an admin only then display group notifications
                // for requests to join the group
                if (groupManager.isFounder(grel.getGroup(), profile)) {
                    List<GroupRequest> greqs =
                        groupManager.getMembershipRequestsByGroup(
                        grel.getGroup(), 0, -1);
                    for (GroupRequest greq : greqs) {
                        messages.add(new MessageImpl(greq));
                    }
                }
            }
        }
        
        // Now that we have all results, return the subset specified
        ArrayList<Message> subset = new ArrayList<Message>();
        int start = offset;
        int end = offset + length + 1;
        for (int i=start; i<end && i<messages.size() ;i++) {
            subset.add(messages.get(i));
        }
        
        RestfulCollection<Message> messageCollection = 
            new RestfulCollection<Message>(subset, offset, messages.size());
        log.trace("END");
        return ImmediateFuture.newInstance(messageCollection);
    }
    
    /*
     * Utility method for creating an appropriate response when a profile
     * cannot be found for a given user id.
     * 
     * @param id The id of a user for which a profile could not be found
     */
    private ResponseItem noProfile(String id) {
        String msg = String.format("Cannot find userId=%s", id);
        log.warn(msg);
        return new ResponseItem(ResponseError.BAD_REQUEST, msg);
    }

    /*
     * Creates a List of profiles from a recipient array in
     * an incoming message. The array could contain person
     * or group IDs. As the compose_mail gadget is written
     * not, this array only has one value.
     */
    private List<Profile> getToProfiles(JSONObject msg,
            ProfileManager profileManager) throws Exception {
        List<Profile> profiles = new ArrayList<Profile>();
        JSONArray jArray = msg.getJSONArray("recipients");
        GroupManager groupManager = Factory.getSocialSite().getGroupManager();
        for (int i = 0; i < jArray.length(); i++) {
            String id = jArray.getString(i);
            if (id.indexOf(GROUP_PREFIX) != -1) {
                id = id.substring(GROUP_PREFIX.length(), id.length());
                Group group = groupManager.getGroupByHandle(id);
                if (group == null) {
                    throw new SocialSiteException(
                        String.format("Cannot find group=%s", id));
                }
                List<GroupRelationship> members =
                    groupManager.getMembershipsByGroup(group, 0, -1);
                for (GroupRelationship gr : members) {
                    profiles.add(gr.getUserProfile());
                }
            } else if (id.indexOf(PERSON_PREFIX) != -1) {
                id = id.substring(PERSON_PREFIX.length(), id.length());
                Profile toProfile = profileManager.getProfileByUserId(id);
                if (toProfile == null) {
                    throw new SocialSiteException(
                        String.format("Cannot find profile=%s", id));
                }
                profiles.add(toProfile);
            } else {
                throw new SocialSiteException("Unknown recipient type: " + id);
            }
        }
        return profiles;
    }

    /*
     * Called from handlePost() to send a message to a user.
     */
    private void sendMessageToProfiles(Profile fromProfile,
            List<Profile> profiles, String subj, String message) {
        try {
            NotificationManager nm =
                Factory.getSocialSite().getNotificationManager();
            boolean savedCopyInSent = true;
            for (Profile p : profiles) {
                nm.recordNotification(fromProfile, p, null,
                    MessageContent.NOTIFICATION, subj, message, savedCopyInSent);
                savedCopyInSent = false;
            }
            Factory.getSocialSite().flush();
        } catch (Exception e) {
            log.error("ERROR sending message", e);
        }
    }

    /*
     * Method used in handleXYZ methods to make sure a user
     * was included in the request.
     */
    private String getUserId(SocialRequestItem reqItem) throws SocialSiteException {
        if (reqItem.getUsers() == null || reqItem.getUsers().size() < 1) {
            throw new SocialSiteException("No user specified");
        }
        UserId userIdObject = reqItem.getUsers().iterator().next();
        return  userIdObject.getUserId(reqItem.getToken());        
    }
    
}
