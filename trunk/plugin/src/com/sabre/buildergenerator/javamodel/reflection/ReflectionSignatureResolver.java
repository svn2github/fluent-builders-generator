package com.sabre.buildergenerator.javamodel.reflection;

import java.lang.reflect.Type;
import java.util.Map;

import com.sabre.buildergenerator.javamodel.ISignatureResolver;
import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureUtil;

public class ReflectionSignatureResolver implements ISignatureResolver<Type, Exception> {
    private final SignatureUtils signatureUtils = new SignatureUtils();

    public String resolveSignature(Type owningType, String signature) throws SignatureParserException, Exception {
        return signature;
    }

    public Type resolveType(Type owningType, String signature) throws Exception, SignatureParserException {
        // TODO Auto-generated method stub
        String typeName = SignatureUtil.signatureToTypeName(signature);
        return Class.forName(typeName);
    }

    public String resolveTypeWithParameterMapping(Type owningType, String typeSignature,
            Map<String, String> typeParameterMapping) throws Exception, SignatureParserException {
        for (String key : typeParameterMapping.keySet()) {
            String keySignature = createTypeSignature(key);

            typeSignature = typeSignature.replaceAll(keySignature, typeParameterMapping.get(key));
        }

        typeSignature = resolveSignature(owningType, typeSignature);

        return typeSignature;
    }

    public String getTypeErasure(String signature) {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getTypeArguments(String resolvedFieldTypeSignature) {
        // TODO Auto-generated method stub
        return signatureUtils.getTypeArguments(resolvedFieldTypeSignature);
    }

    public String createTypeSignature(String typeQName) {
        // TODO Auto-generated method stub
        return signatureUtils.createTypeSignature(typeQName, false);
    }

}
