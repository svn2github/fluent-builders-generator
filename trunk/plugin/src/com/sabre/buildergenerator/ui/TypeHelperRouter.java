package com.sabre.buildergenerator.ui;

import java.util.Collection;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureResolver;
import com.sabre.buildergenerator.sourcegenerator.TypeHelper;


/**
 * Non static class that routes the methods into the static methods
 * of {@link TypeHelper}
 *
 * @author kubek2k
 *
 */
public class TypeHelperRouter {
    public Collection<IMethod> findSetterMethods(IType type) throws Exception {
        return TypeHelper.findSetterMethods(type);
    }

    public IType getSetterSetType(IMethod method) throws Exception {
    	String typeUnresolvedSignature = method.getParameterTypes()[0];
        String typeSignature = SignatureResolver.resolveSignature(method.getDeclaringType(), typeUnresolvedSignature);
    	if (TypeHelper.isCollection(method.getDeclaringType(), typeSignature)) {
    	    typeSignature = TypeHelper.getTypeParameterSignature(typeSignature);
    	}
        return SignatureResolver.resolveType(method.getDeclaringType(), typeSignature);
    }

    public IType resolveSignature(IType owningType, String signature) throws JavaModelException, SignatureParserException {
    	return SignatureResolver.resolveType(owningType, signature);
    }
}
