package org.eclipse.core.resources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.nightlabs.eclipse.compatibility.NotAvailableInRAPException;

public class ProjectScope implements IScopeContext {
	public ProjectScope(IProject project) {
		throw new NotAvailableInRAPException();
	}

	@Override
	public IPath getLocation() {
		throw new NotAvailableInRAPException();
	}

	@Override
	public String getName() {
		throw new NotAvailableInRAPException();
	}

	@Override
	public IEclipsePreferences getNode(String qualifier) {
		throw new NotAvailableInRAPException();
	}
}
