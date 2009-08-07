/*
 * Portions Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */

package com.sun.socialsite.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Property expansion utility.  This utility provides static methods to expand properties appearing in strings.
 * @author <a href="mailto:anil@busybuddha.org">Anil Gangolli</a> (Portions based on code from David Graff submitted for ROL-613)
 */
public final class PropertyExpander {

    // The pattern for a system property.  Matches ${property.name}, with the interior matched reluctantly.
    private static final Pattern EXPANSION_PATTERN = Pattern.compile("(\\$\\{([^}]+?)\\})", java.util.regex.Pattern.MULTILINE);


    // non-instantiable
    private PropertyExpander() {}


    /**
     * Expand property expressions in the input.  Expands property expressions of the form <code>${propertyname}</code>
     * in the input, replacing each such expression with the value associated to the respective key
     * <code>propertyname</code> in the supplied map.  If for a given expression, the property is undefined (has null
     * value) in the map supplied, that expression is left unexpanded in the resulting string.
     * <p/>
     * Note that expansion is not recursive.  If the value of one property contains another expression, the expression
     * appearing in the value will not be expanded further.
     *
     * @param input the input string.  This may be null, in which case null is returned.
     * @param props the map of property values to use for expansion.  This map should have <code>String</code> keys and
     *              <code>String</code> values.  Any object of class {@link java.util.Properties} works here, as will
     *              other implementations of such maps.
     * @return the result of replacing property expressions with the values of the corresponding properties from the
     *         supplied property map, null if the input string is null.
     */
    public static String expand(String input, Map props) {

        if (input == null) return null;

        Matcher matcher = EXPANSION_PATTERN.matcher(input);

        StringBuffer expanded = new StringBuffer(input.length());
        while (matcher.find()) {
            String propName = matcher.group(2);
            String value = (String) props.get(propName);
            // if no value is found, use a value equal to the original expression
            if (value == null) value = matcher.group(0);
            // Fake a literal replacement since Matcher.quoteReplacement() is not present in 1.4.
            matcher.appendReplacement(expanded, "");
            expanded.append(value);
        }
        matcher.appendTail(expanded);

        return expanded.toString();
    }


    /**
     * Returns a Properties object containing the same keys at inputProps, but with expanded values
     * for any entries whose keys are listed in propsToExpand.  If propsToExpand is null, expansion
     * is performed on all entries.
     *
     * @param inputProps
     * @param baseProps
     * @param propsToExpand the names of properties which should be expanded (if null, all properties are expanded).
     */
    public static Properties expandProperties(Properties inputProps, Map baseProps, String[] propsToExpand) {
        List<Map> basePropsList = new ArrayList<Map>(1);
        basePropsList.add(baseProps);
        Map<String, String> resultsMap = expandMap((Map)inputProps, basePropsList, propsToExpand);
        Properties results = new Properties();
        results.putAll(resultsMap);
        return results;
    }


    /**
     * Returns a Properties object containing the same keys at inputProps, but with expanded values
     * for any entries whose keys are listed in propsToExpand.  If propsToExpand is null, expansion
     * is performed on all entries.
     *
     * @param inputProps
     * @param basePropsList
     * @param propsToExpand the names of properties which should be expanded (if null, all properties are expanded).
     */
    public static Properties expandProperties(Properties inputProps, List<Map> basePropsList, String[] propsToExpand) {
        Map<String, String> resultsMap = expandMap(inputProps, basePropsList, propsToExpand);
        Properties results = new Properties();
        results.putAll(resultsMap);
        return results;
    }


    /**
     * Returns a Map object containing the same keys at inputProps, but with expanded values for
     * any entries whose keys are listed in propsToExpand.  If propsToExpand is null, expansion
     * is performed on all entries.
     *
     * @param inputProps
     * @param basePropsList
     * @param propsToExpand the names of properties which should be expanded (if null, all properties are expanded).
     */
    public static Map<String, String> expandMap(Map inputProps, Map baseProps, String[] propsToExpand)
    {
        List<Map> basePropsList = new ArrayList<Map>(1);
        basePropsList.add(baseProps);
        return expandMap(inputProps, basePropsList, propsToExpand);
    }


    /**
     * Returns a Map object containing the same keys at inputProps, but with expanded values for
     * any entries whose keys are listed in propsToExpand.  If propsToExpand is null, expansion
     * is performed on all entries.
     *
     * @param inputProps
     * @param basePropsList
     * @param propsToExpand the names of properties which should be expanded (if null, all properties are expanded).
     */
    public static Map<String, String> expandMap(Map inputProps, List<Map> basePropsList, String[] propsToExpand)
    {

        Map<String, String> results = new HashMap<String, String>();

        Set<String> expansionSet = new HashSet<String>();
        if (propsToExpand != null) {
            for (int i = 0; i < propsToExpand.length; i++) {
                expansionSet.add(propsToExpand[i]);
            }
        }

        for (Object keyObject : inputProps.keySet()) {
            String key = (String)(keyObject);
            String value = (String)(inputProps.get(keyObject));
            if ((propsToExpand == null) || (!expansionSet.contains(key))) {
                for (Map props : basePropsList) {
                    value = expand(value, props);
                }
            }
            results.put(key, value);
        }

        return results;

    }

}
