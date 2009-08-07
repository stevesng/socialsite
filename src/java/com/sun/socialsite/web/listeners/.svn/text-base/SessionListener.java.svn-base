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

package com.sun.socialsite.web.listeners;

import com.sun.socialsite.config.Config;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * SessionListener
 */
public class SessionListener implements HttpSessionActivationListener, HttpSessionListener, ServletContextListener {

    private static final long serialVersionUID = 0L;

    private static Log log = LogFactory.getLog(SessionListener.class);

    private static Map<String, HttpSession> activeSessions = new ConcurrentHashMap<String, HttpSession>();

    private static ConcurrentHashMap<HttpSession, Collection<Object>> listenersMap = new ConcurrentHashMap<HttpSession, Collection<Object>>();

    private static Integer sessionTimeout = null;


    public static void addListener(HttpSession session, Object listener) {
        Collection<Object> listenersForSession = listenersMap.get(session);
        if (listenersForSession == null) {
            listenersMap.putIfAbsent(session, new ConcurrentLinkedQueue<Object>());
            listenersForSession = listenersMap.get(session);
        }
        listenersForSession.add(listener);
        log.debug(String.format("addListener(%s, %s): listenersForSession.size=%d", session, listener, listenersForSession.size()));
    }


    /**
     * Returns all listeners which have registered to receive events
     * for the specified HttpSession.
     * @param session the HttpSession for which listeners are sought.
     */
    private static Collection<Object> getListeners(HttpSession session) {
        Collection<Object> results = new ArrayList<Object>();
        Collection<Object> listenersForSession = listenersMap.get(session);
        if (listenersForSession != null) {
            results.addAll(listenersForSession);
        }
        log.debug(String.format("getListeners(%s): results.size=%d", session, results.size()));
        return results;
    }


    public static HttpSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }


    public void contextInitialized(ServletContextEvent event) {

        activeSessions.clear();

        sessionTimeout = Config.getIntProperty("socialsite.http.session.timeout");
        if (sessionTimeout != 0) {
            log.info(String.format("HttpSession timeout is %d seconds", sessionTimeout));
        } else {
            sessionTimeout = null;
        }

    }


    public void contextDestroyed(ServletContextEvent event) {
        activeSessions.clear();
    }


    public void sessionCreated(HttpSessionEvent se) {

        log.debug(String.format("sessionCreated(%s)", se.getSession().getId()));
        activeSessions.put(se.getSession().getId(), se.getSession());

        if (sessionTimeout != null) {
            se.getSession().setMaxInactiveInterval(sessionTimeout);
        }

        for (Object listener : getListeners(se.getSession())) {
            if (listener instanceof HttpSessionListener) {
                ((HttpSessionListener)listener).sessionCreated(se);
            }
        }

    }


    public void sessionDestroyed(HttpSessionEvent se) {

        log.debug(String.format("sessionDestroyed(%s)", se.getSession().getId()));
        activeSessions.remove(se.getSession().getId());

        for (Object listener : getListeners(se.getSession())) {
            if (listener instanceof HttpSessionListener) {
                ((HttpSessionListener)listener).sessionDestroyed(se);
            }
        }

    }


    public void sessionDidActivate(HttpSessionEvent se) {

        log.debug(String.format("sessionDidActivate(%s)", se.getSession().getId()));
        activeSessions.put(se.getSession().getId(), se.getSession());

        for (Object listener : getListeners(se.getSession())) {
            if (listener instanceof HttpSessionActivationListener) {
                ((HttpSessionActivationListener)listener).sessionDidActivate(se);
            }
        }

    }


    public void sessionWillPassivate(HttpSessionEvent se) {

        log.debug(String.format("sessionWillPassivate(%s)", se.getSession().getId()));
        activeSessions.remove(se.getSession().getId());

        for (Object listener : getListeners(se.getSession())) {
            if (listener instanceof HttpSessionActivationListener) {
                ((HttpSessionActivationListener)listener).sessionWillPassivate(se);
            }
        }

    }

}
