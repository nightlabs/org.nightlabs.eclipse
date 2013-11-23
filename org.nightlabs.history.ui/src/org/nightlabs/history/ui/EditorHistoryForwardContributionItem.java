package org.nightlabs.history.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.history.ui.resource.Messages;

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
	 * @see org.nightlabs.history.ui.AbstractEditorHistoryContributionItem#createImage()
	 */
	@Override
	protected Image createImage() {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		return sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD).createImage();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.AbstractEditorHistoryContributionItem#getText()
	 */
	@Override
	protected String getText() {
		return Messages.getString("org.nightlabs.history.ui.EditorHistoryForwardContributionItem.text"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.AbstractEditorHistoryContributionItem#getToolTip()
	 */
	@Override
	protected String getToolTip() {
		return Messages.getString("org.nightlabs.history.ui.EditorHistoryForwardContributionItem.tooltip"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.AbstractEditorHistoryContributionItem#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return EditorHistory.sharedInstance().canForward();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.history.ui.AbstractEditorHistoryContributionItem#run()
	 */
	@Override
	protected void run() {
		EditorHistory.sharedInstance().historyForward();
		update();
	}

}
