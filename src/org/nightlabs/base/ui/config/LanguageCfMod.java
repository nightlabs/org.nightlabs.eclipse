/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.config;

import java.util.HashSet;
import java.util.Set;

import org.nightlabs.base.ui.language.LanguageManager;
import org.nightlabs.config.CfModList;
import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;
import org.nightlabs.language.LanguageCf;

public class LanguageCfMod
extends ConfigModule
{
	private static final long serialVersionUID = 2L;

	public LanguageCfMod() { }

	protected CfModList<LanguageCf> languages = null;
	
	/**
	 * The languageID the application runs with and should run the next time.
	 */
	protected String languageID;

	public CfModList<LanguageCf> getLanguages() {
		return languages;
	}
	public void setLanguages(CfModList<LanguageCf> languages) {
		this.languages = languages;
	}

	public Set<String> getLanguageIDs()
	{
		Set<String> languageIDs = new HashSet<String>(languages.size());
		for (LanguageCf languageCf : languages)
			languageIDs.add(languageCf.getLanguageID());

		return languageIDs;
	}

	@Override
	public void init()
	throws InitException
	{
		super.init();
		if (languages == null || languages.isEmpty())
			languages = createDefaultLanguage();
		languages.setOwnerCfMod(this);

		for (LanguageCf languageCf : languages) {
			if (languageCf.init(getLanguageIDs()))
				setChanged();
		}
	}

	protected CfModList<LanguageCf> createDefaultLanguage()
	{
		CfModList<LanguageCf> l = new CfModList<LanguageCf>(this);
		l.add(LanguageManager.createDefaultLanguage());
		return l;
	}
	
	/**
	 * @return The languageID the user selected and the application should run with. 
	 *         Can be <code>null</code> indicating that the system default should be used.
	 */
	public String getLanguageID() {
		return languageID;
	}
	/**
	 * Set the languageID.
	 * See {@link #getLanguageID()}.
	 * 
	 * @param languageID The languageID to set.
	 */
	public void setLanguageID(String languageID) {
		this.languageID = languageID;
		setChanged();
	}

}
