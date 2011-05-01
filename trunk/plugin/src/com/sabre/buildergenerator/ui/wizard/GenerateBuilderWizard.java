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

package com.sabre.buildergenerator.ui.wizard;

import com.sabre.buildergenerator.Activator;
import com.sabre.buildergenerator.ui.actions.BuilderGenerationProperties;

import org.eclipse.jface.dialogs.MessageDialog;
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
 *
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

public class GenerateBuilderWizard extends Wizard {
	private static final String IMAGE_URL_STRING = "images/logo.png";
	private final GenerateBuilderWizardPage generateBuilderWizardPage;

	public GenerateBuilderWizard(BuilderGenerationProperties properties) {
		generateBuilderWizardPage = new GenerateBuilderWizardPage("mainPage",
				properties);

		this.addPage(generateBuilderWizardPage);
		this.setWindowTitle("Generate Fluent Builder");
		this.setDefaultPageImageDescriptor(createLogoDescriptor());
	}

	/**
	 * @return
	 */
	private ImageDescriptor createLogoDescriptor() {
		URL url = null;

		try {
			url = new URL(Activator.getDefault().getBundle().getEntry("/"),
					IMAGE_URL_STRING);
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
	 *
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean finish = false;

		if (generateBuilderWizardPage.isValid()) {
			if (!generateBuilderWizardPage.builderFQNameIsUnique()) {
				finish = MessageDialog
						.openConfirm(getShell(), "Builder name is not unique",
								"Builder name is not unique. Do you want to replace the existing class?");
			} else {
				finish = true;
			}
		}

		return finish;
	}
}
