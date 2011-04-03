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

import com.sabre.buildergenerator.javamodel.IModelHelper;
import com.sabre.buildergenerator.javamodel.IModelHelper.IMethodInspector;
import com.sabre.buildergenerator.javamodel.ISignatureResolver;
import com.sabre.buildergenerator.javamodel.ITypeAccessor;
import com.sabre.buildergenerator.javamodel.ITypeResolver;
import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Map;


public class BuilderGenerator<IType, ITypeParameter, IMethod, JavaModelException extends Exception> {
    private static final String SETTER_PREFIX = "set";

    private IModelHelper<IType, IMethod, JavaModelException> typeHelper;
    private ISignatureResolver<IType, JavaModelException> signatureResolver;
    private ITypeResolver<IType, JavaModelException> typeResolver;
    private ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException> typeAccessor;
    private MarkedFields<IType, IMethod, JavaModelException> typesAndFieldsToGenerate;
    private final ClassesToProcess classesToProcess = new ClassesToProcess();

    /**
     * @param type
     * @param packageName
     * @param builderName
     * @param methodProvider
     * @param setterPrefix
     * @param collectionSetterPrefix
     * @param endPrefix
     * @param doFormat
     *  perform formatting ?
     * @return
     * @throws Exception
     */
    public String generateSource(final IType type, String packageName, String builderName,
        MethodProvider<IType, IMethod> methodProvider, String setterPrefix, String collectionSetterPrefix, String endPrefix) throws Exception {
        // find classes and fields to generate
        typesAndFieldsToGenerate.retrieveTypesAndFieldsToGenerate(methodProvider);

        // create source builder
        BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator = new BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException>();
        generator.setTypeAccessor(typeAccessor);
        generator.setTypeResolver(typeResolver);

        generator.setSetterPrefix(setterPrefix);
        generator.setCollectionElementSetterPrefix(collectionSetterPrefix);
        generator.setEndPrefix(endPrefix);

        StringWriter sw = new StringWriter();

        generator.setOut(new PrintWriter(sw));

        // retrieve type parameter names
        ITypeParameter[] typeParameters = typeAccessor.getTypeParameters(type);
        String[] typeParamNames = new String[typeParameters.length];
        String[][] typeParamBounds = new String[typeParameters.length][];
        int i = 0;

        for (ITypeParameter typeParameter : typeParameters) {
            typeParamNames[i] = typeAccessor.getTypeParameterName(typeParameter);
            typeParamBounds[i] = typeAccessor.getTypeParameterBounds(typeParameter);
            i++;
        }

        // generate source
        String typeQName = typeAccessor.getFullyQualifiedName(type);

        generator.generateBuilderClass(type, typeQName, packageName, builderName, typeParamNames, typeParamBounds);
        String typeSignature = signatureResolver.createTypeSignature(typeQName);
        classesToProcess.markAsAlreadyProcessed(typeSignature);

        generateBuilderBaseClass(generator, typeQName, type, true);

        generateBuilderBaseClasses(generator, type);
        generator.finish();
        sw.flush();

        String builderSource = sw.toString();

        return builderSource;
    }

    private void generateBuilderBaseClasses(final BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, final IType enclosingType)
        throws Exception {
        String typeSgn = null;
        while ((typeSgn = classesToProcess.nextClassToProcess()) != null) {
            final IType resolvedType = signatureResolver.resolveType(enclosingType, typeSgn);

            if (resolvedType != null) {
                String typeSpec = SignatureUtil.signatureToTypeName(signatureResolver.resolveSignature(enclosingType,
                            typeSgn));

                generateBuilderBaseClass(generator, typeSpec, resolvedType, false);
            }
        }
    }

    private void generateBuilderBaseClass(final BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, String type, final IType resolvedType,
        boolean isTopLevel) throws JavaModelException, Exception {
        generator.generateBuilderBaseClass(type, resolvedType, isTopLevel); // following methods might add elements to typesUsed

        typeHelper.findSetterMethods(resolvedType, new IMethodInspector<IType, IMethod>() {
                public void nextMethod(IType methodOwnerType, IMethod method, Map<String, String> parameterSubstitution)
                    throws Exception {
                    String fieldName = fieldNameFromSetter(method);

//                    try {
                        String parameterTypeSignature = typeAccessor.getMethodParameterTypes(method)[0];
                        String qualifiedParameterTypeSignature = signatureResolver.resolveTypeWithParameterMapping(
                                methodOwnerType, parameterTypeSignature, parameterSubstitution);

                        String[] exceptionSignatures = typeAccessor.getMethodExceptionTypes(method);

                        for (int i = 0; i < exceptionSignatures.length; i++) {
                            exceptionSignatures[i] = signatureResolver.resolveTypeWithParameterMapping(methodOwnerType,
                                    exceptionSignatures[i], parameterSubstitution);
                        }

                        if (typesAndFieldsToGenerate.isSetterRequestedForField(resolvedType, fieldName)) {
                            generateSimpleSetter(generator, methodOwnerType, exceptionSignatures, fieldName,
                                qualifiedParameterTypeSignature);
                            generateCollectionAdder(generator, methodOwnerType, exceptionSignatures, fieldName,
                                qualifiedParameterTypeSignature);
                            generateCollectionBuilder(generator, methodOwnerType, exceptionSignatures, fieldName,
                                qualifiedParameterTypeSignature);
                            generateFieldBuilder(generator, methodOwnerType, exceptionSignatures, fieldName,
                                qualifiedParameterTypeSignature);
                        }
//                    } catch (Exception e) {
//                        Activator.getDefault().getLog().log(
//                            new Status(IStatus.ERROR, Activator.PLUGIN_ID,
//                                "couldn't handle field '" + fieldName + "' for type '"
//                                + typeAccessor.getFullyQualifiedParameterizedName(resolvedType) + "'", e));
//                    }
                }
            });
    }

    private void generateSimpleSetter(BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, IType enclosingType,
        String[] exceptionSignatures, String fieldName, String fieldTypeSignature) {
        String fieldType = SignatureUtil.signatureToTypeName(fieldTypeSignature);
        String[] exceptionTypes = signaturesToTypes(exceptionSignatures);

        generator.addFieldSetter(fieldName, fieldType, exceptionTypes);
    }

    private void generateCollectionAdder(BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, IType enclosingType,
        String[] exceptionSignatures, String fieldName, String resolvedFieldTypeSignature) throws Exception {
        boolean isFieldACollection = typeHelper.isCollection(enclosingType, resolvedFieldTypeSignature);

        if (isFieldACollection) {
            String elementTypeSignature = typeHelper.getTypeParameterSignature(resolvedFieldTypeSignature);
            String elementType = SignatureUtil.signatureToTypeName(elementTypeSignature);
            String elementName = pluralToSingle(fieldName);

            String fieldTypeErasureSignature = signatureResolver.getTypeErasure(resolvedFieldTypeSignature);
            String concreteCollectionType = abstractToConcreteCollectionType(fieldTypeErasureSignature);

            String[] exceptionTypes = signaturesToTypes(exceptionSignatures);

            generator.addCollectionElementSetter(fieldName, elementName, elementType, concreteCollectionType,
                exceptionTypes);
        }
    }

    private void generateCollectionBuilder(BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, IType enclosingType,
        String[] exceptionSignatures, String fieldName, String resolvedFieldTypeSignature) throws Exception {
        boolean isFieldACollection = typeHelper.isCollection(enclosingType, resolvedFieldTypeSignature);

        if (isFieldACollection) {
            String elementTypeSignature = typeHelper.getTypeParameterSignature(resolvedFieldTypeSignature);
            String elementType = SignatureUtil.signatureToTypeName(elementTypeSignature);
            String elementName = pluralToSingle(fieldName);

            String[] typeArgs = signatureResolver.getTypeArguments(elementTypeSignature);

            for (int i = 0; i < typeArgs.length; i++) {
                String sig = signatureResolver.resolveSignature(enclosingType, typeArgs[i]);

                typeArgs[i] = SignatureUtil.signatureToTypeName(sig);
            }

            if (isSourceClass(enclosingType, elementTypeSignature) && typesAndFieldsToGenerate.isBuilderRequestedForType(elementTypeSignature)) {
                String[] exceptionTypes = signaturesToTypes(exceptionSignatures);

                generator.addCollectionElementBuilder(elementName, elementType, exceptionTypes, typeArgs);
                classesToProcess.addForProcessing(signatureResolver.getTypeErasure(elementTypeSignature));
            }
        }
    }

    private void generateFieldBuilder(BuilderSourceGenerator<IType, ITypeParameter, IMethod, JavaModelException> generator, IType enclosingType,
        String[] exceptionSignatures, String fieldName, String resolvedFieldTypeSignature) throws Exception {
        String fieldType = SignatureUtil.signatureToTypeName(resolvedFieldTypeSignature);

        String[] typeArgs = signatureResolver.getTypeArguments(resolvedFieldTypeSignature);

        for (int i = 0; i < typeArgs.length; i++) {
            String sig = signatureResolver.resolveSignature(enclosingType, typeArgs[i]);

            typeArgs[i] = SignatureUtil.signatureToTypeName(sig);
        }

        if (isSourceClass(enclosingType, resolvedFieldTypeSignature)
                && typesAndFieldsToGenerate.isBuilderRequestedForType(resolvedFieldTypeSignature)) {
            String[] exceptionTypes = signaturesToTypes(exceptionSignatures);

            generator.addFieldBuilder(fieldName, fieldType, exceptionTypes, typeArgs);
            classesToProcess.addForProcessing(signatureResolver.getTypeErasure(resolvedFieldTypeSignature));
        }
    }

    private String abstractToConcreteCollectionType(String collectionTypeErasureSignature) {
        String concreteCollectionType;

        if (collectionTypeErasureSignature.matches("[QL]java.util.Collection;")) {
            concreteCollectionType = "java.util.ArrayList";
        } else if (collectionTypeErasureSignature.matches("[QL]java.util.List;")) {
            concreteCollectionType = "java.util.ArrayList";
        } else if (collectionTypeErasureSignature.matches("[QL]java.util.Set;")) {
            concreteCollectionType = "java.util.HashSet";
        } else if (collectionTypeErasureSignature.matches("[QL]java.util.SortedSet;")) {
            concreteCollectionType = "java.util.TreeSet";
        } else {
            concreteCollectionType = SignatureUtil.signatureToTypeName(collectionTypeErasureSignature);
        }

        return concreteCollectionType;
    }

    private String[] signaturesToTypes(String[] exceptionSignatures) {
        String[] exceptionTypes = new String[exceptionSignatures.length];

        for (int i = 0; i < exceptionSignatures.length; i++) {
            exceptionTypes[i] = SignatureUtil.signatureToTypeName(exceptionSignatures[i]);
        }

        return exceptionTypes;
    }

    private boolean isSourceClass(IType enclosingType, String typeSignature) throws JavaModelException,
        SignatureParserException {
        IType type = signatureResolver.resolveType(enclosingType, typeSignature);

        boolean isClassFromSource = typeAccessor.isClassFromSource(type);
        return type != null && isClassFromSource;
    }

    // TODO move to helper
    public String fieldNameFromSetter(IMethod method) {
        return fieldNameFromSetterName(typeAccessor.getMethodName(method));
    }

    // TODO move to helper
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

    public void setTypeHelper(IModelHelper<IType, IMethod, JavaModelException> typeHelper) {
        this.typeHelper = typeHelper;
    }

    public void setSignatureResolver(ISignatureResolver<IType, JavaModelException> signatureResolver) {
        this.signatureResolver = signatureResolver;
    }

    public void setTypeAccessor(ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException> typeAccessor) {
        this.typeAccessor = typeAccessor;
    }

    public void setTypesAndFieldsToGenerate(MarkedFields<IType, IMethod, JavaModelException> typesAndFieldsToGenerate) {
        this.typesAndFieldsToGenerate = typesAndFieldsToGenerate;
    }

    public void setTypeResolver(ITypeResolver<IType, JavaModelException> typeResolver) {
        this.typeResolver = typeResolver;
    }
}
