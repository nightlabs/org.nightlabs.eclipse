package org.eclipse.core.resources;

public interface IWorkspace {
	void addResourceChangeListener(IResourceChangeListener listener, Object when);
	void removeResourceChangeListener(IResourceChangeListener listener);
	
	IWorkspaceRoot getRoot();
}
