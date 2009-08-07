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

package com.sun.socialsite.installer;

import java.util.Date;
import java.util.Properties;

import javax.persistence.*;

import com.sun.socialsite.userapi.*;

public class CreateAdmin {

    /** 
     * @param admin user
     * @param admin password 
     * @param admin email 
     * @param driver 
     * @param url 
     * @param database user
     * @param database password 
     */
    public static void main(String args[]) throws Exception {

        // get arguments
        if ((args.length < 7)
         || (args[0] == null || args[0].equals(""))
         || (args[1] == null || args[1].equals(""))
         || (args[2] == null || args[2].equals(""))
         || (args[3] == null || args[3].equals(""))
         || (args[4] == null || args[4].equals(""))
         || (args[5] == null || args[5].equals(""))
         || (args[6] == null || args[6].equals(""))) {
            System.out.println("missing arguments");
            return;
        }

        String adminUser = args[0];
        String adminPassword = args[1];
        String adminEmail = args[2];
        String driverClass = args[3];
        String jdbcUrl = args[4];
        String dbUser = args[5];
        String dbPassword = args[6];

        Properties emfProps = makeEmfProps(driverClass, jdbcUrl, dbUser, dbPassword);
        EntityManagerFactory emf = getEntityManagerFactory(emfProps);

        try {
            setupAdminUser(adminUser, adminPassword, adminEmail, emf);
        } finally {
            emf.close();
        }
    }


    private static User createAdminUser(String userName, String password, 
            String emailAddress, Date creationDate, boolean enabled)
            throws Exception {

        User user = new User();
        user.setId(userName);
        user.setUserId(userName);
        user.setUserName(userName);
        user.resetPassword(password, "SHA");
        user.setFullName("");
        user.setEmailAddress(emailAddress);
        user.setCreationDate(creationDate);
        user.setUpdateDate(creationDate);
        user.setAccessDate(creationDate);
        user.setEnabled(enabled);
        return user;
    }


    private static EntityManagerFactory getEntityManagerFactory(Properties emfProps) {
        EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("SocialSite_PU_Standalone", emfProps);
        return emf;
    }


    private static void setupAdminUser(String user, String password,
            String email, EntityManagerFactory emf) 
            throws Exception {

        UserManagerImpl.setEmf(emf);

        EntityManager em = emf.createEntityManager();
        UserManager umgr = new UserManagerImpl(em);

        Date date = new Date();

        User adminUser = createAdminUser(user, password, email, date, true);

        em.getTransaction().begin();

        umgr.registerUser(adminUser);
        umgr.grantRole("admin", adminUser);
        umgr.grantRole("editor", adminUser);
        umgr.grantRole("user", adminUser);

        em.getTransaction().commit();
    }


    /**
     * Returns a Properties object containing appropriate entries for various
     * JPA implementations.  Currently, these include OpenJPA, Toplink, Eclipselink,
     * and Hibernate.
     */
    private static Properties makeEmfProps(String driverClass, String jdbcUrl, String dbUser, String dbPassword) {

        Properties emfProps = new Properties();

        // Set JDBC properties for OpenJPA
        emfProps.setProperty("openjpa.ConnectionDriverName", driverClass);
        emfProps.setProperty("openjpa.ConnectionURL", jdbcUrl);
        emfProps.setProperty("openjpa.ConnectionUserName", dbUser);
        emfProps.setProperty("openjpa.ConnectionPassword", dbPassword);

        // Set JDBC properties for OpenJPA
        emfProps.setProperty("openjpa.ConnectionDriverName", driverClass);
        emfProps.setProperty("openjpa.ConnectionURL", jdbcUrl);
        emfProps.setProperty("openjpa.ConnectionUserName", dbUser);
        emfProps.setProperty("openjpa.ConnectionPassword", dbPassword);

        // And Eclipselink JPA
        emfProps.setProperty("eclipselink.jdbc.driver", driverClass);
        emfProps.setProperty("eclipselink.jdbc.url", jdbcUrl);
        emfProps.setProperty("eclipselink.jdbc.user", dbUser);
        emfProps.setProperty("eclipselink.jdbc.password", dbPassword);

        // And Toplink JPA
        emfProps.setProperty("toplink.jdbc.driver", driverClass);
        emfProps.setProperty("toplink.jdbc.url", jdbcUrl);
        emfProps.setProperty("toplink.jdbc.user", dbUser);
        emfProps.setProperty("toplink.jdbc.password", dbPassword);

        // And Hibernate JPA
        emfProps.setProperty("hibernate.connection.driver_class", driverClass);
        emfProps.setProperty("hibernate.connection.url", jdbcUrl);
        emfProps.setProperty("hibernate.connection.username", dbUser);
        emfProps.setProperty("hibernate.connection.password", dbPassword);

        return emfProps;
    }

}
