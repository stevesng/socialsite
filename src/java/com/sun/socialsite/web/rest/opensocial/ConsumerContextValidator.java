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

import com.sun.socialsite.config.Config;
import com.sun.socialsite.util.PropertyExpander;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * <p>
 *  Determines what assertions (if any) a given ConsumerContext source
 *  should be allowed to provide.
 * </p>
 */
public class ConsumerContextValidator {

    private static Log log = LogFactory.getLog(ConsumerContextValidator.class);

    private static final String DEFAULT_RULES = "/com/sun/socialsite/web/rest/opensocial/socialsite_context.xml";

    private static final String CUSTOM_RULES = "/socialsite_context.xml";

    /** Our validation rules (ordered from first to last). */
    private List<Rule> rules = new ArrayList<Rule>();


    public ConsumerContextValidator() {

        InputStream resourceStream = ConsumerContextValidator.class.getResourceAsStream(CUSTOM_RULES);
        if (resourceStream != null) {
            log.info(String.format("Loading custom context validator rules from '%s'", CUSTOM_RULES));
        } else {
            resourceStream = ConsumerContextValidator.class.getResourceAsStream(DEFAULT_RULES);
            log.info(String.format("Loading default context validator rules from '%s'", DEFAULT_RULES));
        }

        try {
            initRules(resourceStream);
        } finally {
            if (resourceStream != null) {
                try {
                    resourceStream.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }

    }


    public boolean isAttributeAllowed(ConsumerContext.ChainElement element, String attributeName) {

        boolean result = false;

        for (Rule rule : rules) {
            if ((rule.appliesTo(element)) && (rule.allowsAttribute(attributeName))) {
                result = true;
                break;
            }
        }

        if ((result == false) && (log.isWarnEnabled())) {
            String value = element.getString(attributeName);
            String msg = String.format("assertion (%s=%s) rejected from %s", attributeName, value, element);
            log.warn(msg);
        } else if ((result == true) && (log.isDebugEnabled())) {
            String value = element.getString(attributeName);
            String msg = String.format("assertion (%s=%s) accepted from %s", attributeName, value, element);
            log.debug(msg);
        }

        return result;
    }


    private void initRules(InputStream resourceStream) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(resourceStream);
            NodeList nl = doc.getElementsByTagName("rule");
            for (int i = 0; i < nl.getLength(); i++) {
                Rule rule = new Rule((Element)(nl.item(i)));
                rules.add(rule);
            }
        } catch (Exception e) {
            String msg = "Failed to load validator rules";
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }

    }


    /*
     * todo: this will be replaced by the ContextRule pojo.
     * See https://socialsite.dev.java.net/issues/show_bug.cgi?id=336
     * for more information.
     */
    private static class Rule {

        Set<Object> directSources = new HashSet<Object>();
        Set<Object> indirectSources = new HashSet<Object>();
        Set<String> acceptAttributes = new HashSet<String>();
        Set<String> rejectAttributes = new HashSet<String>();


        Rule(Element rule) throws MalformedURLException {

            Map conf = Config.toMap();

            Element sources = (Element)(rule.getElementsByTagName("sources").item(0));
            Element assertions = (Element)(rule.getElementsByTagName("assertions").item(0));

            NodeList direct = sources.getElementsByTagName("direct");
            for (int i = 0; i < direct.getLength(); i++) {
                String s = PropertyExpander.expand(direct.item(i).getFirstChild().getNodeValue().trim(), conf);
                if ("*".equals(s)) {
                  directSources.add(s);
                } else {
                  directSources.add(new URL(s));
                }
            }

            NodeList indirect = sources.getElementsByTagName("indirect");
            for (int i = 0; i < indirect.getLength(); i++) {
                String s = PropertyExpander.expand(indirect.item(i).getFirstChild().getNodeValue().trim(), conf);
                if ("*".equals(s)) {
                  indirectSources.add(s);
                } else {
                  indirectSources.add(new URL(s));
                }
            }

            NodeList accept = assertions.getElementsByTagName("accept");
            for (int i = 0; i < accept.getLength(); i++) {
                String s = PropertyExpander.expand(accept.item(i).getFirstChild().getNodeValue().trim(), conf);
                acceptAttributes.add(s);
                log.debug("acceptAttributes+="+s);
            }

            NodeList reject = assertions.getElementsByTagName("reject");
            for (int i = 0; i < reject.getLength(); i++) {
                String s = PropertyExpander.expand(reject.item(i).getFirstChild().getNodeValue().trim(), conf);
                acceptAttributes.add(s);
                log.debug("rejectAttributes+="+s);
            }

        }


        boolean appliesTo(ConsumerContext.ChainElement element) {
            boolean result = false;
            Set sources = ((element.loadedDirectly) ? directSources : indirectSources);
            URL elementSource = getSourceURL(element.source);
            if (element.source != null) {
                result = ((sources.contains(elementSource)) || (sources.contains("*")));
            } else {
                result = sources.contains("*");
            }
            return result;
        }


        boolean allowsAttribute(String attributeName) {
            boolean result = false;
            if ((rejectAttributes.contains(attributeName)) || (rejectAttributes.contains("*"))) {
                result = false;
            } else if ((acceptAttributes.contains(attributeName)) || (acceptAttributes.contains("*"))) {
                result = true;
            }
            return result;
        }

        /**
         * Returns a URL which corresponds to the input URL in all ways, except that the query and
         * fragment portions are excluded.  This is useful when determining if a given rule applies
         * to some source.
         */
        private static URL getSourceURL(URL url) {
            try {
                if (url == null) {
                    return null;
                }
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), null, null);
                return uri.toURL();
            } catch (Exception e) {
                throw new RuntimeException("Unexpected Exception", e);
            }
        }

    }

}
