package com.sabre.buildergenerator.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;

public class MethodNodeTest extends TestCase {
	private TypeNode parentNode;
	private IMethod method;
	
	public void setUp() {
		parentNode = mock(TypeNode.class);
		method = mock(IMethod.class);
	}

	public void testShouldNotAllowSelectingIfParentTypeNodeIsNotActive() {
		when(parentNode.isActive()).thenReturn(false);

		try {
			TreeNode<IMethod> methodNode = new MethodNode(method, parentNode);
			methodNode.setSelected(true);
			assertTrue(false);
		} catch (IllegalStateException ex) {
			assertTrue(true);
		}
	}

	public void testShouldAllowSelectingIfParentNodeIsActive() {
		when(parentNode.isActive()).thenReturn(true);

		TreeNode<IMethod> methodNode = new MethodNode(method, parentNode);
		methodNode.setSelected(true);
		assertTrue(methodNode.isSelected());
	}
}
