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

	private boolean selected;
	private TypeNode parentTypeNode;

	/**
	 * @param aElement
	 * @param parentTypeNode TODO
	 */
	public MethodNode(IMethod aElement, TypeNode parentTypeNode) {
		super(aElement);
		this.parentTypeNode = parentTypeNode;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean b) {
		if (!parentTypeNode.isActive()) {
			throw new IllegalStateException("Can't select setter of inactive type");
		}
		
		this.selected = b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((parentTypeNode == null) ? 0 : parentTypeNode.hashCode());
		result = prime * result + (selected ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodNode other = (MethodNode) obj;
		if (parentTypeNode == null) {
			if (other.parentTypeNode != null)
				return false;
		} else if (!parentTypeNode.equals(other.parentTypeNode))
			return false;
		if (selected != other.selected)
			return false;
		return true;
	}

	
	
}
