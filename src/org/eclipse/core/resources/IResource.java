package org.eclipse.core.resources;

import org.eclipse.core.runtime.IPath;

public interface IResource {
	public static final int FILE = 0;
	
	String getName();
	String getFileExtension();
	IFile getFile(IPath path);
	
	IPath getFullPath();
	
	int getType();
	
	IPath getProjectRelativePath();
	
	
	boolean isAccessible();
	
	IResource getParent();
	
	IWorkspace getWorkspace();
}
