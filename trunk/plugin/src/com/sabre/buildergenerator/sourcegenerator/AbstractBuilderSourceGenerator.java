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


public abstract class AbstractBuilderSourceGenerator<TClassType> extends SourceGenerator {
    private static final String BUILDER_TYPE_ARG_NAME = "GeneratorT";
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String BUILDER_BASE_SUFFIX = "BuilderBase";
    private static final String FIELD_BUILDER_SUFFIX = "Builder";

    private String setterPrefix = "with";
    private String collectionElementSetterPrefix = "withAdded";
    private String endPrefix = "end";

    private String buildClassName;
    private String buildClassType;
    private String builderClassName;

    private String innerBuildClassName;
    private String innerBuildClassType;
    private String innerBuilderClassName;

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

    public void addBuilderClass(TClassType aBuildClassDescriptor, String aPackageForBuilder, String aBuilderClassName) {
        buildClassName = getClassName(aBuildClassDescriptor);
        buildClassType = getType(aBuildClassDescriptor);
        builderClassName = aBuilderClassName;

        if (aPackageForBuilder.length() > 0) {
            addLine("package %s;", aPackageForBuilder);
            addLine();
        }

        addLine("public class %s extends %s%s<%s> {", builderClassName, buildClassName, BUILDER_BASE_SUFFIX, builderClassName);
        increseIndent();
        startMethod(MODIFIER_PUBLIC + MODIFIER_STATIC, builderClassName, toLowerCaseStart(buildClassName), null);
        endMethod("new %s()", builderClassName);
        addLine();
        startMethod(MODIFIER_PUBLIC, null, builderClassName, null);
        addLine("super(new %s());", buildClassType);
        endMethod();
        addLine();
        startMethod(MODIFIER_PUBLIC, buildClassType, "build", null);
        endMethod("getInstance()");
        closeBlock();

    }

    public void startBuilderBaseClass(TClassType buildClassDescriptor) {
        innerBuildClassName = getClassName(buildClassDescriptor);
        innerBuildClassType = getType(buildClassDescriptor);
        innerBuilderClassName = innerBuildClassName + BUILDER_BASE_SUFFIX;

        addLine();
        addLine("@SuppressWarnings(\"unchecked\")");
        addLine("class %s<%s extends %s> {", innerBuilderClassName, BUILDER_TYPE_ARG_NAME,innerBuilderClassName);
        increseIndent();
        addLine("private %s instance;", innerBuildClassType);
        addLine();
        startMethod(MODIFIER_PROTECTED, null, innerBuilderClassName, innerBuildClassType, "aInstance", null);
        addLine("instance = aInstance;");
        endMethod();
        addLine();
        startMethod(MODIFIER_PROTECTED, innerBuildClassType, "getInstance", null);
        endMethod("instance");
    }

    public void endBuilderBaseClass() {
        closeBlock();
    }

    public void addFieldSetter(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        generateSimpleSetter(fieldName, getType(fieldTypeDescriptor), exceptionTypes, BUILDER_TYPE_ARG_NAME, true);
    }

    private String[] getExceptionTypes(TClassType[] exceptions) {
        String exceptionTypes[] = new String[exceptions.length];
        int i = 0;
        for (TClassType exception : exceptions) {
            exceptionTypes[i++] = getType(exception);
        }
        return exceptionTypes;
    }

    public void addFieldBuilder(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        String fieldClassName = getClassName(fieldTypeDescriptor);
        String fieldClassQName = getClassQName(fieldTypeDescriptor);
        String innerBuilderName = fieldClassName + BUILDER_BASE_SUFFIX;
        String fieldBuilderName = toUpperCaseStart(fieldName + fieldClassName + FIELD_BUILDER_SUFFIX);
        String methodName = prefixed(setterPrefix, fieldName);

        generateBuilderSetter(fieldName, fieldClassQName, methodName, exceptionTypes, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, BUILDER_TYPE_ARG_NAME, true);
    }

    public void addCollectionElementSetter(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType collectionContainerTypeDecriptor, TClassType[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        TClassType elementTypeDescriptor = getInnerType(fieldTypeDescriptor);
        String elementType = getType(elementTypeDescriptor);

        generateCollectionElementSetter(fieldName, getType(collectionContainerTypeDecriptor), elementName,
                elementType, exceptionTypes, BUILDER_TYPE_ARG_NAME, true);
    }

    public void addCollectionElementBuilder(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType collectionConcreteTypeDecriptor, TClassType[] exceptions) {
        String[] exceptionTypes = getExceptionTypes(exceptions);
        TClassType elementTypeDescriptor = getInnerType(fieldTypeDescriptor);
        String elementType = getType(elementTypeDescriptor);
        String fieldClassName = getClassName(elementTypeDescriptor);
        String innerBuilderName = fieldClassName + BUILDER_BASE_SUFFIX;
        String fieldBuilderName = toUpperCaseStart(elementName + fieldClassName + FIELD_BUILDER_SUFFIX);
        String methodName = prefixed(collectionElementSetterPrefix, elementName);

        generateBuilderSetter(elementName, elementType, methodName, exceptionTypes, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, BUILDER_TYPE_ARG_NAME, true);
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

    private void generateBuilderSetter(String fieldName, String fieldType, String methodName,
            String[] exceptions, String fieldBuilderName, String baseBuilderName, String builderClassName2,
            String builderType, boolean castBuilderType) {
        startMethod(MODIFIER_PUBLIC, fieldBuilderName, methodName, exceptions);
        addLine("%s %s = new %s();", fieldType, fieldName, fieldType);
        addLine();
        endMethod("%s(%s).new %s(%s)", methodName, fieldName, fieldBuilderName, fieldName);
        addLine();
        addLine("public class %s extends %s<%s> {", fieldBuilderName, baseBuilderName, fieldBuilderName);
        increseIndent();
        addLine("public %s(%s aInstance) {", fieldBuilderName, fieldType);
        increseIndent();
        addLine("super(aInstance);");
        closeBlock();
        addLine();
        addLine("public %s %s() {", builderType, prefixed(endPrefix, fieldName));
        increseIndent();
        endMethod("%s%s.this", (castBuilderType ? "(" + builderType + ") " : ""), builderClassName2);
        endMethod();
    }

    private void generateCollectionElementSetter(String collectionFieldName, String collectionContainerType,
            String elementName, String elementType, String[] exceptions, String builderType, boolean castBuilderType) {
        String methodName = prefixed(collectionElementSetterPrefix, elementName);
        startMethod(MODIFIER_PUBLIC, builderType, methodName, elementType, "aValue", exceptions);
        addLine("if (instance.%s() == null) {", getterName(collectionFieldName));
        increseIndent();
        addLine("instance.%s(new %s<%s>());", setterName(collectionFieldName), collectionContainerType, elementType);
        closeBlock();
        addLine();
        addLine("((%s<%s>)instance.%s()).add(aValue);", collectionContainerType, elementType,
                getterName(collectionFieldName));
        addLine();
        if (castBuilderType) {
            endMethod("(%s) this", builderType);
        } else {
            endMethod("this");
        }
    }

    private void generateSimpleSetter(String fieldName, String fieldType, String[] exceptions, String builderType,
            boolean castBuilderType) {
        startMethod(MODIFIER_PUBLIC, builderType, prefixed(setterPrefix, fieldName), fieldType, "aValue", exceptions);
        addLine("instance.%s(aValue);", setterName(fieldName));
        addLine();
        if (castBuilderType) {
            endMethod("(%s) this", builderType);
        } else {
            endMethod("this");
        }
    }

    public abstract String getClassName(TClassType t);

    public abstract String getClassQName(TClassType t);

    public abstract String getPackage(TClassType t);

    public abstract TClassType getInnerType(TClassType t);

    public abstract String getType(TClassType t);

    public abstract String getTypeWithParams(TClassType t);

}
