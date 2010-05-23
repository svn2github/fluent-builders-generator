package com.sabre.buildergenerator.typeutils;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.signatureutils.SignatureResolver;

public class TypeResolver {
    // TODO: nonstatic methods
    public static String resolveType(IType owningType, String type) throws JavaModelException {
        type = normalizeType(type);
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
        return processTypeParams(type, new TypeProcessor() {
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

    public static String normalizeType(String qualifiedTypeName) {
        return qualifiedTypeName
            .replaceAll(" +extends +", "=extends=")
            .replaceAll(" +super +", "=super=")
            .replaceAll(" +capture +of +", "=capture=of=")
            .replaceAll(" +& +", "=&=")
            .replaceAll(" ", "")
            .replaceAll("=", " ");
    }

    /**
     * Calls callback on all type parameters. Replaces parameters with processed strings.
     *
     * for type "package.MyType<java.lang.String,? extends package.MyBase>.MyInner<package.MyGeneric<String>>"
     * parameters are:
     * - "java.lang.String"
     * - "? extends package.MyBase"
     * - "package.MyGeneric<String>"
     *
     * @param qualifiedTypeName type to extract parameters
     * @param proc callback
     * @return processed type description
     */
    public static String processTypeParams(String qualifiedTypeName, TypeProcessor proc) {
        StringBuilder buf = new StringBuilder();
        int depth = 0;
        int beg = 0;
        int i = 0;
        for (char c : qualifiedTypeName.toCharArray()) {
            if (c == '<') {
                depth++;
                if (depth == 1) {
                    buf.append(qualifiedTypeName.substring(beg, i));
                    buf.append('<');
                    beg = i + 1;
                }
            } else if (c == '>') {
                if (depth == 1) {
                    buf.append(proc.processType(qualifiedTypeName.substring(beg, i)));
                    buf.append('>');
                    beg = i + 1;
                }
                depth--;
            } else if (c == ',' && depth == 1) {
                buf.append(proc.processType(qualifiedTypeName.substring(beg, i)));
                buf.append(", ");
                beg = i + 1;
            }
            i++;
        }
        if (beg < qualifiedTypeName.length()) {
            buf.append(qualifiedTypeName.substring(beg));
        }
        return buf.toString();
    }

    public static interface TypeProcessor {
        String processType(String type);
    }
}
