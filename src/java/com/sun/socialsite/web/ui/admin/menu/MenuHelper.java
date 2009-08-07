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

package com.sun.socialsite.web.ui.admin.menu;

import com.sun.socialsite.config.RuntimeConfig;
import com.sun.socialsite.util.Utilities;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * A helper class for dealing with UI menus.
 */
public class MenuHelper {

    private static Log log = LogFactory.getLog(MenuHelper.class);

    private static Hashtable<String, ParsedMenu> menus = new Hashtable<String, ParsedMenu>();


    static {
        try {
            // parse menus and cache so we can efficiently reuse them
            // TODO: there is probably a better way than putting the whole path
            ParsedMenu adminMenu = unmarshall(
                MenuHelper.class.getResourceAsStream(
                "/com/sun/socialsite/web/ui/admin/menu/menu.xml"));
            menus.put("admin", adminMenu);

        } catch (Exception ex) {
            log.error("Error parsing menu configs", ex);
        }
    }


    public static Menu getMenu(String menuId, String currentAction) {

        if (menuId == null) {
            return null;
        }

        Menu menu = null;

        // do we know the specified menu config?
        ParsedMenu menuConfig = menus.get(menuId);
        if (menuConfig != null) {
            try {
                menu = buildMenu(menuConfig, currentAction);
            } catch (Exception ex) {
                log.debug("ERROR: fetching user roles", ex);
            }
        }

        return menu;
    }


    private static Menu buildMenu(ParsedMenu menuConfig, String currentAction) throws Exception {

        log.debug("creating menu for action - "+currentAction);

        Menu tabMenu = new Menu();

        // iterate over tabs from parsed config
        ParsedTab configTab = null;
        Iterator<ParsedTab> tabsIter = menuConfig.getTabs().iterator();
        while (tabsIter.hasNext()) {
            configTab = tabsIter.next();

            log.debug("config tab = "+configTab.getName());

            // does this tab have an enabledProperty?
            boolean includeTab = true;
            if (configTab.getEnabledProperty() != null) {
                includeTab = getBooleanProperty(configTab.getEnabledProperty());
            } else if (configTab.getDisabledProperty() != null) {
                includeTab = ! getBooleanProperty(configTab.getDisabledProperty());
            }

            if (includeTab) {
                // user roles check
                if (configTab.getGlobalPermissionActions() != null
                        && !configTab.getGlobalPermissionActions().isEmpty()) {
                    try {

                        // TODO: check does user have permission

                    } catch (Exception ex) {
                        log.debug("ERROR: fetching user roles", ex);
                        includeTab = false;
                    }
                }
            }

            if (includeTab) {
                log.debug("tab allowed - "+configTab.getName());

                // all checks passed, tab should be included
                MenuTab tab = new MenuTab();
                tab.setKey(configTab.getName());

                // setup tab items
                boolean firstItem = true;
                ParsedTabItem configTabItem = null;
                Iterator<ParsedTabItem> itemsIter = configTab.getTabItems().iterator();
                while (itemsIter.hasNext()) {
                    configTabItem = itemsIter.next();

                    log.debug("config tab item = "+configTabItem.getName());

                    boolean includeItem = true;
                    if (configTabItem.getEnabledProperty() != null) {
                        includeItem = getBooleanProperty(configTabItem.getEnabledProperty());
                    } else if (configTabItem.getDisabledProperty() != null) {
                        includeItem = ! getBooleanProperty(configTabItem.getDisabledProperty());
                    }

                    if (includeItem) {
                        // user roles check
                        if (configTabItem.getGlobalPermissionActions() != null
                                && !configTabItem.getGlobalPermissionActions().isEmpty()) {

                            // TODO: check does user have permission

                        }
                    }

                    if (includeItem) {
                        log.debug("tab item allowed - "+configTabItem.getName());

                        // all checks passed, item should be included
                        MenuTabItem tabItem = new MenuTabItem();
                        tabItem.setKey(configTabItem.getName());
                        tabItem.setAction(configTabItem.getAction());

                        // is this the selected item?
                        if (isSelected(currentAction, configTabItem)) {
                            tabItem.setSelected(true);
                            tab.setSelected(true);
                        }

                        // the url for the tab is the url of the first item of the tab
                        if (firstItem) {
                            tab.setAction(tabItem.getAction());
                            firstItem = false;
                        }

                        // add the item
                        tab.addItem(tabItem);
                    }
                }

                // add the tab
                tabMenu.addTab(tab);
            }
        }

        return tabMenu;
    }

    /** Check enabled property, prefers runtime properties */
    private static boolean getBooleanProperty(String propertyName) {
        if (RuntimeConfig.getProperty(propertyName) != null) {
            return RuntimeConfig.getBooleanProperty(propertyName);
        }
        return RuntimeConfig.getBooleanProperty(propertyName);
    }

    private static boolean isSelected(String currentAction, ParsedTabItem tabItem) {

        if (currentAction.equals(tabItem.getAction())) {
            return true;
        }

        // an item is also considered selected if it's subforwards are the current action
        String[] subActions = tabItem.getSubActions();
        if (subActions != null && subActions.length > 0) {
            for(int i=0; i < subActions.length; i++) {
                if (currentAction.equals(subActions[i])) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Unmarshall the given input stream into our defined
     * set of Java objects.
     **/
    private static ParsedMenu unmarshall(InputStream instream)
        throws IOException, JDOMException {

        if (instream == null)
            throw new IOException("InputStream is null!");

        ParsedMenu config = new ParsedMenu();

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(instream);

        Element root = doc.getRootElement();
        List<?> menus = root.getChildren("menu");
        Iterator<?> iter = menus.iterator();
        while (iter.hasNext()) {
            Element e = (Element) iter.next();
            config.addTab(elementToParsedTab(e));
        }

        return config;
    }


    private static ParsedTab elementToParsedTab(Element element) {

        ParsedTab tab = new ParsedTab();

        tab.setName(element.getAttributeValue("name"));
        if (element.getAttributeValue("weblogPerms") != null) {
            tab.setWeblogPermissionActions(Utilities.stringToStringList(element.getAttributeValue("weblogPerms"),","));
        }
        if (element.getAttributeValue("globalPerms") != null) {
            tab.setGlobalPermissionActions(Utilities.stringToStringList(element.getAttributeValue("globalPerms"),","));
        }
        tab.setEnabledProperty(element.getAttributeValue("enabledProperty"));
        tab.setDisabledProperty(element.getAttributeValue("disabledProperty"));

        List<?> menuItems = element.getChildren("menu-item");
        Iterator<?> iter = menuItems.iterator();
        while (iter.hasNext()) {
            Element e = (Element) iter.next();
            tab.addItem(elementToParsedTabItem(e));
        }

        return tab;
    }


    private static ParsedTabItem elementToParsedTabItem(Element element) {

        ParsedTabItem tabItem = new ParsedTabItem();

        tabItem.setName(element.getAttributeValue("name"));
        tabItem.setAction(element.getAttributeValue("action"));

        String subActions = element.getAttributeValue("subactions");
        if (subActions != null) {
            tabItem.setSubActions(subActions.split(","));
        }

        if (element.getAttributeValue("weblogPerms") != null) {
            tabItem.setWeblogPermissionActions(Utilities.stringToStringList(element.getAttributeValue("weblogPerms"), ","));
        }
        if (element.getAttributeValue("globalPerms") != null) {
            tabItem.setGlobalPermissionActions(Utilities.stringToStringList(element.getAttributeValue("globalPerms"), ","));
        }
        tabItem.setEnabledProperty(element.getAttributeValue("enabledProperty"));
        tabItem.setDisabledProperty(element.getAttributeValue("disabledProperty"));

        return tabItem;
    }

}

