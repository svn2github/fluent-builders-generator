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

public class Method {
    private List<String> annotations = new ArrayList<String>();
    private int modifiers;
    private List<String> typeArgs = new ArrayList<String>();
    private String returnType;
    private String name;
    private List<MethodParameter> parameters = new ArrayList<MethodParameter>();
    private List<String> exceptions = new ArrayList<String>();
    private List<Statement> instructions = new ArrayList<Statement>();
    private Statement returnValue;

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

    public List<String> getTypeArgs() {
        return typeArgs;
    }

    public void setTypeArgs(List<String> typeArgs) {
        this.typeArgs = typeArgs;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String type) {
        this.returnType = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<MethodParameter> parameters) {
        this.parameters = parameters;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public List<Statement> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Statement> instructions) {
        this.instructions = instructions;
    }

    public Statement getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Statement returnValue) {
        this.returnValue = returnValue;
    }

    public void print(IndentWriter w) {
        for (String annotation : annotations) {
            w.out.print(w.indent);
            w.out.println(annotation);
        }
        w.out.print(w.indent);
        if ((modifiers & JavaSource.MODIFIER_PUBLIC) != 0) {
            w.out.print("public ");
        } else if ((modifiers & JavaSource.MODIFIER_PROTECTED) != 0) {
            w.out.print("protected ");
        } else if ((modifiers & JavaSource.MODIFIER_PRIVATE) != 0) {
            w.out.print("private ");
        }
        if ((modifiers & JavaSource.MODIFIER_STATIC) != 0) {
            w.out.print("static ");
        }
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
            w.out.print("> ");
        }
        if (returnType != null) {
            w.out.print(returnType);
            w.out.print(" ");
        }
        w.out.printf("%s(", name);
        boolean first = true;
        for (MethodParameter param : parameters) {
            if (!first) {
                w.out.print(", ");
            }
            first = false;
            param.print(w);
        }
        w.out.print(")");
        if (!exceptions.isEmpty()) {
            w.out.print(" throws");
            first = true;
            for (String ex : exceptions) {
                if (!first) {
                    w.out.print(",");
                }
                first = false;
                w.out.print(" ");
                w.out.print(ex);
            }
        }
        w.out.println(" {");
        w.increseIndent();
        for (Statement statement : instructions) {
            if (statement != null) {
                statement.print(w);
            } else {
                w.out.println();
            }
        }
        if (returnValue != null) {
            Statement statement = new Statement();
            statement.setStatement("return " + returnValue.getStatement() + ";");
            statement.setParams(returnValue.getParams());
            statement.print(w);
        }
        w.decreaseIndent();
        w.out.print(w.indent);
        w.out.println("}");
    }
}
