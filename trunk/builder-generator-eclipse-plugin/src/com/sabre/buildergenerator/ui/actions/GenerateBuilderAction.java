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

package com.sabre.buildergenerator.ui.actions;

import com.sabre.buildergenerator.sourcegenerator.BuilderGenerationProperties;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;
import com.sabre.buildergenerator.ui.wizard.GenerateBuilderWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;

import org.eclipse.swt.widgets.Shell;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;

import java.lang.reflect.InvocationTargetException;

import java.util.Set;


/**
 * Title: GenerateBuilderAction.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */
public class GenerateBuilderAction {
    /**
     * Starts the generation procedure for the type
     *
     * @param type
     *  a type that the generator is going to be invoked for
     * @param shell TODO
     * @throws CoreException
     */
    @SuppressWarnings("deprecation")
    public void execute(final IType type, final Shell shell, IRunnableContext runnableContext) throws Exception {
        GenerateBuilderWizard wizard = new GenerateBuilderWizard(new BuilderGenerationProperties(type));
        WizardDialog dialog = new WizardDialog(shell, wizard);

        dialog.setMinimumPageSize(200, 500);

        if (dialog.open() == Dialog.OK) {
            final BuilderGenerationProperties properties = wizard.getBuilderGenerationProperties();

            IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
                    public void run(IProgressMonitor aMonitor) throws InvocationTargetException, InterruptedException {
                        try {
                            aMonitor.beginTask("Generating class", 4);

                            IPackageFragmentRoot selectedSourceFolder = properties.getSourceFolder();

                            //                            IPackageFragment selectedPackage = properties.getPackage();
                            String packageName = properties.getPackageName();
                            String setterPrefix = properties.getMethodsPrefix();
                            String collectionSetterPrefix = properties.getCollectionAddPrefix();
                            String endPrefix = properties.getEndPrefix();
                            String builderClassName = properties.getBuilderClassName();
                            Set<IMethod> selectedSetters = properties.getSelectedMethods();
                            boolean formatCode = properties.isFormatCode();

                            // generate source code
                            String[] fieldNames = new String[selectedSetters.size()];

                            int i = 0;

                            for (IMethod setter : selectedSetters) {
                                String tmp = setter.getElementName().substring(3);

                                fieldNames[i++] = Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);
                            }

                            //                            String packageName = selectedPackage.getElementName();

                            aMonitor.worked(1);

                            // creating package
                            if (!selectedSourceFolder.getPackageFragment(packageName).exists()) {
                                selectedSourceFolder.createPackageFragment(packageName, false, aMonitor);
                            }

                            String source =
                                new BuilderGenerator().generateSource(type, packageName, builderClassName, fieldNames,
                                    setterPrefix, collectionSetterPrefix, endPrefix, formatCode);

                            aMonitor.worked(2);

                            // create source file
                            IPath builderPath = selectedSourceFolder.getPath();

                            for (String s : packageName.split("\\.")) {
                                builderPath = builderPath.append(s);
                            }

                            builderPath = builderPath.append(builderClassName).addFileExtension("java");

                            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(builderPath);

                            file.create(new StringBufferInputStream(source), false, aMonitor);
                        } catch (Throwable e) {
                            throw new InvocationTargetException(e);
                        } finally {
                            aMonitor.done();
                        }
                    }
                };

            try {
                runnableContext.run(true, false, runnableWithProgress);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                StringWriter buf = new StringWriter();
                t.printStackTrace(new PrintWriter(buf));
                MessageDialog.openError(null, "Error", buf.toString());
                t.printStackTrace();
            } catch (InterruptedException e) {
                StringWriter buf = new StringWriter();
                e.printStackTrace(new PrintWriter(buf));
                MessageDialog.openError(null, "Error", buf.toString());
                e.printStackTrace();
            } catch (Throwable e) {
                StringWriter buf = new StringWriter();
                e.printStackTrace(new PrintWriter(buf));
                MessageDialog.openError(null, "Error", buf.toString());
                e.printStackTrace();
            }
        }
    }
}
