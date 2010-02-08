/**
 * Copyright (c) 2009-2010 fluent-builder-generator for Eclipse commiters.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sabre Polska sp. z o.o. - initial implementation during Hackday
 */

package com.sabre.buildergenerator.sourcegenerator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.signatureutils.SignatureResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TypeHelper {
    public static Map<IType, List<IMethod>> findSetterMethodsForInhritedTypes(IType type) throws Exception {
        Map<IType, List<IMethod>> methodsTree = findAllMethodsForInhritedTypes(type);

        for (IType t : methodsTree.keySet()) {
            methodsTree.put(t, filterSetterMethods(methodsTree.get(t)));
        }

        return methodsTree;
    }

    public static Map<IType, List<IMethod>> findAllMethodsForInhritedTypes(IType type) throws Exception {
        Map<IType, List<IMethod>> result = new HashMap<IType, List<IMethod>>();

        result.put(type, new ArrayList<IMethod>(Arrays.asList(type.getMethods())));

        IType superType = type;
        String supertypeSignature;

        while (superType != null && (supertypeSignature = superType.getSuperclassTypeSignature()) != null) {
            String resolvedTypeSignature = SignatureResolver.resolveSignature(type, supertypeSignature);
            if (resolvedTypeSignature == null) {
                break;
            }

            superType = SignatureResolver.resolveType(type, resolvedTypeSignature);

            if (superType == null) {
                break;
            }

            IMethod[] superMethods = superType.getMethods();

            if (superMethods != null) {
                result.put(superType, Arrays.asList(superMethods));
            }
        }

        return result;
    }

    public static String[] findFieldNames(IType type) throws Exception {
        final List<String> fieldNames = new ArrayList<String>();
        findSetterMethods(type, new MethodInspector() {
            public void nextMethod(IMethod method, Map<String, String> typeParameterMapping) {
                fieldNames.add(getFieldName(method));
            }
        });
        return fieldNames.toArray(new String[fieldNames.size()]);
    }

    private static String getFieldName(IMethod method) {
        String tmp = method.getElementName().substring(BuilderGenerator.SETTER_PREFIX.length());
        return capitalize(tmp);
    }

    private static String capitalize(String name) {
        StringBuilder buf = new StringBuilder(name);

        buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));

        return buf.toString();
    }

    private static List<IMethod> findSetterMethods(IType type) throws JavaModelException {
        List<IMethod> methods = findAllMethods(type);
        List<IMethod> setterMethods = filterSetterMethods(methods);

        return setterMethods;
    }

    private static List<IMethod> filterSetterMethods(List<IMethod> methods) throws JavaModelException {
        List<IMethod> setterMethods = new ArrayList<IMethod>();

        for (IMethod method : methods) {
            if (isSetterMethod(method)) {
                setterMethods.add(method);
            }
        }

        return setterMethods;
    }

    private static boolean isSetterMethod(IMethod method) throws JavaModelException {
        return method.getElementName().startsWith(BuilderGenerator.SETTER_PREFIX) && method.getReturnType().equals(Signature.SIG_VOID) && method.getParameterTypes().length == 1;
    }

    private static List<IMethod> findAllMethods(IType type) throws JavaModelException {
        List<IMethod> methods = new ArrayList<IMethod>(Arrays.asList(type.getMethods()));

        ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
        IType[] superTypes = typeHierarchy.getAllSuperclasses(type);

        for (IType superType : superTypes) {
            if (superType.getTypeParameters().length > 0) {
                break;
            }
            IMethod[] superMethods = superType.getMethods();

            if (superMethods != null) {
                methods.addAll(Arrays.asList(superMethods));
            }
        }

        return methods;
    }

    static interface TypeInspector {
        public void nextSuperType(String fullSignature, IType type, Map<String, String> parameterSubstitution) throws Exception;
    }

    public static void walkHierarchyTree(IType type, TypeInspector inspector) throws Exception {
        Map<String, String> typeParameterMapping = new HashMap<String, String>();
        inspector.nextSuperType(Signature.createTypeSignature(type.getFullyQualifiedParameterizedName(), true), type, typeParameterMapping);
        String superclassTypeSignature;
        while((superclassTypeSignature = type.getSuperclassTypeSignature()) != null) {
            String resolvedTypeSignature = SignatureResolver.resolveSignature(type, superclassTypeSignature);
            type = SignatureResolver.resolveType(type, Signature.getTypeErasure(resolvedTypeSignature));
            if (type == null) {
                break;
            }
            typeParameterMapping = addParameterMappings(typeParameterMapping, type, resolvedTypeSignature);
            inspector.nextSuperType(resolvedTypeSignature, type, typeParameterMapping);
        }
    }

    static interface MethodInspector {
        public void nextMethod(IMethod method, Map<String, String> parameterSubstitution) throws Exception;
    }

    public static void findSetterMethods(IType type, final MethodInspector inspector) throws Exception {
        walkHierarchyTree(type, new TypeInspector() {
            public void nextSuperType(String fullSignature, IType type, Map<String, String> typeParameterMapping) throws Exception {
                try {
                    for (IMethod method : type.getMethods()) {
                        if (isSetterMethod(method)) {
                            inspector.nextMethod(method, typeParameterMapping);
                        }
                    }
                } catch (JavaModelException e) {
                }
            }
        });
    }

    private static Map<String, String> addParameterMappings(Map<String, String> typeParameterMapping, IType type,
            String resolvedTypeSignature) throws JavaModelException {
        if (typeParameterMapping == null) {
            typeParameterMapping = new HashMap<String, String>();
        }
        String[] typeArguments = Signature.getTypeArguments(resolvedTypeSignature);
        int i = 0;
        for (ITypeParameter typeParameter : type.getTypeParameters()) {
            typeParameterMapping.put(typeParameter.getElementName(), typeArguments[i++]);
        }
        return typeParameterMapping;
    }
}
