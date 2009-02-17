/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.editor2d.ui;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.util.IOUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractNewEditor2DWizard extends Wizard implements INewWizard {

	protected String defaultFileName = Messages.getString("org.nightlabs.base.ui.action.NewFileAction.defaultFileName"); //$NON-NLS-1$
	protected String defaultPath = ""; //$NON-NLS-1$
//	protected String fileExtension;
	protected int fileCount = 0;

	public AbstractNewEditor2DWizard() {
		super();
	}

	public abstract String getFileExtension();	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() 
	{
		nextFileCount();
		File file = createFile(getFileExtension());
		try {
			Editor2PerspectiveRegistry.sharedInstance().openFile(file, false);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	protected int nextFileCount() {
		return ++fileCount;
	}

	protected String getDefaultPath()
	{
		if (defaultPath.equals("")) { //$NON-NLS-1$
			defaultPath = IOUtil.getTempDir().getAbsolutePath();
		}
		return defaultPath;
	}

	protected File createFile(String fileExtension)
	{
		String fileName = defaultFileName + fileCount + "." + fileExtension; //$NON-NLS-1$
		return new File(getDefaultPath(), fileName);
	}	
}
