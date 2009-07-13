package org.nightlabs.history;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @deprecated not used anymore
 */
@Deprecated
public class EditorHistoryForwardContributionItem
extends AbstractEditorHistoryContributionItem
{
	public static final String ID = EditorHistoryForwardContributionItem.class.getName();

	/**
	 * @param id
	 */
	protected EditorHistoryForwardContributionItem() {
		super(ID);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.AbstractEditorHistoryContributionItem#createImage()
	 */
	@Override
	protected Image createImage() {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		return sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD).createImage();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.AbstractEditorHistoryContributionItem#getText()
	 */
	@Override
	protected String getText() {
		return "EditorHistory Forward";
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.AbstractEditorHistoryContributionItem#getToolTip()
	 */
	@Override
	protected String getToolTip() {
		return "EditorHistory Forward";
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.AbstractEditorHistoryContributionItem#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return EditorHistory.sharedInstance().canForward();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.AbstractEditorHistoryContributionItem#run()
	 */
	@Override
	protected void run() {
		EditorHistory.sharedInstance().historyForward();
		update();
	}

}
