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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

/**
 * Title: TypeNode.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * 
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TypeNode extends TreeNode<IType> {

	private final HashSet<MethodNode> methodNodes;
	private final HashSet<MethodNode> methodNodesPointingAtMe;

	/**
	 * @param type
	 *            type represented by this node
	 * @param definedSettingMethods
	 *            collection of setting methods on type
	 */
	public TypeNode(IType type, Collection<IMethod> definedSettingMethods) {
		super(type, null);

		this.methodNodes = new HashSet<MethodNode>();
		if (definedSettingMethods != null) {
			for (IMethod settingMethod : definedSettingMethods) {
				methodNodes.add(new MethodNode(settingMethod, this));
			}
		}

		methodNodesPointingAtMe = new HashSet<MethodNode>();
	}

	/**
	 * @return
	 */
	public Set<MethodNode> getMethodNodes() {
		return methodNodes;
	}

	public boolean isActive() {
		boolean active = false;

		for (MethodNode node : methodNodesPointingAtMe) {
			if (node.isSelected() && node.getParentNode().isActive()) {
				active = true;
				break;
			}
		}

		return active;
	}

	public void addPointingMethodNode(MethodNode methodNode) {
		methodNodesPointingAtMe.add(methodNode);
	}

	public Collection<MethodNode> getMethodsPointingAtMe() {
		return Collections.unmodifiableCollection(methodNodesPointingAtMe);
	}

	public void setSelected(boolean b) {
		if (!isActive()) {
			throw new IllegalStateException(
					"Can't be selected while being inactive");
		}

		super.setSelected(b);

		for (TreeNode<IMethod> methodNode : methodNodes) {
			methodNode.setSelected(b);
		}
	}

	public TreeNode<IMethod> getMethodNodeFor(IMethod method) {
		for (TreeNode<IMethod> methodNode : getMethodNodes()) {
			if (methodNode.getElement().equals(method)) {
				return methodNode;
			}
		}
		throw new IllegalArgumentException(
				"No such method node for that method");
	}

}
