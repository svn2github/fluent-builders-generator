package com.sabre.buildergenerator.javamodel.eclipse;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.javamodel.ITypeResolver;
import com.sabre.buildergenerator.typeutils.TypeUtil;

public class TypeResolver implements ITypeResolver<IType, JavaModelException> {
    public String resolveType(IType owningType, String type) throws JavaModelException {
        type = TypeUtil.normalizeType(type);
        return doResolveType(owningType, type);
    }

    public static String doResolveType(final IType owningType, String type) throws JavaModelException {
        int end = type.indexOf('<');
        if (end == -1) {
            end = type.length();
        }
        String className = type.substring(0, end);
        String qualifiedClassName;
        qualifiedClassName = SignatureResolver.resolveTypeName(owningType, className);
        type = qualifiedClassName + type.substring(end);
        return TypeUtil.processTypeParams(type, new TypeUtil.TypeProcessor() {
            public String processType(String typeParam) {
                try {
                    return doResolveType(owningType, typeParam);
                } catch (JavaModelException e) {
                    // TODO: use ExceptionWrapper
                    return typeParam;
                }
            }
        });
    }
}
