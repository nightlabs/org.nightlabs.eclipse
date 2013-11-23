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
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;
import org.nightlabs.htmlcontent.ContentTypeUtil;
import org.nightlabs.htmlcontent.IFCKEditorContentFile;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
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
		actions.add(new Action(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileList.openFileActionText")) { //$NON-NLS-1$
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
					String msg = String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileList.appLaunchError"), ex.getLocalizedMessage()); //$NON-NLS-1$
					Activator.err(msg, ex);
					MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.FileList.errorTitle"), msg); //$NON-NLS-1$
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
