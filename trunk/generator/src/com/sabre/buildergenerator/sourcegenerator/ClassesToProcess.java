package com.sabre.buildergenerator.sourcegenerator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ClassesToProcess {
    private final Set<String> typesAlradyGenerated = new HashSet<String>();
    private final Set<String> typesToGenerate = new HashSet<String>();

    public void addForProcessing(String typeSignature) {
        if (!typesAlradyGenerated.contains(typeSignature)) {
            typesToGenerate.add(typeSignature);
        }
    }

    public void markAsAlreadyProcessed(String typeSignature) {
        typesAlradyGenerated.add(typeSignature);
        typesToGenerate.remove(typeSignature);
    }

    public String nextClassToProcess() {
        String typeSignature = null;
        Iterator<String> iterator = typesToGenerate.iterator();
        if (iterator.hasNext()) {
            typeSignature = iterator.next();
            markAsAlreadyProcessed(typeSignature);
        }

        return typeSignature;
    }
}
