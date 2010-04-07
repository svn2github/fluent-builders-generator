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


/**
 * Title: TreeNode.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TreeNode<ElementType> {

	private ElementType element;
	
	private TypeNode parentTypeNode;

	private boolean selected = true;
	
	/**
	 * @param element
	 * @param parentTypeNode TODO
	 */
	public TreeNode(ElementType element, TypeNode parentTypeNode) {
		this.element = element;
		this.parentTypeNode = parentTypeNode ;
	}

	/**
	 * @return the element
	 */
	public ElementType getElement() {
		return element;
	}
	
	public TypeNode getParentNode() {
		return parentTypeNode;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean b) {
		if (getParentNode() != null && !getParentNode().isActive()) {
			throw new IllegalStateException("Can't select setter of inactive type");
		}
		
		this.selected = b;
	}

	@Override
	public boolean equals(Object obj) {
		return getElement().equals(((TreeNode)obj).getElement());
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getElement().hashCode();
	}
	
}
