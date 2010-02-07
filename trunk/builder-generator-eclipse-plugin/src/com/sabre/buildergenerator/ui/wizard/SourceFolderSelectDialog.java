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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import java.util.LinkedList;
import java.util.List;


/**
 * Title: SourceFolderSelectDialog.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class SourceFolderSelectDialog extends ElementListSelectionDialog {
    /**
     * @param aParent
     * @param aRenderer
     */
    public SourceFolderSelectDialog(Shell aParent, IJavaProject project) {
        super(aParent, new JavaElementLabelProvider());

        initElements(project);
    }

    private void initElements(IJavaProject javaProject) {
        List<IPackageFragmentRoot> roots = new LinkedList<IPackageFragmentRoot>();

        try {
            for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
                if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    roots.add(root);
                }
            }
        } catch (JavaModelException e) {
            throw new RuntimeException(e);
        }

        setElements(roots.toArray(new IPackageFragmentRoot[roots.size()]));
    }
}
