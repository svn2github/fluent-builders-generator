package com.sabre.buildergenerator.signatureutils;

public class SignatureParserException extends Exception {
    private static final long serialVersionUID = 8662714979833291087L;

    private final String signature;
    private final int pos;

    public SignatureParserException(String aSignature, int aPosition) {
        super("Syntax error in signature: \"" + aSignature + "\" at position " + aPosition);
        signature = aSignature;
        pos = aPosition;
    }

    public String getSignature() {
        return signature;
    }

    public int getPosition() {
        return pos;
    }
}
