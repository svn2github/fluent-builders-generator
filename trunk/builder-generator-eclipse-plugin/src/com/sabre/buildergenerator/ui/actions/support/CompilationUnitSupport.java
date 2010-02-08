/**
 * Copyright (c) 2009-2010 fluent-builder-generator for Eclipse commiters.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sabre Polska sp. z o.o. - initial implementation during Hackday
 */

package com.sabre.buildergenerator.ui.actions.support;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;


/**
 * Title: CompilationUnitSupport.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class CompilationUnitSupport {
    /**
     * @param compilationUnit
     * @return
     *  a top level IType from CompilationUnit
     * @throws JavaModelException
     */
    public IType getTopLevelType(ICompilationUnit compilationUnit) throws JavaModelException {
        if (compilationUnit != null) {
            IType[] types = compilationUnit.getTypes();

            if (types.length < 1) {
                return null;
            } else {
                return types[0];
            }
        } else {
            return null;
        }
    }
}
