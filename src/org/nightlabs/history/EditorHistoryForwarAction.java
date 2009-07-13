package org.nightlabs.history;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryForwarAction
extends Action
{
	public static final String ID = EditorHistoryForwarAction.class.getName();

	/**
	 *
	 */
	public EditorHistoryForwarAction() {
		super();
		setId(ID);
		setText("History Forward");
		setToolTipText("History Forward");
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
		setActionDefinitionId("org.eclipse.ui.navigate.forwardHistory");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EditorHistory.sharedInstance().historyForward();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return EditorHistory.sharedInstance().canForward();
	}

}
