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

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * ExecuteSql - program for executing sql files
 */
public class ExecuteSql {

    private String url = null;
    private String user = null;
    private String passwd = null;
    private String driver = null;
    private String sqlFilename = null;

    private boolean debug = false;

    private Connection conn = null;
    private Statement stmtObj = null;

    private int succeededSql = 0;
    private int totalSql = 0;


    /**
     * @param url
     * @param user
     * @param password
     * @param driver
     * @param filename
     * @param classpath
     * @param [NODEBUG|DEBUG]
     */
    public static void main(String args[]) {
        boolean dbg;
        if (args.length > 5) {
            if (args[5].equals("NODEBUG")) {
                dbg = false;
            } else {
                dbg = true;
            }
        } else {
            dbg = false;
        }

        if ((args.length < 5)
         || (args[0] == null || args[0].equals(""))
         || (args[1] == null || args[1].equals(""))
         || (args[2] == null || args[2].equals(""))
         || (args[3] == null || args[3].equals(""))
         || (args[4] == null || args[4].equals(""))) {
            System.out.println("missing arguments");
            return;
        }

        ExecuteSql sqlExec = new ExecuteSql();
        sqlExec.setUrl(args[0]);
        sqlExec.setUser(args[1]);
        sqlExec.setPassword(args[2]);
        sqlExec.setDriver(args[3]);
        sqlExec.setFilename(args[4]);
        sqlExec.setDebug(dbg);

        sqlExec.executeSql();

    }


    public void executeSql() {
        BufferedReader in;
        String line = "";
        StringBuffer sqlBuf = new StringBuffer();
        String sqlCommand = "";
        boolean quit = false;
        File sqlFile = new File(sqlFilename);

        try {
            if (debug) {
                System.out.println("Start ExecuteSql");
            }
            Class driverClass = Class.forName(driver);
            Driver driverInst = (Driver) driverClass.newInstance();

            Properties sqlProps = new Properties();
            sqlProps.put("user", user);
            sqlProps.put("password", passwd);
            conn = driverInst.connect(url, sqlProps);

            conn.setAutoCommit(false);

            stmtObj = conn.createStatement();
            stmtObj.setEscapeProcessing(true);

            PrintStream out = System.out;

            System.out.println("Executing file: " + sqlFile.getAbsolutePath());
            Reader reader = new FileReader(sqlFile);

            try {

              in = new BufferedReader(new FileReader(sqlFile));
              sqlCommand = "";

              while ((line = in.readLine()) != null && quit == false) {
                  line = line.trim();

                  if (line.startsWith("//") || line.startsWith("--")) {
                      continue;
                  }

                  StringTokenizer st = new StringTokenizer(line);
                  if (st.hasMoreTokens()) {
                      String token = st.nextToken();
                      if ("REM".equalsIgnoreCase(token)) {
                          continue;
                      }
                  }

                  sqlBuf.append(" " + line);

                  // SQL defines "--" as a comment to EOL and in Oracle it may
                  // contain a hint.  So make sure to end it instead of removing
                  // it.
                  // Is this ever reached?
                  if (line.indexOf("--") >= 0) {
                      sqlBuf.append("\n");
                  }

                  if (sqlBuf.toString().endsWith(";")) {
                      int stmtLen = sqlBuf.length() - 1;
                      execSqlStatement(sqlBuf.substring(0, stmtLen), out);

                      sqlBuf.replace(0, sqlBuf.length(), "");
                  }

              }

              // process any statements not followed by ;
              if (!sqlBuf.equals("")) {
                  execSqlStatement(sqlBuf.toString(), out);
              }

            } finally {
              reader.close();
            }

            if (debug) {
                System.out.println("Committing transaction");
            }

            conn.commit();


        } catch (Exception e) {
            System.out.println("Exception during executeSQL: " + e);
            System.out.println("---------------------------------------------------------------");
            System.out.println(sqlCommand);
            System.out.println("---------------------------------------------------------------");
            e.printStackTrace();
        } finally {
            try {
                if (stmtObj != null ) {
                    stmtObj.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Exception during close connection: " + e);
            }
        }

        System.out.println("Successfully executed: " + succeededSql + " out of " + totalSql + " SQL statements");

        System.exit(1);

    }


    /**
     * Execute the sql statement.
     */
    protected void execSqlStatement(String sqlStmt, PrintStream out) throws SQLException {

        if ("".equals(sqlStmt.trim())) {
            return;
        }

        ResultSet resultSet = null;
        try {
            totalSql++;

            if (debug) {
                System.out.println("SQL: " + sqlStmt);
            }

            int updateCt = 0;
            int updateCtTotal = 0;

            boolean ret = stmtObj.execute(sqlStmt);
            updateCt = stmtObj.getUpdateCount();
            resultSet = stmtObj.getResultSet();

            do {
                if (!ret) {
                    if (updateCt != -1) {
                        updateCtTotal += updateCt;
                    }
                }

                ret = stmtObj.getMoreResults();
                if (ret) {
                    updateCt = stmtObj.getUpdateCount();
                    resultSet = stmtObj.getResultSet();
                }
            } while (ret);

            if (debug) {
                System.out.println(updateCtTotal + " rows affected");
            }

            SQLWarning warning = conn.getWarnings();
            while (warning != null) {
                if (debug) {
                    System.out.println(warning + " sql warning");
                }
                warning = warning.getNextWarning();
            }

            conn.clearWarnings();
            succeededSql++;

        } catch (SQLException e) {
            System.out.println("Failed to execute: " + sqlStmt);
            System.out.println(e.toString());
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }

    }


    /**
     * Sets the database connection URL; required.
     * @param url The url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * Sets the user; required.
     * @param user The user to set
     */
    public void setUser(String user) {
        this.user = user;
    }


    /**
     * Sets the password; required.
     * @param password The password to set
     */
    public void setPassword(String passwd) {
        this.passwd = passwd;
    }


    /**
     * Class name of the JDBC driver; required.
     * @param driver The driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }


    /**
     * Sets the filename; required.
     * @param filename The filename to set
     */
    public void setFilename(String filename) {
        this.sqlFilename = filename;
    }


    /**
     * Sets debug flag; optional.
     * @param debug The debug flag to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}
