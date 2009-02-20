/**
 *
 */
package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

	private final char[] DEFAULT_SEPARATORS = new char[]{COMMA, SEMICOLON, COLON, SPACE, TAB};

	//UI
	private Combo separatorCombo;

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

		new Label(container, SWT.NONE).setText("Selected separator:");

		separatorCombo = new Combo(container, SWT.DROP_DOWN);

		for (char c : DEFAULT_SEPARATORS) {
			if (c == TAB)
				separatorCombo.add("[TAB]");
			else if (c == SPACE)
				separatorCombo.add("[SPACE]");
			else
				separatorCombo.add(Character.toString(c));
		}

		GridData gridData = new GridData();
		separatorCombo.setLayoutData(gridData);
		separatorCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		separatorCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (separatorCombo.getText() != null && !separatorCombo.getText().equals("")) {
					String seperatorString = Character.toString(separatorCombo.getText().charAt(0));
					separatorCombo.setText(seperatorString);
				}
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
				filePath = dialog.open();
				fileText.setText(filePath);
			}
		});

		setControl(container);
	}

	@Override
	public boolean isPageComplete() {
		boolean result = true;
		setErrorMessage(null);

		if (getSeparator() == null) { //$NON-NLS-1$
			result = false;
			setErrorMessage("Please enter a seperator character.");
		}

		if (filePath == null || filePath.equals("")) {
			result = false;
			setErrorMessage("Please select the location & name the file you want to save.");
		}

		return result;
	}

//	@Override
//	public boolean canFlipToNextPage() {
//		boolean result = true;
//		if (showPreviewDataButton.getSelection() == false) {
//			result = false;
//		}
//
//		return result;
//	}

	public Character getSeparator() {
		if (separatorCombo.getSelectionIndex() > 0)
			return DEFAULT_SEPARATORS[separatorCombo.getSelectionIndex()];
		return null;
	}

	public String getFilePath() {
		return filePath;
	}
}