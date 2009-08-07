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

package com.sun.socialsite.business.startup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.sun.socialsite.business.DatabaseProvider;
import com.sun.socialsite.business.MailProvider;


/**
 * Manages the SocialSite startup process.
 */
public final class Startup {

    private static final Log log = LogFactory.getLog(Startup.class);

    private static boolean prepared = false;

    private static DatabaseProvider dbProvider = null;
    private static StartupException dbProviderException = null;
    private static MailProvider mailProvider = null;


    /**
     * This class is non-instantiable.
     */
    private Startup() {
    }


    /**
     * Is the SocialSite app properly prepared to be bootstrapped?
     */
    public static boolean isPrepared() {
        return prepared;
    }


    /**
     * Get a reference to the currently configured DatabaseProvider.
     *
     * @return DatabaseProvider The configured database provider.
     * @throws IllegalStateException If the app has not been properly prepared yet.
     */
    public static DatabaseProvider getDatabaseProvider() {
        if (dbProvider == null) {
            throw new IllegalStateException("SocialSite has not been prepared yet");
        }
        return dbProvider;
    }


    /**
     * Get a reference to the exception thrown while instantiating the
     * database provider, if any.
     *
     * @return StartupException Exception from db provider, or null if no exception thrown.
     */
    public static StartupException getDatabaseProviderException() {
        return dbProviderException;
    }


    /**
     * Get a reference to the currently configured MailProvider, if available.
     *
     * @return MailProvider The configured mail provider, or null if none configured.
     */
    public static MailProvider getMailProvider() {
        return mailProvider;
    }


    /**
     * Run the SocialSite preparation sequence.  Currently, that just means
     * initializing the database provider.
     */
    public static void prepare() throws StartupException {

        // setup mail provider, if configured
        try {
            mailProvider = new MailProvider();
        } catch(StartupException ex) {
            log.warn("Failed to setup mail provider, continuing anways.\n"+
                    "Reason: "+ex.getMessage(), ex);
        }

        try {
            dbProvider = new DatabaseProvider();
        } catch(StartupException ex) {
            dbProviderException = ex;
            throw ex;
        }

        prepared = true;

    }

}
