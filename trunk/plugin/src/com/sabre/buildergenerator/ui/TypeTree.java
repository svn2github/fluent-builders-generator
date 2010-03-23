/*

 * Copyright (c) 2009 by Sabre Holdings Corp.
 * 3150 Sabre Drive, Southlake, TX 76092 USA
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sabre Holdings Corporation ("Confidential Information").
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Sabre Holdings Corporation.
 */

package com.sabre.buildergenerator.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.sabre.buildergenerator.signatureutils.SignatureParserException;

/**
 * Title: TypeTree.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * 
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TypeTree {

	public static final String COLLECTIONS_REGEX = "^(java\\.util\\.Collection|" +
					"java\\.util\\.Set|java\\.util\\.HashSet|" +
					"java\\.util\\.List|java\\.util\\.ArrayList|" +
					"java\\.util\\.LinkedList|java\\.util\\.TreeSet)\\<.*$"; 
	private Map<IType, TypeNode> typeNodes;
	private TypeHelperRouter typeHelperRouter;

	private Set<IMethod> flattenSettersMap(Map<IType, List<IMethod>> setters) {
		HashSet<IMethod> methods = new HashSet<IMethod>();

		for (IType key : setters.keySet()) {
			methods.addAll(setters.get(key));
		}

		return methods;

	}

	/**
	 * @param aType
	 * @param typeHelperRouter
	 * @throws Exception
	 */
	public TypeTree(IType aType, TypeHelperRouter typeHelperRouter)
			throws Exception {
		this.typeNodes = new HashMap<IType, TypeNode>();
		this.typeHelperRouter = typeHelperRouter;
		processType(aType, flattenSettersMap(typeHelperRouter
				.findSetterMethodsForInhritedTypes(aType)));
	}

	private void processType(IType aType, Set<IMethod> setters)
			throws JavaModelException, SignatureParserException, Exception {
		typeNodes.put(aType, new TypeNode(aType, setters));
		for (IMethod setter : setters) {
			IType setType = typeHelperRouter.getSetterSetType(setter);
			if (setType.isClass() && !setType.isBinary()) {
				if (!isCollection(setType)) {
					processType(setType, flattenSettersMap(typeHelperRouter
							.findSetterMethodsForInhritedTypes(setType)));
				} else {
					processCollection(setType);
				}
			}
		}
	}

	private void processCollection(IType setType) throws Exception {
		String typeFullyQualifiedName = setType.getFullyQualifiedName();
		String typeSignature = Signature.createTypeSignature(
				typeFullyQualifiedName, true);
		String innerTypeSignature = getInnerTypeSignature(typeSignature);
		if (innerTypeSignature != null) {
			IType innerType = typeHelperRouter.resolveSignature(setType, innerTypeSignature);
			processType(innerType, flattenSettersMap(typeHelperRouter.findSetterMethodsForInhritedTypes(innerType)));
		}
	}

	static String getInnerTypeSignature(String collectionSignature) {
		Pattern p = Pattern.compile("L[\\w\\.]+\\<(L[\\w\\.]+;)\\>.*;");
		Matcher matcher = p.matcher(collectionSignature);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}

	private boolean isCollection(IType setType) {
		String fullyQName = setType.getFullyQualifiedName();
		
		return fullyQName.matches(COLLECTIONS_REGEX);
	}

	/**
	 * @param aBaseType
	 * @return
	 */
	public TypeNode getNodeFor(IType aBaseType) {
		return typeNodes.get(aBaseType);
	}

}
