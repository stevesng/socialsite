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

package com.sun.socialsite.business.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.business.AbstractManagerImpl;
import com.sun.socialsite.business.ContentManager;
import com.sun.socialsite.pojos.Content;
import com.sun.socialsite.pojos.Profile;
import java.util.List;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JPA implementation content manager.
 */
@Singleton
public class JPAContentManagerImpl extends AbstractManagerImpl implements ContentManager {

    private static Log log = LogFactory.getLog(JPAContentManagerImpl.class);

    private final JPAPersistenceStrategy strategy;

    @Inject
    protected JPAContentManagerImpl(JPAPersistenceStrategy strat) {
        log.debug("Instantiating JPA Content Manager");
        this.strategy = strat;
    }

    public void recordContent(Profile profile, String appId, String title,
            String catscheme, String catlabel,
            String contentType, String content) throws SocialSiteException {
        Content c = new Content();
        c.setProfile(profile);
        c.setAppId(appId);
        c.setTitle(title);
        c.setCatScheme(catscheme);
        c.setCatLabel(catlabel);
        c.setContentType(contentType);
        c.setContent(content);
        saveContent(c);
    }

    /**
     * For string content.
     */
    public void recordContent(Profile profile, String appId, String title,
            String catscheme, String catlabel, String content)
            throws SocialSiteException {
        Content c = new Content();
        c.setProfile(profile);
        c.setAppId(appId);
        c.setTitle(title);
        c.setCatScheme(catscheme);
        c.setCatLabel(catlabel);
        c.setContentType("text/plain");
        c.setContent(content);
        saveContent(c);
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Content> getUserContent(Profile profile, int offset, int length) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }

        Query query = strategy.getNamedQuery("Content.getByUserName");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        return (List<Content>) query.getResultList();
    }

    public List<Content> getUserAndFriendsContentPostings(Profile profile,
            int offset, int length) throws SocialSiteException {
        return null;
    }

    public void saveContent(Content content) throws SocialSiteException {
        strategy.store(content);
    }

    /** Remove content */
    public void removeContent(Content content) throws SocialSiteException {
        strategy.remove(content);
    }

    public Content getContent(String id) throws SocialSiteException {
        return (Content) strategy.load(Content.class, id);
    }

    /**
     * Get content by category and tag.
     *
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<Content> getContentByCategory(Profile profile, String catscheme,
            String tag, int offset, int length) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }

        if (catscheme == null) {
            throw new SocialSiteException("category scheme is null");
        }

        Query query = strategy.getNamedQuery("Content.getByProfileandCatscheme");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        query.setParameter(2, catscheme);
        return (List<Content>) query.getResultList();
    }

    public void release() {
    }

}
