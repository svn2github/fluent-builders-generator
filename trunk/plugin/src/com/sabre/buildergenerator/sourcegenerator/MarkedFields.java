package com.sabre.buildergenerator.sourcegenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sabre.buildergenerator.javamodel.ITypeAccessor;
import com.sabre.buildergenerator.signatureutils.SignatureUtil;


public class MarkedFields<IType, ITypeParameter, IMethod, JavaModelException extends Exception> {
    private static final String SETTER_PREFIX = "set";

    public Map<String, Set<String>> typesAndFieldsToGenerate;
    private ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException> typeAccessor;

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
        String enclosingTypeFullyQualifiedName = typeAccessor.getFullyQualifiedName(enclosingType);
        String enclosingTypeSignature = SignatureUtil.createTypeSignature(enclosingTypeFullyQualifiedName);

        return isSetterRequestedForField(enclosingTypeSignature, fieldName);
    }

    public void retrieveTypesAndFieldsToGenerate(MethodProvider<IType, IMethod> methodProvider) {
        if (methodProvider != null) {
            typesAndFieldsToGenerate = new HashMap<String, Set<String>>();

            methodProvider.process(new MethodConsumer<IType, IMethod>() {
                    public void nextMethod(IType selectedType, IMethod selectedMethod) {
                        String typeName = typeAccessor.getFullyQualifiedName(selectedType);
                        String typeSignature = SignatureUtil.createTypeSignature(typeName);

                        Set<String> fieldNames = typesAndFieldsToGenerate.get(typeSignature);

                        if (fieldNames == null) {
                            fieldNames = new HashSet<String>();
                            typesAndFieldsToGenerate.put(typeSignature, fieldNames);
                        }

                        String fieldName = fieldNameFromSetter(selectedMethod);

                        fieldNames.add(fieldName);
                    }
                });
        }
    }

    // TODO move to helper
    public String fieldNameFromSetter(IMethod method) {
        return fieldNameFromSetterName(typeAccessor.getMethodName(method));
    }

    // TODO move to helper
    private String fieldNameFromSetterName(String setterName) {
        String fieldName = setterName.substring(SETTER_PREFIX.length());

        return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

    public void setTypeAccessor(ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException> typeAccessor) {
        this.typeAccessor = typeAccessor;
    }
}
