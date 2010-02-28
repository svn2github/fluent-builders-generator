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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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

    public String getUnqualified(String qualifiedTypeName, Set<String> nonTypeNames) {
        // remove spaces
        qualifiedTypeName = qualifiedTypeName
        .replaceAll(" +extends +", "=extends=")
        .replaceAll(" +super +", "=super=")
        .replaceAll(" +capture +of +", "=capture=of=")
        .replaceAll(" ", "")
        .replaceAll("=", " ");

        return doGetUnqualified(qualifiedTypeName, nonTypeNames);
    }

    public String doGetUnqualified(String qualifiedTypeName, Set<String> nonTypeNames) {
        String classname = register(qualifiedTypeName, nonTypeNames);
        String[] params = splitType(classname);
        StringBuilder buf = new StringBuilder();
        buf.append(params[0]);
        if (params.length > 1) {
            buf.append("<");
            int paramCnt = params[params.length - 1].equals("[]") ? params.length - 1 : params.length;
            for (int i = 1; i < paramCnt; i++) {
                if (i > 1){
                    buf.append(", ");
                }
                String param = params[i];
                int end = param.indexOf('<');
                int space = (end != -1 ? param.substring(0, end) : param).lastIndexOf(' ');
                if (end > 0 && space > end) {
                    space = -1;
                }
                buf.append(param.substring(0, space + 1));
                String type = param.substring(space + 1);
                buf.append(doGetUnqualified(type, nonTypeNames));
            }
            buf.append(">");
            if (params.length > paramCnt) {
                buf.append(params[paramCnt]);
            }
        }
        return buf.toString();
    }

    public SortedSet<String> getImports() {
        return imports;
    }

    private String register(String qualifiedTypeName, Set<String> nonTypeNames) {
        String typeErasure = getTypeErasure(qualifiedTypeName);
        String classType = getClassType(qualifiedTypeName);
        int i1 = classType.indexOf('<');
        if (i1 == -1){
            i1 = classType.length();
        }
        int i2 = classType.indexOf('[');
        if (i2 == -1){
            i2 = classType.length();
        }
        int nameEnd = Math.min(i1, i2);
        String className = classType.substring(0, nameEnd);

        if (!simpleTypes.contains(className) && (nonTypeNames == null || !nonTypeNames.contains(className))) {
            if (imports.contains(typeErasure)) {                // already imported: return class type
                return classType;
            } else if (importedClasses.contains(className)) {   // name conflict: return full type
                return qualifiedTypeName;
            } else {                                            // not imported: register import, register class name, return class type
                if (!typeErasure.startsWith("java.lang.")) {
                    imports.add(typeErasure);
                }
                importedClasses.add(className);
                return classType;
            }
        } else {
            return qualifiedTypeName;
        }
    }

    private String getTypeErasure(String qualifiedTypeName) {
        StringBuilder buf = new StringBuilder(qualifiedTypeName.length());
        int depth = 0;
        for (char c : qualifiedTypeName.toCharArray()) {
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
            } else if (c == '[' || c == ']') {
            } else if (depth == 0) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    private String getClassType(String qualifiedTypeName) {
        int depth = 0;
        int dot = -1;
        int i = 0;
        for (char c : qualifiedTypeName.toCharArray()) {
            if (c == '<') {
                depth++;
            } else if (c == '>') {
                depth--;
            } else if (c == '.' && depth == 0) {
                dot = i;
            }
            i++;
        }
        return qualifiedTypeName.substring(dot + 1);
    }

    private String[] splitType(String qualifiedTypeName) {
        int depth = 0;
        List<String> p = new ArrayList<String>();
        int beg = 0;
        int i = 0;
        for (char c : qualifiedTypeName.toCharArray()) {
            if (c == '<') {
                depth++;
                if (depth == 1) {
                    p.add(qualifiedTypeName.substring(beg, i));
                    beg = i + 1;
                }
            } else if (c == '>') {
                if (depth == 1) {
                    p.add(qualifiedTypeName.substring(beg, i));
                    beg = i + 1;
                }
                depth--;
            } else if (c == ',' && depth == 1) {
                p.add(qualifiedTypeName.substring(beg, i));
                beg = i + 1;
            }
            i++;
        }
        if (beg < qualifiedTypeName.length()) {
            p.add(qualifiedTypeName.substring(beg));
        }
        return p.toArray(new String[p.size()]);
    }
}
