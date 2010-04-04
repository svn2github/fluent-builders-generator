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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.sabre.buildergenerator.Activator;
import com.sabre.buildergenerator.sourcegenerator.BuilderGenerationProperties;
import com.sabre.buildergenerator.ui.TreeNode;
import com.sabre.buildergenerator.ui.TypeHelperRouter;
import com.sabre.buildergenerator.ui.TypeNode;
import com.sabre.buildergenerator.ui.TypeTree;

/**
 * Title: GenerateBuilderWizardPage.java<br>
 * Description: <br>
 * Created: Dec 9, 2009<br>
 * Copyright: Copyright (c) 2007<br>
 * Company: Sabre Holdings Corporation
 * 
 * @author Jakub Janczak sg0209399
 * @version $Rev$: , $Date$: , $Author$:
 */

class GenerateBuilderWizardPage extends NewElementWizardPage {

	private Text builderClassNameText;

	private Text collectionPrefixText;

	private Text endPrefixText;

	private Button formatCodeButton;
	private Text packageNameText;
	private Text prefixText;

	private final BuilderGenerationProperties properties;

	private CheckboxTreeViewer selectedSettersTreeViewer;
	private Text sourceFolderNameText;

	/**
	 * @param wizardPageName
	 */
	public GenerateBuilderWizardPage(String wizardPageName,
			BuilderGenerationProperties properties) {
		super(wizardPageName);

		this.properties = properties;

		this.setTitle("Generate builder");
		this
				.setDescription("Generates builder for supplied class using it's properties");
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite aParent) {
		try {
			Composite mainComposite = new Composite(aParent, SWT.None);

			GridLayout gridLayout = new GridLayout(3, false);

			mainComposite.setLayout(gridLayout);

			createBuilderNamePart(mainComposite);
			createPackagePart(mainComposite);
			createSourceFolderPart(mainComposite);
			createPrefixPart(mainComposite);
			createCollectionAddedPrefixPart(mainComposite);
			createEndPrefixPart(mainComposite);

			createFormatCodePart(mainComposite);

			createCheckboxTreeViewer(mainComposite);

			setControl(mainComposite);
		} catch (JavaModelException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param aMainComposite
	 */
	private void createFormatCodePart(Composite aMainComposite) {
		createLabel(aMainComposite, "Format code");

		formatCodeButton = new Button(aMainComposite, SWT.CHECK);
		formatCodeButton.setSelection(properties.isFormatCode());
		formatCodeButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent aE) {
				widgetSelected(aE);
			}

			public void widgetSelected(SelectionEvent event) {
				properties
						.setFormatCode(((Button) event.widget).getSelection());
			}
		});

		GridData gridData = createGridData();

		gridData.horizontalAlignment = SWT.BEGINNING;
		gridData.horizontalSpan = 2;
		formatCodeButton.setLayoutData(gridData);
	}

	/**
	 * @param aMainComposite
	 */
	private void createEndPrefixPart(Composite aMainComposite) {
		createLabel(aMainComposite, "'End' method prefix");

		endPrefixText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);

		GridData gridData = createGridData();

		gridData.horizontalSpan = 2;
		endPrefixText.setLayoutData(gridData);

		endPrefixText.setText(properties.getEndPrefix());
		endPrefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent aE) {
				properties.setEndPrefix(endPrefixText.getText());

				handleStatus(prefixChanged("End method prefix", properties
						.getEndPrefix(), false));
			}
		});
	}

	/**
	 * @param aMainComposite
	 */
	private void createCollectionAddedPrefixPart(Composite aMainComposite) {
		createLabel(aMainComposite, "Collection add prefix");

		collectionPrefixText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);
		collectionPrefixText.setText(properties.getCollectionAddPrefix());

		GridData gridData = createGridData();

		gridData.horizontalSpan = 2;
		collectionPrefixText.setLayoutData(gridData);
		collectionPrefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;

				String collectionAddPrefix = text.getText();

				properties.setCollectionAddPrefix(collectionAddPrefix);
				handleStatus(prefixChanged("Collection add prefix",
						collectionAddPrefix, false));
			}
		});
	}

	/**
	 * @param aMainComposite
	 */
	private void createCheckboxTreeViewer(Composite aMainComposite) {
		createLabel(aMainComposite, "Selected setters");

		
		selectedSettersTreeViewer = new CheckboxTreeViewer(aMainComposite,
				SWT.BORDER);

		try {
			final TypeTree settersTypeTree = new TypeTree(properties.getType(),
					new TypeHelperRouter());
			selectedSettersTreeViewer
					.setContentProvider(new SettersTypeTreeContentTreeProvider(
							settersTypeTree));

			selectedSettersTreeViewer
					.setLabelProvider(new JavaElementLabelProvider());
			selectedSettersTreeViewer.setAutoExpandLevel(3);

			selectedSettersTreeViewer
					.addCheckStateListener(new ICheckStateListener() {

						public void checkStateChanged(
								CheckStateChangedEvent event) {
							boolean newState = event.getChecked();
							Object element = event.getElement();

							if (element instanceof IType) {
								typeClicked((IType) element, newState);
							} else {
								methodClicked((IMethod) element, newState);
							}

							transferClickedNodes(settersTypeTree);
							selectedSettersTreeViewer.refresh();
						}

						private void methodClicked(IMethod element,
								boolean newState) {
							// method has to be somewhere inside a type - assume
							IType owningType = (IType) element.getParent();
							TypeNode typeNode = settersTypeTree
									.getNodeFor(owningType);

							TreeNode<IMethod> methodNode = typeNode
									.getMethodNodeFor(element);
							methodNode.setSelected(newState);
						}

						private void typeClicked(IType element, boolean newState) {
							TypeNode typeNode = settersTypeTree
									.getNodeFor(element);
							typeNode.setSelected(newState);
						}

					});

			Object someInput = new Object();
			selectedSettersTreeViewer.setInput(someInput);
			
			GridData gridData = createGridData();
			gridData.horizontalSpan = 1;
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessVerticalSpace = true;
			selectedSettersTreeViewer.getControl().setLayoutData(gridData);

			transferClickedNodes(settersTypeTree);
			
			
			Composite buttonsComposite = new Composite(aMainComposite, SWT.None);
			GridData buttonsGridData = createGridData();
			buttonsComposite.setLayoutData(buttonsGridData);
			buttonsComposite.setLayout(new GridLayout(1, true));
			
			Button selectAllButton = new Button(buttonsComposite, SWT.None);
			selectAllButton.setText("Select All");
			selectAllButton.addSelectionListener(new SelectionListener() {
				
				public void widgetSelected(SelectionEvent e) {
					
					// FIXME - dirty code :|
					for (IType type: settersTypeTree.getSortedActiveTypes()) {
						settersTypeTree.getNodeFor(type).setSelected(true);
					}
					selectedSettersTreeViewer.refresh();
					
					for (IType type: settersTypeTree.getSortedTypes()) {
						settersTypeTree.getNodeFor(type).setSelected(true);
					}
					transferClickedNodes(settersTypeTree);
					selectedSettersTreeViewer.refresh();
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			
			Button deselectAllButton = new Button(buttonsComposite, SWT.None);
			deselectAllButton.setText("Deselect All");
			deselectAllButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					settersTypeTree.getNodeFor(settersTypeTree.getSortedTypes()[0]).setSelected(false);
					transferClickedNodes(settersTypeTree);
					selectedSettersTreeViewer.refresh();
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	private void transferClickedNodes(TypeTree settersTypeTree) {
		for (IType type : settersTypeTree.getSortedActiveTypes()) {
			TypeNode typeNode = settersTypeTree.getNodeFor(type);
			if (typeNode.isActive()) {
				
				boolean allChecked = true;
				boolean noneChecked = true;
				
				for (TreeNode<IMethod> methodNode : typeNode.getMethodNodes()) {
					boolean methodSelected = methodNode.isSelected();
					selectedSettersTreeViewer.setChecked(methodNode
							.getElement(), methodSelected);
					if (!methodSelected) {
						allChecked = false;
					} else {
						noneChecked = false;
					}
				}
				
				if (allChecked) {
					selectedSettersTreeViewer.setChecked(type, true);
					selectedSettersTreeViewer.setGrayed(type, false);
				} else if (noneChecked) {
					selectedSettersTreeViewer.setGrayChecked(type, false);
				} else {
					selectedSettersTreeViewer.setGrayChecked(type, true);
				}
				
			}
		}
	}

	private Label createLabel(Composite aMainComposite, String aString) {
		Label label = new Label(aMainComposite, SWT.None);

		label.setText(aString);

		return label;
	}

	/**
	 * @param aMainComposite
	 */
	private void createBuilderNamePart(Composite aMainComposite) {
		createLabel(aMainComposite, "Builder class name");

		builderClassNameText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);
		builderClassNameText.setText(properties.getBuilderClassName());

		GridData textGridData = createGridData();

		textGridData.horizontalSpan = 2;

		builderClassNameText.setLayoutData(textGridData);
		builderClassNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;

				properties.setBuilderClassName(text.getText());
				handleStatus(typeNameChanged());
			}
		});

		builderClassNameText.setFocus();
	}

	private void handleStatus(IStatus status) {
		if (status != null) {
			switch (status.getSeverity()) {
			case IStatus.ERROR:
				setMessage(status.getMessage(), IMessageProvider.ERROR);

				break;

			case IStatus.WARNING:
				setMessage(status.getMessage(), IMessageProvider.WARNING);

				break;
			}
		} else {
			setMessage(getDescription(), IMessageProvider.NONE);
		}
	}

	private IStatus createError(String message) {
		return new Status(Status.ERROR, Activator.PLUGIN_ID, message);
	}

	private IStatus typeNameChanged() {
		IStatus status = null;

		String typeName = properties.getBuilderClassName();

		// must not be empty
		if (typeName != null && typeName.length() == 0) {
			return createError("Type name can't be empty");
		}

		if (typeName.indexOf('.') != -1) {
			return createError("You've typed in qualified name");
		}

		String[] compliance = getSourceComplianceLevels(getJavaProject());
		IStatus val = JavaConventions.validateJavaTypeName(typeName,
				compliance[0], compliance[1]);

		if (val.getSeverity() == IStatus.ERROR) {
			return createError("Invalid type name: " + val.getMessage());
		} else if (val.getSeverity() == IStatus.WARNING) {
			return createWarning(val.getMessage());
		}

		return status;
	}

	/**
	 * @return
	 */
	private IJavaProject getJavaProject() {
		return properties.getType().getJavaProject();
	}

	private IPackageFragment[] getSourcePackages() throws JavaModelException {
		List<IPackageFragment> packages = new ArrayList<IPackageFragment>();

		for (IPackageFragmentRoot packageFragmentRoot : getJavaProject()
				.getPackageFragmentRoots()) {
			if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
				for (IJavaElement element : packageFragmentRoot.getChildren()) {
					if (element instanceof IPackageFragment) {
						packages.add((IPackageFragment) element);
					}
				}
			}
		}

		return packages.toArray(new IPackageFragment[packages.size()]);
	}

	private <T extends IJavaElement> String[] convertToStringArray(T[] arr) {
		List<String> strings = new ArrayList<String>(arr.length);

		for (T t : arr) {
			strings.add(t.getElementName());
		}

		return strings.toArray(new String[strings.size()]);
	}

	private Status createWarning(String message) {
		return new Status(Status.WARNING, Activator.PLUGIN_ID,
				"Discouraged type name: " + message);
	}

	/**
	 * A prefix for the method has been changed - generic method
	 * 
	 * @param fieldName
	 *            TODO
	 * @param prefix
	 * @param canBeEmpty
	 *            TODO
	 * 
	 * @return
	 */
	private IStatus prefixChanged(String fieldName, String prefix,
			boolean canBeEmpty) {
		// can be empty but if not have to comply with java method name
		if (prefix.length() != 0) {
			String[] compliance = getSourceComplianceLevels(getJavaProject());
			IStatus val = JavaConventions.validateMethodName(prefix + "XXX",
					compliance[0], compliance[1]);

			if (val != null) {
				if (val.getSeverity() == IStatus.ERROR) {
					return createError(val.getMessage());
				} else if (val.getSeverity() == IStatus.WARNING) {
					return createWarning(val.getMessage());
				}
			}
		} else if (!canBeEmpty) {
			return createError("Field " + fieldName + " can't be left empty");
		}

		return null;
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
	private static String[] getSourceComplianceLevels(IJavaElement context) {
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

	/**
	 * @param aMainComposite
	 */
	private void createPrefixPart(Composite aMainComposite) {
		createLabel(aMainComposite, "Builder methods prefix");

		prefixText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);
		prefixText.setText(properties.getMethodsPrefix());

		GridData gridData = createGridData();

		gridData.horizontalSpan = 2;
		prefixText.setLayoutData(gridData);

		prefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;

				String prefix = text.getText();

				properties.setMethodsPrefix(prefix);
				handleStatus(prefixChanged("prefix", prefix, true));
			}
		});
	}

	/**
	 * @param aMainComposite
	 */
	private void createSourceFolderPart(Composite aMainComposite) {
		createLabel(aMainComposite, "Source folder");

		sourceFolderNameText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);
		sourceFolderNameText.setText(properties.getSourceFolder().getPath()
				.toString());
		sourceFolderNameText.setEnabled(false);

		GridData textGridData = createGridData();

		sourceFolderNameText.setLayoutData(textGridData);

		Button selectSourceFolderButton = new Button(aMainComposite, SWT.None);

		selectSourceFolderButton.setText("Browse...");
		selectSourceFolderButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
				selectSourceFolder();
			}

			public void widgetSelected(SelectionEvent event) {
				selectSourceFolder();
			}
		});
	}

	private GridData createGridData() {
		return new GridData(SWT.FILL, SWT.CENTER, true, false);
	}

	private void createPackagePart(Composite mainComposite)
			throws JavaModelException {
		createLabel(mainComposite, "Package");

		packageNameText = new Text(mainComposite, SWT.SINGLE | SWT.BORDER);
		packageNameText.setText(properties.getPackageName());
		packageNameText.setLayoutData(createGridData());

		packageNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent aE) {
				Text text = (Text) aE.widget;

				properties.setPackageName(text.getText());
			}
		});
		new AutoCompleteField(packageNameText, new TextContentAdapter(),
				convertToStringArray(getSourcePackages()));

		Button selectPackageButton = new Button(mainComposite, SWT.None);

		selectPackageButton.setText("Browse...");

		selectPackageButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent aE) {
				selectPackage();
			}

			public void widgetSelected(SelectionEvent aE) {
				selectPackage();
			}
		});
	}

	private boolean isDialogOk(int result) {
		return result == Dialog.OK;
	}

	private void selectSourceFolder() {
		SelectionDialog dialog = new SourceFolderSelectDialog(getShell(),
				getJavaProject());

		if (isDialogOk(dialog.open())) {
			Object[] folders = dialog.getResult();

			if (folders.length > 0) {
				properties.setSourceFolder((IPackageFragmentRoot) folders[0]);
				sourceFolderNameText.setText(properties.getSourceFolder()
						.getPath().toString());
			}
		}
	}

	private void selectPackage() {
		try {
			SelectionDialog dialog = JavaUI.createPackageDialog(getShell(),
					getJavaProject(), 0);

			if (isDialogOk(dialog.open())) {
				Object[] packages = dialog.getResult();

				if (packages.length > 0) {
					properties.setPackageName(((IPackageFragment) packages[0])
							.getElementName());
					packageNameText.setText(properties.getPackageName());
				}
			}
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean notError(IStatus status) {
		if (status != null) {
			return status.getSeverity() != IStatus.ERROR;
		}

		return true;
	}

	/**
	 * @return
	 */
	public boolean isValid() {
		try {
			IStatus prefixStatus = prefixChanged("prefix", properties
					.getMethodsPrefix(), true);
			IStatus collectionAddPrefixStatus = prefixChanged(
					"Collection add prefix", properties
							.getCollectionAddPrefix(), false);
			IStatus typeNameStatus = typeNameChanged();

			return notError(typeNameStatus) && notError(prefixStatus)
					&& notError(collectionAddPrefixStatus);
		} catch (NullPointerException ex) {
			Activator
					.getDefault()
					.getLog()
					.log(
							new Status(
									IStatus.ERROR,
									Activator.PLUGIN_ID,
									"Exception during GenerateBuilderWizardPage.isValid()",
									ex));
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @return
	 */
	public BuilderGenerationProperties getBuilderGenerationProperties() {
		return properties;
	}
}
