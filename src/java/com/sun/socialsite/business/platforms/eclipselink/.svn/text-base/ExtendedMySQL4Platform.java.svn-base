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

package com.sun.socialsite.business.platforms.eclipselink;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.platform.database.MySQLPlatform;


/**
 * Extends MySQL4Platform to make use of LIMIT/OFFSET clauses in queries.
 *
 * Note: using SuppressWarnings annotation because superclass has deprecated methods.
 *
 * TODO: do we still need to override this to fix LIMIT handling in MySQL?
 */
@SuppressWarnings(value="deprecation")
public class ExtendedMySQL4Platform extends MySQLPlatform {

    private static Log log = LogFactory.getLog(ExtendedMySQL4Platform.class);

    public ExtendedMySQL4Platform() {
        super();
    }

    @Override
    public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {

        String s;
        int firstResult = statement.getQuery().getFirstResult();
        int maxRows = statement.getQuery().getMaxRows();

        s = String.format("ExtendedMySQL4Platform.printSQLSelectStatement Inputs: firstResult=%d maxRows=%d]", firstResult, maxRows);
        log.debug(s);

        call.setFields(statement.printSQL(printer));

        if (maxRows > 0) {
            printer.printString(" LIMIT ");
            printer.printString(Integer.toString(maxRows - firstResult));
            call.setIgnoreFirstRowMaxResultsSettings(true);
        }

        if (firstResult > 0) {
            printer.printString(" OFFSET ");
            printer.printString(Integer.toString(firstResult));
            call.setIgnoreFirstRowMaxResultsSettings(true);
        }

        s = String.format("ExtendedMySQL4Platform.printSQLSelectStatement Returning: %s", printer.getWriter().toString());
        log.debug(s);
    }

}
