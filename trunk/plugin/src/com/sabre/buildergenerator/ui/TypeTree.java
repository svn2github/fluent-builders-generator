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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import com.sabre.buildergenerator.javamodelhelper.ModelHelper.TypeMethods;
import com.sabre.buildergenerator.signatureutils.SignatureParserException;
import com.sabre.buildergenerator.ui.TypeHelperRouter.SetType;

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

	private final Map<IType, TypeNode> typeNodes;
	private final TypeHelperRouter typeHelperRouter;
	private final Map<IType, TypeMethods> setterMethods;
	private final Set<IType> typesAlreadyProcessed;

	/**
	 * @param aType
	 * @param typeHelperRouter
	 * @throws Exception
	 */
	public TypeTree(IType aType, TypeHelperRouter typeHelperRouter)
			throws Exception {
		this.typeNodes = new LinkedHashMap<IType, TypeNode>();
		this.typeHelperRouter = typeHelperRouter;
		this.setterMethods = typeHelperRouter.findSetterMethods(aType);
		this.typesAlreadyProcessed = new HashSet<IType>();

		TypeMethods typeMethods = setterMethods.get(aType);
		if (typeMethods != null) {
            processType(new RootTypeNode(aType, typeMethods.getMethods()),
                    typeMethods.getParameterSubstitution());

    		for (TypeNode typeNode : typeNodes.values()) {
    			for (MethodNode methodNode : typeNode.getMethodNodes()) {
    				SetType setType = typeHelperRouter.resolveSetterSetType(
    				        typeNode.getElement(), methodNode.getElement(), typeMethods.getParameterSubstitution());
    				if (!setType.isSimpleType()) {
    					TypeNode setTypeNode = getNodeFor(setType.getType());
    					if (setTypeNode != null) {
    						setTypeNode.addPointingMethodNode(methodNode);
    						methodNode.setPointedTypeNode(setTypeNode);
    					}
    				}
    			}
    		}
		}
	}

	private void processType(TypeNode typeNode,
			Map<String, String> parameterSubstitution)
			throws JavaModelException, SignatureParserException, Exception {
		typeNodes.put(typeNode.getElement(), typeNode);
		for (TreeNode<IMethod> setterNode : typeNode.getMethodNodes()) {
			SetType setType = typeHelperRouter.resolveSetterSetType(typeNode.getElement(), setterNode
                    		.getElement(), parameterSubstitution);
			if (!setType.isSimpleType()) {
				IType setIType = setType.getType();
				TypeMethods newTypeMethods = setterMethods.get(setIType);
				if (!typesAlreadyProcessed.contains(setIType) && newTypeMethods != null
				        && setType.getType().isClass() && !setIType.isBinary()) {
					typesAlreadyProcessed.add(setIType);

                    Map<String, String> newParameterSubstitution = newTypeMethods.getParameterSubstitution();
					processType(createTypeNode(setIType), newParameterSubstitution);
				}
			}
		}
	}

	private TypeNode createTypeNode(IType setType) throws Exception {
		return new TypeNode(setType, setterMethods.get(setType).getMethods());
	}

	/**
	 * @param aBaseType
	 * @return
	 */
	public TypeNode getNodeFor(IType aBaseType) {
		return typeNodes.get(aBaseType);
	}

	public TypeNode[] getSortedTypesNodes() {
		return typeNodes.values().toArray(new TypeNode[typeNodes.size()]);
	}

	public void populateStateChange() {
		for (TypeNode typeNode : typeNodes.values()) {
			typeNode.deactivate();
		}

		TypeNode rootNode = typeNodes.values().iterator().next();
		rootNode.populateStateChange();
	}

}
