package com.sabre.buildergenerator.javamodel;

public interface ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException extends Exception> {

    public abstract ITypeParameter[] getTypeParameters(IType type) throws JavaModelException;

    public abstract String getTypeParameterName(ITypeParameter typeParam);

    public abstract String[] getTypeParameterBounds(ITypeParameter typeParam) throws JavaModelException;

    public abstract String getFullyQualifiedName(IType type);

    public abstract String getFullyQualifiedName(IType type, char c);

    public abstract String getFullyQualifiedParameterizedName(IType resolvedType) throws JavaModelException;

    public abstract String getMethodName(IMethod method);

    public abstract boolean isClassFromSource(IType type) throws JavaModelException;

    public abstract String[] getMethodExceptionTypes(IMethod method) throws JavaModelException;

    public abstract String[] getMethodParameterTypes(IMethod method);
}
