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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

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
	private Map<IType, List<IMethod>> baseTypeMethods;

	private Map<IType, List<IMethod>> createBaseTypeMethodsMap() {
		Map<IType, List<IMethod>> baseTypeMethods = new HashMap<IType, List<IMethod>>();
		baseTypeMethods.put(baseType, asList(method));
		return baseTypeMethods;
	}

	public void setUp() throws Exception {
		baseType = mock(IType.class);
		method = mock(IMethod.class);
		typeHelperRouter = mock(TypeHelperRouter.class);
		
		baseTypeMethods = createBaseTypeMethodsMap();
		when(typeHelperRouter.findSetterMethodsForInhritedTypes(baseType))
			.thenReturn(baseTypeMethods);
	}

	public void testShouldExposeBaseTypeAsRootNode() throws Exception {
		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);
		
		assertNotNull(typeTree.getNodeFor(baseType));
	}

	public void testShouldExposeComplexSubtypeAsOneOfTheRootNodesOfTheTreeAndSetterForBaseType()
			throws Exception {

		IType complexType = mock(IType.class);
		when(typeHelperRouter.getSetterSetType(method)).thenReturn(complexType);
		when(complexType.isClass()).thenReturn(true);

		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);
		
		assertNotNull(typeTree.getNodeFor(complexType));
		assertTrue(typeTree.getNodeFor(baseType).getMethodNodes().contains(new MethodNode(method)));
	}

	
	public void testShouldExposeSimpleTypesSettersOfTheTypeAsMethodNode() throws Exception {
		
		IType simpleType = mock(IType.class);
		when(simpleType.isClass()).thenReturn(false);
		when(typeHelperRouter.getSetterSetType(method)).thenReturn(simpleType);
		
		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		assertTrue(typeTree.getNodeFor(baseType).getMethodNodes().contains(new MethodNode(method)));
		assertNull(typeTree.getNodeFor(simpleType));
	}

	public void testShouldExposeBinaryTypesSettersOfTheTypeAsMethodNode() throws Exception {
		IType binaryType = mock(IType.class);
		when(binaryType.isClass()).thenReturn(true);
		when(binaryType.isBinary()).thenReturn(true);
		when(typeHelperRouter.getSetterSetType(method)).thenReturn(binaryType);
		
		TypeTree typeTree = new TypeTree(baseType, typeHelperRouter);

		typeTree.getNodeFor(baseType).getMethodNodes().contains(new MethodNode(method));
		assertNull(typeTree.getNodeFor(binaryType));
	}
	
	public void testShouldExposeCollectionSetterOfTheTypeAsMethodNodeAndRootNodeForTheComplexCollectionSubType() throws JavaModelException {
		IType collectionType = mock(IType.class);
		when(collectionType.isClass()).thenReturn(true);
		when(collectionType.isBinary()).thenReturn(true);
		
		IType collectionSubType = mock(IType.class);
		when(collectionType.isClass()).thenReturn(true);
		when(collectionType.isBinary()).thenReturn(false);
		
		
	}
}
