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
import com.sabre.buildergenerator.typeutils.TypeResolver;


public class BuilderSourceGenerator {
    private static final String BUILDER_TYPE_ARG_NAME = "GeneratorT";
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String BUILDER_BASE_SUFFIX = "BuilderBase";
    private static final String FIELD_BUILDER_SUFFIX = "Builder";
    private static final String COLLECTION_ELEMENT_BUILDER_PREFIX = "Added";

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
    private JavaSourceBuilder.ClazzBuilderBase<?> innerClassBuilderBase;

    private Set<String> nonTypeNames = null;
    private String builderPackage;

    public BuilderSourceGenerator() {
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

    public void generateBuilderClass(final IType owningType, String aBuildClassType, String aPackageForBuilder, String aBuilderClassName, String[] typeParamNames, String[][] typeParamBounds) {
        builderPackage = aPackageForBuilder;

        buildClassName = getClassName(aBuildClassType);
        buildClassType = imports.getUnqualified(aBuildClassType, nonTypeNames, builderPackage);
        builderClassName = aBuilderClassName;

        // type parameters
        String[] typeParams = new String[typeParamBounds.length];
        int i = 0;
        for (String[] bounds : typeParamBounds) {
            typeParams[i] = typeParamNames[i];
            if (bounds != null && bounds.length > 0) {
                typeParams[i] += " extends ";
                typeParams[i] += toString(" & ", bounds, new StringRetriever<String>() {
                    public String toString(String bound) {
                        try {
                            bound = TypeResolver.resolveType(owningType, bound);
                            return imports.getUnqualified(bound, nonTypeNames, builderPackage);
                        } catch(Exception e) {
                            return "";
                        }
                    }
                });
            }
            i++;
        }

        // type arguments
        String typeArgs = "";
        if (typeParamNames != null && typeParamNames.length > 0) {
            typeArgs = "<";
            typeArgs += toString(", ", typeParamNames, new StringRetriever<String>() {
                public String toString(String typeParamName) {
                    return typeParamName;
                }
            });
            typeArgs += ">";
        }

        // base class type
        String baseClass = buildClassName + BUILDER_BASE_SUFFIX + "<" + builderClassName + typeArgs;
        for (String typeParamName : typeParamNames) {
            baseClass += ", " + typeParamName;
        }
        baseClass += ">";

        // generate class
        if (aPackageForBuilder != null && aPackageForBuilder.length() > 0) {
            javaSourceBuilder.withPackge(aPackageForBuilder);
        }
        topClassBuilder = javaSourceBuilder.withClazz().withModifiers(JavaSource.MODIFIER_PUBLIC)
                .withName(builderClassName).withTypeArgs(Arrays.asList(typeParams)).withBaseClazz(baseClass)
            // static create metod
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC + JavaSource.MODIFIER_STATIC).withTypeArgs(Arrays.asList(typeParams))
                .withName(toLowerCaseStart(buildClassName)).withReturnType(builderClassName + typeArgs)
                .withReturnValue().withStatement("new " + builderClassName + typeArgs + "()").withParam(builderClassName).endReturnValue()
            .endMethod()
            // default constructor
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withName(builderClassName)
                .withInstruction().withStatement("super(new %s());").withParam(buildClassType + typeArgs).endInstruction()
            .endMethod()
            // build()
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withName("build").withReturnType(buildClassType + typeArgs)
                .withReturnValue().withStatement("getInstance()").endReturnValue()
            .endMethod();
        javaSourceBuilder = topClassBuilder.endClazz();
        innerClassBuilderBase = topClassBuilder;
    }

    // TODO: remove usage of IType
    public void generateBuilderBaseClass(String buildClassType, IType type, boolean isTopLevel) throws JavaModelException {
        ITypeParameter[] typeParameters = type.getTypeParameters();

        nonTypeNames = new HashSet<String>();
        for(ITypeParameter typeParam : typeParameters) {
            nonTypeNames.add(typeParam.getElementName());
        }

        innerBuildClassName = getClassName(buildClassType);
        innerBuildClassType = imports.getUnqualified(buildClassType, nonTypeNames, builderPackage);
        innerBuilderClassName = innerBuildClassName + BUILDER_BASE_SUFFIX;

        // class definition, for example:
        // public static MyClassBuilderBase<GeneratorT extends MyClassBuilderBase, T1, T2>
        if (isTopLevel) {
            topClassBuilder = javaSourceBuilder.withClazz();
            generateBuilderBaseClassBody(topClassBuilder, type, typeParameters);
            // end class
            javaSourceBuilder = topClassBuilder.endClazz();
            innerClassBuilderBase = topClassBuilder;
        } else {
            innerClassBuilder = topClassBuilder.withInnerClass()
                .withModifiers(JavaSource.MODIFIER_PUBLIC + JavaSource.MODIFIER_STATIC);
            generateBuilderBaseClassBody(innerClassBuilder, type, typeParameters);
            // end class
            topClassBuilder = innerClassBuilder.endInnerClass();
            innerClassBuilderBase = innerClassBuilder;
        }
    }

    // TODO: remove usage of ITypeParameter
    private void generateBuilderBaseClassBody(JavaSourceBuilder.ClazzBuilderBase<?> innerClassBuilderBase, final IType type, ITypeParameter[] typeParameters)
            throws JavaModelException {
        // type argument list
        String typeArgList = "";
        if (typeParameters != null && typeParameters.length > 0) {
            typeArgList += toString(", ", typeParameters, new StringRetriever<ITypeParameter>() {
                public String toString(ITypeParameter typeParam) {
                    return typeParam.getElementName();
                }
            });
        }

        // class header
        String typeArg = BUILDER_TYPE_ARG_NAME + " extends " + innerBuilderClassName + "<" + BUILDER_TYPE_ARG_NAME
                + (typeArgList.length() > 0 ? ", " + typeArgList : "") + ">";
        innerClassBuilderBase.withName(innerBuilderClassName).withTypeArg(typeArg);
        for (ITypeParameter typeParam : typeParameters) {
            String param = typeParam.getElementName();
            String[] bounds = typeParam.getBounds();
            if (bounds != null && bounds.length > 0) {
                param += " extends ";
                param += toString(" & ", bounds, new StringRetriever<String>() {
                    public String toString(String bound) {
                        try {
                            bound = TypeResolver.resolveType(type, bound);
                            return imports.getUnqualified(bound, nonTypeNames, builderPackage);
                        } catch(Exception e) {
                            return "";
                        }
                    }
                });
            }
            innerClassBuilderBase.withTypeArg(param);
        }

        // type arguments
        String typeArgs = "";
        if (typeArgList != null && typeArgList.length() > 0) {
            typeArgs = "<" + typeArgList +  ">";
        }

        // class members
        innerClassBuilderBase
            // instance variable
            .withDeclaration().withStatement("private %s instance;").withParam(innerBuildClassType + typeArgs).endDeclaration()
            // protected constructor that assigns the instance variable
            .withMethod().withModifiers(JavaSource.MODIFIER_PROTECTED).withName(innerBuilderClassName)
                .withParameter().withType(innerBuildClassType + typeArgs).withName("aInstance").endParameter()
                .withInstruction().withStatement("instance = aInstance;").endInstruction()
            .endMethod()
            // getInstance()
            .withMethod().withModifiers(JavaSource.MODIFIER_PROTECTED).withReturnType(innerBuildClassType + typeArgs).withName("getInstance")
                .withReturnValue().withStatement("instance").endReturnValue()
            .endMethod();
    }

    interface StringRetriever<T> {
        String toString(T obj);
    }

    public <T> String toString(String separator, T[] elements, StringRetriever<T> r) {
        StringBuilder b = new StringBuilder();
        boolean isFirst = true;
        if (elements != null) {
            for (T element : elements) {
                if (!isFirst) {
                    b.append(separator);
                }
                b.append(r.toString(element));
                isFirst = false;
            }
        }
        return b.toString();
    }

    public void addFieldSetter(String fieldName, String fieldType, String[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String type = imports.getUnqualified(fieldType,nonTypeNames, builderPackage);
        generateSimpleSetter(fieldName, type, exceptionTypes, BUILDER_TYPE_ARG_NAME, true);
    }

    public void addFieldBuilder(String fieldName, String fieldType, String[] exceptions, String[] typeArgs) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String fieldClassName = getClassName(fieldType);
        String fieldUType = imports.getUnqualified(fieldType, nonTypeNames, builderPackage);
        String innerBuilderName = fieldClassName + BUILDER_BASE_SUFFIX;
        String fieldBuilderName = toUpperCaseStart(fieldName + fieldClassName + FIELD_BUILDER_SUFFIX);
        String methodName = prefixed(setterPrefix, fieldName);
        for (int i = 0; i < typeArgs.length;i++) {
            typeArgs[i] = imports.getUnqualified(typeArgs[i], nonTypeNames, builderPackage);
        }

        generateBuilderSetter(fieldName, fieldUType, methodName, exceptionTypes, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, BUILDER_TYPE_ARG_NAME, typeArgs, true);
    }

    public void addCollectionElementSetter(String fieldName, String elementName, String elementType,
            String collectionContainerTypeDecriptor, String[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String elementUType = imports.getUnqualified(elementType, nonTypeNames, builderPackage);

        String collectionContainerType = imports.getUnqualified(collectionContainerTypeDecriptor, nonTypeNames, builderPackage);
        generateCollectionElementSetter(fieldName, collectionContainerType, elementName,
                elementUType, exceptionTypes, BUILDER_TYPE_ARG_NAME, true);
    }

    public void addCollectionElementBuilder(String elementName, String elementType, String[] exceptions,
            String[] typeArgs) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String elementUType = imports.getUnqualified(elementType, nonTypeNames, builderPackage);
        String fieldClassName = getClassName(elementType);
        String innerBuilderName = fieldClassName + BUILDER_BASE_SUFFIX;
        String fieldBuilderName = toUpperCaseStart(prefixed(COLLECTION_ELEMENT_BUILDER_PREFIX, elementName) + fieldClassName + FIELD_BUILDER_SUFFIX);
        String methodName = prefixed(collectionElementSetterPrefix, elementName);
        for (int i = 0; i < typeArgs.length;i++) {
            typeArgs[i] = imports.getUnqualified(typeArgs[i], nonTypeNames, builderPackage);
        }

        generateBuilderSetter(elementName, elementUType, methodName, exceptionTypes, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, BUILDER_TYPE_ARG_NAME, typeArgs, true);
    }

    private void generateBuilderSetter(String fieldName, String fieldType, String methodName,
            String[] exceptions, String fieldBuilderName, String baseBuilderName, String builderClassName,
            String builderType, String[] typeArgs, boolean castBuilderType) {
        String baseClass = baseBuilderName + "<" + fieldBuilderName;
        for (String typeArg : typeArgs) {
            baseClass += ", " + imports.getUnqualified(typeArg, nonTypeNames, builderPackage);
        }
        baseClass += ">";
        innerClassBuilderBase
            .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(fieldBuilderName).withName(methodName).withExceptions(Arrays.asList(exceptions))
                .withInstruction().withStatement("%s obj = new %s();").withParam(fieldType).withParam(fieldType).endInstruction()
                .withInstruction().endInstruction()
                .withInstruction().withStatement("%s(obj);").withParam(methodName).endInstruction()
                .withInstruction().endInstruction()
                .withReturnValue().withStatement("new %s(obj)").withParam(fieldBuilderName).endReturnValue()
            .endMethod()
            .withInnerClass()
                .withModifiers(JavaSource.MODIFIER_PUBLIC).withName(fieldBuilderName).withBaseClazz(baseClass)
                // constructor
                .withMethod().withModifiers(JavaSource.MODIFIER_PUBLIC).withName(fieldBuilderName)
                    .withParameter().withType(fieldType).withName("aInstance").endParameter()
                    .withInstruction().withStatement("super(aInstance);").endInstruction()
                .endMethod()
                // end method
                .withMethod().withAnnotation("@SuppressWarnings(\"unchecked\")")
                    .withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(builderType).withName(prefixed(endPrefix, fieldName))
                    .withReturnValue().withStatement(castBuilderType ? "(%s) %s.this" : "this").withParam(builderType).withParam(builderClassName).endReturnValue()
                .endMethod()
            .endInnerClass();
    }

    private void generateCollectionElementSetter(String collectionFieldName, String collectionContainerType,
            String elementName, String elementType, String[] exceptions, String builderType, boolean castBuilderType) {
        String methodName = prefixed(collectionElementSetterPrefix, elementName);
        innerClassBuilderBase
            .withMethod().withAnnotation("@SuppressWarnings(\"unchecked\")")
                .withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(builderType).withName(methodName).withExceptions(Arrays.asList(exceptions))
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
        innerClassBuilderBase
            .withMethod().withAnnotation("@SuppressWarnings(\"unchecked\")")
                .withModifiers(JavaSource.MODIFIER_PUBLIC).withReturnType(builderType)
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

    private String[] getExceptionTypes(String[] exceptions) {
        String exceptionTypes[] = new String[exceptions.length];
        int i = 0;
        for (String exception : exceptions) {
            exceptionTypes[i++] = imports.getUnqualified(exception, nonTypeNames, builderPackage);
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

    private String getClassName(String aType) {
        int e = aType.indexOf('<');
        if (e == -1) {
            e = aType.length();
        }
        int b = aType.lastIndexOf('.', e) + 1;

        return aType.substring(b, e);
    }
}
