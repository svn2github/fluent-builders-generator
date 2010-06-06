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
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import com.sabre.buildergenerator.ui.MethodNode;
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
	private final class SettersTypeTreeCheckStateListener implements
			ICheckStateListener {
		private final TypeTree settersTypeTree;

		private SettersTypeTreeCheckStateListener(TypeTree settersTypeTree) {
			this.settersTypeTree = settersTypeTree;
		}

		public void checkStateChanged(
				CheckStateChangedEvent event) {

			((TreeNode<?>) ((TreeNode<?>) event.getElement()))
					.setSelected(event.getChecked());

			transferTreeModelToUI(settersTypeTree);
		}
	}

	private final class SettersTypeTreeExpansionListener implements
			ITreeViewerListener {
		private TreeNode<?> getTreeNode(TreeExpansionEvent event) {
			return (TreeNode<?>) event.getElement();
		}

		public void treeCollapsed(TreeExpansionEvent aEvent) {
			getTreeNode(aEvent).collapse();
		}

		public void treeExpanded(TreeExpansionEvent aEvent) {
			getTreeNode(aEvent).expand();
		}
	}

	private final class AcitveTypeNodesViewerFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof TypeNode) {
				return ((TypeNode) element).isActive();
			}
			return true;
		}
	}

	ErrorCreator errorCreator;

	final BuilderGenerationProperties properties;

	private Text builderClassNameText;

	private Text collectionPrefixText;

	private Text endPrefixText;

	private Button formatCodeButton;
	private Text packageNameText;
	private Text prefixText;

	private CheckboxTreeViewer selectedSettersTreeViewer;
	private Text sourceFolderNameText;

	private final TypeNameValidator typeNameValidator;

	/**
	 * @param wizardPageName
	 */
	public GenerateBuilderWizardPage(String wizardPageName,
			BuilderGenerationProperties properties) {
		super(wizardPageName);

		this.properties = properties;

		this.setTitle("Generate builder");
		this.setDescription("Generates builder for supplied class using it's properties");

		this.errorCreator = new ErrorCreator();
		this.typeNameValidator = new TypeNameValidator(getJavaProject(),
				errorCreator);
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

			createSettersTreeViewer(mainComposite);

			setControl(mainComposite);
		} catch (JavaModelException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Come up with the name problem if there is one
	 */
	@Override
	public void setVisible(boolean aVisible) {
		super.setVisible(aVisible);

		handleStatus(validateBuilderFQNameAlreadyExists());
	}

	private IStatus validateBuilderFQNameAlreadyExists() {
		return typeNameValidator
				.checkBuilderWithSuchNameAlreadyExists(constructFullyQualifiedName());
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

		GridData gridData = createCenterFillGridData();

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

		GridData gridData = createCenterFillGridData();

		gridData.horizontalSpan = 2;
		endPrefixText.setLayoutData(gridData);

		endPrefixText.setText(properties.getEndPrefix());
		endPrefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent aE) {
				properties.setEndPrefix(endPrefixText.getText());

				handleStatus(methodPrefixChanged("End method prefix",
						properties.getEndPrefix(), false));
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

		GridData gridData = createCenterFillGridData();

		gridData.horizontalSpan = 2;
		collectionPrefixText.setLayoutData(gridData);
		collectionPrefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;

				String collectionAddPrefix = text.getText();

				properties.setCollectionAddPrefix(collectionAddPrefix);
				handleStatus(methodPrefixChanged("Collection add prefix",
						collectionAddPrefix, true));
			}
		});
	}

	/**
	 * @param aMainComposite
	 */
	private void createSettersTreeViewer(Composite aMainComposite) {
		createLabel(aMainComposite, "Selected setters");

		selectedSettersTreeViewer = new CheckboxTreeViewer(aMainComposite,
				SWT.BORDER);

		try {
			final TypeTree settersTypeTree = new TypeTree(properties.getType(),
					new TypeHelperRouter());

			properties.setSettersTypeTree(settersTypeTree);
			selectedSettersTreeViewer
					.setContentProvider(new SettersTypeTreeContentTreeProvider(
							settersTypeTree));

			selectedSettersTreeViewer
					.setLabelProvider(new SettersTypeTreeLabelProvider());
			selectedSettersTreeViewer.setAutoExpandLevel(3);

			selectedSettersTreeViewer
					.setFilters(new ViewerFilter[] { new AcitveTypeNodesViewerFilter() });

			selectedSettersTreeViewer
					.addTreeListener(new SettersTypeTreeExpansionListener());

			selectedSettersTreeViewer
					.addCheckStateListener(new SettersTypeTreeCheckStateListener(settersTypeTree));

			Object someInput = new Object();

			selectedSettersTreeViewer.setInput(someInput);

			GridData gridData = createCenterFillGridData();

			gridData.horizontalSpan = 1;
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessVerticalSpace = true;
			selectedSettersTreeViewer.getControl().setLayoutData(gridData);

			transferClickedNodes(settersTypeTree);

			Composite buttonsComposite = new Composite(aMainComposite, SWT.None);
			GridData buttonsGridData = createCenterFillGridData();
			buttonsGridData.horizontalAlignment = SWT.FILL;
			buttonsGridData.verticalAlignment = SWT.BEGINNING;
			buttonsGridData.grabExcessHorizontalSpace = true;
			buttonsGridData.grabExcessVerticalSpace = false;

			buttonsComposite.setLayoutData(buttonsGridData);
			buttonsComposite.setLayout(new GridLayout(1, true));

			Button selectAllButton = new Button(buttonsComposite, SWT.None);

			selectAllButton.setText("Select All");
			selectAllButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					// FIXME - dirty code :|
					for (TypeNode typeNode : settersTypeTree
							.getSortedTypesNodes()) {
						typeNode.setSelected(true);
					}

					selectedSettersTreeViewer.refresh();

					for (TypeNode typeNode : settersTypeTree
							.getSortedTypesNodes()) {
						typeNode.setSelected(true);
					}

					transferTreeModelToUI(settersTypeTree);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			Button deselectAllButton = new Button(buttonsComposite, SWT.None);

			deselectAllButton.setText("Deselect All");
			deselectAllButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					settersTypeTree.getSortedTypesNodes()[0].setSelected(false);

					transferTreeModelToUI(settersTypeTree);
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});

			Button collapseAllButton = new Button(buttonsComposite, SWT.None);
			collapseAllButton.setText("Collapse All");
			collapseAllButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent aE) {
					for (TypeNode typeNode : settersTypeTree
							.getSortedTypesNodes()) {
						typeNode.collapse();
					}
					selectedSettersTreeViewer.collapseAll();
				}

				public void widgetDefaultSelected(SelectionEvent aE) {
					widgetSelected(aE);
				}

			});

			Button expandAllButton = new Button(buttonsComposite, SWT.None);
			expandAllButton.setText("Expand All");
			expandAllButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent aE) {
					widgetSelected(aE);
				}

				public void widgetSelected(SelectionEvent aE) {
					for (TypeNode typeNode : settersTypeTree
							.getSortedTypesNodes()) {
						typeNode.expand();
					}
					selectedSettersTreeViewer.expandAll();
				}

			});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void transferCollapsedNodes(TypeTree aSettersTypeTree) {
		for (TypeNode typeNode : aSettersTypeTree.getSortedTypesNodes()) {
			if (typeNode.isCollapsed()) {
				selectedSettersTreeViewer.collapseToLevel(typeNode, 1);
			} else {
				selectedSettersTreeViewer.expandToLevel(typeNode, 1);
			}
		}
	}

	private void transferClickedNodes(TypeTree settersTypeTree) {
		for (TypeNode typeNode : settersTypeTree.getSortedTypesNodes()) {

			if (typeNode.isActive()) {
				boolean allMethodsChecked = true;
				boolean noMethodChecked = true;

				for (MethodNode methodNode : typeNode.getMethodNodes()) {
					boolean methodSelected = methodNode.isSelected();

					selectedSettersTreeViewer.setChecked(methodNode,
							methodSelected);

					if (!methodSelected) {
						allMethodsChecked = false;
					} else {
						noMethodChecked = false;
					}
				}

				if (allMethodsChecked) {
					selectedSettersTreeViewer.setChecked(typeNode, true);
					selectedSettersTreeViewer.setGrayed(typeNode, false);
				} else if (noMethodChecked) {
					selectedSettersTreeViewer.setGrayChecked(typeNode, false);
				} else {
					selectedSettersTreeViewer.setGrayChecked(typeNode, true);
				}
			}
		}

	}

	private Label createLabel(Composite aMainComposite, String aString) {
		Label label = new Label(aMainComposite, SWT.None);

		label.setText(aString);

		return label;
	}

	private void createBuilderNamePart(Composite aMainComposite) {
		createLabel(aMainComposite, "Builder class name");

		builderClassNameText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);
		builderClassNameText.setText(properties.getBuilderClassName());

		GridData textGridData = createCenterFillGridData();

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

	public String constructFullyQualifiedName() {
		String typeName = builderClassNameText.getText();
		String packageName = packageNameText.getText();

		if (packageName.length() > 0) {
			typeName = packageName + "." + typeName;
		}

		return typeName;
	}

	private IStatus typeNameChanged() {
		IStatus status;

		status = typeNameValidator.validateTypeName(builderClassNameText
				.getText());

		if (status == null) {
			status = validateBuilderFQNameAlreadyExists();
		}

		return status;
	}

	/**
	 * @return
	 */
	IJavaProject getJavaProject() {
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

	private IStatus methodPrefixChanged(String fieldName, String prefix,
			boolean canBeEmpty) {
		return typeNameValidator.validateMethodPrefix(fieldName, prefix,
				canBeEmpty);
	}

	/**
	 * @param aMainComposite
	 */
	private void createPrefixPart(Composite aMainComposite) {
		createLabel(aMainComposite, "Builder methods prefix");

		prefixText = new Text(aMainComposite, SWT.SINGLE | SWT.BORDER);
		prefixText.setText(properties.getMethodsPrefix());

		GridData gridData = createCenterFillGridData();

		gridData.horizontalSpan = 2;
		prefixText.setLayoutData(gridData);

		prefixText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				Text text = (Text) event.widget;

				String prefix = text.getText();

				properties.setMethodsPrefix(prefix);
				handleStatus(methodPrefixChanged("prefix", prefix, true));
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

		GridData textGridData = createCenterFillGridData();

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

	private GridData createCenterFillGridData() {
		return new GridData(SWT.FILL, SWT.CENTER, true, false);
	}

	private void createPackagePart(Composite mainComposite)
			throws JavaModelException {
		createLabel(mainComposite, "Package");

		packageNameText = new Text(mainComposite, SWT.SINGLE | SWT.BORDER);
		packageNameText.setText(properties.getPackageName());
		packageNameText.setLayoutData(createCenterFillGridData());

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

	public boolean isValid() {
		try {
			IStatus prefixStatus = methodPrefixChanged("prefix", properties
					.getMethodsPrefix(), true);
			IStatus collectionAddPrefixStatus = methodPrefixChanged(
					"Collection add prefix", properties
							.getCollectionAddPrefix(), true);
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

	public boolean builderFQNameIsUnique() {
		return validateBuilderFQNameAlreadyExists() == null;
	}

	/**
	 * @return
	 */
	public BuilderGenerationProperties getBuilderGenerationProperties() {
		return properties;
	}

	private void transferTreeModelToUI(final TypeTree settersTypeTree) {
		selectedSettersTreeViewer.refresh();
		transferClickedNodes(settersTypeTree);
		selectedSettersTreeViewer.refresh();
		transferCollapsedNodes(settersTypeTree);
		selectedSettersTreeViewer.refresh();
	}
}
