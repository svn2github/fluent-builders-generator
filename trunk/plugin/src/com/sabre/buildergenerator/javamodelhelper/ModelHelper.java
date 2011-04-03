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

import com.sabre.buildergenerator.Activator;
import com.sabre.buildergenerator.javamodel.IModelHelper;
import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureResolver;
import com.sabre.buildergenerator.signatureutils.SignatureUtil;

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


public class ModelHelper implements IModelHelper<IType, IMethod, JavaModelException> {
    private static final String OBJECT_SIGNATURE = "Qjava.lang.Object;";
    private static final String COLLECTION_INTERFACE_NAME = "java.util.Collection";
    private static final String SETTER_PREFIX = "set";
    private final SignatureResolver signatureResolver = new SignatureResolver();

    public Map<IType, TypeMethods> findSetterMethodsForAllTypesReferenced(IType type) throws Exception {
        final Map<IType, TypeMethods> result = new HashMap<IType, TypeMethods>();
        final Set<IType> typesDone = new HashSet<IType>();
        final Set<IType> types = new HashSet<IType>();

        types.add(type);

        while (!types.isEmpty()) {
            Iterator<IType> iterator = types.iterator();
            final IType nextType = iterator.next();

            iterator.remove();
            typesDone.add(nextType);
            findSetterMethods(nextType, new IMethodInspector<IType, IMethod>() {
                    public void nextMethod(IType methodOwnerType, IMethod method,
                        Map<String, String> parameterSubstitution) throws Exception {
                        String parameterTypeSignature = method.getParameterTypes()[0];
                        String qualifiedParameterTypeSignature = signatureResolver.resolveTypeWithParameterMapping(
                                nextType, parameterTypeSignature, parameterSubstitution);
                        IType newType = null;

                        if (isCollection(nextType, qualifiedParameterTypeSignature)) {
                            String innerTypeSignature = getTypeParameterSignature(qualifiedParameterTypeSignature);

                            newType = signatureResolver.resolveType(nextType, innerTypeSignature);
                        } else {
                            newType = signatureResolver.resolveType(nextType, qualifiedParameterTypeSignature);
                        }

                        TypeMethods methods = result.get(nextType);

                        if (methods == null) {
                            methods = new TypeMethods(new ArrayList<IMethod>(), parameterSubstitution);
                            result.put(nextType, methods);
                        }

                        methods.methods.add(method);
                        if (Activator.debug) {
                            String parameterType = SignatureUtil.signatureToTypeName(method.getParameterTypes()[0]);
                            String newTypeName = newType != null ? newType.getFullyQualifiedName() : "<null>";
                            Activator.logDebug("found setter method: " + nextType.getFullyQualifiedName()
                                    + "." + method.getElementName() + "(" + parameterType + ")"
                                    + ", new type=" + newTypeName);
                        }

                        if (newType != null && !typesDone.contains(newType)) {
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
        IType type = signatureResolver.resolveType(owningType, typeSignature);

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
        IType type = signatureResolver.resolveType(owningType, typeSignature);

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

            return
                fieldTypeArgumentSignature.charAt(0) == Signature.C_EXTENDS
                || fieldTypeArgumentSignature.charAt(0) == Signature.C_SUPER
                || fieldTypeArgumentSignature.charAt(0) == Signature.C_CAPTURE
                ? fieldTypeArgumentSignature.substring(1)
                : fieldTypeArgumentSignature.equals(String.valueOf(Signature.C_STAR)) ? OBJECT_SIGNATURE
                                                                                      : fieldTypeArgumentSignature;
        }

        return OBJECT_SIGNATURE;
    }

    public Collection<IMethod> findSetterMethods(IType type) throws JavaModelException {
        Collection<IMethod> methods = findAllMethods(type);
        Collection<IMethod> setterMethods = filterSetterMethods(methods);

        return setterMethods;
    }

    private Collection<IMethod> filterSetterMethods(Collection<IMethod> methods) throws JavaModelException {
        List<IMethod> setterMethods = new ArrayList<IMethod>();

        for (IMethod method : methods) {
            if (isReachableSetterMethod(method)) {
                setterMethods.add(method);
            }
        }

        return setterMethods;
    }

    private boolean isReachableSetterMethod(IMethod method) throws JavaModelException {
        return isSetterMethod(method) && !Flags.isPrivate(method.getFlags());
    }

	private boolean isSetterMethod(IMethod method) throws JavaModelException {
		return method.getElementName().startsWith(SETTER_PREFIX) && method.getReturnType().equals(Signature.SIG_VOID)
            && method.getParameterTypes().length == 1;
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

    public void walkHierarchyTree(IType type, ITypeInspector<IType> inspector) throws Exception {
        Map<String, String> typeParameterMapping = new HashMap<String, String>();

        inspector.nextSuperType(Signature.createTypeSignature(type.getFullyQualifiedName(), true), type,
            typeParameterMapping);

        String superclassTypeSignature;
        IType superType = type;

        while ((superclassTypeSignature = superType.getSuperclassTypeSignature()) != null) {
            String resolvedTypeSignature = signatureResolver.resolveSignature(superType, superclassTypeSignature);

            superType = signatureResolver.resolveType(superType, Signature.getTypeErasure(resolvedTypeSignature));

            if (superType == null) {
                break;
            }

            typeParameterMapping = addParameterMappings(typeParameterMapping, superType, resolvedTypeSignature);
            inspector.nextSuperType(resolvedTypeSignature, superType, typeParameterMapping);
        }
    }

    public void findSetterMethods(IType type, final IMethodInspector<IType, IMethod> inspector) throws Exception {
        walkHierarchyTree(type, new ITypeInspector<IType>() {
                public void nextSuperType(String fullSignature, IType superType,
                    Map<String, String> typeParameterMapping) throws Exception {
                    try {
                        for (IMethod method : superType.getMethods()) {
                            if (isReachableSetterMethod(method)) {
                                inspector.nextMethod(superType, method, typeParameterMapping);
                            }
                        }
                    } catch (JavaModelException e) {
                    }
                }
            });
    }

    private Map<String, String> addParameterMappings(Map<String, String> typeParameterMapping, IType type,
        String resolvedTypeSignature) throws JavaModelException, SignatureParserException {
        if (typeParameterMapping == null) {
            typeParameterMapping = new HashMap<String, String>();
        }

        String[] typeArguments = Signature.getTypeArguments(resolvedTypeSignature);
        int i = 0;

        for (ITypeParameter typeParameter : type.getTypeParameters()) {
            final String resolvedSignature = signatureResolver.resolveSignature(type, typeArguments[i++]);

            //            if (resolvedSignature != null) {
            typeParameterMapping.put(typeParameter.getElementName(), resolvedSignature);
            //            }
        }

        return typeParameterMapping;
    }

    public static class TypeMethods implements ITypeMethods<IMethod> {
        final Collection<IMethod> methods;
        private final Map<String, String> parameterSubstitution;

        public TypeMethods(Collection<IMethod> methods, Map<String, String> parameterSubstitution) {
            this.methods = methods;
            this.parameterSubstitution = parameterSubstitution;
        }

        public Collection<IMethod> getMethods() {
            return methods;
        }

        public Map<String, String> getParameterSubstitution() {
            return parameterSubstitution;
        }
    }
}
