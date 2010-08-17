package com.sabre.buildergenerator.sourcegenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator.MethodConsumer;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerator.MethodProvider;

public class MarkedFields {
    public Map<String, Set<String>> typesAndFieldsToGenerate;

    public MarkedFields() {
    }

    public boolean isBuilderRequestedForType(String elementTypeSignature) {
        return typesAndFieldsToGenerate == null || typesAndFieldsToGenerate.get(elementTypeSignature) != null;
    }

    public boolean isSetterRequestedForField(String elementTypeSignature, String fieldName) {
        return typesAndFieldsToGenerate == null
            || typesAndFieldsToGenerate.get(elementTypeSignature) != null && typesAndFieldsToGenerate.get(
                elementTypeSignature).contains(fieldName);
    }

    public boolean isSetterRequestedForField(IType enclosingType, String fieldName) {
        String enclosingTypeFullyQualifiedName = enclosingType.getFullyQualifiedName('.');
        String enclosingTypeSignature = Signature.createTypeSignature(enclosingTypeFullyQualifiedName, false);

        return isSetterRequestedForField(enclosingTypeSignature, fieldName);
    }

    public void retrieveTypesAndFieldsToGenerate(MethodProvider methodProvider) {
        if (methodProvider != null) {
            typesAndFieldsToGenerate = new HashMap<String, Set<String>>();

            methodProvider.process(new MethodConsumer() {
                    public void nextMethod(IType selectedType, IMethod selectedMethod) {
                        String typeName = selectedType.getFullyQualifiedName();
                        String typeSignature = Signature.createTypeSignature(typeName, false);

                        Set<String> fieldNames = typesAndFieldsToGenerate.get(typeSignature);

                        if (fieldNames == null) {
                            fieldNames = new HashSet<String>();
                            typesAndFieldsToGenerate.put(typeSignature, fieldNames);
                        }

                        String fieldName = BuilderGenerator.fieldNameFromSetter(selectedMethod);

                        fieldNames.add(fieldName);
                    }
                });
        }
    }
}
