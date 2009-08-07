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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Allows for easy reading of JSON objects, with descension into nested
 * objects in a manner similar to what would occur in JavaScript.  For example,
 * <code>get("viewer.id")</code> would look for <code>root->viewer->id</code>
 * in the JSON (whereas doing the same on a raw JSONObject would just look for
 * <code>root->viewer.id</code>).
 */
public class JSONWrapper {

    private JSONObject json;

    public JSONWrapper(JSONObject json) {
        this.json = json;
    }

    public Object get(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.get(result.key);
    }

    public boolean getBoolean(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.getBoolean(result.key);
    }

    public double getDouble(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.getDouble(result.key);
    }

    public int getInt(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.getInt(result.key);
    }

    public long getLong(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.getLong(result.key);
    }

    public JSONObject getJSONObject(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.getJSONObject(result.key);
    }

    public String getString(String key) throws JSONException {
        Result result = new Result(key, false);
        return result.base.getString(result.key);
    }

    public boolean has(String key) {
        try {
            Result result = new Result(key, false);
            return result.base.has(result.key);
        } catch (Exception e) {
            return false;
        }
    }

    public Object opt(String key) {
        try {
            Result result = new Result(key, false);
            return result.base.opt(result.key);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            Result result = new Result(key, false);
            return result.base.optBoolean(result.key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double optDouble(String key) {
        return optDouble(key, Double.NaN);
    }

    public double optDouble(String key, double defaultValue) {
        try {
            Result result = new Result(key, false);
            return result.base.optDouble(result.key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int optInt(String key) {
        return optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        try {
            Result result = new Result(key, false);
            return result.base.optInt(result.key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public long optLong(String key) {
        return optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue) {
        try {
            Result result = new Result(key, false);
            return result.base.optLong(result.key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public JSONObject optJSONObject(String key) {
        try {
            Result result = new Result(key, false);
            return result.base.optJSONObject(result.key);
        } catch (Exception e) {
            return null;
        }
    }

    public String optString(String key) {
        return optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        try {
            Result result = new Result(key, false);
            return result.base.optString(result.key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void append(String key, Object value) throws JSONException {
        Result result = new Result(key, true);
        result.base.append(result.key, value);
    }

    public void put(String key, Object value) throws JSONException {
        Result result = new Result(key, true);
        result.base.put(result.key, value);
    }

    @Override
    public String toString() {
        return json.toString();
    }

    private class Result {

        JSONObject base;
        String key;

        /**
         * @param expression
         * @param createIntermediaries if true, intermediate JSONObjects will be created as necessary.
         */
        Result(String expression, boolean createIntermediaries) throws JSONException {
            String[] keyParts = expression.split("\\.");
            JSONObject current = json;
            for (int i = 0; i < (keyParts.length-1); i++) {
                JSONObject next = current.optJSONObject(keyParts[i]);
                if (next == null) {
                    if (createIntermediaries) {
                        next = new JSONObject();
                        current.put(keyParts[i], next);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i2 = 0; i2 <= i; i2++) {
                            if (sb.length() > 0) sb.append(".");
                            sb.append(keyParts[i2]);
                        }
                        String msg = String.format("key '%s' does not exist", sb.toString());
                        throw new JSONException(msg);
                    }
                }
                current = next;
            }
            this.base = current;
            this.key = keyParts[keyParts.length-1];
        }

    }

}
