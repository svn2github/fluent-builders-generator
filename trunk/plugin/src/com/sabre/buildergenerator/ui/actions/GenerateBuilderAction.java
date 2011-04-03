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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import com.sabre.buildergenerator.javamodel.eclipse.EclipseBuilderGenerator;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerationProperties;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator;
import com.sabre.buildergenerator.sourcegenerator.MethodConsumer;
import com.sabre.buildergenerator.sourcegenerator.MethodProvider;
import com.sabre.buildergenerator.ui.MethodNode;
import com.sabre.buildergenerator.ui.TypeNode;
import com.sabre.buildergenerator.ui.TypeTree;
import com.sabre.buildergenerator.ui.actions.support.CompliantCompilationUnitTester;
import com.sabre.buildergenerator.ui.wizard.GenerateBuilderWizard;


/**
 * Title: GenerateBuilderAction.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 *
 * @author Jakub Janczak
 */
public class GenerateBuilderAction {
    private final CompliantCompilationUnitTester compilationUnitTester = new CompliantCompilationUnitTester();
    private final EclipseBuilderGenerator builderGenerator = new EclipseBuilderGenerator();

    /**
     * Starts the generation procedure for the type
     *
     * @param type
     *            a type that the generator is going to be invoked for
     * @param shell
     *                        the shell of the running component
     * @throws CoreException
     */
    @SuppressWarnings("deprecation")
    public void execute(final IType type, final Shell shell, IRunnableContext runnableContext) throws Exception {
        if (!validateAndShowErrorMessageIfNeeded(type, shell)) {
            return;
        }

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

                            String packageName = properties.getPackageName();
                            String builderClassName = properties.getBuilderClassName();

                            aMonitor.worked(1);

                            // creating package
                            if (!selectedSourceFolder.getPackageFragment(packageName).exists()) {
                                selectedSourceFolder.createPackageFragment(packageName, false, aMonitor);
                            }

                            String source = generateSource(builderGenerator, properties);

                            aMonitor.worked(2);

                            // create source file
                            IPath builderPath = selectedSourceFolder.getPath();

                            for (String s : packageName.split("\\.")) {
                                builderPath = builderPath.append(s);
                            }

                            builderPath = builderPath.append(builderClassName).addFileExtension("java");

                            IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(builderPath);

                            if (file.exists()) {
                                file.delete(false, aMonitor);
                            }

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

    private boolean validateAndShowErrorMessageIfNeeded(IType type, Shell shell) throws JavaModelException {
        if (!compilationUnitTester.isTypeSupported(type)) {
            MessageDialog.openError(shell, "Unsupported type selected",
                "Unsupported type selected. Supported class has to: \n" + "Be public\n" + "Have non-arg constructor\n"
                + "Be non-abstract");

            return false;
        }

        return true;
    }

    private String generateSource(BuilderGenerator<IType, ITypeParameter, IMethod, JavaModelException> builderGenerator, final BuilderGenerationProperties properties) throws Exception {

        final TypeTree typeTree = properties.getSettersTypeTree();
        MethodProvider<IType, IMethod> methodProvider = new MethodProvider<IType, IMethod>() {
                public void process(MethodConsumer<IType, IMethod> consumer) {
                    if (typeTree != null) {
                        for (TypeNode typeNode : typeTree.getSortedTypesNodes()) {
                            IType selectedType = typeNode.getElement();

                            if (typeNode.isActive()) {
                                for (MethodNode methodNode : typeNode.getMethodNodes()) {
                                    if (methodNode.isSelected() && methodNode.isAccessibleFromPackage(properties.getPackageName())) {
                                        IMethod selectedMethod = methodNode.getElement();

                                        consumer.nextMethod(selectedType, selectedMethod);
                                    }
                                }
                            }
                        }
                    }
                }
            };

        String builderSource = builderGenerator.generateSource(properties.getType(), properties.getPackageName(), properties.getBuilderClassName(), methodProvider, properties.getMethodsPrefix(),
                properties.getCollectionAddPrefix(), properties.getEndPrefix());
        if (properties.isFormatCode()) {
            builderSource = formatSource(builderSource);
        }

        return builderSource;
    }

    private String formatSource(String sourceCode) {
        TextEdit text = ToolFactory.createCodeFormatter(null).format(CodeFormatter.K_COMPILATION_UNIT, sourceCode, 0,
                sourceCode.length(), 0, "\n");

        // text is null if source cannot be formatted
        if (text != null) {
            Document simpleDocument = new Document(sourceCode);

            try {
                text.apply(simpleDocument);
            } catch (MalformedTreeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                sourceCode = simpleDocument.get();
            }
        }

        return sourceCode;
    }
}
