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
	
	/**
	 * @param element
	 */
	public TreeNode(ElementType element) {
		this.element = element;
	}

	/**
	 * @return the element
	 */
	protected ElementType getElement() {
		return element;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeNode other = (TreeNode) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		return true;
	}
	
}
