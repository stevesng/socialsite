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

import com.google.inject.Singleton;
import com.sun.socialsite.business.ListenerManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Allows objects to listen for entity lifecycle events.
 */
@Singleton
public class JPAListenerManagerImpl implements ListenerManager {

    private static Log log = LogFactory.getLog(JPAListenerManagerImpl.class);

    private static ConcurrentHashMap<Class, Collection<Object>> listenersMap = new ConcurrentHashMap<Class, Collection<Object>>();


    /**
     * {@inheritDoc}
     */
    public void addListener(Class entityClass, Object listener) {
        Collection<Object> listenersForClass = listenersMap.get(entityClass);
        if (listenersForClass == null) {
            listenersMap.putIfAbsent(entityClass, new ConcurrentLinkedQueue<Object>());
            listenersForClass = listenersMap.get(entityClass);
        }
        listenersForClass.add(listener);
        log.debug(String.format("addListener(%s, %s): listenersForClass.size=%d", entityClass.getCanonicalName(), listener, listenersForClass.size()));
    }


    /**
     * {@inheritDoc}
     */
    public void removeListener(Class entityClass, Object listener) {
        Collection<Object> listenersForClass = listenersMap.get(entityClass);
        if (listenersForClass != null) {
            listenersForClass.remove(listener);
        }
    }


    /**
     * Returns all listeners which have registered to receive lifecycle
     * events for a class to which the specified entity belongs.
     * @param entity the entity for which listeners are sought.
     */
    private static Collection<Object> getListeners(Object entity) {
        Collection<Object> results = new ArrayList<Object>();
        Set<Class> classes = new HashSet<Class>();
        for (Class clazz = entity.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            classes.add(clazz);
            Class[] declaredClasses = entity.getClass().getDeclaredClasses();
            for (int i = 0; i < declaredClasses.length; i++) {
                classes.add(declaredClasses[i]);
            }
        }
        for (Class clazz : classes) {
            Collection<Object> listenersForClass = listenersMap.get(clazz);
            if (listenersForClass != null) {
                results.addAll(listenersForClass);
            }
        }
        //log.debug(String.format("getListeners(%s): entityClass=%s results.size=%d", entity, entity.getClass().getCanonicalName(), results.size()));
        return results;
    }


    /**
     * Calls any appropriately-annotated methods in listeners which
     * have registered to receive lifecycle events for a class to
     * which the specified entity belongs.
     * @param entity the entity experiencing a lifecycle event.
     * @param annotationClass the annotation class corresponding to the event.
     */
    private static void notifyListeners(Object entity, Class<? extends Annotation> annotationClass) {

        //log.trace(String.format("notifyListeners(%s, %s)", entity, annotationClass.getCanonicalName()));

        Collection<Object> listeners = getListeners(entity);
        for (Object listener : listeners) {
            Method[] methods = listener.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getAnnotation(annotationClass) != null) {
                    try {
                        log.debug(String.format("calling %s.%s(%s)", listener, method.getName(), entity));
                        // Handle cases where the listener is a nested class
                        if (Modifier.isPublic(method.getModifiers())) method.setAccessible(true);
                        method.invoke(listener, entity);
                    } catch (Throwable t) {
                        // TODO: Catch and Handle individual Exception types
                        log.error("Exception", t);
                    }
                }
            }
        }
    }


    /**
     * Helper used to ensure that the JPAListenerManagerImpl itself
     * receives all entities' lifecycle events (so that it can
     * then determine whether they should be passed along to any
     * registered consumers).  This class should be listed in an
     * {@link javax.persistence.ListenerManagers} annotation for
     * all JPA entities (except when entities are themselves a
     * subclass of other entities: listing this as a listener
     * for both would then cause us to receive duplicate events).
     */
    public static class Listener {

        @PostLoad
        public void postLoad(Object entity) {
            notifyListeners(entity, PostLoad.class);
        }


        @PrePersist
        public void prePersist(Object entity) {
            notifyListeners(entity, PrePersist.class);
        }


        @PostPersist
        public void postPersist(Object entity) {
            notifyListeners(entity, PostPersist.class);
        }


        @PreRemove
        public void preRemove(Object entity) {
            notifyListeners(entity, PreRemove.class);
        }


        @PostRemove
        public void postRemove(Object entity) {
            notifyListeners(entity, PostRemove.class);
        }


        @PreUpdate
        public void preUpdate(Object entity) {
            notifyListeners(entity, PreUpdate.class);
        }


        @PostUpdate
        public void postUpdate(Object entity) {
            notifyListeners(entity, PostUpdate.class);
        }

    }

}
