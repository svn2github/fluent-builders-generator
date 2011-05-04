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

package com.sabre.buildergenerator.sourcegenerator.java;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sabre.buildergenerator.typeutils.TypeUtil;

public class Imports {
    private static final Set<String> simpleTypes = new HashSet<String>();

    private final SortedSet<String> imports = new TreeSet<String>();
    private final Set<String> importedClasses = new HashSet<String>();

    static {
        simpleTypes.add("int");
        simpleTypes.add("long");
        simpleTypes.add("float");
        simpleTypes.add("double");
        simpleTypes.add("byte");
        simpleTypes.add("char");
        simpleTypes.add("boolean");
        simpleTypes.add("?");
    }

    public SortedSet<String> getImports() {
        return imports;
    }

    /**
     * Adds all classes referenced within the type description to the imports.
     * Returns type description with package names removed.
     */
    public String getUnqualified(String qualifiedTypeName, Set<String> nonTypeNames, String packageName) {
        // remove spaces
        qualifiedTypeName = TypeUtil.normalizeType(qualifiedTypeName);

        return doGetUnqualified(qualifiedTypeName, nonTypeNames, packageName);
    }

    /**
     * Processes top class and goes recurrently down into type parameters.
     */
    public String doGetUnqualified(String qualifiedTypeName, final Set<String> nonTypeNames, final String packageName) {
        String classname = register(qualifiedTypeName, nonTypeNames, packageName);
        return TypeUtil.processTypeParams(classname, new TypeUtil.TypeProcessor() {

            public String processType(String param) {
                StringBuilder buf = new StringBuilder();
                int end = param.indexOf('<');
                int space = (end != -1 ? param.substring(0, end) : param).lastIndexOf(' ');

                if (end > 0 && space > end) {
                    space = -1;
                }

                buf.append(param.substring(0, space + 1));
                String type = param.substring(space + 1);
                buf.append(doGetUnqualified(type, nonTypeNames, packageName));

                return buf.toString();
            }

        });
    }

    /**
     * Processes single class. Registers import for main class
     * and returns type description without the main class package.
     */
    private String register(String qualifiedType, Set<String> nonTypeNames, String packageName) {
                                                                                   // "package.MyClass<String>"
        String qualifiedClassName = getClassName(qualifiedType);                   // "package.MyClass"
        String unqualifiedType = getTypeDescriptionWithoutPackage(qualifiedType);  // "MyClass<String>"
        String className = getClassName(unqualifiedType);                          // "MyClass"

        if (!simpleTypes.contains(className) && (nonTypeNames == null || !nonTypeNames.contains(className))) {
            if (imports.contains(qualifiedClassName)) {         // already imported: return class type
                return unqualifiedType;
            } else if (importedClasses.contains(className)) {   // name conflict: return full type
                if (!isPackage("java.lang", qualifiedClassName) && !isPackage(packageName, qualifiedClassName)) {
                    return qualifiedType;
                } else {
                    return unqualifiedType;
                }
            } else {                                            // not imported: register import, register class name, return class type
                if (!isPackage("java.lang", qualifiedClassName) && !isPackage(packageName, qualifiedClassName)) {
                    imports.add(qualifiedClassName);
                }
                importedClasses.add(className);
                return unqualifiedType;
            }
        } else {
            return qualifiedType;
        }
    }

    /**
     * Removes type parameters and array brackets.
     *
     * package.MyClass<String> --> packageMyClass
     * MyClass<String>         --> MyClass
     * MyClass[]               --> MyClass
     */
    private String getClassName(String classType) {
        int i = classType.indexOf('<');
        if (i != -1) {
            return classType.substring(0, i);
        } else {
            i = classType.indexOf('[');
            if (i != -1) {
                return classType.substring(0, i);
            } else {
                return classType;
            }
        }
    }

    /**
     * Checks if the given class description is in the package specified.
     *
     * @param packageName package name
     * @param classType class description
     * @return true if packages match
     */
    private boolean isPackage(String packageName, String classType) {
        if (isEmptyString(packageName)) {
            return classType.indexOf('.') == -1;
        } else {
            return (classType.startsWith(packageName) && classType.lastIndexOf('.') == packageName.length());
        }
    }

    /**
     * Checks if the string is null or empty.
     */
    private boolean isEmptyString(String packageName) {
        return (packageName == null || packageName.length() == 0);
    }

    /**
     * Removes package form type description.
     *
     * "package.MyClass<String>" --> "MyClass<String>"
     */
    private String getTypeDescriptionWithoutPackage(String qualifiedTypeName) {
        int mainTypeEnd = qualifiedTypeName.indexOf('<');
        if (mainTypeEnd == -1) {
            mainTypeEnd = qualifiedTypeName.length();
        }
        int dot = qualifiedTypeName.substring(0, mainTypeEnd).lastIndexOf('.');
        if (dot != -1) {
            return qualifiedTypeName.substring(dot + 1);
        } else {
            return qualifiedTypeName;
        }
    }

}
