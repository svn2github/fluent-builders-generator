/**
 * Copyright (c) 2009-2010 fluent-builder-generator for Eclipse commiters.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sabre Polska sp. z o.o. - initial implementation during Hackday
 */

package com.sabre.buildergenerator.ui.wizard.setters;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Title: SettersContentTreeProvider.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class SettersContentTreeProvider implements ITreeContentProvider {
    private Map<IType, Collection<IMethod>> entries;

    public SettersContentTreeProvider(Map<IType, Collection<IMethod>> aMap) {
        this.entries = new HashMap<IType, Collection<IMethod>>(aMap);
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object aParentElement) {
        return entries.get(aParentElement).toArray();
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object aElement) {
        for (Entry<IType, Collection<IMethod>> entry : entries.entrySet()) {
            if (entry.getValue().contains(aElement)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object aElement) {
        if (aElement instanceof IType) {
            Collection<IMethod> methods = entries.get(aElement);

            if (methods != null) {
                return !methods.isEmpty();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object aInputElement) {
        if (aInputElement instanceof IType) {
            Collection<IMethod> list = entries.get(aInputElement);

            if (list != null) {
                return list.toArray();
            } else {
                return null;
            }
        } else {
            return entries.keySet().toArray();
        }
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        entries.clear();
        entries = null;
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer aViewer, Object aOldInput, Object aNewInput) {
//    	System.out.println("Input changed : " + aViewer + " oldINput" + aOldInput + " new input " + aNewInput);
    }
}
