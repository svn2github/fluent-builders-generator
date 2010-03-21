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

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.signatureutils.SignatureResolver;

/**
 * Title: TypeTree.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TypeTree {

	private Map<IType, TypeNode> typeNodes;
	
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
	public TypeTree(IType aType, TypeHelperRouter typeHelperRouter) throws Exception {
		typeNodes = new HashMap<IType, TypeNode>();
		Set<IMethod> flattenSetters = flattenSettersMap(typeHelperRouter.findSetterMethodsForInhritedTypes(aType));
		processSetters(aType, flattenSetters, typeHelperRouter);
	}

	private void processSetters(IType aType, Set<IMethod> setters,
			TypeHelperRouter typeHelperRouter) throws JavaModelException,
			SignatureParserException, Exception {
		typeNodes.put(aType, new TypeNode(aType, setters));
		for (IMethod setter : setters) {
			IType setType = typeHelperRouter.getSetterSetType(setter);
			if (setType.isClass() && !setType.isBinary() && isNotCollection(setType)) {
				processSetters(setType, flattenSettersMap(typeHelperRouter.findSetterMethodsForInhritedTypes(setType)), typeHelperRouter);
			}
		}
	}

	private boolean isNotCollection(IType setType) {
		return true;
	}

	/**
	 * @param aBaseType
	 * @return
	 */
	public TypeNode getNodeFor(IType aBaseType) {
		return typeNodes.get(aBaseType);
	}


}
 