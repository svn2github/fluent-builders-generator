package com.sabre.buildergenerator.ui.wizard;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.sabre.buildergenerator.ui.MethodNode;
import com.sabre.buildergenerator.ui.TypeNode;
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
		if (parentElement instanceof TypeNode) {
			return ((TypeNode) parentElement).getMethodNodes().toArray();
		}

		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof MethodNode) {
			return ((MethodNode) element).getParentNode();
		} else {
			return null;
		}

	}

	public boolean hasChildren(Object element) {
		return element instanceof TypeNode;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TypeNode) {
			return ((TypeNode) inputElement).getMethodNodes().toArray();
		} else {
			return settersTypeTree.getSortedTypesNodes();
		}
	}

}
