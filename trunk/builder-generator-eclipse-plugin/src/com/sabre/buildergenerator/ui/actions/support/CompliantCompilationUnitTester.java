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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;


/**
 * Title: CompliantCompilationUnitTester.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class CompliantCompilationUnitTester {
    private final CompilationUnitSupport compilationUnitSupport;

    /**
    *
    */
    public CompliantCompilationUnitTester() {
        this.compilationUnitSupport = new CompilationUnitSupport();
    }

    public boolean isCompilationUnitSupported(ICompilationUnit compilationUnit) throws JavaModelException {
        if (compilationUnit.isStructureKnown()) {
            return isTypeSupported(compilationUnitSupport.getTopLevelType(compilationUnit));
        } else {
            return false;
        }
    }

    public boolean isTypeSupported(IType type) throws JavaModelException {
        boolean supported = true;

        supported &= checkFlags(type);
        supported &= checkContructor(type);

        return supported;
    }

    /**
     * @param aType
     * @param aSupported
     * @return
     * @throws JavaModelException
     */
    private boolean checkContructor(IType aType) throws JavaModelException {
        boolean hasDefinedConstructor = false;
        boolean hasNonParameterConstructor = false;

        for (IMethod method : aType.getMethods()) {
            if (method.isConstructor()) {
                hasDefinedConstructor = true;

                if (method.getParameterNames().length == 0) {
                    hasNonParameterConstructor = true;
                }
            }
        }

        if (hasDefinedConstructor && (!hasNonParameterConstructor)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkFlags(IType type) throws JavaModelException {
        int flags = type.getFlags();

        return !Flags.isAbstract(flags) && Flags.isPublic(flags);
    }
}
