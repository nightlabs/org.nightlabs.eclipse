package org.nightlabs.base.ui.entity.editor.overview;

import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public interface IStatusFormPage extends IFormPage 
{
	/**
	 * 
	 * @return null if page is complete or the error message.
	 */
	String isComplete();
}
