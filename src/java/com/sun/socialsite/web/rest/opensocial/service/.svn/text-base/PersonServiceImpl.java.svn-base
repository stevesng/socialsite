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
package com.sun.socialsite.web.rest.opensocial.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.web.rest.model.PersonEx;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;
import org.json.JSONObject;


/**
 * SocialSite does not implement the Shindig PersonService interface, but we
 * still need this to satisfy some of Shindig's Dependency Injection logic.
 */
public class PersonServiceImpl implements PersonService {

    private static Log log = LogFactory.getLog(PersonServiceImpl.class);

    private static BeanJsonConverter jsonConverter;

    @Inject
    public PersonServiceImpl(
            @Named("shindig.bean.converter.json") BeanConverter jsonConverter) {
        PersonServiceImpl.jsonConverter = (BeanJsonConverter)jsonConverter;
    }

    //--------------------------------------------------------------------------
    
    public Future<RestfulCollection<Person>> getPeople(
            Set<UserId>       userIds,
            GroupId           groupId,
            CollectionOptions options,
            Set<String>       fields,
            SecurityToken     token) throws SocialSpiException {

        if (log.isDebugEnabled()) {
            for (UserId userId : userIds) {
                log.debug("userID.getType()="+userId.getType());
                log.debug("userID.getUserId(token)="+userId.getUserId(token));
            }
            if (groupId != null) {
                log.debug("groupId.getType()="+groupId.getType());
                log.debug("groupId.getGroupId()="+groupId.getGroupId());
            }
            if (options != null) {
                log.debug("options.getSortOrder()="+options.getSortOrder());
                log.debug("options.getFilterOperation()="+options.getFilterOperation());
                log.debug("options.getFirst()="+options.getFirst());
                log.debug("options.getMax()="+options.getMax());
            }
            for (String field : fields) {
                log.debug("field="+field);
            }
            if (token != null) {
                log.debug("token="+token);
            }
        }

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        List<Person> people = new ArrayList<Person>();
        int totalResults = 0;

        try {
            // TODO: more efficient way to do this query. Currently we get all
            // results, do a join in memory and then return a specified subset.
            // e.g. instead of loop use one query that specifies list of users?
                
            // Use a sorted set to sort people by lastname, firstname
            Set<Person> allPeople = new TreeSet<Person>(new Comparator<Person>() {
                public int compare(Person p1, Person p2) {
                    int ret = p1.getId().compareTo(p2.getId());
                    if (p1.getName() != null && p2.getName() != null) {
                        if (p1.getName().getFamilyName() != null && p2.getName().getFamilyName() != null) {
                            ret = p1.getName().getFamilyName().compareTo(p2.getName().getFamilyName());
                        } 
                        if (ret == 0 && p1.getName().getGivenName() != null && p2.getName().getGivenName() != null) {
                            ret = p1.getName().getGivenName().compareTo(p2.getName().getGivenName());
                        }
                    }
                    return ret;
                }
            });
            String gid = ((groupId != null) ? groupId.getGroupId() : null);
            
            // Now for each user get the people specified
            for (Iterator<UserId> uit = userIds.iterator(); uit.hasNext();) {
                UserId userId = uit.next();
                String uid = ((userId != null) ? userId.getUserId(token) : null);
                switch (groupId.getType()) {
                    case all:
                        break;
                    case friends: {
                        // Group is type "friends" so get friends
                        List<Profile> profiles = pmgr.getProfiles(
                            uid, null, token.getViewerId(),
                            options,
                            0, -1, fields
                        );
                        for (Profile profile : profiles) {
                            JSONObject json = profile.toJSON(
                                Profile.Format.OPENSOCIAL, token.getViewerId(), fields);
                            Person person = getPerson(json, token);
                            allPeople.add(person);
                        }
                        break;
                    }
                    case groupId: {
                        // Group is simply a groupId, so get group members
                        List<Profile> profiles = pmgr.getProfiles(
                            null, gid, token.getViewerId(),
                            options,
                            0, -1, fields
                        );
                        for (Profile profile : profiles) {
                            JSONObject json = profile.toJSON(
                                Profile.Format.OPENSOCIAL, token.getViewerId(), fields);
                            Person person = getPerson(json, token);
                            allPeople.add(person);
                        }
                        break;
                    }
                    case self: {
                        // return only self
                        Profile profile = pmgr.getProfileByUserId(uid);
                        JSONObject json = profile.toJSON(
                            Profile.Format.OPENSOCIAL, token.getViewerId(), fields);
                        Person person = getPerson(json, token);
                        allPeople.add(person);
                        break;
                    }
                }
            }
            
            // Now that we have all results, return the subset specified
            totalResults = allPeople.size();
            Person[] peopleArray = allPeople.toArray(new Person[0]);
            int start = options.getFirst();
            int end = options.getFirst() + options.getMax() + 1;
            for (int i=start; i<end && i<peopleArray.length ;i++) {
                people.add(peopleArray[i]);
            }

        } catch (Exception e) {
            log.debug("ERROR getting people", e);
            throw new SocialSpiException(
                ResponseError.INTERNAL_ERROR, "Problem getting people", e);
        }

        RestfulCollection<Person> collection = new RestfulCollection<Person>(
            people, options.getFirst(), totalResults);
        return ImmediateFuture.newInstance(collection);
    }


    //--------------------------------------------------------------------------
    
    public Future<Person> getPerson(
            UserId userId, Set<String> fields, SecurityToken token)
            throws SocialSpiException {
        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        try {
            Profile profile = pmgr.getProfileByUserId(userId.getUserId(token));
            JSONObject json = profile.toJSON(
                Profile.Format.OPENSOCIAL, token.getViewerId(), fields);
            Person person = getPerson(json, token);
            return ImmediateFuture.newInstance(person);
            
        } catch (SocialSiteException e) {
            // TODO: Something Better
            throw new RuntimeException(e);
        }
    }
    
    
    //--------------------------------------------------------------------------
    
    /**
     * Obtains a Person object equivalent to the specified JSON (and
     * customized for the specified token context).
     */
    private Person getPerson(JSONObject json, SecurityToken token)
            throws SocialSiteException {
        Person person =
            jsonConverter.convertToObject(json.toString(), PersonEx.class);
        person.setIsViewer(person.getId().equals(token.getViewerId()));
        person.setIsOwner(person.getId().equals(token.getOwnerId()));
        return person;
    }

    //--------------------------------------------------------------------------
    
    /**
     * @return the jsonConverter
     */
    public static BeanJsonConverter getJsonConverter() {
        return jsonConverter;
    }

}
