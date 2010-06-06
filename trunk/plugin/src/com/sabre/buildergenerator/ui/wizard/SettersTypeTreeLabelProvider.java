package com.sabre.buildergenerator.ui.wizard;

import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.sabre.buildergenerator.ui.TreeNode;

public class SettersTypeTreeLabelProvider extends JavaElementLabelProvider implements IStyledLabelProvider {

	private Object getTreeNodeElement(Object element) {
		return ((TreeNode<?>)element).getElement();
	}

	@Override
	public String getText(Object element) {
		return super.getText(getTreeNodeElement(element));
	}

	@Override
	public Image getImage(Object element) {
		return super.getImage(getTreeNodeElement(element));
	}

	@Override
	public StyledString getStyledText(Object element) {
		return super.getStyledText(getTreeNodeElement(element));
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return super.isLabelProperty(getTreeNodeElement(element), property);
	}
}
