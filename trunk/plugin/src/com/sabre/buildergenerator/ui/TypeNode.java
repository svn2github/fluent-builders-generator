
package com.sabre.buildergenerator.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class TypeNode extends TreeNode<IType> {

	private final HashSet<MethodNode> methodNodes;
	private final HashSet<MethodNode> methodNodesPointingAtMe;
	private boolean active = true;

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
		return active;
	}

	public void addPointingMethodNode(MethodNode methodNode) {
		methodNodesPointingAtMe.add(methodNode);
	}

	public Collection<MethodNode> getMethodsPointingAtMe() {
		return Collections.unmodifiableCollection(methodNodesPointingAtMe);
	}

	@Override
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

	public MethodNode getMethodNodeFor(IMethod method) {
		for (MethodNode methodNode : getMethodNodes()) {
			if (methodNode.getElement().equals(method)) {
				return methodNode;
			}
		}
		throw new IllegalArgumentException(
				"No such method node for that method");
	}

	@Override
    public String toString() {
		return getElement().getFullyQualifiedName().toString();
	}

	public void deactivate() {
		active = false;
		
	}

	public void populateStateChange() {
		active = true;
		
		for (MethodNode methodNode : getMethodNodes()) {
			if (methodNode.isSelected()) {
				TypeNode pointedTypeNode = methodNode.getPointedTypeNode();
				if (pointedTypeNode != null && !pointedTypeNode.isActive()) {
					pointedTypeNode.populateStateChange();
				}
			}
		}
	}
}
