package com.sabre.buildergenerator.ui;

import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.javamodelhelper.ModelHelper;
import com.sabre.buildergenerator.javamodelhelper.ModelHelper.TypeMethods;
import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureResolver;

/**
 * Non static class that routes the methods into the static methods of
 * {@link ModelHelper}
 *
 * @author kubek2k
 *
 */
public class TypeHelperRouter {
    private final ModelHelper modelHelper = new ModelHelper();
    private final SignatureResolver signatureResolver = new SignatureResolver();

	public boolean isArray(String signature) {
		return signature.charAt(0) == Signature.C_ARRAY;
	}

	public boolean isSimpleTypeSignature(String signature) {
		char firstChar = signature.charAt(0);
		switch (firstChar) {
		case Signature.C_BOOLEAN:
		case Signature.C_BYTE:
		case Signature.C_CHAR:
		case Signature.C_DOUBLE:
		case Signature.C_FLOAT:
		case Signature.C_INT:
		case Signature.C_LONG:
		case Signature.C_SHORT:
			return true;
		}

		return false;
	}

	public Map<IType, TypeMethods> findSetterMethods(IType type) throws Exception {
		return modelHelper.findSetterMethodsForAllTypesReferenced(type);
	}

	// public String resolveSetTypeSignature(IMethod method)
	// throws JavaModelException, SignatureParserException {
	// String unresolvedSetTypeSig = method.getParameterTypes()[0];
	// return SignatureResolver.resolveSignature(method.getDeclaringType(),
	// unresolvedSetTypeSig);
	// }
	//
	// public IType resolveTypeSignatureToIType(String signature, IType
	// owningType)
	// throws JavaModelException, SignatureParserException {
	// return SignatureResolver.resolveType(owningType, signature);
	// }

	public SetType resolveSetterSetType(IType owningType, IMethod method, Map<String, String> parameterSubstitution) throws Exception {
		String typeUnresolvedSignature = method.getParameterTypes()[0];
		String typeSignature = signatureResolver.resolveTypeWithParameterMapping(owningType, typeUnresolvedSignature, parameterSubstitution);

		if (isSimpleTypeSignature(typeSignature)) {
			return new SetType();
		} else {
			if (modelHelper.isCollection(owningType, typeSignature)) {
				typeSignature = modelHelper.getTypeParameterSignature(typeSignature);
			}

			return new SetType(signatureResolver.resolveType(owningType, typeSignature));
		}
	}

	public IType resolveSignature(IType owningType, String signature)
			throws JavaModelException, SignatureParserException {
		return signatureResolver.resolveType(owningType, signature);
	}

	public static class SetType {

		private IType complexType;

		/**
		 * Constructor for simple type
		 */
		SetType() {
		}

		/**
		 * Constructor for complex type
		 *
		 * @param type
		 */
		SetType(IType type) {
			this.complexType = type;
		}

		public boolean isSimpleType() {
			return complexType == null;
		}

		public IType getType() {
			return complexType;
		}
	}

}
