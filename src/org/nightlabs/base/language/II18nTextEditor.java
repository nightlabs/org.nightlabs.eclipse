package org.nightlabs.base.language;

import org.eclipse.swt.events.ModifyListener;
import org.nightlabs.base.language.I18nTextEditor.EditMode;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;

public interface II18nTextEditor
{
	/**
	 * Add a {@link ModifyListener} to this editor. The listener
	 * will get triggered only when the text was actually modified by the user, 
	 * not when the edit changes its text.
	 * 
	 * @param l The {@link ModifyListener} to add.
	 */
	void addModifyListener(ModifyListener l);

	/**
	 * Remove the given modify listener.
	 * @param l The listener to remove.
	 */
	void removeModifyListener(ModifyListener l);

	/**
	 * In contrast to {@link #getI18nText()}, this method always returns the same instance
	 * as passed by {@link #setI18nText(I18nText)} before. In the {@link EditMode#DIRECT},
	 * this method is exactly identical to {@link #getI18nText()} 
	 *
	 * @return the original object as passed to {@link #setI18nText(I18nText)}
	 */
	I18nText getOriginal();

	/**
	 * This method first applies all modifications to the work-<code>I18nText</code> instance
	 * (which is either the original or a buffer, depending on the {@link EditMode}) and
	 * then returns it.
	 *  
	 * @return the {@link I18nText} buffer used by the editor, if {@link EditMode#BUFFERED}
	 *		is used, or the original object, if {@link EditMode#DIRECT} is used.
	 *
	 * @see #getOriginal()
	 */
	I18nText getI18nText();

	/**
	 * Copies all values from the buffer
	 * to the {@link I18nText} originally passed
	 * on the last call to {@link #setI18nText(I18nText)}.
	 */
	void copyToOriginal();

	/**
	 * Set the instance of {@link I18nText} that shall be edited by this editor.
	 * This method will call
	 * {@link #setEditMode(org.nightlabs.base.language.I18nTextEditor.EditMode)}
	 * and set {@link EditMode#DIRECT}, if the <code>i18nText</code> is an instance
	 * of {@link I18nTextBuffer}. For all other implementations of {@link I18nText},
	 * it will set {@link EditMode#BUFFERED}. If you don't like this "auto-sensing",
	 * you can call {@link #setI18nText(I18nText, org.nightlabs.base.language.I18nTextEditor.EditMode)}
	 * with a non-<code>null</code> <code>editMode</code>.
	 * 
	 * @param i18nText The {@link I18nText} that should be displayed and made editable.
	 *		Can be <code>null</code> (wich will render this editor read-only).
	 */
	void setI18nText(I18nText i18nText);

	/**
	 * @param i18nText The {@link I18nText} to be edited. Can be <code>null</code> (wich will render this editor read-only).
	 * @param editMode The mode - i.e. how to edit. If <code>editMode == null</code>
	 *		this method behaves like {@link #setI18nText(I18nText)}.
	 */
	void setI18nText(I18nText i18nText, EditMode editMode);

	EditMode getEditMode();

	void refresh();

	/**
	 * Sets whether the text can be edited. 
	 * @param editable the editable.
	 * @see org.eclipse.swt.widgets.Text#setEditable(boolean)
	 */
	void setEditable(boolean editable);

	/**
	 * Resets the I18nTextEditor in a way that the connection to the associated I18nText is removed
	 * and nothing is displayed in the text field.
	 */
	void reset();

	/**
	 * adds a {@link ModificationFinishedListener} to listen when the modification has finished
	 * @param listener the listener to add
	 */
	void addModificationFinishedListener(ModificationFinishedListener listener);
	
	/**
	 * removes a previously added {@link ModificationFinishedListener}
	 * @param listener the listener to remove
	 */
	void removeModificationFinishedListener(ModificationFinishedListener listener);
}
