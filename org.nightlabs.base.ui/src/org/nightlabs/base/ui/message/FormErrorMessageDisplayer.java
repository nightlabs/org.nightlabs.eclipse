package org.nightlabs.base.ui.message;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.ui.forms.widgets.Form;

/**
 * Implementation of {@link IErrorMessageDisplayer} which displayes/delegates the messages
 * in a {@link Form}.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class FormErrorMessageDisplayer
implements IErrorMessageDisplayer
{
	// The form to display the messages
	private Form form;

	/**
	 *
	 * @param form
	 */
	public FormErrorMessageDisplayer(Form form) {
		this.form = form;
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
	public void setMessage(String message, int type) {
		if (!form.isDisposed())
			form.setMessage(message, type);
	}

	/**
	 * @see org.nightlabs.base.ui.message.IMessageDisplayer#setMessage(java.lang.String, org.nightlabs.base.ui.message.MessageType)
	 */
	@Override
	public void setMessage(String message, MessageType type) {
		setMessage(message, type.ordinal());
	}

}
