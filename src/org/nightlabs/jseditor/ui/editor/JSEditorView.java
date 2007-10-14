package org.nightlabs.jseditor.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class JSEditorView extends ViewPart {
	/**
	 * The constructor.
	 */
	public JSEditorView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		/*JSEditorComposite c = */new JSEditorComposite(getViewSite().getWorkbenchWindow(), parent);
	}

//	private void showMessage(String message) {
//		MessageDialog.openInformation(
//			viewer.getControl().getShell(),
//			"Sample View",
//			message);
//	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
//		viewer.getControl().setFocus();
	}
}