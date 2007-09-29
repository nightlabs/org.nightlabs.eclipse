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

package org.nightlabs.base.io;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

public class FileEditorInput
implements IEditorInput,
					 IPathEditorInput, IPersistableElement
{
	protected File file;

	public FileEditorInput(File f) 
	{
		if (f == null)
			throw new IllegalArgumentException("Param f must not be null!"); //$NON-NLS-1$
		
		this.file = f;
	}
	
	public boolean exists()
	{
		return file.exists();			
	}

	public File getFile() 
	{
		return file;
	}
	
  public String getName() 
  {
		return file.getName();
  }
  
	public ImageDescriptor getImageDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IPersistableElement getPersistable()
	{
		return this;
	}

	public String getToolTipText()
	{
//		return file.getAbsolutePath();		
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getAdapter(Class adapter)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public IPath getPath() 
	{
		try {
			String path = file.getCanonicalPath();
			return new Path(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int hashCode() 
	{
		return file.hashCode();
	}
	
	public boolean equals(Object o) 
	{
		if (o == this)
			return true;
		
		if (o instanceof FileEditorInput) {
			FileEditorInput input = (FileEditorInput) o;
			return file.equals(input.getFile());		
		}
		
		return false;
	}	
		
	protected boolean saved = true;
	public boolean isSaved() 
	{
		if (!exists())
			return false;
		
		return saved;		
	}
	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public String getFactoryId() {
		return FileEditorInputFactory.class.getName();
	}

	public void saveState(IMemento memento) {
		FileEditorInputFactory.storeFileEditorInput(memento, this);
	}
}
