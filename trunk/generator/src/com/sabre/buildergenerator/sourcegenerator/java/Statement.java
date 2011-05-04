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

import java.util.List;

public class Statement {
    private String statement;
    private List<Object> params;

    public String getStatement() {
        return statement;
    }

    public void setStatement(String format) {
        this.statement = format;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public void print(IndentWriter w) {
        if (statement != null){
            w.out.print(w.indent);
            if (params != null) {
                w.out.printf(statement, params.toArray(new Object[params.size()]));
            } else {
                w.out.print(statement);
            }
        }
        w.out.println();
    }
}
