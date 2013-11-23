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
package org.nightlabs.eclipse.ui.fckeditor.file.image;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.nightlabs.eclipse.ui.fckeditor.Activator;
import org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard;
import org.nightlabs.eclipse.ui.fckeditor.resource.Messages;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class ContentImageFileWizard extends Wizard implements IContentFileWizard
{
	private ContentImageFileBasePage basePage;
	private ContentImageFileCropPage cropPage;

	/**
	 * Create a new ContentImageFileWizard instance.
	 */
	public ContentImageFileWizard()
	{
		basePage = new ContentImageFileBasePage();
		addPage(basePage);
		cropPage = new ContentImageFileCropPage();
		addPage(cropPage);
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					monitor.beginTask(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ContentImageFileWizard.preparingTaskName"), 6); //$NON-NLS-1$
					basePage.performFinish(new SubProgressMonitor(monitor, 1));
					cropPage.performFinish(new SubProgressMonitor(monitor, 5));
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			String msg = String.format(Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ContentImageFileWizard.prepareError"), e.getLocalizedMessage()); //$NON-NLS-1$
			Activator.err(msg, e);
			MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.eclipse.ui.fckeditor.file.image.ContentImageFileWizard.errorTitle"), msg); //$NON-NLS-1$
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getData()
	 */
	@Override
	public byte[] getData()
	{
		return cropPage.getBinaryImageData();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return basePage.getUserFileDescription();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getMimeType()
	 */
	@Override
	public String getMimeType()
	{
		return cropPage.getMimeType();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#getName()
	 */
	@Override
	public String getName()
	{
		return basePage.getUserFileName();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.fckeditor.file.IContentFileWizard#setSourceFile(java.io.File, java.lang.String)
	 */
	@Override
	public void setSourceFile(File file, String mimeType)
	{
		basePage.setSourceFile(file);
		cropPage.setSourceFile(file);
	}
}
