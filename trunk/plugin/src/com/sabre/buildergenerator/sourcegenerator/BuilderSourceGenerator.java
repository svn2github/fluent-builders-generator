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



public class BuilderSourceGenerator extends AbstractBuilderSourceGenerator<String> {
    private String getClassQName(String aT) {
        int i = aT.indexOf('<');

        return i != -1 ? aT.substring(0, i) : aT;
    }

    @Override public String getClassName(String aT) {
        String classQName = getClassQName(aT);
        int i = classQName.lastIndexOf('.');

        return i != -1 ? classQName.substring(i + 1) : classQName;
    }

    @Override public String getType(String aT) {
        return aT;
    }
}
