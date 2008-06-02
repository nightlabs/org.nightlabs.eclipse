/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
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
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;
import org.nightlabs.htmlcontent.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
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
		setMessage(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.SelectFileDialog.dialogMessage")); //$NON-NLS-1$
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
				actions.add(0, new Action(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.SelectFileDialog.useFileActionText")) { //$NON-NLS-1$
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
