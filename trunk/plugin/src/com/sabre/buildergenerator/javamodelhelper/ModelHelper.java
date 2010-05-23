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

package com.sabre.buildergenerator.javamodelhelper;

import com.sabre.buildergenerator.signatureutils.SignatureResolver;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ModelHelper {
    private static final String OBJECT_SIGNATURE = "Qjava.lang.Object;";
    private static final String COLLECTION_INTERFACE_NAME = "java.util.Collection";
    private static final String SETTER_PREFIX = "set";

    private final SignatureResolver typeResolver = new SignatureResolver();

    public Map<IType, Collection<IMethod>> findSetterMethodsForAllTypesReferenced(IType type) throws Exception {
        final Map<IType, Collection<IMethod>> result = new HashMap<IType, Collection<IMethod>>();
        final Set<IType> types = new HashSet<IType>();
        types.add(type);

        while (!types.isEmpty()) {
            Iterator<IType> iterator = types.iterator();
            final IType nextType = iterator.next();
            iterator.remove();
            findSetterMethods(nextType, new MethodInspector() {
                public void nextMethod(IType methodOwnerType, IMethod method, Map<String, String> parameterSubstitution)
                        throws Exception {
                    String parameterTypeSignature = method.getParameterTypes()[0];
                    String qualifiedParameterTypeSignature = typeResolver.resolveTypeWithParameterMapping(
                            methodOwnerType, parameterTypeSignature, parameterSubstitution);
                    IType newType = null;
                    if (isCollection(methodOwnerType, qualifiedParameterTypeSignature)) {
                        String innerTypeSignature = getTypeParameterSignature(qualifiedParameterTypeSignature);
                        newType = typeResolver.resolveType(methodOwnerType, innerTypeSignature);
                    } else {
                        newType = typeResolver.resolveType(methodOwnerType, qualifiedParameterTypeSignature);
                    }

                    inspectSetter(nextType, method, newType);
                }

                private void inspectSetter(final IType nextType, IMethod method, IType newType) {
                    Collection<IMethod> methods = result.get(nextType);
                    if (methods == null) {
                        methods = new ArrayList<IMethod>();
                        result.put(nextType, methods);
                    }
                    methods.add(method);
                    if (newType != null) {
                        types.add(newType);
                    }
                }
            });
        }

        return result;
    }

    public boolean isCollection(IType owningType, String typeSignature) throws Exception {
        return implementsInterface(owningType, typeSignature, COLLECTION_INTERFACE_NAME);
    }

    public boolean isCollection(IType type) throws Exception {
        return implementsInterface(type, COLLECTION_INTERFACE_NAME);
    }

    public boolean implementsInterface(IType owningType, String typeSignature, String interfaceName) throws Exception {
        IType type = typeResolver.resolveType(owningType, typeSignature);
        return type != null ? implementsInterface(type, interfaceName) : false;
    }

    public boolean implementsInterface(IType type, String interfaceName) throws JavaModelException {
        ITypeHierarchy supertypeHierarchy = type.newSupertypeHierarchy(null);
        IType[] superInterfaces = supertypeHierarchy.getAllInterfaces();
        for (IType interfaceType : superInterfaces) {
            if (interfaceType.getFullyQualifiedName().equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSuperType(IType owningType, String typeSignature, String superTypeName) throws Exception {
        IType type = typeResolver.resolveType(owningType, typeSignature);
        return type != null ? hasSuperType(type, superTypeName) : false;
    }

    public boolean hasSuperType(IType type, String superTypeName) throws JavaModelException {
        ITypeHierarchy supertypeHierarchy = type.newSupertypeHierarchy(null);
        IType[] superTypes = supertypeHierarchy.getAllTypes();
        for (IType superType : superTypes) {
            if (superType.getFullyQualifiedName().equals(superTypeName)) {
                return true;
            }
        }
        return false;
    }

    public String getTypeParameterSignature(String resolvedTypeSignature) {
        String[] typeArguments = Signature.getTypeArguments(resolvedTypeSignature);
        if (typeArguments != null && typeArguments.length == 1) {
            String fieldTypeArgumentSignature = typeArguments[0];
            return fieldTypeArgumentSignature.charAt(0) == Signature.C_EXTENDS
                    || fieldTypeArgumentSignature.charAt(0) == Signature.C_SUPER
                    || fieldTypeArgumentSignature.charAt(0) == Signature.C_CAPTURE
                    ? fieldTypeArgumentSignature.substring(1)
                    : fieldTypeArgumentSignature.equals(String.valueOf(Signature.C_STAR)) ? OBJECT_SIGNATURE
                            : fieldTypeArgumentSignature;
        }
        return null;
    }

    public Collection<IMethod> findSetterMethods(IType type) throws JavaModelException {
        Collection<IMethod> methods = findAllMethods(type);
        Collection<IMethod> setterMethods = filterSetterMethods(methods);

        return setterMethods;
    }

    private Collection<IMethod> filterSetterMethods(Collection<IMethod> methods) throws JavaModelException {
        List<IMethod> setterMethods = new ArrayList<IMethod>();

        for (IMethod method : methods) {
            if (isSetterMethod(method)) {
                setterMethods.add(method);
            }
        }

        return setterMethods;
    }

    private boolean isSetterMethod(IMethod method) throws JavaModelException {
        return method.getElementName().startsWith(SETTER_PREFIX)
            && method.getReturnType().equals(Signature.SIG_VOID) && method.getParameterTypes().length == 1
            && Flags.isPublic(method.getFlags());
    }

    private Collection<IMethod> findAllMethods(IType type) throws JavaModelException {
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

    public void walkHierarchyTree(IType type, TypeInspector inspector) throws Exception {
        Map<String, String> typeParameterMapping = new HashMap<String, String>();

        inspector.nextSuperType(Signature.createTypeSignature(type.getFullyQualifiedName(), true), type,
            typeParameterMapping);

        String superclassTypeSignature;
        IType superType = type;

        while ((superclassTypeSignature = superType.getSuperclassTypeSignature()) != null) {
            String resolvedTypeSignature = typeResolver.resolveSignature(superType, superclassTypeSignature);

            superType = typeResolver.resolveType(superType, Signature.getTypeErasure(resolvedTypeSignature));

            if (superType == null) {
                break;
            }

            typeParameterMapping = addParameterMappings(typeParameterMapping, superType, resolvedTypeSignature);
            inspector.nextSuperType(resolvedTypeSignature, superType, typeParameterMapping);
        }
    }

    public void findSetterMethods(IType type, final MethodInspector inspector) throws Exception {
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

    private Map<String, String> addParameterMappings(Map<String, String> typeParameterMapping, IType type,
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

    public static interface MethodInspector {
        public void nextMethod(IType methodOwnerType, IMethod method, Map<String, String> parameterSubstitution)
            throws Exception;
    }

    public static interface TypeInspector {
        public void nextSuperType(String fullSignature, IType superType, Map<String, String> parameterSubstitution)
            throws Exception;
    }
}
