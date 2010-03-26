package com.sabre.buildergenerator.ui;

import java.util.List;
import java.util.Map;

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
    public Map<IType, List<IMethod>> findSetterMethodsForInhritedTypes(IType type) throws Exception {
    	return TypeHelper.findSetterMethodsForAllTypesReferenced(type);
    }

    public IType getSetterSetType(IMethod method) throws JavaModelException, SignatureParserException {
    	return SignatureResolver.resolveType(method.getDeclaringType(), method.getParameterTypes()[0]);
    }
    
    public IType resolveSignature(IType owningType, String signature) throws JavaModelException, SignatureParserException {
    	return SignatureResolver.resolveType(owningType, signature);
    }
}
