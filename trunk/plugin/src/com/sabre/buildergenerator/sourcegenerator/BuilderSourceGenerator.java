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
    @Override public String getClassName(String aType) {
        int e = aType.indexOf('<');
        if (e == -1) {
            e = aType.length();
        }
        int b = aType.lastIndexOf('.', e) + 1;

        return aType.substring(b, e);
    }

    @Override public String getType(String aT) {
        return aT;
    }
}
