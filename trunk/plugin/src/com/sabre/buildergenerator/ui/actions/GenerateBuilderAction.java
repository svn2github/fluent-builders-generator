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

import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

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

import com.sabre.buildergenerator.sourcegenerator.BuilderGenerationProperties;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;
import com.sabre.buildergenerator.ui.wizard.GenerateBuilderWizard;


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
        WizardDialog wizardDialog = new WizardDialog(shell, wizard);

        wizardDialog.setMinimumPageSize(200, 500);

        if (wizardDialog.open() == Dialog.OK) {
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

                            //                            String packageName = selectedPackage.getElementName();

                            aMonitor.worked(1);

                            // creating package
                            if (!selectedSourceFolder.getPackageFragment(packageName).exists()) {
                                selectedSourceFolder.createPackageFragment(packageName, false, aMonitor);
                            }

                            String source =
                                new BuilderGenerator().generateSource(type, packageName, builderClassName, selectedSetters,
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
