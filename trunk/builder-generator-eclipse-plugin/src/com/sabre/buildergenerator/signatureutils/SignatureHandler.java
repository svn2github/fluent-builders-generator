package com.sabre.buildergenerator.signatureutils;

public interface SignatureHandler {
    void simpleType(char type) throws ExceptionWrapper;
    void array() throws ExceptionWrapper;
    void typeVariable(String identifier) throws ExceptionWrapper;
    void startResolvedType(String identifier) throws ExceptionWrapper;
    void startUnresolvedType(String identifier) throws ExceptionWrapper;
    void innerType(String identifier) throws ExceptionWrapper;
    void endType() throws ExceptionWrapper;
    void startTypeArguments() throws ExceptionWrapper;
    void endTypeArguments() throws ExceptionWrapper;
    void wildcardAny() throws ExceptionWrapper;
    void wildcardExtends() throws ExceptionWrapper;
    void wildcardSuper() throws ExceptionWrapper;
    void captureOf() throws ExceptionWrapper;
}
