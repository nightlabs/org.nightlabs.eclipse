package org.nightlabs.eclipse.ui.fckeditor.file;

import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.eclipse.ui.fckeditor.Activator;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class NewImageDialog extends TitleAreaDialog
{
	private Text fileText;
	private Button browseButton;
	private Text targetWidth;
	private Text targetHeight;

	/**
	 * Create a new NewImageDialog instance.
	 * @param parentShell
	 */
	public NewImageDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("New Image");
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		// create a composite with standard margins and spacing
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		applyDialogFont(composite);

		createFileInput(composite);
		createResizeArea(composite);

		return composite;
	}

	protected Control createFileInput(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		composite.setLayoutData(gridData);

		new Label(composite, SWT.NONE).setText("File:");
		fileText = new Text(composite, SWT.BORDER);
		fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(browseButton.getShell());
				fileDialog.setText("Open File");
				String filepath = fileDialog.open();
				if(filepath != null)
					fileText.setText(filepath);
			}
		});

		return composite;
	}

	protected Control createResizeArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(4, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gridData);

		Label targetSizeLabel = new Label(composite, SWT.NONE);
		targetSizeLabel.setText("Target size:");
		targetWidth = new Text(composite, SWT.BORDER);
		new Label(composite, SWT.NONE).setText("x");
		targetHeight = new Text(composite, SWT.BORDER);

		Label originalSizeLabel = new Label(composite, SWT.NONE);
		originalSizeLabel.setText("Original size:");
		Text originalWidth = new Text(composite, SWT.BORDER);
		originalWidth.setEnabled(false);
		new Label(composite, SWT.NONE).setText("x");
		Text originalHeight = new Text(composite, SWT.BORDER);
		originalHeight.setEnabled(false);

		createImageView(composite);

		return composite;
	}

	private Control createImageView(Composite composite)
	{
		ImageClippingArea imageView = new ImageClippingArea(composite, SWT.NONE);
		imageView.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		imageView.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 4;
		gridData.widthHint = 300;
		gridData.heightHint = 300;
		imageView.setLayoutData(gridData);

		// TODO
		try {
			Image img = new Image(getShell().getDisplay(), Activator.getDefault().getBundle().getResource("/icons/test/DSC00313.JPG").openStream());
			imageView.setSourceImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imageView;
	}
}
