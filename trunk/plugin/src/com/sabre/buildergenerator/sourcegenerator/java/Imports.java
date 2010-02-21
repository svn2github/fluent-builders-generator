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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class Imports {
    private final SortedSet<String> imports = new TreeSet<String>();
    private final Set<String> importedClasses = new HashSet<String>();

    public String getClassname(String qualifiedTypeName) {
        String className = qualifiedTypeName.substring(qualifiedTypeName.lastIndexOf('.') + 1);

        if (imports.contains(qualifiedTypeName)) {
            return className;
        } else if (importedClasses.contains(className)) {
            return qualifiedTypeName;
        } else {
            imports.add(qualifiedTypeName);
            importedClasses.add(className);
            return className;
        }
    }

    public SortedSet<String> getImports() {
        return imports;
    }
}
