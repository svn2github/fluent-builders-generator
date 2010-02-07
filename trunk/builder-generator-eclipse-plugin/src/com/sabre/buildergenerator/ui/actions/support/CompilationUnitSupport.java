/*
* Copyright (c) 2009 by Sabre Holdings Corp.
* 3150 Sabre Drive, Southlake, TX 76092 USA
* All rights reserved.
*
* This software is the confidential and proprietary information
* of Sabre Holdings Corporation ("Confidential Information").
* You shall not disclose such Confidential Information and shall
* use it only in accordance with the terms of the license agreement
* you entered into with Sabre Holdings Corporation.
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
