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

package com.sun.socialsite.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;


/**
 * Utility class for obtaining dynamic object proxies.
 */
public class ProxyUtil {

    /**
     * Used to create a dynamic proxy which allows for one object (overriderObject)
     * to override the methods of another (origObject).  This is useful in cases
     * where you want to "wrap" one object with another that will override some of
     * its methods, but don't want to implement its entire interface.
     */
    public static Object getOverrideProxy(Object origObject, Object overriderObject) {
        Class clazz = origObject.getClass();
        return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new ProxyHelper(origObject, overriderObject));
    }

}


class ProxyHelper implements InvocationHandler {

    private Object origObject;

    private Object overriderObject;


    public ProxyHelper(Object origObject, Object overriderObject) {
        this.origObject = origObject;
        this.overriderObject = overriderObject;
    }


    public Object invoke(Object proxy, Method origMethod, Object[] args) throws Throwable {

        Object result = null;

        try {

            Method overrideMethod = findMethod(overriderObject, origMethod.getName(), getClasses(args));

            if (overrideMethod != null) {

                // Handle cases where overrideObject is a nested class
                if (Modifier.isPublic(overrideMethod.getModifiers())) {
                    overrideMethod.setAccessible(true);
                }

                result = overrideMethod.invoke(overriderObject, args);

            } else {

                result = origMethod.invoke(origObject, args);

            }

        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("Unexpected Exception: " + e.getMessage(), e);
        }

        return result;
    }


    /**
     * Returns the Method in object which has the specified name and accepts the
     * specified argument types.  If no such method exists, null is returned.
     */
    private Method findMethod(Object object, String methodName, Class[] argsClasses) {

        Method method = null;

        try {
            method = object.getClass().getMethod(methodName, argsClasses);
        } catch (NoSuchMethodException e) {
        }

        return method;
    }


    /**
     * Returns an array where each element is the class of the corresponding
     * object in the input array.  If the input array is null, null is returned.
     */
    private Class[] getClasses(Object[] objects) {

        Class[] classes = null;

        if (objects != null) {
            classes = new Class[objects.length];
            for (int i = 0; i < objects.length; i++) classes[i] = objects[i].getClass();
        }

        return classes;
    }

}
