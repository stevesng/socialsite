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

package com.sun.socialsite.web.rest.opensocial;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.ProfileManager;
import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.util.JSONWrapper;
import java.util.List;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.social.opensocial.model.ListField;
import org.apache.shindig.social.opensocial.model.Name;
import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Handles automatic creation of profiles.  When enabled, profiles are automatically
 * created the first time they're referenced in a valid <code>ConsumerContext</code>.
 */
public class ProfileCreator implements ConsumerContextHandler {

    private static Log log = LogFactory.getLog(ProfileCreator.class);

    /** Name of the RuntimeConfig property the enables/disables profile autocreation. */
    private static final String PROPNAME_ENABLED = "socialsite.profile.autocreation.enabled";

    /** The JSON<->Bean converter we'll use. */
    private BeanJsonConverter jsonConverter;


    /**
     * TODO: Document
     */
    @Inject
    public ProfileCreator(@Named("shindig.bean.converter.json") BeanConverter jsonConverter) {
        this.jsonConverter = (BeanJsonConverter) jsonConverter;
    }


    /**
     * TODO: Document
     */
    public void handleContext(ConsumerContext context) throws SocialSiteException {

        boolean autoCreationEnabled = RuntimeConfig.getBooleanProperty(PROPNAME_ENABLED);
        if (autoCreationEnabled == false) {
            log.debug("Returning without action, since autocreation is disabled");
            return;
        }

        try {

            JSONWrapper json = new JSONWrapper(context.getTrustedJSON());
            log.debug("context.json="+json);

            if (json.has("assertions.viewer")) {
                JSONObject viewerJson = json.getJSONObject("assertions.viewer");
                createProfile(viewerJson);
            }

            if (json.has("assertions.owner")) {
                JSONObject ownerJson = json.getJSONObject("assertions.owner");
                createProfile(ownerJson);
            }

        } catch (JSONException e) {
            throw new SocialSiteException(e);
        }

    }


    protected void createProfile(JSONObject json) throws SocialSiteException {

        Person person = jsonConverter.convertToObject(json.toString(), Person.class);
        ProfileManager profileManager = Factory.getSocialSite().getProfileManager();
        Profile profile = profileManager.getProfileByUserId(person.getId());

        if (profile != null) {

            log.debug(String.format("Person (%s) -> Existing Profile (%s)", person.getId(), profile.getUserId()));

        } else if (StringUtils.isNotEmpty(person.getId())) {

            profile = new Profile();
            profile.setUserId(person.getId());

            // Update the fields which reside directly in the profile object.
            // TODO: should this really be necessary?
            // Or should profile.update(...) do this automatically?
            Name name = person.getName();
            if (name != null) {
                if (StringUtils.isNotEmpty(name.getGivenName())) profile.setFirstName(name.getGivenName());
                if (StringUtils.isNotEmpty(name.getFamilyName())) profile.setLastName(name.getFamilyName());
                if (StringUtils.isNotEmpty(name.getFormatted())) profile.setDisplayName(name.getFormatted());
            }
            List<ListField> emails = person.getEmails();
            if (emails != null) {
                for (ListField email : emails) {
                    if (BooleanUtils.isTrue(email.getPrimary())) {
                        profile.setPrimaryEmail(email.getValue());
                    } else {
                        String msg = String.format("TODO: handle ListValue[primary=%s,value=%s", email.getPrimary(), email.getValue());
                        log.debug(msg);
                    }
                }
                if (profile.getPrimaryEmail() == null) {
                    // We never found an explicit primary email, so just choose one arbitrarily
                    profile.setPrimaryEmail(emails.get(0).getValue());
                }
            }

            profileManager.saveProfile(profile, false);
            Factory.getSocialSite().flush();

            // Update fields which reside in ProfileProperty objects
            profile.update(Profile.Format.OPENSOCIAL, json);
            Factory.getSocialSite().flush();
            log.debug("newProfile="+profile);

        } else {

            log.debug("Cannot auto-create a profile for a person with no ID");

        }

    }

}
