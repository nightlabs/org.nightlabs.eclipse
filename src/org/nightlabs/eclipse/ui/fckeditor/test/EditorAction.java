package org.nightlabs.eclipse.ui.fckeditor.test;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorContent;
import org.nightlabs.eclipse.ui.fckeditor.FCKEditorInput;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorContent;
import org.nightlabs.eclipse.ui.fckeditor.IFCKEditorInput;

public class EditorAction implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		IFCKEditorContent content = new FCKEditorContent();
		double rand = Math.random();
		content.setHtml("<p>Bla bla bla, <b>mein</b> Text</p><p>Hallo! "+rand+"</p>");
		IFCKEditorInput editorInput = new FCKEditorInput(content, "Bla bla "+rand);
		try {
			window.getActivePage().openEditor(editorInput, "org.nightlabs.eclipse.ui.fckeditor.FCKEditor");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction arg0, ISelection arg1) {
		// TODO Auto-generated method stub

	}

}
