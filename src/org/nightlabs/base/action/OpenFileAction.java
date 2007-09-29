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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.config.RecentFileCfMod;
import org.nightlabs.base.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.editor.EditorFileFilterRegistry;
import org.nightlabs.base.resource.Messages;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

public class OpenFileAction 
extends Action
{
	public static final String ID = OpenFileAction.class.getName();
	public static final String FILTER_ALL = "*"; //$NON-NLS-1$
	
	protected RecentFileCfMod historyConfig;
		
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	};
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	};
	
	public OpenFileAction() {
		super();
		init();
	}
	
	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.base.action.OpenFileAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.base.action.OpenFileAction.toolTipText")); //$NON-NLS-1$

		try {
			historyConfig = (RecentFileCfMod) Config.sharedInstance().createConfigModule(RecentFileCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}	
	
	protected List<String> getRecentFileNames() {
		if (historyConfig != null)
			return historyConfig.getRecentFileNames();
		
		return null;
	}
	
	private File lastDirectory = null; 
	public void run() 
	{
		FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
		String[] fileExtensions = getFileExtensions(editorRegistry);
		dialog.setFilterExtensions(fileExtensions);
		if (lastDirectory != null)
			dialog.setFilterPath(lastDirectory.getAbsolutePath());
		String fullFileName = dialog.open();		
		// Cancel pressed
		if (fullFileName == null)
			return;
		
		File file = new File(fullFileName);
		if (!file.exists()) {
			return;
		}
		
		if (file.isDirectory())
			lastDirectory = file;
		else
			lastDirectory = file.getParentFile();
		
		try {
			boolean foundEditor = Editor2PerspectiveRegistry.sharedInstance().openFile(file);
			if (foundEditor)
				addFileToHistory(fullFileName);				
		} catch (PartInitException e) {
			Logger.getLogger(OpenFileAction.class).error("Cannot open file: " + fullFileName, e); //$NON-NLS-1$
			RCPUtil.showErrorDialog(
					String.format(Messages.getString("org.nightlabs.base.action.OpenFileAction.errorOpeningFileFailed"), new Object[] { fullFileName }) //$NON-NLS-1$
			);
		}
	}
	
	public static final String HISTORY_FILE_ADDED = "history file added"; //$NON-NLS-1$
	protected void addFileToHistory(String fileName) 
	{
		if (getRecentFileNames() != null) 
		{
			// add file only if it is not already contained
			if (!getRecentFileNames().contains(fileName)) 
			{				
				getRecentFileNames().add(fileName);
				pcs.firePropertyChange(HISTORY_FILE_ADDED, null, fileName);
			}
			historyConfig.setChanged(true);			
		}
	}
		
	protected String[] getFileExtensions(IEditorRegistry editorRegistry) 
	{
		IFileEditorMapping[] mappings = editorRegistry.getFileEditorMappings();
		List<String> extensions = new ArrayList<String>(mappings.length);		
		for (int mapCount=0; mapCount<mappings.length; mapCount++) {
			IFileEditorMapping map = mappings[mapCount];
			String extension = map.getExtension();			
			IEditorDescriptor[] editorDescritors = map.getEditors();
			for (int descCount=0; descCount<editorDescritors.length; descCount++) {
				IEditorDescriptor descriptor = editorDescritors[descCount];
				String id = descriptor.getId();					
				if (EditorFileFilterRegistry.sharedInstance().doesMatchEditorID(id)) {
					if (isValid(extension)) {
						extensions.add(extension);
					}											
				}
			}
		}
		if (isUseFilterAll()) {
			extensions.add(0, FILTER_ALL);
		}
		String[] fileExtensions = new String[extensions.size()];
		for (int j=0; j<extensions.size(); j++) {
			String fileExtension = (String) extensions.get(j);
			fileExtensions[j] = concatExtension(fileExtension); 
		}
		return fileExtensions;
	}
	
	protected boolean isValid(String s) 
	{
		if (s == null)
			return false;
		if (s.equals("") || s.equals(" ")) //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		
		return true;
	}
	
	protected String concatExtension(String s) {
		return "*." + s; //$NON-NLS-1$
	}
		
	protected boolean useFilterAll = true;
	public boolean isUseFilterAll() {
		return useFilterAll;
	}
	public void setUseFilterAll(boolean useFilterAll) {
		this.useFilterAll = useFilterAll;
	}
		
}
