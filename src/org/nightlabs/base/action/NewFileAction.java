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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.util.IOUtil;

public class NewFileAction 
extends Action 
implements INewFileAction
{
	public final String ID = NewFileAction.class.getName() + " " + hashCode(); //$NON-NLS-1$
	// TODO isn't the above ID wrong? Shouldn't the ID be a real static id? And static, too? If it's meant to be subclassed, it should do sth. like this.getClass().getName()

	public NewFileAction() 
	{
		super();
		init();
	}

	protected void init() {
		setId(ID);
	}		
		
	protected String fileExtension;
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	protected int fileCount = 0;
	protected int nextFileCount() {
		return ++fileCount;
	}
	
	protected String defaultFileName = Messages.getString("org.nightlabs.base.action.NewFileAction.defaultFileName"); //$NON-NLS-1$
	protected String defaultPath = ""; //$NON-NLS-1$

	protected String getDefaultPath() 
	{
    if (defaultPath.equals("")) { //$NON-NLS-1$
//    	String tmpDir = System.getProperty("java.io.tmpdir");
//    	defaultPath = tmpDir;
    	defaultPath = IOUtil.getTempDir().getAbsolutePath();
    }
    return defaultPath;
	}

	public void run() 
	{		
		nextFileCount();		
		File file = createFile(fileExtension);
		try {			
			Editor2PerspectiveRegistry.sharedInstance().openFile(file, false);
		} catch (PartInitException e) {			
			throw new RuntimeException(e);
		} 
	}
		 	
	protected File createFile(String fileExtension) 
	{
		String fileName = defaultFileName + fileCount + "." + fileExtension; //$NON-NLS-1$
		return new File(getDefaultPath(), fileName);		
	}	
}
