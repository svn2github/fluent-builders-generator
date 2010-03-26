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

package com.sabre.buildergenerator.sourcegenerator;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Title: GenerationProperties.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class BuilderGenerationProperties {
    //    private IPackageFragment aPackage;
    private String builderClassName;
    private String collectionAddPrefix;
    private String endPrefix;
    private boolean formatCode;
    private boolean generateComments;
    private String methodsPrefix;
    private String packageName;
    private Set<IMethod> selectedMethods;
    private IPackageFragmentRoot sourceFolder;
    private IType type;

    /**
     * @param aType
     * @throws JavaModelException
     */
    public BuilderGenerationProperties(IType aType) throws Exception {
        type = aType;

        IPackageFragment aPackage = type.getPackageFragment();

        packageName = aPackage.getElementName();
        builderClassName = aType.getElementName() + "Builder";
        generateComments = true;
        formatCode = true;

        methodsPrefix = "with";
        collectionAddPrefix = "withAdded";
        endPrefix = "end";

        selectedMethods = getAllSetters(aType);
        sourceFolder = getSourceFolder(aPackage);
    }

    /**
     * @return the formatCode
     */
    public boolean isFormatCode() {
        return formatCode;
    }

    /**
     * @param aFormatCode the formatCode to set
     */
    public void setFormatCode(boolean aFormatCode) {
        formatCode = aFormatCode;
    }

    /**
    * @param aType
    * @return
    * @throws JavaModelException
    */
    private Set<IMethod> getAllSetters(IType aType) throws Exception {
        Set<IMethod> result = new HashSet<IMethod>();
        Map<IType, Collection<IMethod>> map = TypeHelper.findSetterMethodsForAllTypesReferenced(aType);

        for (Collection<IMethod> methods : map.values()) {
            result.addAll(methods);
        }

        return result;
    }

    /**
    * @param aPackageFragment
    * @return
    */
    private IPackageFragmentRoot getSourceFolder(IPackageFragment aPackageFragment) {
        if (aPackageFragment.getParent() instanceof IPackageFragmentRoot) {
            return (IPackageFragmentRoot) aPackageFragment.getParent();
        } else {
            return null;
        }
    }

    /**
     * @return the builderClassName
     */
    public String getBuilderClassName() {
        return builderClassName;
    }

    /**
     * @return the collectionAddPrefix
     */
    public String getCollectionAddPrefix() {
        return collectionAddPrefix;
    }

    /**
     * @return the endPrefix
     */
    public String getEndPrefix() {
        return endPrefix;
    }

    /**
     * @return the generateComments
     */
    public boolean isGenerateComments() {
        return generateComments;
    }

    /**
     * @return the methodsPrefix
     */
    public String getMethodsPrefix() {
        return methodsPrefix;
    }

    /**
     * @return the selectedMethods
     */
    public Set<IMethod> getSelectedMethods() {
        return selectedMethods;
    }

    /**
     * @return the sourceFolder
     */
    public IPackageFragmentRoot getSourceFolder() {
        return sourceFolder;
    }

    /**
     * @return the type
     */
    public IType getType() {
        return type;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param aPackageName the packageName to set
     */
    public void setPackageName(String aPackageName) {
        packageName = aPackageName;
    }

    /**
    * @param aBuilderClassName the builderClassName to set
    */
    public void setBuilderClassName(String aBuilderClassName) {
        builderClassName = aBuilderClassName;
    }

    /**
     * @param aCollectionAddPrefix the collectionAddPrefix to set
     */
    public void setCollectionAddPrefix(String aCollectionAddPrefix) {
        collectionAddPrefix = aCollectionAddPrefix;
    }

    /**
     * @param aEndPrefix the endPrefix to set
     */
    public void setEndPrefix(String aEndPrefix) {
        endPrefix = aEndPrefix;
    }

    /**
     * @param aGenerateComments the generateComments to set
     */
    public void setGenerateComments(boolean aGenerateComments) {
        generateComments = aGenerateComments;
    }

    /**
     * @param aMethodsPrefix the methodsPrefix to set
     */
    public void setMethodsPrefix(String aMethodsPrefix) {
        methodsPrefix = aMethodsPrefix;
    }

    /**
     * @param aSelectedMethods the selectedMethods to set
     */
    public void setSelectedMethods(Set<IMethod> aSelectedMethods) {
        selectedMethods = aSelectedMethods;
    }

    /**
     * @param aSourceFolder the sourceFolder to set
     */
    public void setSourceFolder(IPackageFragmentRoot aSourceFolder) {
        sourceFolder = aSourceFolder;
    }

    /**
     * @param aType the type to set
     */
    public void setType(IType aType) {
        type = aType;
    }
}
