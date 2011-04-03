package com.sabre.buildergenerator.sourcegenerator;

public interface MethodProvider<IType, IMethod> {
    void process(MethodConsumer<IType, IMethod> consumer);
}