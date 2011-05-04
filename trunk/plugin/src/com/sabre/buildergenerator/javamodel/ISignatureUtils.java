package com.sabre.buildergenerator.javamodel;

public interface ISignatureUtils {
    public String createTypeSignature(String typeName, boolean isResolved);

    public String[] getTypeArguments(String resolvedFieldTypeSignature);
}
