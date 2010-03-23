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

	private HashSet<MethodNode> methodNodes;
	private HashSet<MethodNode> methodNodesPointingAtMe;
	private boolean selected = false;

	/**
	 * @param type
	 * @param definedSettingMethods
	 *            TODO
	 */
	public TypeNode(IType type, Set<IMethod> definedSettingMethods) {
		super(type);

		this.methodNodes = new HashSet<MethodNode>();
		for (IMethod settingMethod : definedSettingMethods) {
			methodNodes.add(new MethodNode(settingMethod, this));
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
			if (node.isSelected()) {
				active = true;
				break;
			}
		}
		
		return active;
	}

	public void addPointingMethodNode(MethodNode methodNode) {
		methodNodesPointingAtMe.add(methodNode);
	}

	public void setSelected(boolean b) {
		if (!isActive()) {
			throw new IllegalStateException("Can't be selected while being inactive");
		}
		
		this.selected = b;
		
		for (MethodNode methodNode  : methodNodes) {
			methodNode.setSelected(b);
		}
	}
	
	public boolean isSelected() {
		return selected;
	}

}
