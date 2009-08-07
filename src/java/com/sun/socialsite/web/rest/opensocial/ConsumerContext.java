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

import com.sun.socialsite.SocialSiteException;
import com.sun.socialsite.util.JSONBuilder;
import com.sun.socialsite.util.JSONWrapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * <p>
 *  Represents the context in which a SocialSiteToken is constructed and used.
 *  This context consists of a chain, where each element in the chain has the
 *  following information:
 *  <ul>
 *   <li>source (URL)</li>
 *   <li>loadedDirectly? (boolean)</li>
 *   <li>attributes (string-based key/value pairs)</li>
 *  </ul>
 * </p>
 * <p>
 *  When an attribute value is requested, the chain is walked (in order) to
 *  find the first element which is both trusted to provide the specified
 *  attribute and has the attribute populated.  The value associated with
 *  the attribute in that element is then returned.
 * </p>
 */
public class ConsumerContext {

    private static Log log = LogFactory.getLog(ConsumerContext.class);

    /** ChainElements comprising our context chain (ordered from first to last). */
    private List<ChainElement> elements = new ArrayList<ChainElement>();

    /** Determines what assertions are acceptable from a given source. */
    private static ConsumerContextValidator validator = new ConsumerContextValidator();


    /**
     * Constructs a ConsumerContext object, with the first element in
     * the context chain having the specified source and contents.
     *
     * @param source the source URL from which the base context chain element's
     *  contents were obtained.  This is used for determining trustworthiness
     *  and interpreting relative URLs within the contents.
     * @param contents the actual contents of the base contents element.
     */
    public ConsumerContext(URL source, JSONObject contents) throws SocialSiteException {
        try {
            appendToChain(source, false, contents);
        } catch (SocialSiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }
    }


    /**
     * Retrieves an attribute value as a string.  The context chain is walked
     * in order to find the first element which is both trusted to provide the
     * specified attribute and has the attribute populated.  The value associated
     * with the attribute in that element is returned.
     *
     * @param key the name of the attribute to be retrieved.
     * @return the value associated with the specified attribute name (or null,
     *  if no such attribute exists).
     */
    public String getString(String key) {
        for (ChainElement element : elements) {
            if (element.getString(key) != null) {
                if (validator.isAttributeAllowed(element, key)) {
                    return element.getString(key);
                }
            }
        }
        return null;
    }


    public JSONObject getTrustedJSON() throws JSONException {
        // TODO: Handle trust filtering
        JSONBuilder jb = new JSONBuilder();
        for (ChainElement element : elements) {
            jb.addAll(element.contents);
        }
        return jb.toJSONObject();
    }


    /**
     * Returns the timeout value (in seconds) for this object.  This is determined
     * by finding the minimum timeout value for the elements in our context chain.
     * If no element in our context chain has a timeout value, null is returned.
     * 
     * @return the timeout value (in seconds) for this object.
     */
    public Long timeout() {
        Long timeout = null;
        for (ChainElement element : elements) {
            if (element.contents.has("timeout")) {
                Long val = element.contents.optLong("timeout");
                timeout = ((timeout == null) ? val : Math.min(timeout, val));
            }
        }
        return timeout;
    }


    /**
     * Adds an element to this object's context chain.  If the specified
     * element has a "delegate" attribute, this method will retrieve the
     * delegation element and recursively add it also.
     *
     * @param element the ChainElement to be added to the context chain.
     */
    private void appendToChain(URL source, boolean loadedDirectly, JSONObject contents) throws SocialSiteException {

        try {

            ChainElement element = null;
            if (contents.has("attributes")) {
                element = new LegacyChainElement(source, loadedDirectly, contents);
            } else {
                element = new ChainElement(source, loadedDirectly, contents);
            }

            elements.add(element);

            if (element.contents.has("delegate")) {

                JSONObject delegate = element.contents.getJSONObject("delegate");

                URL url = null;
                if (element.source != null) {
                    url = new URL(element.source, delegate.getString("url"));
                } else {
                    url = new URL(delegate.getString("url"));
                }

                HttpClient httpClient = new HttpClient();
                HttpMethod method = null;
                if ("GET".equalsIgnoreCase(delegate.getString("method"))) {
                    method = new GetMethod(url.toExternalForm());
                }
                if (delegate.has("headers")) {
                    JSONObject headers = delegate.getJSONObject("headers");
                    for (Iterator<?> iterator = headers.keys(); iterator.hasNext();) {
                        String name= iterator.next().toString();
                        String value = headers.get(name).toString();
                        method.addRequestHeader(name, value);
                    }
                }
                if (element.source != null) {
                    method.setRequestHeader("Referer", element.source.toString());
                }

                int responseCode = httpClient.executeMethod(method);
                if (responseCode == 200) {
                    String responseBody = method.getResponseBodyAsString();
                    appendToChain(url, true, new JSONObject(responseBody));
                    if (log.isDebugEnabled()) {
                        String msg = String.format("%s %s returned %d: %s", method.getName(), url, responseCode, responseBody);
                        log.debug(msg);
                    }
                } else {
                  String msg = String.format("%s %s returned %d", method.getName(), url, responseCode);
                  throw new SocialSiteException(msg);
                }

            }

        } catch (SocialSiteException e) {
            throw e;
        } catch (Exception e) {
            throw new SocialSiteException(e);
        }

    }


    /**
     * Internal representation of an item in a context chain.
     */
    static class ChainElement {

        URL source;

        boolean loadedDirectly;

        JSONObject contents;

        public ChainElement(URL source, boolean loadedDirectly, JSONObject contents) {
            this.source = source;
            this.loadedDirectly = loadedDirectly;
            this.contents = contents;
        }

        public URL getSource() {
            return this.source;
        }

        public String getString(String key) {
            JSONObject assertions = contents.optJSONObject("assertions");
            String result = ((assertions != null) ? new JSONWrapper(assertions).optString(key, null) : null);
            return result;
        }

        @Override
        public String toString() {
            return String.format("%s[source=%s, loadedDirectly=%s]", getClass().getSimpleName(), this.source, this.loadedDirectly);
        }

    }


    private static class LegacyChainElement extends ChainElement {

        /**
         * Modifies the contents of the specified JSON so that values present under
         * legacy keys (such as "viewerId") are copied into the equivalent modern
         * location (such as "viewer"->"id").
         *
         * @return the original JSONObject (whose contents will have been updated).
         */
        private static JSONObject getUpdatedContents(JSONObject contents) {
            JSONObject attributes = contents.optJSONObject("attributes");
            if (attributes != null) {
                try {
                    JSONWrapper wrapper = new JSONWrapper(contents);
                    if (attributes.has("ownerId")) {
                        wrapper.put("assertions.owner.id", attributes.optString("ownerId"));
                    }
                    if (attributes.has("viewerId")) {
                        wrapper.put("assertions.viewer.id", attributes.optString("viewerId"));
                    }
                    if (attributes.has("groupHandle")) {
                        wrapper.put("assertions.group.handle", attributes.optString("groupHandle"));
                    }
                } catch (Exception e) {
                    log.error("Unexpected Failure", e);
                    throw new RuntimeException(e);
                }
            }
            return contents;
        }

        public LegacyChainElement(URL source, boolean loadedDirectly, JSONObject contents) {
            super(source, loadedDirectly, getUpdatedContents(contents));
            log.warn("Legacy context format detected; this format will soon be unsupported!");
        }

    }

}
