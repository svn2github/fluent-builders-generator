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

package com.sabre.buildergenerator.sourcegenerator;

import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureResolver;
import com.sabre.buildergenerator.sourcegenerator.TypeHelper.MethodInspector;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class BuilderGenerator {
    static final String SETTER_PREFIX = "set";

    private final Set<String> typesAlradyGeneratedInnerBuilders = new HashSet<String>();
    private final Set<String> typesToGenerateInnerBuilders = new HashSet<String>();
    private Map<String, Set<String>> typesAndFieldsToGenerate;

    public static interface MethodProvider {
        void process(MethodConsumer consumer);
    }

    public static interface MethodConsumer {
        void nextMethod(IType type, IMethod method);
    }

    public String generateSource(final IType type, String packageName, String builderName, MethodProvider methodProvider,
        String setterPrefix, String collectionSetterPrefix, String endPrefix, boolean doFormat) throws Exception {

        // find classes and fields to generate
        typesAndFieldsToGenerate = retrieveTypesAndFieldsToGenerate(methodProvider);

        // create source builder
        final BuilderSourceGenerator generator = new BuilderSourceGenerator();

        generator.setSetterPrefix(setterPrefix);
        generator.setCollectionElementSetterPrefix(collectionSetterPrefix);
        generator.setEndPrefix(endPrefix);

        StringWriter sw = new StringWriter();

        generator.setOut(new PrintWriter(sw));

        // retrieve type parameter names
        ITypeParameter[] typeParameters = type.getTypeParameters();
        String[] typeParamNames = new String[typeParameters.length];
        int i = 0;
        for (ITypeParameter typeParameter : typeParameters) {
            typeParamNames[i++] = typeParameter.getElementName();
        }

        // generate source
        String typeQName = type.getFullyQualifiedName();
        generator.generateBuilderClass(typeQName, packageName, builderName, typeParamNames);

        generateBuilderBaseClass(generator, typeQName, type, true);
        generateBuilderBaseClasses(generator, type);
        generator.finish();
        sw.flush();

        String builderSource = sw.toString();

        // format source
        if (doFormat) {
            builderSource = formatSource(builderSource);
        }

        return builderSource;
    }

    private Map<String, Set<String>> retrieveTypesAndFieldsToGenerate(MethodProvider methodProvider) {
        if (methodProvider != null) {
            final Map<String, Set<String>> typesAndFieldsToGenerate = new HashMap<String, Set<String>>();
            methodProvider.process(new MethodConsumer() {
                public void nextMethod(IType selectedType, IMethod selectedMethod) {
                    String typeName = selectedType.getFullyQualifiedName();
                    String typeSignature = Signature.createTypeSignature(typeName, false);

                    Set<String> fieldNames = typesAndFieldsToGenerate.get(typeSignature);
                    if (fieldNames == null) {
                        fieldNames = new HashSet<String>();
                        typesAndFieldsToGenerate.put(typeSignature, fieldNames);
                    }

                    String fieldName = fieldNameFromSetter(selectedMethod);
                    fieldNames.add(fieldName);
                }
            });
            return typesAndFieldsToGenerate;
        }
        return null;
    }

    private String formatSource(String builderSource) {
        TextEdit text = ToolFactory.createCodeFormatter(null).format(CodeFormatter.K_COMPILATION_UNIT, builderSource, 0,
                builderSource.length(), 0, "\n");

        // text is null if source cannot be formatted
        if (text != null) {
            Document simpleDocument = new Document(builderSource);

            try {
                text.apply(simpleDocument);
            } catch (MalformedTreeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                builderSource = simpleDocument.get();
            }
        }

        return builderSource;
    }

    private void addTypeToGenerateInnerBuilder(String elementTypeSignature) {
        if ((isBuilderRequestedForType(elementTypeSignature))
                && !typesAlradyGeneratedInnerBuilders.contains(elementTypeSignature)) {
            typesToGenerateInnerBuilders.add(elementTypeSignature);
        }
    }

    private boolean isBuilderRequestedForType(String elementTypeSignature) {
        return typesAndFieldsToGenerate == null || typesAndFieldsToGenerate.get(elementTypeSignature) != null;
    }

    private boolean isSetterRequestedForField(String elementTypeSignature, String fieldName) {
        return typesAndFieldsToGenerate == null || typesAndFieldsToGenerate.get(elementTypeSignature) != null
                && typesAndFieldsToGenerate.get(elementTypeSignature).contains(fieldName);
    }

    private boolean isSetterRequestedForField(IType enclosingType, String fieldName) {
        String enclosingTypeFullyQualifiedName = enclosingType.getFullyQualifiedName('.');
        String enclosingTypeSignature = Signature.createTypeSignature(enclosingTypeFullyQualifiedName, false);
        return isSetterRequestedForField(enclosingTypeSignature, fieldName);
    }

    private void generateBuilderBaseClasses(final BuilderSourceGenerator generator, final IType enclosingType)
        throws Exception {
        while (!typesToGenerateInnerBuilders.isEmpty()) {
            String typeSgn = typesToGenerateInnerBuilders.iterator().next();
            String typeSpec = SignatureResolver.signatureToTypeName(SignatureResolver.resolveSignature(enclosingType,
                        typeSgn));

            final IType resolvedType = SignatureResolver.resolveType(enclosingType, typeSgn);

            generateBuilderBaseClass(generator, typeSpec, resolvedType, false);
            typesToGenerateInnerBuilders.remove(typeSgn);
            typesAlradyGeneratedInnerBuilders.add(typeSgn);
        }
    }

    private void generateBuilderBaseClass(final BuilderSourceGenerator generator, String type,
            final IType resolvedType, boolean isTopLevel) throws JavaModelException, Exception {
        generator.generateBuilderBaseClass(type, resolvedType, isTopLevel); // following methods might add elements to typesUsed

        TypeHelper.findSetterMethods(resolvedType, new MethodInspector() {
                public void nextMethod(IType methodOwnerType, IMethod method,
                    Map<String, String> parameterSubstitution) throws Exception {
                    //Activator.getDefault().getLog().log(new Status(IStatus.OK, Activator.PLUGIN_ID, "nextMethod method=" + method.getElementName() + " type=" + methodOwnerType.getElementName()));

                    String fieldName = fieldNameFromSetter(method);

                    try {
                        String parameterTypeSignature = method.getParameterTypes()[0];
                        String qualifiedParameterTypeSignature = SignatureResolver.resolveTypeWithParameterMapping(
                                resolvedType, parameterTypeSignature, parameterSubstitution);

                        String[] exceptionTypes;

                        exceptionTypes = method.getExceptionTypes();

                        for (int i = 0; i < exceptionTypes.length; i++) {
                            exceptionTypes[i] = SignatureResolver.resolveTypeWithParameterMapping(resolvedType,
                                exceptionTypes[i], parameterSubstitution);
                            exceptionTypes[i] = SignatureResolver.signatureToTypeName(exceptionTypes[i]);
                        }

                        generateSimpleSetter(generator, resolvedType, exceptionTypes, fieldName, qualifiedParameterTypeSignature);
                        generateCollectionAdder(generator, resolvedType, exceptionTypes, fieldName, qualifiedParameterTypeSignature);
                        generateCollectionBuilder(generator, resolvedType, exceptionTypes, fieldName, qualifiedParameterTypeSignature);
                        generateFieldBuilder(generator, resolvedType, exceptionTypes, fieldName, qualifiedParameterTypeSignature);
                    } catch (JavaModelException e) {
                    }
                }
            });
    }

    private void generateSimpleSetter(BuilderSourceGenerator generator, IType enclosingType, String[] exceptionTypes, String fieldName,
        String fieldTypeSignature) {
        if (isSetterRequestedForField(enclosingType, fieldName)) {
            String fieldType = SignatureResolver.signatureToTypeName(fieldTypeSignature);
            generator.addFieldSetter(fieldName, fieldType, exceptionTypes);
        }
    }

    private void generateCollectionAdder(BuilderSourceGenerator generator, IType enclosingType, String[] exceptionTypes,
        String fieldName, String resolvedFieldTypeSignature) throws Exception {
        boolean isFieldACollection = TypeHelper.isCollection(enclosingType, resolvedFieldTypeSignature);
        if (isFieldACollection && isSetterRequestedForField(enclosingType, fieldName)) {
            String elementTypeSignature = TypeHelper.getTypeParameterSignature(resolvedFieldTypeSignature);
            String elementType = SignatureResolver.signatureToTypeName(elementTypeSignature);
            String elementName = pluralToSingle(fieldName);

            String fieldTypeErasureSignature = Signature.getTypeErasure(resolvedFieldTypeSignature);
            String concreteCollectionType = abstractToConcreteCollectionType(fieldTypeErasureSignature);

            generator.addCollectionElementSetter(fieldName, elementName, elementType, concreteCollectionType, exceptionTypes);
        }
    }

    private void generateCollectionBuilder(BuilderSourceGenerator generator, IType enclosingType,
        String[] exceptionTypes, String fieldName, String resolvedFieldTypeSignature) throws Exception {
        boolean isFieldACollection = TypeHelper.isCollection(enclosingType, resolvedFieldTypeSignature);
        if (isFieldACollection && isSetterRequestedForField(enclosingType, fieldName)) {
            String elementTypeSignature = TypeHelper.getTypeParameterSignature(resolvedFieldTypeSignature);
            String elementType = SignatureResolver.signatureToTypeName(elementTypeSignature);
            String elementName = pluralToSingle(fieldName);

            String[] typeParams = Signature.getTypeArguments(elementTypeSignature);

            for (int i = 0; i < typeParams.length; i++) {
                String sig = SignatureResolver.resolveSignature(enclosingType, typeParams[i]);
                typeParams[i] = SignatureResolver.signatureToTypeName(sig);
            }

            if (isSourceClass(enclosingType, elementTypeSignature) && isBuilderRequestedForType(elementTypeSignature)) {
                generator.addCollectionElementBuilder(elementName, elementType, exceptionTypes, typeParams);
                addTypeToGenerateInnerBuilder(Signature.getTypeErasure(elementTypeSignature));
            }
        }
    }

    private void generateFieldBuilder(BuilderSourceGenerator generator, IType enclosingType,
        String[] exceptionTypes, String fieldName, String resolvedFieldTypeSignature)
        throws Exception {
        String fieldType = SignatureResolver.signatureToTypeName(resolvedFieldTypeSignature);

        String[] typeParams = Signature.getTypeArguments(resolvedFieldTypeSignature);
        for (int i = 0; i < typeParams.length; i++) {
            String sig = SignatureResolver.resolveSignature(enclosingType, typeParams[i]);
            typeParams[i] = SignatureResolver.signatureToTypeName(sig);
        }

        if (isSourceClass(enclosingType, resolvedFieldTypeSignature)
                && isSetterRequestedForField(enclosingType, fieldName)
                && isBuilderRequestedForType(resolvedFieldTypeSignature)) {
            generator.addFieldBuilder(fieldName, fieldType, exceptionTypes, typeParams);
            addTypeToGenerateInnerBuilder(Signature.getTypeErasure(resolvedFieldTypeSignature));
        }
    }

    private String abstractToConcreteCollectionType(String collectionTypeErasureSignature) {
        String concreteCollectionType;
        if (collectionTypeErasureSignature.equals("Qjava.util.Collection;")) {
            concreteCollectionType = "java.util.ArrayList";
        } else if (collectionTypeErasureSignature.equals("Qjava.util.List;")) {
            concreteCollectionType = "java.util.ArrayList";
        } else if (collectionTypeErasureSignature.equals("Qjava.util.Set;")) {
            concreteCollectionType = "java.util.HashSet";
        } else if (collectionTypeErasureSignature.equals("Qjava.util.SortedSet;")) {
            concreteCollectionType = "java.util.TreeSet";
        } else {
            concreteCollectionType = SignatureResolver.signatureToTypeName(collectionTypeErasureSignature);
        }
        return concreteCollectionType;
    }

    private boolean isSourceClass(IType enclosingType, String typeSignature) throws JavaModelException, SignatureParserException {
        IType type = SignatureResolver.resolveType(enclosingType, typeSignature);
        return type != null && type.isClass() && type.isStructureKnown() && !type.isBinary();
    }

    private String fieldNameFromSetter(IMethod method) {
        return fieldNameFromSetterName(method.getElementName());
    }

    private String fieldNameFromSetterName(String setterName) {
        String fieldName = setterName.substring(SETTER_PREFIX.length());

        return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    private String pluralToSingle(String name) {
        String elementName;

        if (name.endsWith("Houses")) {
            elementName = name.substring(0, name.length() - 1);
        } else if (name.endsWith("ses")) {
            elementName = name.substring(0, name.length() - 2);
        } else if (name.endsWith("ies")) {
            elementName = name.substring(0, name.length() - 3) + "y";
        } else if (name.endsWith("ves")) {
            elementName = name.substring(0, name.length() - 3) + "f";
        } else if (name.endsWith("ees")) {
            elementName = name.substring(0, name.length() - 1);
        } else if (name.endsWith("s")) {
            elementName = name.substring(0, name.length() - 1);
        } else {
            elementName = name + "Element";
        }

        return elementName;
    }
}
