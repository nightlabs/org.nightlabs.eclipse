/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.action;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;

public class ReOpenFileAction 
extends Action
{
	public static final String ID = ReOpenFileAction.class.getName();
	private static final Logger logger = Logger.getLogger(ReOpenFileAction.class);

	protected String fileName;

	public ReOpenFileAction(String fileName) 
	{
		if (fileName == null)
			throw new IllegalArgumentException("Param fileName must not be null!"); //$NON-NLS-1$
		
		this.fileName = fileName;
		init();
	}

	protected void init() 
	{
		setId(ID + "-" + fileName + fileName.hashCode()); //$NON-NLS-1$
		setText(fileName);
		setToolTipText(fileName);
	}

	public String getFileName() {
		return fileName;
	}

	public void run() 
	{
		try
		{
			File file = new File(fileName);
			if (file.exists())						 
				Editor2PerspectiveRegistry.sharedInstance().openFile(file);
		}
		catch (PartInitException e) 
		{
			logger.error("Opening file failed: " + fileName, e);	 //$NON-NLS-1$
			RCPUtil.showErrorDialog(
					String.format(Messages.getString("org.nightlabs.base.action.ReOpenFileAction.errorOpeningFileFailed"), new Object[] { fileName }) //$NON-NLS-1$
			);
		}
	}

}
