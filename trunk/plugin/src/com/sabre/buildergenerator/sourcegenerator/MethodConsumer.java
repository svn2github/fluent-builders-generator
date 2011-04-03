package com.sabre.buildergenerator.sourcegenerator;

public interface MethodConsumer<IType, IMethod> {
    void nextMethod(IType type, IMethod method);
}