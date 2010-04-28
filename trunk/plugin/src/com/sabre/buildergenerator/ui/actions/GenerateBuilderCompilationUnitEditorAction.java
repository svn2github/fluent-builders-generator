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

package com.sabre.buildergenerator.ui.actions;

import com.sabre.buildergenerator.ui.actions.support.CompilationUnitSupport;
import com.sabre.buildergenerator.ui.actions.support.CompliantCompilationUnitTester;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;


public class GenerateBuilderCompilationUnitEditorAction implements IEditorActionDelegate {
    private IEditorPart activeEditor;

    private CompilationUnitSupport compilationUnitSupport;

    private CompliantCompilationUnitTester compilationUnitTester;

    private GenerateBuilderAction generateBuilderAction;

    public GenerateBuilderCompilationUnitEditorAction() {
        this.generateBuilderAction = new GenerateBuilderAction();
        this.compilationUnitTester = new CompliantCompilationUnitTester();
        this.compilationUnitSupport = new CompilationUnitSupport();
    }

    public void setActiveEditor(IAction aAction, IEditorPart aTargetEditor) {
        this.activeEditor = aTargetEditor;
    }

    public void run(IAction aAction) {
        try {
            IType topLevelType = null;

            topLevelType = compilationUnitSupport.getTopLevelType(getCurrentlyEditedCompilationUnit());

            if (topLevelType != null) {
                try {
                    generateBuilderAction.execute(topLevelType, activeEditor.getSite().getShell(),
                        activeEditor.getSite().getWorkbenchWindow());
                } catch (CoreException e) {
                    MessageDialog.openError(activeEditor.getSite().getShell(), "Error", e.getMessage());
                }
            } else {
                MessageDialog.openError(activeEditor.getSite().getShell(), "Error",
                    "No top level type in supplied compilation unit");
            }
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private ICompilationUnit getCurrentlyEditedCompilationUnit() {
        IJavaElement javaElement = JavaUI.getEditorInputJavaElement(activeEditor.getEditorInput());

        if (javaElement instanceof ICompilationUnit) {
            return (ICompilationUnit) javaElement;
        } else {
            return null;
        }
    }

	/**
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction aAction, ISelection aSelection) {
		
	}

}
