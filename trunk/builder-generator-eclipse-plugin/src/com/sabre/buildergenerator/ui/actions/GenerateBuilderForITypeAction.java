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

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IType;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public class GenerateBuilderForITypeAction implements IObjectActionDelegate {
    private IWorkbenchPart activePart;
    private IType currentSelectedType;
    private final GenerateBuilderAction generateBuilderAction;

    public GenerateBuilderForITypeAction() {
        this.generateBuilderAction = new GenerateBuilderAction();
    }

    public void setActivePart(IAction aAction, IWorkbenchPart aTargetPart) {
        this.activePart = aTargetPart;
    }

    public void run(IAction aAction) {
        try {
            if (currentSelectedType != null) {
                try {
                    generateBuilderAction.execute(currentSelectedType, activePart.getSite().getShell(),
                        activePart.getSite().getWorkbenchWindow());
                } catch (CoreException e) {
                    MessageDialog.openError(activePart.getSite().getShell(), "Error", e.getMessage());
                }
            } else {
                MessageDialog.openError(activePart.getSite().getShell(), "Error",
                    "No top level type in supplied compilation unit");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void selectionChanged(IAction aAction, ISelection aSelection) {
        if (aSelection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) aSelection;
            Object element = structuredSelection.getFirstElement();

            if (element instanceof IType) {
                currentSelectedType = (IType) element;
            } else {
                currentSelectedType = null;
            }
        } else {
            currentSelectedType = null;
        }
    }
}
