package com.sabre.buildergenerator.javamodel;

public interface ITypeResolver<IType, JavaModelException extends Exception> {

    public abstract String resolveType(IType owningType, String type) throws JavaModelException;

}
