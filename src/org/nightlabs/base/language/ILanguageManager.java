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

import java.util.Collection;
import java.util.Locale;

import org.nightlabs.language.LanguageCf;

public interface ILanguageManager
{
	/** 
	 * @deprecated use {@link Locale#getDefault()}<code>.getLanguage()</code> instead
	 * @return the current languageID
	 */	
	public String getCurrentLanguageID();
	
	/**
	 * adds a Language based on the given languageID
	 * @param languageID the ID of the Language
	 */
	public void addLanguage(String languageID);
	
	/** 
	 * @return a java.util.Collection which contains @link{org.nightlabs.language.LanguageCf}s
	 */
	public Collection<LanguageCf> getLanguages();
	
	/** 
	 * @deprecated use {@link Locale#setDefault(Locale)} instead
	 * @param newLanguageID the id of the new Language
	 */
	public void setCurrentLanguageID(String newLanguageID); 
}
