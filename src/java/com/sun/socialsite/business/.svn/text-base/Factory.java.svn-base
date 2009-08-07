/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.business;

import com.sun.socialsite.business.startup.Startup;
import com.sun.socialsite.config.Config;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides access to the SocialSite instance.
 */
public abstract class Factory {

    private static Log log = LogFactory.getLog(Factory.class);

    // Guice injector
    private static Injector injector;

    // maintain our own singleton instance of SocialSite
    private static SocialSite socialSite;

    // allow the default socialSite to be overridden for individual threads
    private static ThreadLocal<SocialSite> threadLocalSocialSite = new ThreadLocal<SocialSite>();


    // non-instantiable
    private Factory() {
    }


    /**
     * Static accessor for the instance of SocialSite
     */
    public static SocialSite getSocialSite() {
        if (socialSite == null) {
            throw new IllegalStateException("SocialSite has not been bootstrapped yet");
        }
        return ((threadLocalSocialSite.get() != null) ? threadLocalSocialSite.get() : socialSite);
    }


    public static SocialSite getThreadLocalSocialSite() {
      return threadLocalSocialSite.get();
    }


    public static void setThreadLocalSocialSite(SocialSite ss) {
      threadLocalSocialSite.set(ss);
    }


    /**
     * True if bootstrap process was completed, False otherwise.
     */
    public static boolean isBootstrapped() {
        return (socialSite != null);
    }


    /**
     * Bootstrap the SocialSite business tier.
     *
     * Bootstrapping the application effectively instantiates all the necessary
     * pieces of the business tier and wires them together so that the app is
     * ready to run.
     *
     * @throws IllegalStateException If the app has not been properly prepared yet.
     * @throws BootstrapException If an error happens during the bootstrap process.
     */
    public static final void bootstrap() throws BootstrapException {

        // if the app hasn't been properly started so far then bail
        if (!Startup.isPrepared()) {
            throw new IllegalStateException("Cannot bootstrap until application has been properly prepared");
        }

        String moduleClassname = Config.getProperty("guice.backend.module");
        if (moduleClassname == null) {
            throw new NullPointerException("unable to lookup default guice module via property 'guice.backend.module'");
        }

        try {
            Class moduleClass = Class.forName(moduleClassname);
            Module module = (Module)moduleClass.newInstance();
            injector = Guice.createInjector(module);

        } catch (Throwable e) {
            // Fatal misconfiguration, cannot recover
            throw new RuntimeException("Error instantiating backend module " + moduleClassname, e);
        }

        socialSite = injector.getInstance(SocialSite.class);

        // make sure we are all set
        if (socialSite == null) {
            throw new BootstrapException("Bootstrapping failed, SocialSite instance is null");
        }

        log.info("SocialSite business tier successfully bootstrapped");
    }


    public static final void shutdown() {
        getSocialSite().shutdown();
        socialSite = null;
    }

}
