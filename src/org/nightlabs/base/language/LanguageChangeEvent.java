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

import java.io.Serializable;

import org.nightlabs.language.LanguageCf;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LanguageChangeEvent implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private LanguageChooser languageChooser;
	private LanguageCf oldLanguage;
	private LanguageCf newLanguage;

	public LanguageChangeEvent(LanguageChooser languageChooser, LanguageCf oldLanguage, LanguageCf newLanguage)
	{
		this.languageChooser = languageChooser;
		this.oldLanguage = oldLanguage;
		this.newLanguage = newLanguage;
	}
	/**
	 * @return Returns the languageChooser.
	 */
	public LanguageChooser getLanguageChooser()
	{
		return languageChooser;
	}
	/**
	 * @return Returns the newLanguage.
	 */
	public LanguageCf getNewLanguage()
	{
		return newLanguage;
	}
	/**
	 * @return Returns the oldLanguage.
	 */
	public LanguageCf getOldLanguage()
	{
		return oldLanguage;
	}
}
