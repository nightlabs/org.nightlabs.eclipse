/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.language;

import org.nightlabs.language.LanguageCf;

/**
 * This interface must be implemented in all GUI elements that allow
 * choosing a current language. The LanguageChooser is usually only responsible
 * for a local scope - e.g. a view.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * 
 * @see org.nightlabs.ipanema.language.LanguageChooserCombo
 * @see org.nightlabs.ipanema.language.LanguageChooserList
 */
public interface LanguageChooser
{
	/**
	 * @return Returns the currently selected language.
	 */
	LanguageCf getLanguage();
	
	/**
	 * Adds a new LanguageChangeListener to react whenever the language is changed.
	 * @param l The listener to add.
	 */
	void addLanguageChangeListener(LanguageChangeListener l);
	
	/**
	 * Remove a previously added listener. If it is not registered, this method does nothing.
	 * @param l The listener to remove.
	 */
	void removeLanguageChangeListener(LanguageChangeListener l);
	
	/**
	 * This method exists to manually fire a LanguageChangeEvent. This is useful
	 * to initialize a GUI (e.g. view) after all elements have been created.
	 */
	void fireLanguageChangeEvent();
}
