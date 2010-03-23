package com.sabre.buildergenerator.ui;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IMethod;

import junit.framework.TestCase;

public class MethodNodeTest extends TestCase {
	public void testShouldNotAllowSetingToTrueIfParentTypeNodeIsNotActive() {
		TypeNode typeNode = mock(TypeNode.class);
		when(typeNode.isActive()).thenReturn(false);
		IMethod method = mock(IMethod.class);
		try {
			MethodNode methodNode = new MethodNode(method, typeNode);
			methodNode.setSelected(true);
			assertTrue(false);
		} catch (IllegalStateException ex) {
			assertTrue(true);
		}
	}
}
