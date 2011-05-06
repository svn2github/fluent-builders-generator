package com.sabre.buildergenerator.javamodel.reflection;

import com.sabre.buildergenerator.javamodel.ISignatureUtils;
import com.sabre.buildergenerator.signatureutils.SignatureUtil;

public class SignatureUtils implements ISignatureUtils {

    public String createTypeSignature(String typeName, boolean isResolved) {
        return SignatureUtil.typeNameToSignature(typeName);
    }

    public String[] getTypeArguments(String signature) {
        throw new UnsupportedOperationException("getTypeArguments");
    }

}
