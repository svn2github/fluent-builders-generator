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

package com.sabre.buildergenerator.ui.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Title: TypeNameValidator.java<br>
 * Description: <br>
 * Created: Apr 28, 2010<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * 
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class TypeNameValidator {

	private ErrorCreator errorCreator;
	private IJavaProject project;

	public TypeNameValidator(IJavaProject project, ErrorCreator errorCreator) {
		this.errorCreator = errorCreator;
		this.project = project;
	}

	IStatus validateTypeName(String builderClassName) {
		IStatus status = null;

		String typeName = builderClassName;

		// must not be empty
		if (typeName != null && typeName.length() == 0) {
			return errorCreator.createError("Type name can't be empty");
		}

		if (typeName.indexOf('.') != -1) {
			return errorCreator.createError("You've typed in qualified name");
		}

		String[] compliance = TypeNameValidator
				.getSourceComplianceLevels(project);
		IStatus val = JavaConventions.validateJavaTypeName(builderClassName,
				compliance[0], compliance[1]);

		if (val.getSeverity() == IStatus.ERROR) {
			status = errorCreator.createError("Invalid type name: "
					+ val.getMessage());
		} else if (val.getSeverity() == IStatus.WARNING) {
			status = errorCreator.createWarning(val.getMessage());
		}

		return status;

	}

	/**
	 * @param builderFQName
	 *            TODO
	 * @return
	 */
	IStatus checkBuilderWithSuchNameAlreadyExists(String builderFQName) {
		try {
			IStatus status = null;

			IType existingType = project.findType(builderFQName);

			if (existingType != null) {
				status = errorCreator.createWarning("Class with name "
						+ builderFQName + " already exists.");
			}

			return status;
		} catch (JavaModelException ex) {
			return errorCreator.createError(ex.getMessage());
		}
	}

	/**
	 * copied from jdt
	 * 
	 * @param context
	 *            an {@link IJavaElement} or <code>null</code>
	 * @return a <code>String[]</code> whose <code>[0]</code> is the
	 *         {@link JavaCore#COMPILER_SOURCE} and whose <code>[1]</code> is
	 *         the {@link JavaCore#COMPILER_COMPLIANCE} level at the given
	 *         <code>context</code>.
	 */
	static String[] getSourceComplianceLevels(IJavaElement context) {
		if (context != null) {
			IJavaProject javaProject = context.getJavaProject();

			if (javaProject != null) {
				return new String[] {
						javaProject.getOption(JavaCore.COMPILER_SOURCE, true),
						javaProject.getOption(JavaCore.COMPILER_COMPLIANCE,
								true) };
			}
		}

		return new String[] { JavaCore.getOption(JavaCore.COMPILER_SOURCE),
				JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE) };
	}

}
