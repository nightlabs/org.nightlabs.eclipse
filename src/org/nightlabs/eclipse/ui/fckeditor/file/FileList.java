package org.nightlabs.eclipse.ui.fckeditor.file;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class FileList extends Composite
{
	private List<IFCKEditorContentFile> files;
	private IImageProvider imageProvider;

	public FileList(Composite parent, int style, List<IFCKEditorContentFile> files, IImageProvider imageProvider)
	{
		super(parent, style);
		this.files = files;
		this.imageProvider = imageProvider;
		createContents();
	}

	protected List<IAction> getActions(final IFCKEditorContentFile file)
	{
		final String extension = ContentTypeUtil.getFileExtension(file);
		List<IAction> actions = new ArrayList<IAction>();
		actions.add(new Action("&Open File...") {
			@Override
			public boolean isEnabled()
			{
				return Desktop.isDesktopSupported() && extension != null;
			}

			@Override
			public void runWithEvent(Event event)
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
				} catch(Throwable ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Error", "Error launching application: "+ex.getLocalizedMessage());
				}
			}
		});
		return actions;
	}

	protected void createContents()
	{
		setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);

		for (final IFCKEditorContentFile file : files) {
			FileListEntry fileListEntry = new FileListEntry(this, SWT.NONE, file, imageProvider, getActions(file));
			GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			fileListEntry.setLayoutData(gridData);
		}
	}

	public void setFiles(List<IFCKEditorContentFile> files)
	{
		for(Control child : getChildren())
			child.dispose();
		this.files = files;
		createContents();
		layout(true, true);
	}
}
