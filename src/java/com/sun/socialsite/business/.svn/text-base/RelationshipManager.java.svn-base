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

package com.sun.socialsite.business;

import com.google.inject.ImplementedBy;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.impl.JPARelationshipManagerImpl;
import com.sun.socialsite.pojos.Relationship;
import com.sun.socialsite.pojos.RelationshipRequest;
import com.sun.socialsite.pojos.Profile;
import java.util.List;


/**
 * Request, acccept and access personal relationships with negotiation.
 * <p>Process of requesting and then accepting, ignoring or clarifying a
 * relationship is known as negotiation. Here's how it works:</p>
 *
 * <p>First some terminology:</p>
 * <dl>
 * <dt>Relationship</dt><dd>Personal relationship that exists from one user to
 * another, with a relationship level.</dd>
 *
 * <dt>Relationship level</dt><dd>Integer index indicating strength of a
 * relationship.</dd>
 *
 * <dt>Friendship level</dt><dd>The minimum relationship level that is
 * considered to be a friendship.</dd>
 *
 * <dt>Mutual Relationship</dt><dd>When two users have relations with each
 * other each and, (optionally) have agreed on a how-know message.</dd>
 *
 * <dt>Relationship negotiation</dt><dd>The process of accepting, ignoring
 * or clarifying a relationship. Occurs when a user requests a friendship
 * level (or above) relationship with another user.</dd>
 * </dl>
 *
 * <p>Here's an outline of the relationship negotiation process:</p>
 * <ul>
 * <li>User1 requests relationship with user2, specifying a relationship
 * level. This creates a one-way relationship from user1 to user2.
 *
 * If the level is below friendship level, then a one-way relationship is
 * established and there is no negotiation. But if that level is friendship
 * level or higher then a how-know message must be negotiated with user2 and so
 * a RelationshipRequest will be sent.</li>
 *
 * <li>User2 receives the request and can choose to accept it, ignore it or
 * clarify it. Here's how the three optons work:
 *     <ul>
 *     <li>Accept: user2 accepts the request, specifying their own relationship
 *     level for the relationship.</li>
 *
 *     <li>Ignore: user2 doesn't know user1 and chooses to ignore the request.
 *     The first user keeps his one-way relation.</li>
 *
 *     <li>Clarify: user2 disagrees with the howknows message, and chooses to
 *     clarify the request. So, user2 changes the how-know message and the
 *     request is sent back to the first user1, who may choose to accept,
 *     ignore or clarify.</li>
 *     </ul>
 * </li>
 * </ul>
 *
 * <p>Notes</p>
 * 
 * <p>Users cannot see the relationship levels assigned by other users.</p>
 *
 * <p>How know messages are optional, but if one is set then both users must
 * agree on it; by accepting the request if it is true or by clarifying or
 * ignoring the request if it is not true.</p>
 *
 */
@ImplementedBy(JPARelationshipManagerImpl.class)
public interface RelationshipManager extends Manager {

    /**
     * Return array of relationship level name I18N keys,
     * indexed by relationship level.
     */
    public String[] getRelationshipLevelKeys();

    /**
     * Return array of relationship level names for default server locale, 
     * indexed by relationship level.
     */
    public String[] getRelationshipLevelNames();

    /**
     * Return the relationship level that is to be considered a "friendship"
     */
    public int getFriendshipLevel();

    /**
     * Create a one-way relationship without any negotiation.
     *
     * @param from  From profile
     * @param to    To profile
     * @param level Relationship level
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void createRelationship(
        Profile from, Profile to, int level)
        throws SocialSiteException;

    /**
     * Create a two-way relationship without any negotiation.
     *
     * @param profile1  One user
     * @param level1    Level for user1's edge of the relationship
     * @param profile2  Another user
     * @param level2    Level for user2's edge of the relationship
     * @param know      How user1 and user2 know each other
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void createMutualRelationship(
        Profile profile1, int level1, Profile profile2, int level2, String know)
        throws SocialSiteException;

    /**
     * Get all relationships from a specified profile.
     *
     * @param user   User to get
     * @param offset Start index
     * @param length Number of results to return
     * @return List of relationships, empty to none exist
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<Relationship> getRelationships(
        Profile profile, int offset, int length) throws SocialSiteException;

    /**
     * Get relationship from one profile to another.
     *
     * @param from From profile
     * @param to   To profile
     * @return Relationship or null of none exists
     * @throws com.sun.socialsite.SocialSiteException
     */
    public Relationship getRelationship(
            Profile from, Profile to) throws SocialSiteException;

    /**
     * Remove the one relationship from one profile to another, no negotiation.
     *
     * @param from From profile
     * @param to   To profile
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void removeRelationship(
        Profile from, Profile to) throws SocialSiteException;

    /**
     * Set new level for relationship.
     * @param from  Profile from
     * @param to    Profile to
     * @param level New level
     */
    public void adjustRelationship(Profile from, Profile to, int level)
        throws SocialSiteException;

    /**
     * Remove the up-to-two relationships between to users, no negotiation.
     * 
     * @param profile1 First user
     * @param profile2 Second user
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void removeRelationships(
        Profile profile1, Profile profile2) throws SocialSiteException;

    /**
     * Request a relationship from one profile to another, with negotiation.
     *
     * @param from   From profile
     * @param to     To profile
     * @param level  Relationship level
     * @param know   How the users know one another
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void requestRelationship(
        Profile from, Profile to, int level, String know)
        throws SocialSiteException;

    /**
     * Accept relationship requested between two users, ends negotiation.
     *
     * @param from  Profile from
     * @param to    Profile to
     * @param level New level for the to user's edge of the relationship
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void acceptRelationshipRequest(
        RelationshipRequest rreq, int level) throws SocialSiteException;

    /**
     * The to user wishes to clarify the relationship request, continues negotiation.
     * This will send the request back to the from user for action.
     * @param from  Profile from
     * @param to    Profile to
     * @param know  New how-know text
     * @param level New level for the to user's edge of the relationship
     * @throws com.sun.socialsite.SocialSiteException if no such request exists
     */
    public void clarifyRelationshipRequest(
        RelationshipRequest rreq, int level, String howknow)
        throws SocialSiteException;

    /**
     * Ignore the relationship specified by the request, ends negotiation.
     *
     * @param from From profile
     * @param to   To profile
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void ignoreRelationshipRequest(
        RelationshipRequest rreq) throws SocialSiteException;

    /**
     * Get relationship request from user to another
     *
     * @param from From profile
     * @param to   To profile
     * @return Request or null if none exists.
     * @throws com.sun.socialsite.SocialSiteException
     */
    public RelationshipRequest getRelationshipRequest(
        Profile from, Profile to) throws SocialSiteException;

    /**
     * Get all relationship requsts to a specified user.
     *
     * @param to     To profile
     * @param offset Start index
     * @param length Number of results to return
     * @return List of relationship requests, empty if none exist
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<RelationshipRequest> getRelationshipRequestsByToProfile(
        Profile to, int offset, int length) throws SocialSiteException;

    /**
     * Get all relationship requsts from a specified user.
     *
     * @param from   From profile
     * @param offset Start index
     * @param length Number of results to return
     * @return List of relationship requests, empty if none exist
     * @throws com.sun.socialsite.SocialSiteException
     */
    public List<RelationshipRequest> getRelationshipRequestsByFromProfile(
        Profile from, int offset, int length) throws SocialSiteException;

    /**
     * Remove a relationship request.
     *
     * @param rreq Request to remove
     * @throws com.sun.socialsite.SocialSiteException
     */
    public void removeRelationshipRequest(
        RelationshipRequest rreq) throws SocialSiteException;
}



