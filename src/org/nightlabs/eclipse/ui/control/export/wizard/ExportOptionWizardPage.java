/**
 *
 */
package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class ExportOptionWizardPage
extends WizardPage
{
	private final char TAB = '\t';
	private final char COMMA = ',';
	private final char SEMICOLON = ';';
	private final char COLON = ':' ;
	private final char SPACE = ' ';

	private final char[] DEFAULT_SEPERATORS = new char[]{COMMA, SEMICOLON, COLON, SPACE, TAB};

	//UI
	private Combo seperatorCombo;

	private Button showPreviewDataButton;

	private Text fileText;

	//Data
	private String filePath;

	protected ExportOptionWizardPage(String pageName) {
		super(pageName);
		setTitle("Option");
		setDescription("Description");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);

		new Label(container, SWT.NONE).setText("Selected Seperator :");

		seperatorCombo = new Combo(container, SWT.DROP_DOWN);

		for (char c : DEFAULT_SEPERATORS) {
			if (c == TAB)
				seperatorCombo.add("[TAB]");
			else if (c == SPACE)
				seperatorCombo.add("[SPACE]");
			else
				seperatorCombo.add(Character.toString(c));
		}

		GridData gridData = new GridData();
		seperatorCombo.setLayoutData(gridData);
		seperatorCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		showPreviewDataButton = new Button(container, SWT.CHECK);
		showPreviewDataButton.setText("Show exported preview data");
		showPreviewDataButton.setSelection(true);
		showPreviewDataButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}
		});
		
		Composite fileLocationComposite = new Composite(container, SWT.NULL);
		final GridLayout gridLayout2 = new GridLayout(3, false);
		fileLocationComposite.setLayout(gridLayout2);
		fileLocationComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(fileLocationComposite, SWT.NONE).setText("Save Location: ");

		fileText = new Text(fileLocationComposite, SWT.SINGLE);
		fileText.setTextLimit(1);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gridData);
		fileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		Button browseButton = new Button(fileLocationComposite, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.NULL);
				String path = dialog.open();
				filePath = path;
				fileText.setText(filePath);
			}
		});

		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		boolean result = true;
		setErrorMessage(null);

		if (getSeperator() == ' ') { //$NON-NLS-1$
			result = false;
			setErrorMessage("Please enter a seperator character.");
		}

		if (filePath == null || filePath.equals("")) {
			result = false;
			setErrorMessage("Please select the location & name the file you want to save.");
		}

		return result;
	}

	@Override
	public boolean canFlipToNextPage() {
		boolean result = true;
		if (showPreviewDataButton.getSelection() == false) {
			result = false;
		}

		return result;
	}

	public char getSeperator() {
		if (seperatorCombo.getSelectionIndex() > 0)
			return DEFAULT_SEPERATORS[seperatorCombo.getSelectionIndex()];
		return COMMA;
	}

	public String getFilePath() {
		return filePath;
	}
}