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

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

/**
 * Title: TreeTest.java<br>
 * Description: <br>
 * Created: Mar 19, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TypeTreeTest extends TestCase {
	private <T> Set<T> createSet(T...elements) {
		Set<T> set = new HashSet<T>();
		
		for (T element : elements) {
			set.add(element);
		}
		
		return set;
	}
	
	public void testShouldExposeMethodsDefinedByIType() {
		IType baseType = mock(IType.class);
		IMethod method = mock(IMethod.class);
		
		Map<IType, Set<IMethod>> definedMethods = new HashMap<IType, Set<IMethod>>();
		definedMethods.put(baseType, createSet(method));
		
		TypeTree typeTree = new TypeTree(baseType, definedMethods);
		
		assertTrue(typeTree.getNodeFor(baseType).getMethodNodes().contains(new TreeNode<IMethod>(method)));
	}
}
