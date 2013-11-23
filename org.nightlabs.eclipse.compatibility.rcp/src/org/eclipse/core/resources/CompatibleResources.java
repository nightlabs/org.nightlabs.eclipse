package org.eclipse.core.resources;

import java.io.File;

public class CompatibleResources {
	public static File getWorkspaceRootLocationPath() {
		return new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
	}
	
	public static File getResourceAsFile(IResource resource) {
		return new File(resource.getWorkspace().getRoot().getLocation().toFile(), resource.getFullPath().toOSString());
	}
	
	public static IProject[] getProjects() {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}
	
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}	
}
