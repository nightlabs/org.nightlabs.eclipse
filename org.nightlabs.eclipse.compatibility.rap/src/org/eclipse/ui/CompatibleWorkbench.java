package org.eclipse.ui;

import org.nightlabs.eclipse.compatibility.NotAvailableInRAPException;

public class CompatibleWorkbench {
	public static IKeyBindingService getKeyBindingService(IWorkbenchPartSite site) {
		return null;
	}
	
	public static void showEditor(IWorkbenchPage page, IEditorReference editor) {
		page.showEditor(editor);
	}
	
	public static void hideEditor(IWorkbenchPage page, IEditorReference editor) {
		page.hideEditor(editor);
	}
	
	public static boolean restart() {
		throw new NotAvailableInRAPException();
		//was: PlatformUI.getWorkbench().restart()
	}
}
