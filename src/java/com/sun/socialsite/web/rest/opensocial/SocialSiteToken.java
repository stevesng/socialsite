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

import com.sun.socialsite.business.Factory;
import com.sun.socialsite.business.URLStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.SecurityToken;


public abstract class SocialSiteToken implements SecurityToken {

    private static Log log = LogFactory.getLog(SocialSiteToken.class);

    private String stringToken;
    private String domain;

    private URLStrategy urlStrategy = Factory.getSocialSite().getURLStrategy();


    protected SocialSiteToken(String stringToken) {
        this(stringToken, "socialsite");
    }

    protected SocialSiteToken(String stringToken, String containerId) {
        if (stringToken == null) {
            throw new IllegalArgumentException("SocialSiteToken cannot have a null serialization");
        }
        this.stringToken = stringToken;
        this.domain = containerId;
        SocialSiteTokenDecoder.addToken(this);
    }

    /**
     * {@inheritDoc}
     */
    public String toSerialForm() {
        return stringToken;
    }

    /**
     * {@inheritDoc}
     */
    public abstract String getAppId();

    /**
     * {@inheritDoc}
     */
    public String getDomain() {
        return (domain == null) ? "socialsite" : domain;
    }

    /**
     * {@inheritDoc}
     */
    public abstract String getOwnerId();

    /**
     * {@inheritDoc}
     */
    public abstract String getViewerId();

    /**
     *
     */
    public abstract String getGroupHandle();

    /**
     *
     */
    public abstract boolean hasPermission(String permissionName);

    /**
     * {@inheritDoc}
     */
    public abstract long getModuleId();

    /**
     *
     */
    public abstract boolean isForContainerPage();

    /**
     * Set the URLStrategy which will be used in portions of our
     * OpenSocial implementation which rely on this token.
     *
     * TODO: generalize this (in a more Guice-aligned manner?)
     */
    public final void setURLStrategy(URLStrategy urlStrategy) {
        this.urlStrategy = urlStrategy;
    }

    /**
     * Get the URLStrategy which will be used in portions of our
     * OpenSocial implementation which rely on this token.
     *
     * TODO: generalize this (in a more Guice-aligned manner?)
     */
    public final URLStrategy getURLStrategy() {
        return urlStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s[serialForm=%s, ownerId=%s, groupHandle=%s, viewerId=%s]", getClass().getSimpleName(), toSerialForm(), getOwnerId(), getGroupHandle(), getViewerId());
    }

}
