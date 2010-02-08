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


public abstract class AbstractBuilderSourceGenerator<TClassType> {
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String BUILDER_SUFFIX = "Builder";

    private static final int MODIFIER_PUBLIC = 1;
    private static final int MODIFIER_PRIVATE = 2;
    private static final int MODIFIER_STATIC = 4;

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
    private String indent = "";

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

    /**
     * @return the out
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * @param aOut the out to set
     */
    public void setOut(PrintWriter aOut) {
        out = aOut;
    }

    public void startBuilderClass(TClassType aBuildClassDescriptor, String aPackageForBuilder, String aBuilderClassName) {
        buildClassName = getClassName(aBuildClassDescriptor);
        buildClassType = getType(aBuildClassDescriptor);
        builderClassName = aBuilderClassName;

        if (aPackageForBuilder.length() > 0) {
            out.println("package " + aPackageForBuilder + ";");
            out.println();
        }

        out.println("@SuppressWarnings(\"unchecked\")");
        out.println("public class " + builderClassName + " {");
        out.println("    private " + buildClassType + " instance;");
        out.println();
        out.println("    public static " + builderClassName + " build" + buildClassName + "() {");
        out.println("        return new " + builderClassName + "();");
        out.println("    }");
        out.println();
        out.println("    public " + builderClassName + "() {");
        out.println("        instance = new " + buildClassType + "();");
        out.println("    }");
        out.println();
        out.println("    public " + buildClassType + " build() {");
        out.println("        return instance;");
        out.println("    }");

        increseIndent();
    }

    public void endBuilderClass() {
        out.println("}");
        out.flush();
    }

    public void addFieldSetter(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        generateSimpleSetter(fieldName, getType(fieldTypeDescriptor), exceptions, builderClassName, false);
    }

    public void addFieldBuilder(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        String fieldClassQName = getClassQName(fieldTypeDescriptor);
        String methodName = prefixed(setterPrefix, fieldName);
        String innerBuilderName = getClassName(fieldTypeDescriptor) + BUILDER_SUFFIX;
        String fieldBuilderName = capitalize(fieldName + innerBuilderName);

        generateBuilderSetter(fieldName, fieldClassQName, methodName, exceptions, fieldBuilderName,
                innerBuilderName, builderClassName, builderClassName, false);
    }

    public void addCollectionElementSetter(String collectionFieldName, TClassType collectionFieldTypeDescriptor, String elementName,
            TClassType collectionContainerTypeDecriptor, TClassType[] exceptions) {
        TClassType elementTypeDescriptor = getInnerType(collectionFieldTypeDescriptor);
        String elementType = getType(elementTypeDescriptor);

        generateCollectionElementSetter(collectionFieldName, getType(collectionContainerTypeDecriptor), elementName, elementType,
                exceptions, builderClassName, false);
    }

    public void addCollectionElementBuilder(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType collectionConcreteTypeDecriptor, TClassType[] exceptions) {
        TClassType elementTypeDescriptor = getInnerType(fieldTypeDescriptor);
        String elementType = getType(elementTypeDescriptor);
        String fieldClassName = getClassName(elementTypeDescriptor);
        String innerBuilderName = fieldClassName + BUILDER_SUFFIX;
        String fieldBuilderName = capitalize(elementName + innerBuilderName);
        String methodName = prefixed(collectionElementSetterPrefix, elementName);

        generateBuilderSetter(elementName, elementType, methodName, exceptions, fieldBuilderName,
                innerBuilderName, builderClassName, builderClassName, false);
    }

    public void startInnerBuilderClass(TClassType buildClassDescriptor) {
        innerBuildClassName = getClassName(buildClassDescriptor);
        innerBuildClassType = getType(buildClassDescriptor);
        innerBuilderClassName = innerBuildClassName + BUILDER_SUFFIX;

        out.println();
        out.println("    public static class " + innerBuilderClassName + "<T extends " + innerBuilderClassName + "> {");
        out.println("        private " + innerBuildClassType + " instance;");
        out.println();
        out.println("        private " + innerBuilderClassName + "(" + innerBuildClassType + " aInstance) {");
        out.println("            instance = aInstance;");
        out.println("        }");

        increseIndent();
    }

    public void endInnerBuilderClass() {
        decreaseIndent();
        out.println("    }");
    }

    public void addInnerFieldSetter(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        generateSimpleSetter(fieldName, getType(fieldTypeDescriptor), exceptions, "T", true);
    }

    public void addInnerFieldBuilder(String fieldName, TClassType fieldTypeDescriptor, TClassType[] exceptions) {
        String fieldClassName = getClassName(fieldTypeDescriptor);
        String fieldClassQName = getClassQName(fieldTypeDescriptor);
        String innerBuilderName = fieldClassName + BUILDER_SUFFIX;
        String fieldBuilderName = capitalize(fieldName + innerBuilderName);
        String methodName = prefixed(setterPrefix, fieldName);

        generateBuilderSetter(fieldName, fieldClassQName, methodName, exceptions, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, "T", true);
    }

    public void addInnerCollectionElementSetter(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType collectionContainerTypeDecriptor, TClassType[] exceptions) {
        TClassType elementTypeDescriptor = getInnerType(fieldTypeDescriptor);
        String elementType = getType(elementTypeDescriptor);

        generateCollectionElementSetter(fieldName, getType(collectionContainerTypeDecriptor), elementName,
                elementType, exceptions, "T", true);
    }

    public void addInnerCollectionElementBuilder(String fieldName, TClassType fieldTypeDescriptor, String elementName,
            TClassType collectionConcreteTypeDecriptor, TClassType[] exceptions) {
        TClassType elementTypeDescriptor = getInnerType(fieldTypeDescriptor);
        String elementType = getType(elementTypeDescriptor);
        String fieldClassName = getClassName(elementTypeDescriptor);
        String innerBuilderName = fieldClassName + BUILDER_SUFFIX;
        String fieldBuilderName = capitalize(elementName + innerBuilderName);
        String methodName = prefixed(collectionElementSetterPrefix, elementName);

        generateBuilderSetter(elementName, elementType, methodName, exceptions, fieldBuilderName,
                innerBuilderName, innerBuilderClassName, "T", true);
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

    private String capitalize(String name) {
        StringBuilder buf = new StringBuilder(name);

        buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));

        return buf.toString();
    }

    private void generateBuilderSetter(String fieldName, String fieldType, String methodName,
            TClassType[] exceptions, String fieldBuilderName, String baseBuilderName, String builderClassName2,
            String builderType, boolean castBuilderType) {
        startSetterMethod(MODIFIER_PUBLIC, fieldBuilderName, methodName, exceptions);
        addCodeLine("%s %s = new %s();", fieldType, fieldName, fieldType);
        addEmptyLine();
        endMethod("%s(%s).new %s(%s)", methodName, fieldName, fieldBuilderName, fieldName);
        addEmptyLine();
        addCodeLine("public class %s extends %s<%s> {", fieldBuilderName, baseBuilderName, fieldBuilderName);
        increseIndent();
        addCodeLine("public %s(%s aInstance) {", fieldBuilderName, fieldType);
        increseIndent();
        addCodeLine("super(aInstance);");
        closeBlock();
        addEmptyLine();
        addCodeLine("public %s %s() {", builderType, prefixed(endPrefix, fieldName));
        increseIndent();
        endMethod("%s%s.this", (castBuilderType ? "(" + builderType + ") " : ""), builderClassName2);
        endMethod();
    }

    private void generateCollectionElementSetter(String collectionFieldName, String collectionContainerType,
            String elementName, String elementType, TClassType[] exceptions, String builderType, boolean castBuilderType) {
        String methodName = prefixed(collectionElementSetterPrefix, elementName);
        startSetterMethod(MODIFIER_PUBLIC, builderType, methodName, elementType, "aValue", exceptions);
        addCodeLine("if (instance.%s() == null) {", getterName(collectionFieldName));
        increseIndent();
        addCodeLine("instance.%s(new %s<%s>());", setterName(collectionFieldName), collectionContainerType, elementType);
        closeBlock();
        addEmptyLine();
        addCodeLine("((%s<%s>)instance.%s()).add(aValue);", collectionContainerType, elementType,
                getterName(collectionFieldName));
        addEmptyLine();
        if (castBuilderType) {
            endMethod("(%s)this", builderType);
        } else {
            endMethod("this");
        }
    }

    private void generateSimpleSetter(String fieldName, String fieldType, TClassType[] exceptions, String builderType,
            boolean castBuilderType) {
        startSetterMethod(MODIFIER_PUBLIC, builderType, prefixed(setterPrefix, fieldName), fieldType, "aValue", exceptions);
        addCodeLine("instance.%s(aValue);", setterName(fieldName));
        addEmptyLine();
        if (castBuilderType) {
            endMethod("(%s)this", builderType);
        } else {
            endMethod("this");
        }
    }

    private void startSetterMethod(int modifiers, String returnType, String methodName, TClassType[] exceptions) {
        startSetterMethod(modifiers, returnType, methodName, null, null, exceptions);
    }

    private void startSetterMethod(int modifiers, String returnType, String methodName, String parameterType, String parameterName, TClassType[] exceptions) {
        out.println();
        out.print(indent);

        if ((modifiers & MODIFIER_PUBLIC) != 0) {
            out.print("public ");
        }
        else if ((modifiers & MODIFIER_PRIVATE) != 0) {
            out.print("private ");
        }
        if ((modifiers & MODIFIER_STATIC) != 0) {
            out.print("static ");
        }

        out.printf("%s %s(", returnType, methodName);

        if (parameterType != null && parameterName != null) {
            out.printf("%s %s", parameterType, parameterName);
        }

        out.print(") ");

        if (exceptions != null && exceptions.length > 0) {
            out.print("throws ");

            int i = exceptions.length;

            for (TClassType exceptionTypeDescriptor : exceptions) {
                boolean isLast = --i == 0;

                out.print(getType(exceptionTypeDescriptor) + (isLast ? " " : ", "));
            }
        }

        out.println("{");
        increseIndent();
    }

    private void addEmptyLine() {
        out.println();
    }

    private void addCodeLine(String codeLine, Object... args) {
        out.print(indent);
        out.printf(codeLine, args);
        out.println();
    }

    private void endMethod(String returnExpression, Object... args) {
        addCodeLine("return " + returnExpression + ";", args);
        endMethod();
    }

    private void endMethod() {
        closeBlock();
    }

    private void closeBlock() {
        decreaseIndent();
        addCodeLine("}");
    }

    private void increseIndent() {
        indent += "    ";
    }

    private void decreaseIndent() {
        indent = "                ".substring(0, indent.length() - 4);
    }

    public abstract String getClassName(TClassType t);

    public abstract String getClassQName(TClassType t);

    public abstract String getPackage(TClassType t);

    public abstract TClassType getInnerType(TClassType t);

    public abstract String getType(TClassType t);

    public abstract String getTypeWithParams(TClassType t);

}
