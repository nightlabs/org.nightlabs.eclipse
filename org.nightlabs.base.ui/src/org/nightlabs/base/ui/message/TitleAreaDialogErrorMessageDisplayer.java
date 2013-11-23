package org.nightlabs.base.ui.message;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;

/**
 * Implementation of {@link IErrorMessageDisplayer} which displayes/delegates the messages
 * in a {@link TitleAreaDialog}.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class TitleAreaDialogErrorMessageDisplayer
implements IErrorMessageDisplayer
{
	// the dialog to display the messages
	private TitleAreaDialog dialog;

	/**
	 * Creates a TitleAreaDialogErrorMessageDisplayer
	 * @param dialog the {@link TitleAreaDialog} to display the messages
	 */
	public TitleAreaDialogErrorMessageDisplayer(TitleAreaDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * @see org.nightlabs.base.ui.message.IErrorMessageDisplayer#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String errorMessage) {
		setMessage(errorMessage, IMessageProvider.ERROR);
	}

	/**
	 * @see org.nightlabs.base.ui.message.IMessageDisplayer#setMessage(java.lang.String, int)
	 */
	@Override
	public void setMessage(String message, int type)
	{
		if (!dialog.getShell().isDisposed())
			dialog.setMessage(message, type);
	}

	/**
	 * @see org.nightlabs.base.ui.message.IMessageDisplayer#setMessage(java.lang.String, org.nightlabs.base.ui.message.MessageType)
	 */
	@Override
	public void setMessage(String message, MessageType type) {
		setMessage(message, type.ordinal());
	}

}
