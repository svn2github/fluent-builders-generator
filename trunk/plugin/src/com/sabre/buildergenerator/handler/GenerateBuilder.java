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

package com.sabre.buildergenerator.handler;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.sabre.buildergenerator.ui.actions.GenerateBuilderAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.handlers.HandlerUtil;


public class GenerateBuilder extends AbstractHandler {
    private final GenerateBuilderAction generateBuilderAction;

    public GenerateBuilder() {
        generateBuilderAction = new GenerateBuilderAction();
    }

    public Object execute(ExecutionEvent aEvent) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(aEvent);

        Object firstElement = selection.getFirstElement();

        if (firstElement instanceof ICompilationUnit) {
            ICompilationUnit cu = (ICompilationUnit) firstElement;

            try {
                IType[] allTypes = cu.getTypes();

                if (allTypes != null && allTypes.length == 1) {
                    generateBuilderAction.execute(allTypes[0], null, HandlerUtil.getActiveWorkbenchWindow(aEvent));
                } else {
                    MessageDialog.openError(HandlerUtil.getActiveShell(aEvent), "Error",
                        "No top level type in supplied compilation unit");
                }
            } catch (CoreException e) {
                MessageDialog.openError(HandlerUtil.getActiveShell(aEvent), "Error", e.getMessage());
            } catch (Throwable e) {
                StringWriter buf = new StringWriter();
                e.printStackTrace(new PrintWriter(buf));
                MessageDialog.openError(HandlerUtil.getActiveShell(aEvent), "Error", buf.toString());
                throw new ExecutionException("Error", e);
            }
        } else {
            MessageDialog.openInformation(HandlerUtil.getActiveShell(aEvent), "Information",
                "Please select a Java source file");
        }

        return null;
    }
}
