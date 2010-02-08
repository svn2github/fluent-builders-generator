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
