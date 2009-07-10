package org.nightlabs.history;

import org.eclipse.jface.action.Action;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryNextAction extends Action {

	public static final String ID = EditorHistoryNextAction.class.getName();

	/**
	 *
	 */
	public EditorHistoryNextAction() {
		super();
		setId(ID);
		setText("History Forward");
		setToolTipText("History Forward");
//		ISharedImages sharedImages = RCPUtil.getActiveWorkbenchWindow().getWorkbench().getSharedImages();
//		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EditorHistory.sharedInstance().historyForward();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.action.Action#isEnabled()
//	 */
//	@Override
//	public boolean isEnabled() {
//		return EditorHistory.sharedInstance().canForward();
//	}

}
