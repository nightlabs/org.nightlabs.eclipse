// $Id$
package org.nightlabs.eclipse.ui.fckeditor.file;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class SelectFileDialog extends FileListDialog
{
	private IFCKEditorContentFile selectedFile;

	/**
	 * Create a new SelectFileDialog instance.
	 */
	public SelectFileDialog(Shell parent, List<IFCKEditorContentFile> files, IImageProvider imageProvider)
	{
		super(parent, files, imageProvider);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Control c = super.createDialogArea(parent);
		setMessage("Select a file to use in the editor by clicking 'Use File in Editor'");
		return c;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.FileListDialog#createFileList(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected FileList createFileList(Composite parent)
	{
		return new FileList(parent, SWT.NONE, getFiles(), getImageProvider())
		{
			@Override
			protected List<IAction> getActions(final IFCKEditorContentFile file)
			{
				List<IAction> actions = super.getActions(file);
				actions.add(0, new Action("&Use File in Editor") {
					@Override
					public void runWithEvent(Event event)
					{
						//System.out.println("Selected file: "+file.getName());
						selectedFile = file;
						okPressed();
					}
				});
				//System.out.println("Added action");
				return actions;
			}
		};
	}

	/**
	 * Get the selectedFile.
	 * @return the selectedFile
	 */
	public IFCKEditorContentFile getSelectedFile()
	{
		return selectedFile;
	}
}
