package org.eclipse.ui;


public class CompatibleWorkbench {
	public static IKeyBindingService getKeyBindingService(IWorkbenchPartSite site) {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getSite().getKeyBindingService();
	}
	
	public static void showEditor(IWorkbenchPage page, IEditorReference editor) {
		page.showEditor(editor);
	}
	
	public static void hideEditor(IWorkbenchPage page, IEditorReference editor) {
		page.hideEditor(editor);
	}
	
	public static boolean restart() {
		return PlatformUI.getWorkbench().restart();
	}
}
