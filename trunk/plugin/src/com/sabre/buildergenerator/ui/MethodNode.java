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

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Title: MethodNode.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * 
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class MethodNode extends TreeNode<IMethod> {

	private TypeNode pointedTypeNode;

	/**
	 * @param aElement
	 *            a eclipse jdt model element
	 * @param parentTypeNode
	 *            owning type node
	 */
	public MethodNode(IMethod aElement, TypeNode parentTypeNode) {
		super(aElement, parentTypeNode);
	}

	public TypeNode getPointedTypeNode() {
		return pointedTypeNode;
	}

	/**
	 * @param aPackage
	 *            a package string representation
	 * @return is method accessible from the specified package
	 */
	public boolean isAccessibleFromPackage(String aPackage) {
		if (!isPublic()) {
			if (!isPrivate()) {
				IPackageFragment owningClassPackage = getElement()
						.getDeclaringType().getPackageFragment();
				return owningClassPackage.getElementName().equals(aPackage);
			} else {
				return false;
			}
		} else {
			return true;
		}

	}

	private boolean isPrivate() {
		try {
			return Flags.isPrivate(getElement().getFlags());
		} catch (JavaModelException exception) {
			throw new RuntimeException(exception);
		}
	}

	private boolean isPublic() {
		try {
			return Flags.isPublic(getElement().getFlags());
		} catch (JavaModelException exception) {
			throw new RuntimeException(exception);
		}
	}

	void setPointedTypeNode(TypeNode pointedTypeNode) {
		this.pointedTypeNode = pointedTypeNode;
	}

	@Override
	public String toString() {
		return getParentNode().toString() + "#" + getElement().getElementName();
	}
}
