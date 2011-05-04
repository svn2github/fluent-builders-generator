package com.sabre.buildergenerator.javamodel.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sabre.buildergenerator.javamodel.IModelHelper;

public class ReflectionModelHelper implements IModelHelper<Type, Method, Exception> {
    private final SignatureUtils signatureUtils = new SignatureUtils();

    public Map<Type, ITypeMethods<Method>> findSetterMethodsForAllTypesReferenced(Type type) throws Exception {
        throw new UnsupportedOperationException("findSetterMethodsForAllTypesReferenced");
    }

    public boolean isCollection(Type owningType, String typeSignature) throws Exception {
        throw new UnsupportedOperationException("isCollection");
    }

    public boolean isCollection(Type type) throws Exception {
        throw new UnsupportedOperationException("isCollection");
    }

    public boolean implementsInterface(Type owningType, String typeSignature, String interfaceName) throws Exception {
        throw new UnsupportedOperationException("implementsInterface");
    }

    public boolean implementsInterface(Type type, String interfaceName) throws Exception {
        if (type instanceof Class) {
            Class<?> typeClass = (Class<?>) type;
            for (Class<?> interfce : typeClass.getInterfaces()) {
                if (interfce.getName().equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasSuperType(Type owningType, String typeSignature, String superTypeName) throws Exception {
        throw new UnsupportedOperationException("hasSuperType");
    }

    public boolean hasSuperType(Type type, String superTypeName) throws Exception {
        throw new UnsupportedOperationException("hasSuperType");
    }

    public String getTypeParameterSignature(String resolvedTypeSignature) {
        throw new UnsupportedOperationException("getTypeParameterSignature");
    }

    public Collection<Method> findSetterMethods(Type type) throws Exception {
        throw new UnsupportedOperationException("findSetterMethods");
    }

    public void walkHierarchyTree(Type type, ITypeInspector<Type> inspector) throws Exception {
        if (type != null) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parametrizedType = (ParameterizedType) type;
                Class<?> rawType = (Class<?>) parametrizedType.getRawType();
                Map<String, String> parameterSubstitution = new HashMap<String, String>();
                int i = 0;
                for (Type actualTypeArgument : parametrizedType.getActualTypeArguments()) {
                    Class<?> c = (Class<?>) actualTypeArgument;
                    parameterSubstitution.put(rawType.getTypeParameters()[i++].getName(), c.getName());
                }
                String signature = typeNameToSignature(rawType.getName());
                inspector.nextSuperType(signature, type, parameterSubstitution);
                walkHierarchyTree(rawType.getGenericSuperclass(), inspector);
            } else if (type instanceof Class) {
                Class<?> clazz = (Class<?>) type;
                String signature = typeNameToSignature(clazz.getName());
                inspector.nextSuperType(signature, type, Collections.<String, String> emptyMap());
                walkHierarchyTree(clazz.getGenericSuperclass(), inspector);
            }
        }
    }

    public void findSetterMethods(Type type, final IMethodInspector<Type, Method> inspector) throws Exception {
        walkHierarchyTree(type, new ITypeInspector<Type>() {
            public void nextSuperType(String fullSignature, Type superType, Map<String, String> typeParameterMapping)
                    throws Exception {
                if (superType instanceof Class) {
                    Class<?> superTypeClass = (Class<?>) superType;
                    for (Method method : superTypeClass.getDeclaredMethods()) {
                        if (isReachableSetterMethod(method)) {
                            inspector.nextMethod(superType, method, typeParameterMapping);
                        }
                    }
                }
            }
        });
    }

    public String typeNameToSignature(String typeName) {
        // TODO
        return signatureUtils.createTypeSignature(typeName, false);
    }

    private boolean isReachableSetterMethod(Method method) {
        // TODO
        return method.getName().startsWith("set");
    }
}
