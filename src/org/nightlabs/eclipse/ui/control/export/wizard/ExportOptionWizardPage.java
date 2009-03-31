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
import org.nightlabs.eclipse.ui.control.export.resource.Messages;

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

	protected ExportOptionWizardPage(String pageName) {
		super(pageName);
		setTitle(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.page.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.page.description")); //$NON-NLS-1$
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);

		Composite wrapper = new Composite(container, SWT.NONE);
		wrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		wrapper.setLayout(new GridLayout(2, false));

		separatorCombo = new Combo(wrapper, SWT.DROP_DOWN);
		for (char c : DEFAULT_SEPARATORS) {
			if (c == TAB)
				separatorCombo.add(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.3")); //$NON-NLS-1$
			else if (c == SPACE)
				separatorCombo.add(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.4")); //$NON-NLS-1$
			else
				separatorCombo.add(Character.toString(c));
		}
		separatorCombo.select(0);
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
				if (separatorCombo.getText() != null && !separatorCombo.getText().equals("")) { //$NON-NLS-1$
					String seperatorString = Character.toString(separatorCombo.getText().charAt(0));
					separatorCombo.setText(seperatorString);
				}
				getContainer().updateButtons();
			}
		});

		new Label(wrapper, SWT.NONE).setText(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.label.separator.text")); //$NON-NLS-1$

		Composite previewWrapper = new Composite(container, SWT.NONE);
		previewWrapper.setLayout(new GridLayout());
		previewWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showPreviewDataButton = new Button(previewWrapper, SWT.CHECK);
		showPreviewDataButton.setText(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.button.showPreviewData.text")); //$NON-NLS-1$
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

		new Label(fileLocationComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.label.saveLocation.text")); //$NON-NLS-1$

		fileText = new Text(fileLocationComposite, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gridData);
		fileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});

		fileText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				getContainer().updateButtons();
			}
		});

		fileText.setFocus();
		
		Button browseButton = new Button(fileLocationComposite, SWT.PUSH);
		browseButton.setText(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.button.browse.text")); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.NULL);
				String result = dialog.open();
				fileText.setText(result == null? "": result); //$NON-NLS-1$
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
			setErrorMessage(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.errorMessage.noSeparator")); //$NON-NLS-1$
		}

		if (fileText.getText() == null || fileText.getText().equals("")) { //$NON-NLS-1$
			result = false;
			setErrorMessage(Messages.getString("org.nightlabs.eclipse.ui.control.export.wizard.ExportOptionWizardPage.errorMessage.noLocation")); //$NON-NLS-1$
		}

		return result;
	}

	@Override
	public boolean canFlipToNextPage() {
		boolean result = true;
		if (showPreviewDataButton.getSelection() == false || fileText.getText() == null || fileText.getText().equals("")) { //$NON-NLS-1$
			result = false;
		}

		return result;
	}

	public Character getSeparator() {
		if (separatorCombo.getText() == null || separatorCombo.getText().equals("")) //$NON-NLS-1$
			return DEFAULT_SEPARATORS[0];
		return separatorCombo.getText().toCharArray()[0];
	}

	public String getFilePath() {
		return fileText.getText();
	}
}