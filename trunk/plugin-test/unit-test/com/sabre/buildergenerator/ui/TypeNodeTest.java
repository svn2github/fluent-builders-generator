package com.sabre.buildergenerator.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class TypeNodeTest extends TestCase {

	private IType type;

	public void setUp() {
		type = mock(IType.class);
	}

	public void testShouldBeInactiveIfNoMethodNodeIsPointingAtIt() {
		TypeNode typeNode = new TypeNode(type, Collections.<IMethod> emptySet());

		assertFalse(typeNode.isActive());
	}

	public void testShouldBeActiveIfThereIsAtLeastOneSelectedMethodNodePointingAtIt() {
		TypeNode typeNode = new TypeNode(type, Collections.<IMethod> emptySet());

		TypeNode methodsParentTypeNode = mock(TypeNode.class);
		when(methodsParentTypeNode.isActive()).thenReturn(true);

		MethodNode methodNode = mock(MethodNode.class);
		when(methodNode.isSelected()).thenReturn(false);
		when(methodNode.getParentNode()).thenReturn(methodsParentTypeNode);
		typeNode.addPointingMethodNode(methodNode);

		methodNode = mock(MethodNode.class);
		when(methodNode.isSelected()).thenReturn(true);
		when(methodNode.getParentNode()).thenReturn(methodsParentTypeNode
				);
		typeNode.addPointingMethodNode(methodNode);

		assertTrue(typeNode.isActive());
	}

	public void testShouldBeInactiveIfThereAreNoSelectedMethodNodesPointingAtIt() {
		TypeNode typeNode = new TypeNode(type, Collections.<IMethod> emptySet());
		MethodNode methodNode = mock(MethodNode.class);
		when(methodNode.isSelected()).thenReturn(false);

		typeNode.addPointingMethodNode(methodNode);

		assertFalse(typeNode.isActive());
	}

	public void testShouldNotAllowSelectingWhileBeingInactive() {
		TypeNode typeNode = new TypeNode(type, Collections.<IMethod> emptySet());
		try {
			typeNode.setSelected(true);
			assertTrue(false);
		} catch (IllegalStateException ex) {
			assertTrue(true);
		}
	}

	public void testSelectingTypeNodeShouldResultInSelectingAllMethodNodesWithin() {
		IMethod method = mock(IMethod.class);
		TypeNode typeNode = new TypeNode(type, createSet(method));

		TypeNode methodParentTypeNode = mock(TypeNode.class);
		when(methodParentTypeNode.isActive()).thenReturn(true);
		// ensure type node is active
		MethodNode pointingMethodNode = mock(MethodNode.class);
		when(pointingMethodNode.isSelected()).thenReturn(true);
		when(pointingMethodNode.getParentNode()).thenReturn(methodParentTypeNode);
		typeNode.addPointingMethodNode(pointingMethodNode);

		for (TreeNode<IMethod> node : typeNode.getMethodNodes()) {
			node.setSelected(false);
		}

		typeNode.setSelected(true);

		for (TreeNode<IMethod> node : typeNode.getMethodNodes()) {
			assertTrue(node.isSelected());
		}
	}

	public void testUnselectingTypeNodeShouldResultInUnselectingAllMethodNodesWithin() {
		IMethod method = mock(IMethod.class);
		TypeNode typeNode = new TypeNode(type, createSet(method));

		// ensure type node is active
		TypeNode methodParentTypeNode = mock(TypeNode.class);
		when(methodParentTypeNode.isActive()).thenReturn(true);
		
		MethodNode pointingMethodNode = mock(MethodNode.class);
		when(pointingMethodNode.isSelected()).thenReturn(true);
		when(pointingMethodNode.getParentNode()).thenReturn(methodParentTypeNode);
		typeNode.addPointingMethodNode(pointingMethodNode);

		for (TreeNode<IMethod> node : typeNode.getMethodNodes()) {
			node.setSelected(true);
		}

		typeNode.setSelected(false);

		for (TreeNode<IMethod> node : typeNode.getMethodNodes()) {
			assertFalse(node.isSelected());
		}
	}

	public void testShouldReturnMethodNodeForIMethodPassed() {
		IMethod method = mock(IMethod.class);
		TypeNode typeNode = new TypeNode(type, createSet(method));

		TreeNode<IMethod> methodNode = typeNode.getMethodNodeFor(method);

		assertEquals(methodNode.getElement(), method);
	}

	public void testShouldFailWhenGettingMethodNodeForWrongMethod() {
		IMethod method = mock(IMethod.class);
		TypeNode typeNode = new TypeNode(type, Collections.<IMethod> emptySet());
		try {
			typeNode.getMethodNodeFor(method);
			fail();
		} catch (IllegalArgumentException ex) {
			assertTrue(true);

		}
	}

	private Set<IMethod> createSet(IMethod method) {
		Set<IMethod> set = new HashSet<IMethod>();
		set.add(method);
		return set;
	}
}
