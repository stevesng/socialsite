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
import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.PermissionManager;
import com.sun.socialsite.pojos.App;
import com.sun.socialsite.pojos.PermissionGrant;
import com.sun.socialsite.pojos.Profile;
import com.sun.socialsite.security.FeaturePermission;
import com.sun.socialsite.web.rest.opensocial.SocialSiteToken;
import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;


/**
 * JPA implementation of PermissionManager.
 */
@Singleton
public class JPAPermissionManagerImpl extends AbstractManagerImpl implements PermissionManager {

    private static Log log = LogFactory.getLog(JPAPermissionManagerImpl.class);

    private final JPAPersistenceStrategy strategy;


    @Inject
    protected JPAPermissionManagerImpl(JPAPersistenceStrategy strategy) {
        log.debug("Instantiating JPA Policy Manager");
        this.strategy = strategy;
    }


    public void release() {
    }


    /**
     * {@inheritDoc}
     */
    public void checkPermission(Permission requiredPermission, SecurityToken token) throws SocialSiteException {
        Permissions grantedPermissions = getPermissions(token);
        log.debug("requiredPermission="+requiredPermission);
        log.debug("grantedPermissions="+grantedPermissions);
        if (grantedPermissions.implies(requiredPermission) == false) {
            throw new SecurityException("Access Denied");
        }
    }


    private Permissions getPermissions(SecurityToken token) throws SocialSiteException {

        try {

            Permissions permissions = new Permissions();
            List<PermissionGrant> permissionGrants = new ArrayList<PermissionGrant>();

            if (token != null) {
                if (token instanceof SocialSiteToken && ((SocialSiteToken)token).isForContainerPage()) {
                    permissions.add(new FeaturePermission("*"));
                }
                if (token.getAppId() != null) {
                    App app = Factory.getSocialSite().getAppManager().getApp(token.getAppId());
                    permissionGrants.addAll(getPermissionGrants(app, 0, -1));
                    permissionGrants.addAll(getPermissionGrants(app.getURL().getHost(), 0, -1));
                }
                if (token.getViewerId() != null) {
                    Profile viewer = Factory.getSocialSite().getProfileManager().getProfileByUserId(token.getViewerId());
                    permissionGrants.addAll(getPermissionGrants(viewer, 0, -1));
                }
            }

            for (PermissionGrant permissionGrant : permissionGrants) {
                String type = permissionGrant.getType();
                String name = permissionGrant.getName();
                String actions = permissionGrant.getActions();
                try {
                    Class<?> clazz = Class.forName(type);
                    Permission permission = null;
                    if (actions == null) {
                        Constructor constructor = clazz.getConstructor(String.class);
                        permission = (Permission)(constructor.newInstance(name));
                    } else {
                        Constructor constructor = clazz.getConstructor(String.class, String.class);
                        permission = (Permission)(constructor.newInstance(name, actions));
                    }
                    permissions.add(permission);
                } catch (Exception e) {
                    String msg = String.format("Failed to construct Permission(type=%s,name=%s,actions=%s)", type, name, actions);
                    log.error(msg, e);
                }
            }

            return permissions;

        } catch (Exception e) {
            log.error("token="+token);
            throw (SocialSiteException)((e instanceof SocialSiteException) ? e : new SocialSiteException(e));
        }

    }


    /**
     * {@inheritDoc}
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public void savePermissionGrant(PermissionGrant permissionGrant) throws SocialSiteException {

        List<String> clauses = new ArrayList<String>();
        Map<String, Object> parameters = new HashMap<String, Object>();

        if (permissionGrant.getName() != null) {
            clauses.add("pg.name=:name");
            parameters.put("name", permissionGrant.getName());
        } else {
            clauses.add("pg.name IS NULL");
        }

        if (permissionGrant.getActions() != null) {
            clauses.add("pg.actions=:actions");
            parameters.put("actions", permissionGrant.getActions());
        } else {
            clauses.add("pg.actions IS NULL");
        }

        if (permissionGrant.getApp() != null) {
            clauses.add("pg.app=:app");
            parameters.put("app", permissionGrant.getApp());
        } else {
            clauses.add("pg.app IS NULL");
        }

        if (permissionGrant.getGadgetDomain() != null) {
            clauses.add("pg.gadgetDomain=:gadgetDomain");
            parameters.put("gadgetDomain", permissionGrant.getGadgetDomain());
        } else {
            clauses.add("pg.gadgetDomain IS NULL");
        }

        if (permissionGrant.getProfileId() != null) {
            clauses.add("pg.profileId=:profileId");
            parameters.put("profileId", permissionGrant.getProfileId());
        } else {
            clauses.add("pg.profileId IS NULL");
        }

        if (permissionGrant.getGroupId() != null) {
            clauses.add("pg.groupId=:groupId");
            parameters.put("groupId", permissionGrant.getGroupId());
        } else {
            clauses.add("pg.groupId IS NULL");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT pg FROM PermissionGrant pg");
        sb.append(" WHERE ").append(clauses.get(0));
        for (String clause : clauses.subList(1, clauses.size())) {
            sb.append(" AND ").append(clause);
        }

        Query query = strategy.getDynamicQuery(sb.toString());
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        List<PermissionGrant> results = query.getResultList();
        if (results.size() == 0) {
            strategy.store(permissionGrant);
        } else {
            if (log.isDebugEnabled()) {
                String msg = String.format("Ignoring redundant call (permissionGrant=%s)", permissionGrant);
                log.debug(msg);
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void removePermissionGrant(PermissionGrant permissionGrant) throws SocialSiteException {
        strategy.remove(permissionGrant);
    }


    /**
     * {@inheritDoc}
     */
    public PermissionGrant getPermissionGrant(String id) throws SocialSiteException {
        if (id == null) {
            throw new SocialSiteException("id is null");
        }
        Query query = strategy.getNamedQuery("PermissionGrant.getById");
        query.setParameter(1, id);
        try {
            return (PermissionGrant)query.getSingleResult();
        } catch (NonUniqueResultException ne) {
            throw new SocialSiteException("ERROR: more than one PermissionGrant with id: " + id, ne);
        } catch (NoResultException ex) {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<PermissionGrant> getPermissionGrants(int offset, int length) throws SocialSiteException {
        Query query = strategy.getNamedQuery("PermissionGrant.getAll");
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<PermissionGrant>)query.getResultList();
    }


    /**
     * {@inheritDoc}
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<PermissionGrant> getPermissionGrants(App app, int offset, int length) throws SocialSiteException {
        log.debug("app="+app);
        Query query = strategy.getNamedQuery("PermissionGrant.getByApp");
        query.setParameter(1, app);
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<PermissionGrant>)query.getResultList();
    }


    /**
     * {@inheritDoc}
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<PermissionGrant> getPermissionGrants(String gadgetDomain, int offset, int length) throws SocialSiteException {
        log.debug("gadgetDomain="+gadgetDomain);
        Query query = strategy.getNamedQuery("PermissionGrant.getByGadgetDomain");
        query.setParameter(1, gadgetDomain);
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<PermissionGrant>)query.getResultList();
    }


    /**
     * {@inheritDoc}
     * Note: using SuppressWarnings annotation because the JPA API is not genericized.
     */
    @SuppressWarnings(value="unchecked")
    public List<PermissionGrant> getPermissionGrants(Profile profile, int offset, int length) throws SocialSiteException {
        log.debug("profile="+profile);
        Query query = strategy.getNamedQuery("PermissionGrant.getByProfileId");
        query.setParameter(1, profile.getId());
        if (offset != 0) query.setFirstResult(offset);
        if (length != -1) query.setMaxResults(length);
        return (List<PermissionGrant>)query.getResultList();
    }

}
