package com.sabre.buildergenerator.sourcegenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ClassesToProcess {
    private final Set<String> typesAlradyGeneratedInnerBuilders = new HashSet<String>();
    private final Set<String> typesToGenerateInnerBuilders = new HashSet<String>();

    public void addForProcessing(String typeSignature) {
        if (!typesAlradyGeneratedInnerBuilders.contains(typeSignature)) {
            typesToGenerateInnerBuilders.add(typeSignature);
        }
    }

    public void markAsAlreadyProcessed(String typeSignature) {
        typesAlradyGeneratedInnerBuilders.add(typeSignature);
    }

    public String nextClassToProcess() {
        String typeSgn = null;
        Iterator<String> iterator = typesToGenerateInnerBuilders.iterator();
        if (iterator.hasNext()) {
            typeSgn = iterator.next();
            typesToGenerateInnerBuilders.remove(typeSgn);
            markAsAlreadyProcessed(typeSgn);
        }

        return typeSgn;
    }
}
