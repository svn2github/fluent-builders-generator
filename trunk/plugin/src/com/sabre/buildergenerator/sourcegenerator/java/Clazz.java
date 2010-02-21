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
import java.util.List;

public class Clazz {
    private List<String> annotations = new ArrayList<String>();
    private int modifiers;
    private String name;
    private List<String> typeArgs = new ArrayList<String>();
    private String baseClazz;
    private List<String> interfaces = new ArrayList<String>();
    private List<Statement> declarations = new ArrayList<Statement>();
    private List<Method> methods = new ArrayList<Method>();
    private List<Clazz> innerClasses = new ArrayList<Clazz>();

    public List<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<String> annotations) {
        this.annotations = annotations;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Statement> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Statement> declarations) {
        this.declarations = declarations;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getBaseClazz() {
        return baseClazz;
    }

    public void setBaseClazz(String baseClazz) {
        this.baseClazz = baseClazz;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public List<String> getTypeArgs() {
        return typeArgs;
    }

    public void setTypeArgs(List<String> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public List<Clazz> getInnerClasses() {
        return innerClasses;
    }

    public void setInnerClasses(List<Clazz> inerrClasses) {
        this.innerClasses = inerrClasses;
    }

    public void print(IndentWriter w) {
        for (String annotation : annotations) {
            w.out.print(w.indent);
            w.out.println(annotation);
        }
        w.out.print(w.indent);
        if ((modifiers & JavaSource.MODIFIER_PUBLIC) != 0) {
            w.out.print("public ");
        } else if ((modifiers & JavaSource.MODIFIER_PRIVATE) != 0) {
            w.out.print("private ");
        } else if ((modifiers & JavaSource.MODIFIER_PROTECTED) != 0) {
            w.out.print("protected ");
        } else if ((modifiers & JavaSource.MODIFIER_STATIC) != 0) {
            w.out.print("static ");
        }
        w.out.printf("class %s", name);
        if (!typeArgs.isEmpty()) {
            w.out.print("<");
            boolean first = true;
            for (String arg : typeArgs) {
                if (!first) {
                    w.out.print(", ");
                }
                first = false;
                w.out.print(arg);
            }
            w.out.print(">");
        }
        if (baseClazz != null) {
            w.out.printf(" extends %s", baseClazz);
        }
        if (!interfaces.isEmpty()) {
            w.out.print(" implements");
            boolean first = true;
            for (String interf : interfaces) {
                if (!first) {
                    w.out.print(",");
                    first = true;
                }
                first = false;
                w.out.print(" ");
                w.out.print(interf);
            }
        }
        w.out.println(" {");
        w.increseIndent();
        if (!declarations.isEmpty()) {
            for (Statement decl : declarations) {
                decl.print(w);
            }
            w.out.println();
        }
        boolean first = true;
        for (Method method : methods) {
            if (!first) {
                w.out.println();
            }
            first = false;
            method.print(w);
        }
        for (Clazz clazz : innerClasses) {
            if (!first) {
                w.out.println();
            }
            first = false;
            clazz.print(w);
        }
        w.decreaseIndent();
        w.out.println(w.indent + "}");
    }
}
