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
import com.sun.socialsite.business.ContextRuleManager;
import com.sun.socialsite.business.InitializationException;
import com.sun.socialsite.pojos.ContextRule;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JPA implementation of ContextRuleManager.
 */
@Singleton
public class JPAContextRuleManagerImpl extends AbstractManagerImpl
    implements ContextRuleManager {

    private static Log log = LogFactory.getLog(JPAContextRuleManagerImpl.class);
    private final JPAPersistenceStrategy strategy;

    @Inject
    protected JPAContextRuleManagerImpl(JPAPersistenceStrategy strat) {
        log.debug("Instantiating JPA ContextRule Manager");
        this.strategy = strat;
    }

    public ContextRule getRuleById(String id) throws SocialSiteException {
        if (id == null) {
            throw new SocialSiteException("userid is null");
        }
        Query query = strategy.getNamedQuery("ContextRule.getById");
        query.setParameter(1, id);
        try {
            return (ContextRule) query.getSingleResult();
        } catch (NonUniqueResultException ne) {
            throw new SocialSiteException(String.format(
                "ERROR: more than one rule with id: %s", id));
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Note: using SuppressWarnings annotation because the JPA API is
     * not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<ContextRule> getRules() throws SocialSiteException {
        Query query = strategy.getNamedQuery("ContextRule.getAll");
        return (List<ContextRule>) query.getResultList();
    }

    public void removeRule(ContextRule rule) throws SocialSiteException {
        strategy.remove(rule);
    }

    public void saveRule(ContextRule rule) throws SocialSiteException {
        strategy.store(rule);
    }

}
