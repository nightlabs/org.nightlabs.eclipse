package org.nightlabs.eclipse.ui.fckeditor.file;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FileList extends Composite
{
	private List<IFCKEditorContentFile> files;
	private ImageProvider imageProvider;

	public FileList(Composite parent, int style, List<IFCKEditorContentFile> files)
	{
		super(parent, style);
		this.files = files;
		imageProvider = new ImageProvider(getShell().getDisplay());
		imageProvider.setThumbnailSize(128);
		createContents();
	}

	protected void createContents()
	{
		setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		for (IFCKEditorContentFile file : files) {
			FileListEntry fileListEntry = new FileListEntry(this, SWT.NONE, file, imageProvider);
			GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			fileListEntry.setLayoutData(gridData);
		}
	}

	@Override
	public void dispose()
	{
		super.dispose();
		imageProvider.dispose();
		imageProvider = null;
	}
}
