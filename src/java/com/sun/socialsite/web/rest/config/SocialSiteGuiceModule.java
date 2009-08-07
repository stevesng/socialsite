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
package com.sun.socialsite.web.rest.config;

import com.google.common.collect.ImmutableSet;
import com.sun.socialsite.web.rest.opensocial.*;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.sun.socialsite.config.Config;
import com.sun.socialsite.util.DebugBeanJsonConverter;
import com.sun.socialsite.util.PropertyExpander;
import com.sun.socialsite.web.rest.core.AppRegistrationHandler;
import com.sun.socialsite.web.rest.core.GadgetHandler;
import com.sun.socialsite.web.rest.opensocial.service.AppDataServiceImpl;
import com.sun.socialsite.web.rest.core.ProfileDefinitionHandler;
import com.sun.socialsite.web.rest.core.ProfileHandler;
import com.sun.socialsite.web.rest.core.PropertiesHandler;
import com.sun.socialsite.web.rest.core.GroupDefinitionHandler;
import com.sun.socialsite.web.rest.core.GroupMemberHandler;
import com.sun.socialsite.web.rest.core.GroupProfilesHandler;
import com.sun.socialsite.web.rest.core.GroupsHandler;
import com.sun.socialsite.web.rest.core.MessageHandler;
import com.sun.socialsite.web.rest.core.SectionPrivacyHandler;
import com.sun.socialsite.web.rest.opensocial.service.PersonServiceImpl;
import com.sun.socialsite.web.rest.core.PersonHandlerImpl;
import com.sun.socialsite.web.rest.core.SearchHandler;
import com.sun.socialsite.web.rest.model.PersonEx;
import com.sun.socialsite.web.rest.opensocial.oauth.SocialSiteOAuthDataStore;
import com.sun.socialsite.web.rest.opensocial.service.ActivityServiceImpl;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.auth.AnonymousAuthenticationHandler;
import org.apache.shindig.auth.AuthenticationHandler;
import org.apache.shindig.auth.SecurityTokenDecoder;
import org.apache.shindig.common.servlet.ParameterFetcher;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.protocol.DataServiceServletFetcher;
import org.apache.shindig.protocol.DefaultHandlerRegistry;
import org.apache.shindig.protocol.HandlerRegistry;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanXStreamConverter;
import org.apache.shindig.protocol.conversion.xstream.XStreamConfiguration;
import org.apache.shindig.social.core.oauth.AuthenticationHandlerProvider;
import org.apache.shindig.social.core.util.BeanXStreamAtomConverter;
import org.apache.shindig.social.core.util.xstream.XStream081Configuration;
import org.apache.shindig.social.opensocial.model.Person;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.service.ActivityHandler;
import org.apache.shindig.social.opensocial.service.AppDataHandler;
import org.apache.shindig.social.opensocial.service.PersonHandler;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.PersonService;

/**
 * Hook SocialSite properties, configuration and REST API implemetation into Shindig.
 */
public class SocialSiteGuiceModule extends AbstractModule {

    private static Log log = LogFactory.getLog(SocialSiteGuiceModule.class);

    private final Properties properties;


    /**
     * Creates module with standard properties.
     */
    public SocialSiteGuiceModule() {

        // Read properties from standard SocialSite configuration file
        Properties shindigProperties = 
            Config.getPropertiesStartingWith("shindig."); 
        properties = PropertyExpander.expandProperties(
            shindigProperties, Config.toMap(), null);

        if (log.isDebugEnabled()) {
            for (Object key : this.properties.keySet()) {
                log.debug(String.format("%s[%s]=%s", 
                   "socialsite.properties", key, this.properties.get(key)));
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {

        //--- bind standard Shindig Social API classes

        bind(HandlerRegistry.class).to(DefaultHandlerRegistry.class);

        bind(ParameterFetcher.class).annotatedWith(Names.named("DataServiceServlet"))
            .to(DataServiceServletFetcher.class);

        bind(String.class).annotatedWith(Names.named("shindig.canonical.json.db"))
            .toInstance("sampledata/canonicaldb.json");

        bind(Boolean.class)
            .annotatedWith(Names.named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
            .toInstance(Boolean.TRUE);
        bind(XStreamConfiguration.class).to(XStream081Configuration.class);
        bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.xml")).to(
            BeanXStreamConverter.class);
        bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.json")).to(
            DebugBeanJsonConverter.class);
        bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.atom")).to(
            BeanXStreamAtomConverter.class);

        bind(new TypeLiteral<List<AuthenticationHandler>>(){}).toProvider(
            AuthenticationHandlerProvider.class);

        // Added SocialSite handlers here:
        bind(new TypeLiteral<Set<Object>>(){}).annotatedWith(Names.named("org.apache.shindig.handlers"))
            .toInstance(ImmutableSet.<Object>of(

                // standard Shindig handlers
                ActivityHandler.class,
                AppDataHandler.class,
                //PersonHandler.class, // SocialSite extend this one

                // SocialSite handlers
                AppRegistrationHandler.class,
                GadgetHandler.class,
                GroupDefinitionHandler.class,
                GroupMemberHandler.class,
                GroupsHandler.class,
                GroupProfilesHandler.class,
                MessageHandler.class,
                PersonHandlerImpl.class,
                ProfileDefinitionHandler.class,
                ProfileHandler.class,
                PropertiesHandler.class,
                SectionPrivacyHandler.class,
                SearchHandler.class,
                SectionPrivacyHandler.class
            )
        );
        
        //--- bind SocialSite classes    

        // hook in our properties and expanded configuration
        Names.bindProperties(this.binder(), properties);
        bind(ContainerConfig.class).to(ExpandingContainerConfig.class).in(Scopes.SINGLETON);

        // hook in our token infrastructure
        bind(SecurityTokenDecoder.class).to(SocialSiteTokenDecoder.class);

        // hook in our proxy and concat URLs via content rewriter
        //bind(ContentRewriter.class).to(SocialSiteContentRewriter.class);

        // hook in our service implementations    
        bind(AppDataService.class).to(AppDataServiceImpl.class);
        bind(ActivityService.class).to(ActivityServiceImpl.class);
        bind(PersonService.class).to(PersonServiceImpl.class);

        // hook in some of our extensions via the standard person handler
        bind(PersonHandler.class).to(PersonHandlerImpl.class);

        // hook in our extended person object
        bind(Person.class).to(PersonEx.class);

        // hook in our OAuth data store
        bind(OAuthDataStore.class).to(SocialSiteOAuthDataStore.class);

        // hook in our MakeRequestHandler
        //bind(MakeRequestHandler.class).to(RestrictedMakeRequestHandler.class);
    }
}
