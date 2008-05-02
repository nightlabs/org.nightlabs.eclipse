package org.nightlabs.jseditor.ui.scriptwizard;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jseditor.ui.editor.JSEditorComposite;
import org.nightlabs.jseditor.ui.resource.Messages;

public class JSEditorScriptWizardPage
extends WizardHopPage
{
	private TabFolder tabFolder;
	private TabItem tabItem1;
	private TabItem tabItem2;

	private List scriptList;
	private JSEditorComposite srcText;

	private SourceViewer sourceViewer;

	private Text descText;

	public JSEditorScriptWizardPage(SourceViewer sourceViewer){
		super(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.pageName")); //$NON-NLS-1$
		this.sourceViewer = sourceViewer;

		setTitle(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		/******************************
		 * Tab Folder
		 ******************************/
		tabFolder =	new TabFolder(parent, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gridData);

		GridLayout tabFolderLayout = new GridLayout(1, true);
		tabFolder.setLayout(tabFolderLayout);

		/******************************
		 * Tab Items
		 ******************************/
		tabItem1 =
			new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.availableScriptsTabItem.text")); //$NON-NLS-1$

		tabItem2 =
			new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.seachScriptsTabItem.text")); //$NON-NLS-1$

		/******************************
		 * Main Composite
		 ******************************/
		XComposite scriptTabItemComposite =
			new XComposite(tabFolder, SWT.NONE);
		scriptTabItemComposite.getGridLayout().numColumns = 4;

		/******************************
		 * Script Group
		 ******************************/
		Group scriptGroup = new Group(scriptTabItemComposite, SWT.NONE);
		scriptGroup.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.scriptGroup.text")); //$NON-NLS-1$
		scriptGroup.setLayout(new GridLayout());

		gridData = new GridData();
		gridData.verticalSpan = 3;
		gridData.horizontalSpan = 1;
		gridData.grabExcessVerticalSpace = true;
		scriptGroup.setLayoutData(gridData);

		/******************************
		 * Script List
		 ******************************/
		scriptList = new List(scriptGroup, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 1;
		scriptList.setLayoutData(gridData);


// Marco: This project (org.nightlabs.jseditor.ui) must not have a dependency on JFireTrade stuff.
// 1st, this would create an illegal circular dependency and 2nd we want to use the jseditor in
// other apps (which might not have JFireTrade deployed).
// commented the following line:
		appendListFunction(String.class);

		/******************************
		 * Selection Listener
		 ******************************/
		scriptList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String[] selected = scriptList.getSelection();
				if (selected.length > 0){
					descText.setText(scriptMap.get(selected[0].toString()).toGenericString());
				}//if
			}
			@Override
			public void widgetDefaultSelected(
					SelectionEvent event) {
				String[] selected = scriptList.getSelection();
				if (selected.length > 0){
					//Do it later ;-)
				}//if
			}
		});

		/******************************
		 * Mouse Listener
		 ******************************/
		scriptList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				String[] selected = scriptList.getSelection();
				srcText.getDocument().set(srcText.getDocument().get().concat(selected[0]));
			}
		});

		/******************************
		 * Button
		 ******************************/
		Button addButton = new Button(scriptTabItemComposite, SWT.PUSH);
		addButton.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.addButton.text")); //$NON-NLS-1$

		/******************************
		 * Right Composite
		 ******************************/
		XComposite rightComposite = new XComposite(scriptTabItemComposite, SWT.NONE);
		rightComposite.getGridLayout().numColumns = 1;

		/******************************
		 * Editor Group
		 ******************************/
		Group editorGroup = new Group(rightComposite, SWT.NONE);
		editorGroup.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.editorGroup.text")); //$NON-NLS-1$
		editorGroup.setLayout(new GridLayout());
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 2;
		editorGroup.setLayoutData(gridData);

		/******************************
		 * Source Preview
		 ******************************/
		srcText = new JSEditorComposite(editorGroup);
		if(sourceViewer != null){
			srcText.getDocument().set(sourceViewer.getTextWidget().getText());
		}//if
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		srcText.setLayoutData(gridData);

		srcText.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {

			}

			@Override
			public void focusLost(FocusEvent e) {
				srcText.getSourceViewer().getMark();
//				srcText.getSourceViewer().getTextWidget().setFont(new Font())
			}
		});
		/******************************
		 * Evaluation Group
		 ******************************/
		Group evalGroup = new Group(rightComposite, SWT.NONE);
		evalGroup.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.evaluateGroup.text")); //$NON-NLS-1$
		evalGroup.setLayout(new GridLayout());
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 2;
		evalGroup.setLayoutData(gridData);

		/******************************
		 * Description Group
		 ******************************/
		Group descGroup = new Group(scriptTabItemComposite, SWT.NONE);
		descGroup.setText(Messages.getString("org.nightlabs.jseditor.ui.scriptwizard.JSEditorScriptWizardPage.descriptionGroup.text")); //$NON-NLS-1$
		descGroup.setLayout(new GridLayout());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		descGroup.setLayoutData(gridData);

		/******************************
		 * Description Text
		 ******************************/
		descText = new Text(descGroup, SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		descText.setLayoutData(gridData);

		tabItem1.setControl(scriptTabItemComposite);
		/***********End***********/

		return tabFolder;
	}


	private Map<String, Method> scriptMap;
	public void appendListFunction(Class<?> c){
		if(scriptMap == null){
			scriptMap = new HashMap<String, Method>();
		}//if

		String[] sArray = new String[c.getMethods().length];
		int i = 0;
		for(Method m : c.getMethods()){
			//Method name
			String s = m.getName();
			//Method description
			scriptMap.put(s, m);
			sArray[i++] = s;
		}//for

		scriptList.setItems(sArray);
	}

	public JSEditorComposite getSrcText(){
		return srcText;
	}
}