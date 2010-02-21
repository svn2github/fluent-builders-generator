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

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class JavaSource {
    public static final int MODIFIER_PUBLIC = 1;
    public static final int MODIFIER_PROTECTED = 2;
    public static final int MODIFIER_PRIVATE = 4;
    public static final int MODIFIER_STATIC = 8;

    private String packge;
    private List<String> imports = new ArrayList<String>();
    private List<Clazz> clazzes = new ArrayList<Clazz>();

    public String getPackge() {
        return packge;
    }

    public void setPackge(String packge) {
        this.packge = packge;
    }

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public List<Clazz> getClazzes() {
        return clazzes;
    }

    public void setClazzes(List<Clazz> classes) {
        this.clazzes = classes;
    }

    public void print(PrintStream out) {
        IndentWriter w = new IndentWriter();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
        w.out = new PrintWriter(outputStreamWriter);
        print(w);
    }

    public void print(IndentWriter w) {
        if (packge != null) {
            w.out.print("package ");
            w.out.print(packge);
            w.out.println(";");
            w.out.println();
        }
        for (String imp : imports) {
            w.out.println("import " + imp + ";");
        }
        if (!imports.isEmpty() && !clazzes.isEmpty()) {
            w.out.println();
        }
        boolean first = true;
        for (Clazz clazz : clazzes) {
            if (!first) {
                w.out.println();
            }
            first = false;
            clazz.print(w);
        }
        w.out.flush();
    }

    public void addImports(Imports newImports) {
        for (String imp : newImports.getImports()) {
            imports.add(imp);
        }
    }
}
