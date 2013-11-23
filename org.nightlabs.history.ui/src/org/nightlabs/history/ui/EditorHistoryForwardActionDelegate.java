package org.nightlabs.history.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryForwardActionDelegate implements IWorkbenchWindowActionDelegate {

	private IAction action;

	private IEditorHistoryChangedListener listener = new IEditorHistoryChangedListener(){
		@Override
		public void editorHistoryChanged(EditorHistoryChangedEvent event) {
			if (action != null) {
				action.setEnabled(EditorHistory.sharedInstance().canForward());
			}
		}
	};

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose() {
		EditorHistory.sharedInstance().removeEventHistoryListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void init(IWorkbenchWindow window) {
		EditorHistory.sharedInstance().addEventHistoryListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		this.action = action;
		EditorHistory.sharedInstance().historyForward();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
		action.setEnabled(EditorHistory.sharedInstance().canForward());
	}

}
