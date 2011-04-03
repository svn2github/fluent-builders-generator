package com.sabre.buildergenerator.javamodel;

import java.util.Collection;
import java.util.Map;


public interface IModelHelper<IType, IMethod, JavaModelException extends Exception> {

    public abstract Map<IType, ? extends ITypeMethods<IMethod>> findSetterMethodsForAllTypesReferenced(IType type) throws Exception;

    public abstract boolean isCollection(IType owningType, String typeSignature) throws Exception;

    public abstract boolean isCollection(IType type) throws Exception;

    public abstract boolean implementsInterface(IType owningType, String typeSignature, String interfaceName)
            throws Exception;

    public abstract boolean implementsInterface(IType type, String interfaceName) throws JavaModelException;

    public abstract boolean hasSuperType(IType owningType, String typeSignature, String superTypeName) throws Exception;

    public abstract boolean hasSuperType(IType type, String superTypeName) throws JavaModelException;

    public abstract String getTypeParameterSignature(String resolvedTypeSignature);

    public abstract Collection<IMethod> findSetterMethods(IType type) throws JavaModelException;

    public abstract void walkHierarchyTree(IType type, ITypeInspector<IType> inspector) throws Exception;

    public abstract void findSetterMethods(IType type, final IMethodInspector<IType, IMethod> inspector) throws Exception;

    public static interface ITypeInspector<IType> {
        public void nextSuperType(String fullSignature, IType superType, Map<String, String> parameterSubstitution)
            throws Exception;
    }

    public static interface IMethodInspector<IType, IMethod> {
        public void nextMethod(IType methodOwnerType, IMethod method, Map<String, String> parameterSubstitution)
            throws Exception;
    }

    public static interface ITypeMethods<IMethod> {

        public abstract Collection<IMethod> getMethods();

        public abstract Map<String, String> getParameterSubstitution();

    }
}
