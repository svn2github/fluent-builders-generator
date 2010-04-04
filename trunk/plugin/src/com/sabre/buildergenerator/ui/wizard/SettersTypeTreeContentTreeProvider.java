package com.sabre.buildergenerator.ui.wizard;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sabre.buildergenerator.ui.MethodNode;
import com.sabre.buildergenerator.ui.TreeNode;
import com.sabre.buildergenerator.ui.TypeTree;

public class SettersTypeTreeContentTreeProvider implements ITreeContentProvider {

	private TypeTree settersTypeTree;

	public SettersTypeTreeContentTreeProvider(TypeTree settersTypeTree) {
		this.settersTypeTree = settersTypeTree;
	}

	public void dispose() {
		settersTypeTree = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// we don't care
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IType) {
			return getIMethodsFromTypeNode((IType) parentElement);
		}

		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof IMethod) {
			return ((IMethod)element).getParent();
		} else {
			return null;
		}
		
	}

	public boolean hasChildren(Object element) {
		return element instanceof IType;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IType) {
			return getIMethodsFromTypeNode((IType) inputElement);
		} else {
			return settersTypeTree.getSortedActiveTypes();
		}
	}

	private Object[] getIMethodsFromTypeNode(IType typeNode) {
		Set<MethodNode> methodNodes = settersTypeTree.getNodeFor(typeNode)
				.getMethodNodes();
		ArrayList<IMethod> iMethods = new ArrayList<IMethod>(methodNodes.size());
		for (TreeNode<IMethod> methodNode : methodNodes) {
			iMethods.add(methodNode.getElement());
		}
		return iMethods.toArray();
	}

}
