/**
 *
 */
package org.nightlabs.eclipse.ui.control.export.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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
	private final String[] DEFAULT_SEPERATORS = new String[]{",",";",":"};

	//UI
	private Combo seperatorCombo;

	private Button showControl;
	private Button showPreviewData;

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
		seperatorCombo.setItems(DEFAULT_SEPERATORS);
		seperatorCombo.setTextLimit(1);
		GridData gridData = new GridData();
		seperatorCombo.setLayoutData(gridData);

		showControl = new Button(container, SWT.CHECK);
		showControl.setSelection(true);
		showControl.setText("Show selected control");

		showPreviewData = new Button(container, SWT.CHECK);
		showControl.setSelection(true);
		showPreviewData.setText("Show exported preview data");

		Composite fileLocationComposite = new Composite(container, SWT.NULL);
		final GridLayout gridLayout2 = new GridLayout(3, false);
		fileLocationComposite.setLayout(gridLayout2);
		fileLocationComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(fileLocationComposite, SWT.NONE).setText("Save Location: ");

		fileText = new Text(fileLocationComposite, SWT.SINGLE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gridData);

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

	public char getSeperator() {
		return seperatorCombo.getText().toCharArray()[0];
	}

	public String getFilePath() {
		return filePath;
	}
}