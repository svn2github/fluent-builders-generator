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

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.sourcegenerator.java.Imports;
import com.sabre.buildergenerator.sourcegenerator.java.IndentWriter;
import com.sabre.buildergenerator.sourcegenerator.java.JavaSource;
import com.sabre.buildergenerator.sourcegenerator.java.JavaSourceBuilder;


public abstract class AbstractBuilderSourceGenerator<TClassType> {
    private static final String BUILDER_TYPE_ARG_NAME = "GeneratorT";
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String BUILDER_BASE_SUFFIX = "Builder";
    private static final String FIELD_BUILDER_SUFFIX = "Builder";
    private static final String MAIN_BUILDER_NAME = "Instance";

    private String setterPrefix = "with";
    private String collectionElementSetterPrefix = "withAdded";
    private String endPrefix = "end";

    private String buildClassName;
    private String buildClassType;
    private String builderClassName;

    private String innerBuildClassName;
    private String innerBuildClassType;
    private String innerBuilderClassName;

    private PrintWriter out;

    private final Imports imports = new Imports();
    private JavaSourceBuilder javaSourceBuilder;
    private JavaSourceBuilder.ClazzClazzBuilder topClassBuilder;
    private JavaSourceBuilder.ClazzClazzBuilder.InnerClassClazzBuilder innerClassBuilder;

    private Set<String> nonTypeNames = null;
    private String builderPackage;

    public AbstractBuilderSourceGenerator() {
        javaSourceBuilder = JavaSourceBuilder.javaSource();
    }

    /**
     * @return the collectionElementSetterPrefix
     */
    public String getCollectionElementSetterPrefix() {
        return collectionElementSetterPrefix;
    }

    /**
     * @param aCollectionElementSetterPrefix the collectionElementSetterPrefix to set
     */
    public void setCollectionElementSetterPrefix(String aCollectionElementSetterPrefix) {
        collectionElementSetterPrefix = aCollectionElementSetterPrefix;
    }

    /**
     * @return the endPrefix
     */
    public String getEndPrefix() {
        return endPrefix;
    }

    /**
     * @param aEndPrefix the endPrefix to set
     */
    public void setEndPrefix(String aEndPrefix) {
        endPrefix = aEndPrefix;
    }

    /**
     * @return the setterPrefix
     */
    public String getSetterPrefix() {
        return setterPrefix;
    }

    /**
     * @param aSetterPrefix the setterPrefix to set
     */
    public void setSetterPrefix(String aSetterPrefix) {
        setterPrefix = aSetterPrefix;
    }

    public void setOut(PrintWriter printWriter) {
        out = printWriter;
    }

    public void startBuilderClass(TClassType aBuildClassDescriptor, String aPackageForBuilder, String aBuilderClassName, String[] typeParamNames) {
        builderPackage = aPackageForBuilder;

        buildClassName = getClassName(aBuildClassDescriptor);
        buildClassType = imports.getUnqualified(getType(aBuildClassDescriptor), nonTypeNames, builderPackage);
        builderClassName = aBuilderClassName;

        String baseClass = buildClassName + BUILDER_BASE_SUFFIX + "<" + MAIN_BUILDER_NAME;
        for (String typeParamName : typeParamNames) {
            baseClass += ", " + typeParamName;
        }
        baseClass += ">";
        if (aPackageForBuilder != null && aPackageForBuilder.length() > 0) {
            javaSourceBuilder.withPackge(aPackageForBuilder);
        }
        topClassBuilder = javaSourceBuilder.withClazz().withModifiers(JavaSource.MODIFIER_PUBLIC).withName(builderClassName)
            // static create metod
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC + JavaSource.MODIFIER_STATIC).withName(toLowerCaseStart(buildClassName)).withReturnType(MAIN_BUILDER_NAME)
                .withReturnValue().withStatement("new " + MAIN_BUILDER_NAME + "()").withParam(builderClassName).endReturnValue()
            .endMethod()
            // inner builder instance
            .withInnerClass().withModifiers(JavaSource.MODIFIER_PUBLIC + JavaSource.MODIFIER_STATIC).withName(MAIN_BUILDER_NAME).withBaseClazz(baseClass)
                // default constructor
                .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withName(MAIN_BUILDER_NAME)
                    .withInstruction().withStatement("super(new %s());").withParam(buildClassType).endInstruction()
                .endMethod()
                // build()
                .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withName("build").withReturnType(buildClassType)
                    .withReturnValue().withStatement("getInstance()").endReturnValue()
                .endMethod()
            .endInnerClass();
    }

    public void endBuilderClass() {
        javaSourceBuilder = topClassBuilder.endClazz();
    }

    public void startBuilderBaseClass(TClassType buildClassDescriptor, IType type) throws JavaModelException {
        ITypeParameter[] typeParameters = type.getTypeParameters();

        nonTypeNames = new HashSet<String>();
        for(ITypeParameter typeParam : typeParameters) {
            nonTypeNames.add(typeParam.getElementName());
        }

        innerBuildClassName = getClassName(buildClassDescriptor);
        innerBuildClassType = imports.getUnqualified(getType(buildClassDescriptor), nonTypeNames, builderPackage);
        innerBuilderClassName = innerBuildClassName + BUILDER_BASE_SUFFIX;

        String typeArg = BUILDER_TYPE_ARG_NAME + " extends " + innerBuilderClassName;
        innerClassBuilder = topClassBuilder.withInnerClass()
            .withModifiers(JavaSource.MODIFIER_PUBLIC + JavaSource.MODIFIER_STATIC)
            .withAnnotation("@SuppressWarnings(\"unchecked\")")
            .withName(innerBuilderClassName).withTypeArg(typeArg);
        for (ITypeParameter param : typeParameters) {
            innerClassBuilder.withTypeArg(param.getSource());
        }
        innerClassBuilder
            .withDeclaration().withStatement("private %s instance;").withParam(innerBuildClassType).endDeclaration()
            .withMethod().withModifiers(JavaSource.MODIFIER_PROTECTED).withName(innerBuilderClassName)
                .withParameter().withType(innerBuildClassType).withName("aInstance").endParameter()
                .withInstruction().withStatement("instance = aInstance;").endInstruction()
            .endMethod()
            .withMethod().withModifiers(JavaSource.MODIFIER_PROTECTED).withReturnType(innerBuildClassType).withName("getInstance")
                .withReturnValue().withStatement("instance").endReturnValue()
            .endMethod();
    }

    public void endBuilderBaseClass() {
        topClassBuilder = innerClassBuilder.endInnerClass();
    }

    public void addFieldSetter(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String type = imports.getUnqualified(getType(fieldTypeDescriptor),nonTypeNames, builderPackage);
        generateSimpleSetter(fieldName, type, exceptionTypes, BUILDER_TYPE_ARG_NAME, true);
    }

    public void addFieldBuilder(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions, String[] typeParamNames) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String fieldClassName = getClassName(fieldTypeDescriptor);
        String fieldType = imports.getUnqualified(getType(fieldTypeDescriptor), nonTypeNames, builderPackage);
        String innerBuilderName = fieldClassName + BUILDER_BASE_SUFFIX;
        String fieldBuilderName = toUpperCaseStart(fieldName + fieldClassName + FIELD_BUILDER_SUFFIX);
        String methodName = prefixed(setterPrefix, fieldName);

        generateBuilderSetter(fieldName, fieldType, methodName, exceptionTypes, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, BUILDER_TYPE_ARG_NAME, typeParamNames, true);
    }

    public void addCollectionElementSetter(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType elementTypeDescriptor, TClassType collectionContainerTypeDecriptor, TClassType[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String elementType = imports.getUnqualified(getType(elementTypeDescriptor), nonTypeNames, builderPackage);

        String collectionContainerType = imports.getUnqualified(getType(collectionContainerTypeDecriptor), nonTypeNames, builderPackage);
        generateCollectionElementSetter(fieldName, collectionContainerType, elementName,
                elementType, exceptionTypes, BUILDER_TYPE_ARG_NAME, true);
    }

    public void addCollectionElementBuilder(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType elementTypeDescriptor, TClassType[] exceptions, String[] typeParams) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String elementType = imports.getUnqualified(getType(elementTypeDescriptor), nonTypeNames, builderPackage);
        String fieldClassName = getClassName(elementTypeDescriptor);
        String innerBuilderName = fieldClassName + BUILDER_BASE_SUFFIX;
        String fieldBuilderName = toUpperCaseStart(elementName + fieldClassName + FIELD_BUILDER_SUFFIX);
        String methodName = prefixed(collectionElementSetterPrefix, elementName);
        for (int i = 0; i < typeParams.length;i++) {
            typeParams[i] = imports.getUnqualified(typeParams[i], nonTypeNames, builderPackage);
        }

        generateBuilderSetter(elementName, elementType, methodName, exceptionTypes, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, BUILDER_TYPE_ARG_NAME, typeParams, true);
    }

    private void generateBuilderSetter(String fieldName, String fieldType, String methodName,
            String[] exceptions, String fieldBuilderName, String baseBuilderName, String builderClassName2,
            String builderType, String[] typeParams, boolean castBuilderType) {
        String baseClass = baseBuilderName + "<" + fieldBuilderName;
        for (String typeParam : typeParams) {
            baseClass += ", " + typeParam;
        }
        baseClass += ">";
        innerClassBuilder
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(fieldBuilderName).withName(methodName).withExceptions(Arrays.asList(exceptions))
                .withInstruction().withStatement("%s obj = new %s();").withParam(fieldType).withParam(fieldType).endInstruction()
                .withInstruction().endInstruction()
                .withInstruction().withStatement("%s(obj);").withParam(methodName).endInstruction()
                .withInstruction().endInstruction()
                .withReturnValue().withStatement("new %s(obj)").withParam(fieldBuilderName).endReturnValue()
            .endMethod()
            .withInnerClass().withModifiers(JavaSource.MODIFIER_PUBLIC).withName(fieldBuilderName).withBaseClazz(baseClass)
                .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withName(fieldBuilderName)
                    .withParameter().withType(fieldType).withName("aInstance").endParameter()
                    .withInstruction().withStatement("super(aInstance);").endInstruction()
                .endMethod()
                .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(builderType).withName(prefixed(endPrefix, fieldName))
                    .withReturnValue().withStatement(castBuilderType ? "(%s) %s.this" : "this").withParam(builderType).withParam(builderClassName2).endReturnValue()
                .endMethod()
            .endInnerClass();
    }

    private void generateCollectionElementSetter(String collectionFieldName, String collectionContainerType,
            String elementName, String elementType, String[] exceptions, String builderType, boolean castBuilderType) {
        String methodName = prefixed(collectionElementSetterPrefix, elementName);
        innerClassBuilder
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(builderType).withName(methodName).withExceptions(Arrays.asList(exceptions))
                .withParameter().withType(elementType).withName("aValue").endParameter()
                .withInstruction().withStatement("if (instance.%s() == null) {").withParam(getterName(collectionFieldName)).endInstruction()
                .withInstruction().withStatement("    instance.%s(new %s<%s>());").withParam(setterName(collectionFieldName)).withParam(collectionContainerType).withParam(elementType).endInstruction()
                .withInstruction().withStatement("}").endInstruction()
                .withInstruction().endInstruction()
                .withInstruction().withStatement("((%s<%s>)instance.%s()).add(aValue);").withParam(collectionContainerType).withParam(elementType).withParam(getterName(collectionFieldName)).endInstruction()
                .withInstruction().endInstruction()
                .withReturnValue().withStatement(castBuilderType ? "(%s) this" : "this").withParam(builderType).endReturnValue()
            .endMethod();
    }

    private void generateSimpleSetter(String fieldName, String fieldType, String[] exceptions, String builderType,
            boolean castBuilderType) {
        innerClassBuilder.withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(builderType)
                .withName(prefixed(setterPrefix, fieldName))
                .withParameter().withType(fieldType).withName("aValue").endParameter()
                .withExceptions(Arrays.asList(exceptions))
                .withInstruction().withStatement("instance.%s(aValue);").withParam(setterName(fieldName)).endInstruction()
                .withInstruction().endInstruction()
                .withReturnValue().withStatement(castBuilderType ? "(%s) this" : "this").withParam(builderType).endReturnValue()
            .endMethod();
    }

    public void finish() {
        IndentWriter w = new IndentWriter();
        w.out = out;
        JavaSource javaSource = javaSourceBuilder.build();
        javaSource.addImports(imports);
        w.out.println("// CHECKSTYLE:OFF");
        w.out.println("/**");
        w.out.println(" * Source code generated by Fluent Builders Generator");
        w.out.println(" * Do not modify this file");
        w.out.println(" * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/");
        w.out.println(" */");
        w.out.println();
        javaSource.print(w);
    }

    private String[] getExceptionTypes(TClassType[] exceptions) {
        String exceptionTypes[] = new String[exceptions.length];
        int i = 0;
        for (TClassType exception : exceptions) {
            exceptionTypes[i++] = imports.getUnqualified(getType(exception), nonTypeNames, builderPackage);
        }
        return exceptionTypes;
    }

    private String setterName(String fieldName) {
        return prefixed(SETTER_PREFIX, fieldName);
    }

    private String getterName(String fieldName) {
        return prefixed(GETTER_PREFIX, fieldName);
    }

    private String prefixed(String prefix, String fieldName) {
        if (prefix != null && prefix.length() > 0) {
            int prefixLen = prefix.length();
            StringBuilder buf = new StringBuilder(prefixLen + fieldName.length());

            buf.append(prefix);
            buf.append(fieldName);

            buf.setCharAt(prefixLen, Character.toUpperCase(buf.charAt(prefixLen)));

            return buf.toString();
        } else {
            return fieldName;
        }
    }

    private String toUpperCaseStart(String name) {
        StringBuilder buf = new StringBuilder(name);

        buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));

        return buf.toString();
    }

    private String toLowerCaseStart(String name) {
        StringBuilder buf = new StringBuilder(name);

        buf.setCharAt(0, Character.toLowerCase(buf.charAt(0)));

        return buf.toString();
    }

    public abstract String getClassName(TClassType t);

    public abstract String getType(TClassType t);
}
