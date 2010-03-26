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

import com.sabre.buildergenerator.signatureutils.SignatureResolver;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TypeHelper {
    public static Map<IType, List<IMethod>> findSetterMethodsForAllTypesReferenced(IType type) throws Exception {
        Map<IType, List<IMethod>> result = new HashMap<IType, List<IMethod>>();
        final Set<IType> types = new HashSet<IType>();
        types.add(type);

        while (!types.isEmpty()) {
            Iterator<IType> iterator = types.iterator();
            IType nextType = iterator.next();
            iterator.remove();
            final ArrayList<IMethod> methods = new ArrayList<IMethod>();
            findSetterMethods(nextType, new MethodInspector() {
                public void nextMethod(IType methodOwnerType, IMethod method, Map<String, String> parameterSubstitution)
                        throws Exception {
                    methods.add(method);
                    String parameterTypeSignature = method.getParameterTypes()[0];
                    String qualifiedParameterTypeSignature = SignatureResolver.resolveTypeWithParameterMapping(
                            methodOwnerType, parameterTypeSignature, parameterSubstitution);
                    IType newType = null;
                    if (isCollection(qualifiedParameterTypeSignature)) {
                        String innerTypeSignature = getInner(qualifiedParameterTypeSignature);
                        newType = SignatureResolver.resolveType(methodOwnerType, innerTypeSignature);
                    } else {
                        newType = SignatureResolver.resolveType(methodOwnerType, qualifiedParameterTypeSignature);
                    }
                    if (newType != null) {
                        types.add(newType);
                    }
                }
            });
            if (!methods.isEmpty()) {
                result.put(nextType, methods);
            }
        }

        return result;
    }

    private static boolean isCollection(String fieldType) {
        boolean isCollection = fieldType.contains("java.util.Collection<");
        boolean isList = fieldType.contains("java.util.List<");
        boolean isArrayList = fieldType.contains("java.util.ArrayList<");
        boolean isLinkedList = fieldType.contains("java.util.LinkedList<");
        boolean isSet = fieldType.contains("java.util.Set<");
        boolean isHashSet = fieldType.contains("java.util.HashSet<");
        boolean isTreeSet = fieldType.contains("java.util.TreeSet<");
        return isCollection || isList || isArrayList || isLinkedList || isSet || isHashSet || isTreeSet;
    }

    private static String getInner(String qualifiedParameterTypeSignature) {
        int beg = qualifiedParameterTypeSignature.indexOf('<');
        if (beg != -1) {
            int end = qualifiedParameterTypeSignature.lastIndexOf('>');
            String innerContent = qualifiedParameterTypeSignature.substring(beg + 1, end);
            if (innerContent.charAt(0) == Signature.C_STAR
                    || innerContent.charAt(0) == Signature.C_EXTENDS
                    || innerContent.charAt(0) == Signature.C_SUPER
                    || innerContent.charAt(0) == Signature.C_CAPTURE) {
                return innerContent.substring(1);
            } else {
                return innerContent;
            }
        }
        return qualifiedParameterTypeSignature;
    }

    @SuppressWarnings("unused")
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
        return method.getElementName().startsWith(BuilderGenerator.SETTER_PREFIX)
            && method.getReturnType().equals(Signature.SIG_VOID) && method.getParameterTypes().length == 1;
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

    public static void walkHierarchyTree(IType type, TypeInspector inspector) throws Exception {
        Map<String, String> typeParameterMapping = new HashMap<String, String>();

        inspector.nextSuperType(Signature.createTypeSignature(type.getFullyQualifiedName(), true), type,
            typeParameterMapping);

        String superclassTypeSignature;
        IType superType = type;

        while ((superclassTypeSignature = superType.getSuperclassTypeSignature()) != null) {
            String resolvedTypeSignature = SignatureResolver.resolveSignature(superType, superclassTypeSignature);

            superType = SignatureResolver.resolveType(superType, Signature.getTypeErasure(resolvedTypeSignature));

            if (superType == null) {
                break;
            }

            typeParameterMapping = addParameterMappings(typeParameterMapping, superType, resolvedTypeSignature);
            inspector.nextSuperType(resolvedTypeSignature, superType, typeParameterMapping);
        }
    }

    public static void findSetterMethods(IType type, final MethodInspector inspector) throws Exception {
        walkHierarchyTree(type, new TypeInspector() {
                public void nextSuperType(String fullSignature, IType superType,
                    Map<String, String> typeParameterMapping) throws Exception {
                    try {
                        for (IMethod method : superType.getMethods()) {
                            if (isSetterMethod(method)) {
                                inspector.nextMethod(superType, method, typeParameterMapping);
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

    static interface MethodInspector {
        public void nextMethod(IType methodOwnerType, IMethod method, Map<String, String> parameterSubstitution)
            throws Exception;
    }

    static interface TypeInspector {
        public void nextSuperType(String fullSignature, IType superType, Map<String, String> parameterSubstitution)
            throws Exception;
    }
}
