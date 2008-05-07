package org.nightlabs.eclipse.ui.fckeditor.file;


import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

public class FileListEntry extends Composite
{
	private IFCKEditorContentFile file;
	ImageProvider imageProvider;
	private Label imageLabel;

	public FileListEntry(Composite parent, int style, IFCKEditorContentFile file, ImageProvider imageProvider)
	{
		super(parent, style);
		this.file = file;
		this.imageProvider = imageProvider;
		createContents();
	}

	protected void createContents()
	{
		setBackground(getParent().getBackground());
		setLayout(new GridLayout(3, false));

		Composite imageWrapper = new Composite(this, SWT.NONE);
		imageWrapper.setBackground(imageWrapper.getParent().getBackground());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		imageWrapper.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		gridData.widthHint = imageProvider.getThumbnailSize();
		gridData.heightHint = imageProvider.getThumbnailSize();
		imageWrapper.setLayoutData(gridData);

		imageLabel = new Label(imageWrapper, SWT.NONE);
		imageLabel.setBackground(imageLabel.getParent().getBackground());
		imageLabel.setImage(imageProvider.getImage(file));
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
//		gridData.widthHint = imageProvider.getThumbnailSize();
//		gridData.heightHint = imageProvider.getThumbnailSize();
		imageLabel.setLayoutData(gridData);

		Composite right = new Composite(this, SWT.NONE);
		right.setBackground(right.getParent().getBackground());
		right.setLayout(new GridLayout(2, false));
		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		right.setLayoutData(gridData);

		createEntry(right, "Name:", file.getName());
		createEntry(right, "Type:", file.getContentType());
		createEntry(right, "Size:", String.format("%d bytes", file.getData().length));
		createEntry(right, "Noch was:", "Bla bla bla bla");

//		Label nameLabelLabel = new Label(right, SWT.NONE);
//		nameLabelLabel.setBackground(right.getParent().getBackground());
//		nameLabelLabel.setText("Name:");
//		Label nameLabel = new Label(right, SWT.NONE);
//		nameLabel.setBackground(right.getParent().getBackground());
//		nameLabel.setText(file.getName());
//		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//		nameLabel.setLayoutData(gridData);
//
//		Label contentTypeLabelLabel = new Label(right, SWT.NONE);
//		contentTypeLabelLabel.setBackground(right.getParent().getBackground());
//		contentTypeLabelLabel.setText("Type:");
//		Label contentTypeLabel = new Label(right, SWT.NONE);
//		contentTypeLabel.setBackground(right.getParent().getBackground());
//		contentTypeLabel.setText(file.getContentType());
//		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//		contentTypeLabel.setLayoutData(gridData);
//
//		Label sizeLabelLabel = new Label(right, SWT.NONE);
//		sizeLabelLabel.setBackground(right.getParent().getBackground());
//		sizeLabelLabel.setText("Size:");
//		Label sizeLabel = new Label(right, SWT.NONE);
//		sizeLabel.setBackground(right.getParent().getBackground());
//		sizeLabel.setText(String.format("%d bytes", file.getData().length));
//		gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
//		sizeLabel.setLayoutData(gridData);

		Composite buttonWrapper = new Composite(this, SWT.NONE);
		buttonWrapper.setBackground(buttonWrapper.getParent().getBackground());
		buttonWrapper.setLayout(new GridLayout(2, false));
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		buttonWrapper.setLayoutData(gridData);

		Button openButton = new Button(buttonWrapper, SWT.PUSH);
		openButton.setText("&Open File...");
		final String extension = getFileExtension(file);
		if(extension == null || !Desktop.isDesktopSupported()) {
			openButton.setEnabled(false);
		} else {
			openButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					try {
						final File tmpFile = File.createTempFile(file.getName(), extension);
						tmpFile.deleteOnExit();
						FileOutputStream out = new FileOutputStream(tmpFile);
						try {
							out.write(file.getData());
						} finally {
							out.close();
						}
						Desktop.getDesktop().open(tmpFile);
					} catch(IOException ex) {
						MessageDialog.openError(getShell(), "Error", "Error launching application: "+ex.getLocalizedMessage());
					}
				}
			});
		}
	}

	private static String getFileExtension(IFCKEditorContentFile file)
	{
		if("application/pdf".equals(file.getContentType()))
			return ".pdf";
		else if("image/jpeg".equals(file.getContentType()))
			return ".jpg";
		else if("image/gif".equals(file.getContentType()))
			return ".gif";
		else if("image/png".equals(file.getContentType()))
			return ".png";
		else if("text/html".equals(file.getContentType()))
			return ".html";
		return null;
	}

	private void createEntry(Composite parent, String label, String value)
	{
		Label labelLabel = new Label(parent, SWT.NONE);
		labelLabel.setBackground(parent.getParent().getBackground());
		labelLabel.setText(label);
		Label valueLabel = new Label(parent, SWT.NONE);
		valueLabel.setBackground(parent.getParent().getBackground());
		valueLabel.setText(value);
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		valueLabel.setLayoutData(gridData);
	}
}