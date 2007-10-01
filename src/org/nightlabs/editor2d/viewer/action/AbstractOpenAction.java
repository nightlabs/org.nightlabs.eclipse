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

package org.nightlabs.editor2d.viewer.action;

import java.io.FileInputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.io.IOFilterRegistry;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.AbstractViewerDialog;
import org.nightlabs.editor2d.viewer.ViewerPlugin;
import org.nightlabs.editor2d.viewer.resource.Messages;
import org.nightlabs.io.IOFilter;
import org.nightlabs.io.IOFilterMan;
import org.nightlabs.util.IOUtil;

public abstract class AbstractOpenAction 
extends Action 
{
	public static final String ID = AbstractOpenAction.class.getName(); 
	
	public AbstractOpenAction() 
	{
		super();
		init();
	}
	
	protected void init() 
	{
		setId(ID);
		setText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.AbstractOpenAction.text"))); //$NON-NLS-1$
		setToolTipText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.AbstractOpenAction.tooltip"))); //$NON-NLS-1$
	}
	
	public void run() 
	{
		FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell());
		IOFilterMan ioFilterMan = IOFilterRegistry.sharedInstance().getIOFilterMan();
//		String[] fileExtensions = ioFilterMan.getAvailableFileExtensionsAsStrings(true);
		String[] fileExtensions = ioFilterMan.getReadFileExtensions(true);
		fileDialog.setFilterExtensions(fileExtensions);
		String fileName = fileDialog.open();
		if (fileName != null) {
			String fileExtension = IOUtil.getFileExtension(fileName);
			if (fileExtension != null) {
				IOFilter ioFilter = ioFilterMan.getIOFilter(fileExtension);
				if (ioFilter != null) {
					DrawComponent dc;
					try {
						dc = (DrawComponent) ioFilter.read(new FileInputStream(fileName));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					AbstractViewerDialog dialog = initViewerDialog(Display.getCurrent().getActiveShell(), dc);					
					dialog.open();					
				}				
			}
		}		 
	}
	
	protected abstract AbstractViewerDialog initViewerDialog(Shell shell, DrawComponent dc);
}
