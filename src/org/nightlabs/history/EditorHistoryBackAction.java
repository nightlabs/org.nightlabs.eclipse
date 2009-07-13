package org.nightlabs.history;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryBackAction extends Action {

	public static final String ID = EditorHistoryBackAction.class.getName();

	public EditorHistoryBackAction() {
		super();
		setId(ID);
		setText("History Back");
		setToolTipText("History Back");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
		setActionDefinitionId("org.eclipse.ui.navigate.backwardHistory");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EditorHistory.sharedInstance().historyBack();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return EditorHistory.sharedInstance().canBackward();
	}

}
