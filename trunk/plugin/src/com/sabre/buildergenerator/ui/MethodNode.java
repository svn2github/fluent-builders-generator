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

import org.eclipse.jdt.core.IMethod;

/**
 * Title: MethodNode.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class MethodNode extends TreeNode<IMethod> {

	/**
	 * @param aElement
	 *  a eclipse jdt model element	
	 * @param parentTypeNode 
	 *  owning type node 
	 */
	public MethodNode(IMethod aElement, TypeNode parentTypeNode) {
		super(aElement, parentTypeNode);
	}

	@Override
	public String toString() {
		return getParentNode().toString() + "#" + getElement().getElementName();
	}
}
