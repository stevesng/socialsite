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
import com.sun.socialsite.business.MessageContentManager;
import com.sun.socialsite.pojos.MessageContent;
import com.sun.socialsite.pojos.Profile;
import java.util.List;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * JPA implementation Message manager.
 */
@Singleton
public class JPAMessageContentManagerImpl extends JPAContentManagerImpl implements MessageContentManager {

    private static Log log = LogFactory.getLog(JPAMessageContentManagerImpl.class);
    private final JPAPersistenceStrategy strategy;

    @Inject
    protected JPAMessageContentManagerImpl(JPAPersistenceStrategy strat) {
        super(strat);
        log.debug("Instantiating JPA Message Manager");
        this.strategy = strat;
    }

    public void recordMessage(Profile profile, String descType,
            String title) throws SocialSiteException {
        MessageContent a = new MessageContent();
        a.setProfile(profile);
        a.setDescType(descType);
        a.setTitle(title);
        saveMessage(a);
    }

    public void recordMessage(Profile profile, String descType, String title, String catlabel) throws SocialSiteException {
        MessageContent a = new MessageContent();
        a.setProfile(profile);
        a.setDescType(descType);
        a.setTitle(title);
        a.setCatLabel(catlabel);
        saveMessage(a);
    }

    public void saveMessage(MessageContent message) throws SocialSiteException {
        strategy.store(message);
    }

    public void removeMessage(MessageContent message) throws SocialSiteException {
        strategy.remove(message);
    }

    public MessageContent getMessage(String id) throws SocialSiteException {
        return (MessageContent) strategy.load(MessageContent.class, id);
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<MessageContent> getUserMessages(Profile profile, int offset, int length) throws SocialSiteException {
        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("MessageContent.getByProfile");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        return (List<MessageContent>) query.getResultList();
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<MessageContent> getUserMessages(Profile profile, String catlabel,
            int offset, int length) throws SocialSiteException {

        if (profile == null) {
            throw new SocialSiteException("profile is null");
        }
        Query query = strategy.getNamedQuery("MessageContent.getByProfileAndLabel");
        if (offset != 0) {
            query.setFirstResult(offset);
        }
        if (length != -1) {
            query.setMaxResults(length);
        }
        query.setParameter(1, profile);
        query.setParameter(2, catlabel);
        return (List<MessageContent>) query.getResultList();

    }

    public void release() {
    }

}
