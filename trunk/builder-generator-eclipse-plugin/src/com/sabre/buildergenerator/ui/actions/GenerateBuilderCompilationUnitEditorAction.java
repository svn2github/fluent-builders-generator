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

    public void selectionChanged(IAction aAction, ISelection aSelection) {
        try {
            ICompilationUnit compilationUnit = getCurrentlyEditedCompilationUnit();

            if (compilationUnitTester.isCompilationUnitSupported(compilationUnit)) {
                enableAction(aAction);
            } else {
                // TODO throw not supported type exception
                disableAction(aAction);
            }
        } catch (JavaModelException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void disableAction(IAction aAction) {
        aAction.setEnabled(false);
    }

    private void enableAction(IAction action) {
        action.setEnabled(true);
    }
}
