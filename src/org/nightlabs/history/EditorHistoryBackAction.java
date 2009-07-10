package org.nightlabs.history;

import org.eclipse.jface.action.Action;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class EditorHistoryBackAction extends Action {

	public static final String ID = EditorHistoryBackAction.class.getName();

	/**
	 *
	 */
	public EditorHistoryBackAction() {
		super();
		setId(ID);
		setText("History Back");
		setToolTipText("History Back");
//		ISharedImages sharedImages = RCPUtil.getActiveWorkbenchWindow().getWorkbench().getSharedImages();
//		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		EditorHistory.sharedInstance().historyBack();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.jface.action.Action#isEnabled()
//	 */
//	@Override
//	public boolean isEnabled() {
//		return EditorHistory.sharedInstance().canBackward();
//	}

}
