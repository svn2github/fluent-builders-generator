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
    @Override public String getClassQName(String aT) {
        int i = aT.indexOf('<');

        return i != -1 ? aT.substring(0, i) : aT;
    }

    @Override public String getClassName(String aT) {
        String classQName = getClassQName(aT);
        int i = classQName.lastIndexOf('.');

        return i != -1 ? classQName.substring(i + 1) : classQName;
    }

    @Override public String getPackage(String aT) {
        int i = aT.lastIndexOf('.');

        return i != -1 ? aT.substring(0, i) : aT;
    }

    @Override public String getInnerType(String aT) {
        int i = aT.indexOf('<');

        String ret = i != -1 ? aT.substring(i + 1, aT.length() - 1) : "";

        i = ret.lastIndexOf(' ');

        if (i != -1) {
            ret = ret.substring(i + 1);
        }

        return ret;
    }

    @Override public String getType(String aT) {
        return aT;
    }

    @Override public String getTypeWithParams(String aT) {
        int i = aT.lastIndexOf('.');

        return i != -1 ? aT.substring(i + 1) : aT;
    }
}
