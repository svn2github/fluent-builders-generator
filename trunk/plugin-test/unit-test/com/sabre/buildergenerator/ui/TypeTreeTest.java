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

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.isA;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import com.sabre.buildergenerator.javamodel.eclipse.ModelHelper.TypeMethods;
import com.sabre.buildergenerator.ui.TypeHelperRouter.SetType;

/**
 * Title: TreeTest.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 *
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TypeTreeTest extends TestCase {
	private IType baseType;
	private IMethod method;
	private TypeHelperRouter typeHelperRouter;
	private Map<IType, TypeMethods> baseTypeMethods;

	private Map<IType, TypeMethods> createBaseTypeMethodsMap() {
	    HashMap<IType, TypeMethods> map = new HashMap<IType, TypeMethods>();
	    map.put(baseType, new TypeMethods(asList(method), Collections.<String, String> emptyMap()));
		return map;
	}

	@Override
    public void setUp() throws Exception {
		baseType = mock(IType.class);
		method = mock(IMethod.class);
		typeHelperRouter = mock(TypeHelperRouter.class);

		baseTypeMethods = createBaseTypeMethodsMap();
		when(typeHelperRouter.findSetterMethods(baseType))
				.thenReturn(baseTypeMethods);
	}

	public void testRootNodeShouldBeAlwaysMarkedAsActive() throws Exception {
		when(typeHelperRouter.findSetterMethods(baseType))
				.thenReturn(Collections.<IType, TypeMethods> emptyMap());
		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		assertTrue(typeTree.getNodeFor(baseType).isActive());
	}

	public void testShouldExposeBaseTypeAsRootNode() throws Exception {
		when(typeHelperRouter.findSetterMethods(baseType))
				.thenReturn(Collections.<IType, TypeMethods> emptyMap());

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);
		assertNotNull(typeTree.getNodeFor(baseType));
	}

	public void testShouldExposeComplexSubtypeAsOneOfTheRootNodesOfTheTreeAndSetterForBaseType()
			throws Exception {
		IType complexType = mock(IType.class);

		when(typeHelperRouter.resolveSetterSetType(isA(IType.class), method, Collections.<String, String> emptyMap())).thenReturn(new SetType(complexType));
		when(complexType.isClass()).thenReturn(true);
		when(complexType.getFullyQualifiedName()).thenReturn("A");

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		assertNotNull(typeTree.getNodeFor(complexType));
		TypeNode baseTypeNode = typeTree.getNodeFor(baseType);
		assertTrue(baseTypeNode.getMethodNodes().contains(
				new MethodNode(method, baseTypeNode)));
	}

	public void testShouldExposeSimpleTypesSettersOfTheTypeAsMethodNode()
			throws Exception {
		when(typeHelperRouter.resolveSetterSetType(isA(IType.class), method, Collections.<String, String> emptyMap())).thenReturn(new SetType());

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		TypeNode baseTypeNode = typeTree.getNodeFor(baseType);
		assertTrue(baseTypeNode.getMethodNodes().contains(
				new MethodNode(method, baseTypeNode)));
	}

	public void testShouldExposeBinaryTypesSettersOfTheTypeAsMethodNode()
			throws Exception {
		IType binaryType = mock(IType.class);
		when(binaryType.isClass()).thenReturn(true);
		when(binaryType.isBinary()).thenReturn(true);
		when(typeHelperRouter.resolveSetterSetType(isA(IType.class), method, Collections.<String, String> emptyMap())).thenReturn(new SetType(binaryType));

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		typeTree.getNodeFor(baseType).getMethodNodes().contains(
				new MethodNode(method, null));
		assertNull(typeTree.getNodeFor(binaryType));
	}

	public void testShouldExposeCollectionSetterOfTheTypeAsMethodNodeAndRootNodeForTheComplexCollectionSubType()
			throws Exception {
		IType collectionType = mock(IType.class);
		when(collectionType.isClass()).thenReturn(true);
		when(collectionType.isBinary()).thenReturn(true);
		String collectionFullyQName = "java.util.List<Abcd>";
		when(collectionType.getFullyQualifiedName()).thenReturn(
				collectionFullyQName);

		when(typeHelperRouter.resolveSetterSetType(isA(IType.class), method, Collections.<String, String> emptyMap())).thenReturn(
				new SetType(collectionType));

		IType collectionSubType = mock(IType.class);
		when(collectionType.isClass()).thenReturn(true);
		when(collectionType.isBinary()).thenReturn(false);

		when(typeHelperRouter.resolveSignature(collectionType, "LAbcd;"))
				.thenReturn(collectionSubType);
		when(typeHelperRouter.findSetterMethods(collectionSubType))
				.thenReturn(Collections.<IType, TypeMethods> emptyMap());

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		assertNotNull(typeTree.getNodeFor(collectionSubType));
		// creating signature:
		// String fullQName = type.getFullyQualifiedName();
		// String sig = Signature.createSignature(fullQName, true);
	}

	public void testAfterTreeInitializationAllPointingMethodNodesShouldBeAttachedToTargetTypeNodes()
			throws Exception {
		IType complexPointedType = mock(IType.class);

		when(typeHelperRouter.resolveSetterSetType(isA(IType.class), method, Collections.<String, String> emptyMap())).thenReturn(new SetType(complexPointedType));

		when(complexPointedType.isBinary()).thenReturn(false);
		when(complexPointedType.isClass()).thenReturn(true);
		when(complexPointedType.getFullyQualifiedName()).thenReturn("");

		when(typeHelperRouter.findSetterMethods(complexPointedType))
				.thenReturn(Collections.<IType, TypeMethods> emptyMap());

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		assertTrue(typeTree
				.getNodeFor(complexPointedType)
				.getMethodsPointingAtMe()
				.contains(new MethodNode(method, typeTree.getNodeFor(baseType))));
	}

}
