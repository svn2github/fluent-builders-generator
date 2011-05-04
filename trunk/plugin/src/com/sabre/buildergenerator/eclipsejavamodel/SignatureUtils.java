package com.sabre.buildergenerator.eclipsejavamodel;

import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.javamodel.ISignatureUtils;

public class SignatureUtils implements ISignatureUtils {

    public String createTypeSignature(String typeName, boolean isResolved) {
        return Signature.createTypeSignature(typeName, isResolved);
    }

    public String[] getTypeArguments(String parameterizedTypeSignature) {
        return Signature.getTypeArguments(parameterizedTypeSignature);
    }

}
