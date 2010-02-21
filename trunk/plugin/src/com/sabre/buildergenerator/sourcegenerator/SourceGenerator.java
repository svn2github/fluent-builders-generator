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

public class SourceGenerator {

    protected static final int MODIFIER_PUBLIC = 1;
    protected static final int MODIFIER_PROTECTED = 2;
    protected static final int MODIFIER_PRIVATE = 4;
    protected static final int MODIFIER_STATIC = 8;
    protected PrintWriter out;
    protected String indent = "";

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

    protected void increseIndent() {
        indent += "    ";
    }

    protected void decreaseIndent() {
        indent = "                ".substring(0, indent.length() - 4);
    }

    protected void startMethod(int modifiers, String returnType, String methodName, String[] exceptions) {
        startMethod(modifiers, returnType, methodName, null, null, exceptions);
    }

    protected void startMethod(int modifiers, String returnType, String methodName, String parameterType, String parameterName, String[] exceptions) {
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

        if (returnType != null) {
            out.printf("%s %s(", returnType, methodName);
        } else {
            out.printf("%s(", methodName);
        }

        if (parameterType != null && parameterName != null) {
            out.printf("%s %s", parameterType, parameterName);
        }

        out.print(") ");

        if (exceptions != null && exceptions.length > 0) {
            out.print("throws ");

            int i = exceptions.length;

            for (String exceptionType : exceptions) {
                boolean isLast = --i == 0;

                out.print(exceptionType + (isLast ? " " : ", "));
            }
        }

        out.println("{");
        increseIndent();
    }

    protected void addLine() {
        out.println();
    }

    protected void addLine(String codeLine, Object... args) {
        out.print(indent);
        out.printf(codeLine, args);
        out.println();
    }

    protected void endMethod(String returnExpression, Object... args) {
        addLine("return " + returnExpression + ";", args);
        endMethod();
    }

    protected void endMethod() {
        closeBlock();
    }

    protected void closeBlock() {
        decreaseIndent();
        addLine("}");
    }

}
