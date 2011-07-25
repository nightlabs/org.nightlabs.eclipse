package org.eclipse.core.resources;

import java.io.File;

import org.nightlabs.eclipse.compatibility.NotAvailableInRAPException;

public class CompatibleResources {
	public static File getWorkspaceRootLocationPath() {
		throw new NotAvailableInRAPException();
		//was: return new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
	}
	
	public static File getResourceAsFile(IResource resource) {
		throw new NotAvailableInRAPException();
		//was: return new File(resource.getWorkspace().getRoot().getLocation().toFile(), resource.getFullPath().toOSString());
	}
	
	public static IProject [] getProjects() {
		throw new NotAvailableInRAPException();

		//RCP: ResourcesPlugin.getWorkspace().getRoot().getProjects()
	}
	
	public static IWorkspace getWorkspace() {
		throw new NotAvailableInRAPException();
		
		//RCP: ResourcesPlugin.getWorkspace()
	}
	
}
