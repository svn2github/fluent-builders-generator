package com.sabre.buildergenerator.ui;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class TypeNodeTest extends TestCase {

    private IType type;

    public void setUp() {
        type = mock(IType.class);
    }

    public void testShouldBeActiveByDefault() {
        TypeNode node = new TypeNode(type, Collections.<IMethod> emptySet());
        assertTrue(node.isActive());
    }

    public void testShouldDeactivateIt() {
        TypeNode node = new TypeNode(type, Collections.<IMethod> emptySet());
        node.deactivate();
        assertFalse(node.isActive());
    }

    public void testShouldAttachAMethodNodeForEachIMethodPassed() {
        // given
        IMethod method1 = mock(IMethod.class);
        IMethod method2 = mock(IMethod.class);

        // when
        TypeNode node = new TypeNode(type, asList(method1, method2));

        // then
        assertEquals(2, node.getMethodNodes().size());
        assertNotNull(node.getMethodNodeFor(method1));
        assertNotNull(node.getMethodNodeFor(method2));
    }

    public void testShouldPopulateStateChangeToSelectedMethodNodesInActiveAncestors() {
        // given
        IMethod method1 = mock(IMethod.class);
        TypeNode pointedTypeNode = mock(TypeNode.class);
        when(pointedTypeNode.isActive()).thenReturn(false);
        TypeNode node = new TypeNode(type, asList(method1));
        node.getMethodNodeFor(method1).setPointedTypeNode(pointedTypeNode);

        // when
        node.populateStateChange();

        // then
        verify(pointedTypeNode).populateStateChange();
    }

    public void testShouldNotPopulateStateChangeToSelectedMethodNodesActiveAncestors() {
        // given
        IMethod method1 = mock(IMethod.class);
        TypeNode pointedTypeNode = mock(TypeNode.class);
        when(pointedTypeNode.isActive()).thenReturn(true);
        TypeNode node = new TypeNode(type, asList(method1));
        node.getMethodNodeFor(method1).setPointedTypeNode(pointedTypeNode);

        // when
        node.populateStateChange();

        // then
        verify(pointedTypeNode).isActive();
        verifyNoMoreInteractions(pointedTypeNode);
    }

    public void testShouldNotPopulateStateChangeToNotSelectedMethodNodesAncestors() {
        // given
        IMethod method1 = mock(IMethod.class);
        TypeNode pointedTypeNode = mock(TypeNode.class);
        when(pointedTypeNode.isActive()).thenReturn(true);
        TypeNode node = new TypeNode(type, asList(method1));
        node.getMethodNodeFor(method1).setPointedTypeNode(pointedTypeNode);
        node.getMethodNodeFor(method1).setSelected(false);

        // when
        node.populateStateChange();

        // then
        verifyNoMoreInteractions(pointedTypeNode);
    }
}
