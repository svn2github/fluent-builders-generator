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

import java.io.PrintWriter;

public class IndentWriter {
    public PrintWriter out;
    public String indent = "";

    protected void increseIndent() {
        indent += "    ";
    }

    protected void decreaseIndent() {
        indent = indent.substring(4);
    }
}
