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

import com.sabre.buildergenerator.Activator;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerationProperties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Title: GenerateBuilderWizard.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class GenerateBuilderWizard extends Wizard {
    private static final String IMAGE_URL_STRING = "images/logo.png";
    private final GenerateBuilderWizardPage generateBuilderWizardPage;

    public GenerateBuilderWizard(BuilderGenerationProperties properties) {
        generateBuilderWizardPage = new GenerateBuilderWizardPage("mainPage", properties);

        this.addPage(generateBuilderWizardPage);
        this.setWindowTitle("Generate builder");
        this.setDefaultPageImageDescriptor(createLogoDescriptor());
    }

    /**
     * @return
     */
    private ImageDescriptor createLogoDescriptor() {
        URL url = null;

        try {
            url = new URL(Activator.getDefault().getBundle().getEntry("/"), IMAGE_URL_STRING);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return ImageDescriptor.createFromURL(url);
    }

    /**
    * @return
    */
    public BuilderGenerationProperties getBuilderGenerationProperties() {
        return generateBuilderWizardPage.getBuilderGenerationProperties();
    }

    /**
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override public boolean performFinish() {
        return generateBuilderWizardPage.isValid();
    }
}
