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

package com.sun.socialsite.web.rest.model;

import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.GroupRequest;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.RelationshipRequest;
import org.apache.shindig.social.opensocial.model.Message;
import org.apache.shindig.social.opensocial.model.Message.Type;
import org.json.JSONObject;

/**
 * Implementation of Opensocial Message interface. This class
 * wraps messages or social requests to be sent to gadgets.
 */
public class MessageImpl implements Message {

    private final String     id;
    private final String     body;
    private final JSONObject group; // see getGroupJson()
    private final JSONObject sender; // see getProfileJson()
    private final String     status;
    private final String     title;
    private final Type       type;

    /** Relationship request level hint */
    private int level = 2;

    /** Relationship request howknow message */
    private String howknow = null;

    /** SocialSite type */
    private final ExtendedType extendedType;

    // create once and use when json data not needed
    private static final JSONObject EMPTY_JSON = new JSONObject();

    /**
     * The type of a message.
     */
    public enum ExtendedType {

        /** An email. */
        EMAIL("EMAIL"),

        /** A short private message. */
        NOTIFICATION("NOTIFICATION"),

        /** A message to a specific user that can be seen only by that user. */
        PRIVATE_MESSAGE("PRIVATE_MESSAGE"),

        /** A message to a specific user that can be seen by more than that user. */
        PUBLIC_MESSAGE("PUBLIC_MESSAGE"),

        /** Relationship request from one user to another */
        RELATIONSHIP_REQUEST("RELATIONSHIP_REQUEST"),

        /** Invitation to join a group from one user to another */
        GROUP_INVITE("GROUP_INVITE"),

        /** Request for membership in a group, from group to a user */
        GROUP_MEMBERSHIP_REQUEST("GROUP_MEMBERSHIP_REQUEST");

        /**
         * The type of message.
         */
        private final String jsonString;

        /**
         * Create a message type based on a string token.
         * @param jsonString the type of message
         */
        private ExtendedType(String jsonString) {
            this.jsonString = jsonString;
        }

        /**
         * @return a string representation of the enum.
         */
        @Override
        public String toString() {
            return this.jsonString;
        }

    }

    /**
     * Utility constructor for code that doesn't
     * care about notifications.
     *
     * @param content A message content object representing a message
     *     sent from one person to another.
     */
    public MessageImpl(MessageContent content) {
        this(content, false);
    }

    /**
     * Constructor that takes a MessageContext object.
     *
     * @param content MessageContent object to be wrapped.
     * @param isGroupInvite If true, tells the class to set type
     *     to 'notification'
     */
    public MessageImpl(MessageContent content, boolean isGroupInvite) {
        if (isGroupInvite) {
            this.title = "Invitation to join group " + content.getGroup().getName();
            this.body = "";
            this.group = getGroupJson(content.getGroup());
            this.type = Type.NOTIFICATION;
            this.extendedType = ExtendedType.GROUP_INVITE;
        } else {
            this.title = content.getSummary();
            this.body = content.getContent();
            this.group = EMPTY_JSON;
            this.type = Type.NOTIFICATION;
            this.extendedType = ExtendedType.PRIVATE_MESSAGE;
        }
        this.id = content.getId();
        this.sender = getProfileJson(content.getProfile());
        this.status = content.getStatus();
    }

    /**
     * Constructor that takes a FriendshipRequest object.
     *
     * @param request The friendship request to wrap
     */
    public MessageImpl(RelationshipRequest request) {
        Profile fromUser = request.getProfileFrom();
        this.body = "";
        this.group = EMPTY_JSON;
        this.id = request.getId();
        this.sender = getProfileJson(fromUser);
        this.status = request.getStatus().toString();
        this.title = "Relationship Request from " + fromUser.getName();
        this.type = Type.NOTIFICATION;
        this.extendedType = ExtendedType.RELATIONSHIP_REQUEST;
        this.level = request.getLevelTo();
        this.howknow = request.getHowknow();
    }

    /**
     * Constructor that takes a GroupRequest object.
     *
     * @param request The group request to wrap
     */
    public MessageImpl(GroupRequest request) {
        Profile fromUser = request.getProfileFrom();
        this.body = "";
        this.group = getGroupJson(request.getGroup());
        this.id = request.getId();
        this.sender = getProfileJson(fromUser);
        this.status = request.getStatus().toString();
        this.title = "Group Membership Request from " + fromUser.getName() +
                " for group '" + request.getGroup().getName() + "'";
        this.type = Type.NOTIFICATION;
        this.extendedType = ExtendedType.GROUP_MEMBERSHIP_REQUEST;
    }

    /**
     * @return Content of MessageContent object;
     */
    public String getBody() {
        return this.body;
    }

    /**
     * @return Summary of MessageContent object;
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns standard OpenSocial type.
     * @return Hard coded to 'private' message for now.
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Returns SocialSite extended type.
     * @return the extendedType.
     */
    public ExtendedType getExtendedType() {
        return this.extendedType;
    }

    // -------------------------------------------------------------------
    // The following getters are not part of Opensocial Message, but will
    // be called anyway when this object is converted to a json response.
    // -------------------------------------------------------------------

    /**
     * @return The profile url, display name, etc.,  of the
     * group referenced in the message.
     */
    public JSONObject getGroup() {
        return this.group;
    }

    /**
     * @return The ID of the message.
     */
    public String getId() {
        return id;
    }

    /**
     * @return The profile url, display name, etc.,  of the
     * user who sent the message.
     */
    public JSONObject getSender() {
        return this.sender;
    }

    /**
     * @return The message status, e.g. READ or UNREAD
     */
    public String getStatus() {
        return this.status;
    }

    // -------------------------------------------------------------------
    // End public getters added to Message implementation
    // -------------------------------------------------------------------

    /**
     * Not supported as this impl class is used for returning data only.
     */
    public void setBody(String body) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not supported as this impl class is used for returning data only.
     */
    public void setTitle(String title) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Not supported as this impl class is used for returning data only.
     */
    public void setType(Type type) {
        throw new UnsupportedOperationException("Not supported.");
    }

    // See TODO in Message.java interface
    public String sanitizeHTML(String html) {
        return html;
    }

    /**
     * Relationship request level hint
     */
    public int getLevel() {
        return level;
    }

    /**
     * Not supported as this impl class is used for returning data only.
     */
    public void setLevel(int level) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /**
     * Relationship request how-know message or null if none
     */
    public String getHowknow() {
        return howknow;
    }

    /**
     * Not supported as this impl class is used for returning data only.
     */
    public void setHowknow(String howknow) {
        throw new UnsupportedOperationException("Not supported.");
    }

    /*
     * Create a json representation of a user to include
     * profile URL and display name. This hopefully will
     * be replaced with an OpenSocial Person object in a
     * future version of spec.
     */
    private JSONObject getProfileJson(Profile profile) {
        // the case for system notifications
        if (profile == null) {
            return null;
        }
        return profile.toJSON(Profile.Format.OPENSOCIAL_MINIMAL);
    }

    /*
     * Create a json representation of a Group object. It
     * would be good to have a 'payload' JSON field in Message
     * for gadget-specific information. In this case, group
     * invites and join requests need to convey the group's
     * information to the receiver.
     */
    private JSONObject getGroupJson(Group group) {
        return  group.toJSON(Group.Format.OPENSOCIAL, null);
    }

}
