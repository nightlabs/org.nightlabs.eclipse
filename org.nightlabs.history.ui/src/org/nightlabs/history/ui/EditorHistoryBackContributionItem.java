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
public class EditorHistoryBackContributionItem
extends AbstractEditorHistoryContributionItem
{
	public static final String ID = EditorHistoryBackContributionItem.class.getName();

	/**
	 *
	 */
	protected EditorHistoryBackContributionItem() {
		super(ID);
	}

	@Override
	protected String getText() {
		return Messages.getString("org.nightlabs.history.ui.EditorHistoryBackContributionItem.text"); //$NON-NLS-1$
	}

	@Override
	protected String getToolTip() {
		return Messages.getString("org.nightlabs.history.ui.EditorHistoryBackContributionItem.tooltip"); //$NON-NLS-1$
	}

	@Override
	protected void run() {
		EditorHistory.sharedInstance().historyBack();
		update();
	}

	@Override
	protected Image createImage()
	{
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		return sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK).createImage();
	}

	@Override
	public boolean isEnabled() {
		return EditorHistory.sharedInstance().canBackward();
	}

}
