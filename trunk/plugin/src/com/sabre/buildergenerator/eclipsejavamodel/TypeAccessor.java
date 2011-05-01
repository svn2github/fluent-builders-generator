package com.sabre.buildergenerator.eclipsejavamodel;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.javamodel.ITypeAccessor;

public class TypeAccessor implements ITypeAccessor<IType, ITypeParameter, IMethod, JavaModelException> {
    public ITypeParameter[] getTypeParameters(IType type) throws JavaModelException {
        return type.getTypeParameters();
    }

    public String getTypeParameterName(ITypeParameter typeParam) {
        return typeParam.getElementName();
    }

    public String[] getTypeParameterBounds(ITypeParameter typeParam) throws JavaModelException {
        return typeParam.getBounds();
    }

    public String getFullyQualifiedName(IType type) {
        return type.getFullyQualifiedName();
    }

    public String getFullyQualifiedName(IType type, char c) {
        return type.getFullyQualifiedName(c);
    }

    public String getFullyQualifiedParameterizedName(IType resolvedType) throws JavaModelException {
        return resolvedType.getFullyQualifiedParameterizedName();
    }

    public String getMethodName(IMethod method) {
        return method.getElementName();
    }

    public boolean isClassFromSource(IType type) throws JavaModelException {
        return type.isClass() && type.isStructureKnown() && !type.isBinary();
    }

    public String[] getMethodExceptionTypes(IMethod method) throws JavaModelException {
        return method.getExceptionTypes();
    }

    public String[] getMethodParameterTypes(IMethod method) {
        return method.getParameterTypes();
    }

}
