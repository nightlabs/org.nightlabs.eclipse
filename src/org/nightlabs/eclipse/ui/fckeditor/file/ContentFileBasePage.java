// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class ContentFileBasePage extends WizardPage
{
	private Text fileText;
//	private Button browseButton;
	private Text nameText;
	private Text descriptionText;
	private File sourceFile;

	private String userFileName;
	private String description;

	/**
	 * Create a new ContentFileBasePage instance.
	 */
	public ContentFileBasePage()
	{
		this(ContentFileBasePage.class.getName(), "File Settings", null);
	}

	/**
	 * Create a new ContentFileBasePage instance.
	 */
	public ContentFileBasePage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	public void setSourceFile(File sourceFile)
	{
		this.sourceFile = sourceFile;
		applySourceFile();
	}

	protected void applySourceFile()
	{
		if(sourceFile != null) {
			String filepath = sourceFile.getAbsolutePath();
			if(fileText != null)
				fileText.setText(filepath);
			if(nameText != null) {
				int idx = filepath.lastIndexOf(File.separatorChar);
				if(idx == -1)
					nameText.setText(filepath);
				else
					nameText.setText(filepath.substring(idx+1));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(gridData);

		Label l = new Label(composite, SWT.NONE);
		l.setText("File:");
		fileText = new Text(composite, SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fileText.setEnabled(false);

		l = new Label(composite, SWT.NONE);
		l.setText("Name:");
		nameText = new Text(composite, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		l = new Label(composite, SWT.NONE);
		l.setText("Description:");
		l.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		descriptionText = new Text(composite, SWT.BORDER | SWT.MULTI);
		descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		createCustomControls(composite);

		applySourceFile();

		setControl(composite);

		nameText.addModifyListener(new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			@Override
			public void modifyText(ModifyEvent e)
			{
				getWizard().getContainer().updateButtons();
			}
		});
	}

	protected void createCustomControls(Composite parent)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete()
	{
		File f = new File(fileText.getText());
		if(!f.isFile()) {
			setErrorMessage("Please choose a file.");
			return false;
		}
		if(nameText.getText().isEmpty()) {
			setErrorMessage("Please enter a name.");
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	public String getUserFileDescription()
	{
		return description;
	}

	public String getUserFileName()
	{
		return userFileName;
	}

	public boolean performFinish(IProgressMonitor monitor)
	{
		monitor.beginTask("Preparing file", 1);
		monitor.subTask("Preparing file");

		this.userFileName = nameText.getText();
		this.description = descriptionText.getText();

		monitor.worked(1);
		monitor.done();
		return true;
	}

	/**
	 * Get the sourceFile.
	 * @return the sourceFile
	 */
	public File getSourceFile()
	{
		return sourceFile;
	}
}
