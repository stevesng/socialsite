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

import com.google.common.collect.ImmutableSet;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.SocialSiteActivityManager;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.GroupManager;
import com.sun.socialsite.business.RelationshipManager;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.pojos.Relationship;
import com.sun.socialsite.pojos.Group;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.pojos.SocialSiteActivity;
import com.sun.socialsite.pojos.SocialSiteMediaItem;
import com.sun.socialsite.pojos.TemplateParameter;
import com.sun.socialsite.util.DateUtil;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.ResponseError;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.MediaItemImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.SocialSpiException;
import org.apache.shindig.social.opensocial.spi.UserId;


public class ActivityServiceImpl implements ActivityService {

    /** control query size */
    private static final int MAX_RETURNED = 100;

    private static Log log = LogFactory.getLog(ActivityServiceImpl.class);

    public ActivityServiceImpl() {
    }

    public Future<RestfulCollection<Activity>> getActivities(Set<UserId> userIds,
            GroupId groupId, String appId, Set<String> fields,
            CollectionOptions opts, SecurityToken token) throws SocialSpiException {
        return ImmediateFuture.newInstance(
            getActivitiesInternal(userIds, groupId, appId, fields, opts, null, token));
    }

    public Future<RestfulCollection<Activity>> getActivities(UserId userId,
            GroupId groupId, String appId, Set<String> fields,
            CollectionOptions opts, Set<String> activityIds, SecurityToken token) throws SocialSpiException {
        return ImmediateFuture.newInstance(
            getActivitiesInternal(ImmutableSet.of(userId), groupId, appId, fields, opts, activityIds, token));
    }

    public Future<Activity> getActivity(UserId userId, GroupId groupId, String appId,
      Set<String> fields, String activityId, SecurityToken token) throws SocialSpiException {
        return ImmediateFuture.newInstance(
            getActivityInternal(userId, groupId, appId, fields, activityId, token));
    }

    public Future<Void> deleteActivities(UserId userId, GroupId groupId, String appId,
      Set<String> activityIds, SecurityToken token) throws SocialSpiException {
        return ImmediateFuture.newInstance(
            deleteActivityInternal(userId, groupId, appId, activityIds, token));
    }

    public Future<Void> createActivity(UserId userId, GroupId groupId, String appId,
            Set<String> fields, Activity activity, SecurityToken token) {
        return ImmediateFuture.newInstance(
            createActivityInternal(userId, groupId, appId, fields, activity, token));
    }

    //--------------------------------------------------------------------------
        
    private RestfulCollection<Activity> getActivitiesInternal(Set<UserId> userIds,
            GroupId groupId, String appId, Set<String> fields, 
            CollectionOptions opts, Set<String> activityIds, SecurityToken token)
            throws SocialSpiException{

        if (log.isDebugEnabled()) {
            for (UserId userId : userIds) {
                log.debug("userID.getType()="+userId.getType());
                log.debug("userID.getUserId(token)="+userId.getUserId(token));
            }
            if (groupId != null) {
                log.debug("groupId.getType()="+groupId.getType());
                log.debug("groupId.getGroupId()="+groupId.getGroupId());
            }
            if (token != null) {
                log.debug("token="+token);
            }
        }

        ProfileManager pmgr = Factory.getSocialSite().getProfileManager();
        SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();

        RestfulCollection<Activity> collection = null;

        // get group handle from token if we can
        String groupHandle = null;
        if (token instanceof SocialSiteToken) {
            groupHandle = ((SocialSiteToken)token).getGroupHandle();
        }

        try {
            // TODO: more efficient way to do this query. Currently we get all
            // results, do a join in memory and then return a specified subset.
            // e.g. instead of loop use one query that specifies list of users?
             
            // Use a sorted set to sort activities by post time
            Set<Activity> allActivities = new TreeSet<Activity>(new Comparator<Activity>() {
                public int compare(Activity a1, Activity a2) {
                    int ret = 0;
                    if (a1.getPostedTime() != null && a2.getPostedTime() != null) {
                        ret = a2.getPostedTime().compareTo(a1.getPostedTime());
                    }
                    if (ret == 0) {
                        // posted at same time so compare arbitrarily by ID
                        ret = a2.getId().compareTo(a1.getId());
                    }
                    return ret;
                }
            });
            
            // Now for each user get the activities specified
            for (Iterator<UserId> uit = userIds.iterator(); uit.hasNext();) {
                UserId userId = uit.next();
                String uid = ((userId != null) ? userId.getUserId(token) : null);
                Profile profile = pmgr.getProfileByUserId(uid);
                switch (groupId.getType()) {
                    case all: {
                        break;
                    }
                    case friends: {
                        // Get activities of friends
                        RelationshipManager fmgr = Factory.getSocialSite().getRelationshipManager();
                        for (Relationship rel : fmgr.getRelationships(profile, 0, MAX_RETURNED)) {
                            Profile friend = rel.getProfileTo();
                            List<SocialSiteActivity> activities = 
                                amgr.getUserActivities(friend, opts.getFirst(), opts.getMax());
                            allActivities.addAll(makeShindigActivities(activities, token));
                        }
                        break;
                    }
                    case groupId: {
                        // Get activities for specified group
                        String gid = groupId.getGroupId();
                        gid = "@current".equals(gid) ? groupHandle : gid;
                        GroupManager gmgr = Factory.getSocialSite().getGroupManager();
                        Group group = gmgr.getGroupByHandle(gid);
                        allActivities.addAll(makeShindigActivities(
                            amgr.getActivitiesByGroup(group, 0, MAX_RETURNED), token));
                        break;
                    }
                    case self: {
                        // Get user's activities only
                        List<SocialSiteActivity> activities = 
                            amgr.getUserActivities(profile, 0, MAX_RETURNED);
                        allActivities.addAll(makeShindigActivities(activities, token));
                        break;
                    }
                }
            }

            // Now that we have all results, return the subset specified
            int totalResults = allActivities.size();
            List<Activity> results = new ArrayList<Activity>();
            Activity[] activitiesArray = allActivities.toArray(new Activity[0]);
            int start = opts.getFirst();
            int end = opts.getFirst() + opts.getMax() + 1;
            for (int i=start; i<end && i<activitiesArray.length ;i++) {
                results.add(activitiesArray[i]);
            }

            collection = new RestfulCollection<Activity>(results, opts.getFirst(), totalResults);

        } catch (SocialSiteException e) {
            log.debug("ERROR getting activities", e);
            throw new SocialSpiException(
                ResponseError.INTERNAL_ERROR, "Problem getting activities", e);
        }

        return collection;
    }


    //--------------------------------------------------------------------------
        
    /**
     * Creates the passed in activity for the passed in user and group. Once createActivity is
     * called, getActivities will be able to return the Activity.
     *
     * @param userId   The id of the person to create the activity for.
     * @param groupId  The group.
     * @param appId    The app id.
     * @param fields   The fields to return.
     * @param activity The activity to create.
     * @param token    A valid SecurityToken
     * @return a response item containing any errors
     */
    private Void createActivityInternal(UserId userId, GroupId groupId, String appId,
            Set<String> fields, Activity activity, SecurityToken token) throws SocialSpiException {

        String uid = userId.getUserId(token);
        log.trace(String.format("createActivity(%s, %s)", uid, activity));

        try {
            SocialSiteActivityManager amgr =
                    Factory.getSocialSite().getSocialSiteActivityManager();
            ProfileManager profileManager =
                    Factory.getSocialSite().getProfileManager();

            Profile profile = profileManager.getProfileByUserId(uid);

            activity.setPostedTime(new Date().getTime());
            SocialSiteActivity socialSiteActivity = new SocialSiteActivity();
            socialSiteActivity.setAppId(appId);
            if(activity.getBodyId() != null) {
                socialSiteActivity.setBodyId(activity.getBodyId());
            } else {
                socialSiteActivity.setBody(activity.getBody());
            }
            socialSiteActivity.setExternalId(activity.getExternalId());
            socialSiteActivity.setCreated(new Date(activity.getPostedTime()));
            if(activity.getTitleId() != null) {
                socialSiteActivity.setTitleId(activity.getTitleId());
            } else {
                socialSiteActivity.setTitle(activity.getTitle());
            }
            socialSiteActivity.setUpdated(new Date());
            socialSiteActivity.setProfile(profile);
            socialSiteActivity.setType(SocialSiteActivity.APP_MESSAGE);
            if(activity.getMediaItems() != null) {
                for(MediaItem m : activity.getMediaItems()) {
                    SocialSiteMediaItem ssm = new SocialSiteMediaItem();
                    ssm.setMimetype(m.getMimeType());
                    ssm.setType(m.getType().toString());
                    ssm.setUrl(m.getUrl());
                    socialSiteActivity.addMediaItems(ssm);
                }
            }
            if(activity.getTemplateParams() != null) {
                for(String names : activity.getTemplateParams().keySet()) {
                    TemplateParameter tmp = new TemplateParameter();
                    tmp.setName(names);
                    tmp.setValue(activity.getTemplateParams().get(names));
                    socialSiteActivity.addTemplateParameters(tmp);
                }                
            }
            amgr.saveActivity(socialSiteActivity);
            Factory.getSocialSite().flush();

        } catch (SocialSiteException e) {
            log.debug("ERROR creating activity", e);
            throw new SocialSpiException(
                ResponseError.INTERNAL_ERROR, "ERROR creating activity", e);
        }
        
        return null;
    }

    
    //--------------------------------------------------------------------------
        
    /**
     * Returns the activity for the passed in user and group that corresponds to
     * the activityId.
     *
     * @param userId     The id of the person to fetch activities for.
     * @param groupId    Indicates whether to fetch activities for a group.
     * @param appId      The app id.
     * @param fields     The fields to return.
     * @param activityId The id of the activity to fetch.
     * @param token      A valid SecurityToken
     * @return a response item with the list of activities.
     */
    private Activity getActivityInternal(UserId userId, GroupId groupId, String appId,
            Set<String> fields, String activityId, SecurityToken token) throws SocialSpiException {

        String uid = userId.getUserId(token);
        log.trace(String.format("getActivity(%s, %s)", uid, activityId));

        try {
            SocialSiteActivityManager amgr =
                    Factory.getSocialSite().getSocialSiteActivityManager();
            SocialSiteActivity activity = amgr.getActivity(activityId);
            if (activity == null) {
                throw new SocialSpiException(
                    ResponseError.BAD_REQUEST, "No such activity: " + activityId);
            }
            if (!uid.equals(activity.getProfile().getUserId())) {
                throw new SocialSpiException(
                    ResponseError.BAD_REQUEST,
                    String.format("Activity(%s) does not belong to User(%s): ", activity.getId(), uid));
            }
            return makeShindigActivity(activity);

        } catch (SocialSiteException e) {
            log.debug("ERROR deleting activities", e);
            throw new SocialSpiException(
                ResponseError.INTERNAL_ERROR, "Problem deleting activities", e);
        }

    }


    //--------------------------------------------------------------------------
        
    /**
     * Deletes the activity for the passed in user and group that corresponds to
     * the activityId.
     *
     * @param userId     The user.
     * @param groupId    The group.
     * @param appId      The app id.
     * @param activityId The id of the activity to delete.
     * @param token      A valid SecurityToken.
     * @return a response item containing any errors
     */
    private Void deleteActivityInternal(UserId userId, GroupId groupId, 
        String appId, Set<String> activityIds, SecurityToken token)  throws SocialSpiException {

        String uid = userId.getUserId(token);
        log.trace(String.format("deleteActivity(%s, %s)", uid, activityIds.toString()));
        
        try {
            SocialSiteActivityManager amgr = Factory.getSocialSite().getSocialSiteActivityManager();
            String viewerId = token.getViewerId();
            for (Iterator<String> it = activityIds.iterator(); it.hasNext();) {
                String aid = it.next();
                SocialSiteActivity acontent = amgr.getActivity(aid);
                if (acontent == null) {
                    throw new SocialSpiException(
                        ResponseError.BAD_REQUEST, "No such activity: " + aid);
                }

                // Ensure that only owning user can delete his own activities
                Profile ownerProfile = acontent.getProfile();
                if (ownerProfile.getUserId().equals(token.getViewerId())) {
                    amgr.removeActivity(acontent);
                } else {
                    throw new SocialSpiException(
                        ResponseError.BAD_REQUEST,
                        String.format("Activity(%s) does not belong to User(%s): ", 
                            aid, ownerProfile.getUserId()));                
                }
            }
            Factory.getSocialSite().flush();
            
        } catch (SocialSiteException e) {
            log.debug("ERROR deleting activities", e);
            throw new SocialSpiException(
                ResponseError.INTERNAL_ERROR, "Problem deleting activities", e);
        }
        
        return null;
    }
    
    
    //--------------------------------------------------------------------------
        
    private List<Activity> makeShindigActivities(
            List<SocialSiteActivity> socialsiteActivities,
            SecurityToken token) throws SocialSiteException {

        List<Activity> shindigActivities = new ArrayList<Activity>(socialsiteActivities.size());
        for (SocialSiteActivity socialsiteActivity : socialsiteActivities) {
            shindigActivities.add(makeShindigActivity(socialsiteActivity));
        }
        return shindigActivities;
    }
    

    //--------------------------------------------------------------------------
        
    private Activity makeShindigActivity(
            SocialSiteActivity ssa) throws SocialSiteException {
        
        Activity activity = new ActivityImpl(
            ssa.getId(), ssa.getProfile().getUserId());
        
        activity.setAppId(ssa.getAppId());
        if(ssa.getTitleId()!=null)
            activity.setTitleId(ssa.getTitleId());
        else
            activity.setTitle(ssa.getTitle()+
            " ("+DateUtil.format(ssa.getUpdated(), DateUtil.roundedRelativeFormat())+")");
        if(ssa.getBodyId()!=null)
            activity.setBodyId(ssa.getBodyId());
        else
            activity.setBody(ssa.getBody()+
            " ("+DateUtil.format(ssa.getUpdated(), DateUtil.roundedRelativeFormat())+")");
        activity.setExternalId(ssa.getExternalId());
        activity.setUpdated(ssa.getUpdated());
        if (ssa.getCreated() != null) {
            activity.setPostedTime(ssa.getCreated().getTime());
        } else if (ssa.getUpdated() != null) {
            activity.setPostedTime(ssa.getUpdated().getTime());
        }
        if(ssa.getMediaItems() != null) {
            ArrayList<MediaItem> items = 
                    new ArrayList<MediaItem>(ssa.getMediaItems().size());
            for(SocialSiteMediaItem m : ssa.getMediaItems()) {
                MediaItemImpl thisItem = new MediaItemImpl(
                        m.getMimetype(),
                        MediaItem.Type.valueOf(m.getType()),
                        m.getUrl());
                items.add(thisItem);
            }
            activity.setMediaItems(items);
        }
        if(ssa.getTemplateParameters() != null) {
            HashMap<String, String> params = 
                    new HashMap<String, String>(ssa.getTemplateParameters().size());
            for(String tp : ssa.getTemplateParameters().keySet()) {
                TemplateParameter thisParam = ssa.getATemplateParameter(tp);
                params.put(thisParam.getName(), thisParam.getValue());
            }
            activity.setTemplateParams(params);
        }
        return activity;
    }

}
