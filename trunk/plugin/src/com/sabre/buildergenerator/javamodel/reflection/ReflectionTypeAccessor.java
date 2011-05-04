package com.sabre.buildergenerator.javamodel.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.sabre.buildergenerator.javamodel.ITypeAccessor;

public class ReflectionTypeAccessor implements ITypeAccessor<Type, TypeVariable<?>, Method, Exception> {
    private final SignatureUtils signatureUtils = new SignatureUtils();

    public TypeVariable<?>[] getTypeParameters(Type type) throws Exception {
        return ((Class<?>)type).getTypeParameters();
    }

    public String getTypeParameterName(TypeVariable<?> typeParam) {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getTypeParameterBounds(TypeVariable<?> typeParam) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public String getFullyQualifiedName(Type type) {
        return ((Class<?>)type).getName();
    }

    public String getFullyQualifiedName(Type type, char c) {
        // TODO Auto-generated method stub
        return ((Class<?>)type).getName();
    }

    public String getFullyQualifiedParameterizedName(Type resolvedType) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMethodName(Method method) {
        return method.getName();
    }

    public boolean isClassFromSource(Type type) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    public String[] getMethodExceptionTypes(Method method) throws Exception {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        String[] result = new String[exceptionTypes.length];
        int i = 0;
        for (Class<?> type : exceptionTypes) {
            result[i++] = signatureUtils.createTypeSignature(type.getName(), false);
        }
        return result;
    }

    public String[] getMethodParameterTypes(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] result = new String[parameterTypes.length];
        int i = 0;
        for (Class<?> type : parameterTypes) {
            result[i++] = signatureUtils.createTypeSignature(type.getName(), false);
        }
        return result;
    }

}
