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
import com.sun.socialsite.business.impl.JPAProfileManagerImpl;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.ProfileDefinition;
import com.sun.socialsite.pojos.ProfileProperty;
import com.sun.socialsite.pojos.SectionPrivacy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;


/**
 * Manages Profiles and their associated ProfileProps.
 * Profile objects store user id and names.
 * ProfileProp objects store everything else.
 * ProfileDef object defines what ProfileProps are allowed.
 */
@ImplementedBy(JPAProfileManagerImpl.class)
public interface ProfileManager extends Manager {

    /**
     * Get profile property metadata. Defines properties, objects and
     * collections that make up the profile object model.
     *
     * @return Profile property metadata.
     */
    public ProfileDefinition getProfileDefinition();

    //------------------------------------------------------------ Profile CRUD

    public void saveProfile(Profile profile) throws SocialSiteException;

    public void saveProfile(Profile profile, boolean createActivity) throws SocialSiteException;

    public void removeProfile(Profile profile) throws SocialSiteException;

    public Profile getProfileByUserId(String userid) throws SocialSiteException;

    public Profile getProfile(String id) throws SocialSiteException;

    public List<Profile> getProfiles(int offset, int length) throws SocialSiteException;

    public List<Profile> getMostRecentlyUpdatedProfiles(int offset, int length) throws SocialSiteException;

    public List<Profile> getOldestProfiles(int offset, int length) throws SocialSiteException;

    public  List<Profile> searchProfiles(int offset, int length, String pattern) throws SocialSiteException;

    /**
     * Return Profiles for set of users.
     *
     * @param userId    User ID to filter on, or null if none
     * @param groupId   Group ID to filter on, or null if none
     * @param viewerId  User ID of viewer requesting dat
     * @param sort      Sort orde
     * @param filter    Filter (all, topFriends, hasApp)
     * @param first     Index into result collection
     * @param max       Max results to return
     * @param details   Properties to be returned
     */
    public List<Profile> getProfiles(
        String      userId,
        String      groupId,
        String      viewerId,
        CollectionOptions collectionOptions,
        int         first,
        int         max,
        Set<String> profileDetails) throws SocialSiteException;


    //-------------------------------------------------------- ProfileProp CRUD

    public void saveProfileProperty(ProfileProperty profileProp) throws SocialSiteException;

    public void removeProfileProperty(ProfileProperty profileProp) throws SocialSiteException;


    //---------------------------------------------------- Profile privacy CRUD

    /**
     * Get a profile's section privacy settings as a map keyed by section names.
     */
    public Map<String, SectionPrivacy> getSectionPrivacies(Profile profile)
            throws SocialSiteException;

    /**
     * Updates a profile section privacy setting. This sets the visibility
     * fields of the properties that represent the sections AND the properties
     * within those sections, to make queries easy.
     */
    void updateSectionPrivacy(Profile profile, SectionPrivacy privacy) throws SocialSiteException;

}
