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

package org.nightlabs.base.ui.language;

import java.util.Locale;

import org.eclipse.jface.action.Action;

public class LanguageAction
extends Action
{
	protected static final String ID = LanguageAction.class.getName();
	
	protected ILanguageManager langMan;
	protected String languageID;
	public LanguageAction(ILanguageManager langMan, String languageID) {
		super();
		this.langMan = langMan;
		this.languageID = languageID;
		init();
	}

	protected void init()
	{
		setId(ID+'#'+languageID);
		setText(LanguageManager.getNativeLanguageName(languageID));
//		setImageDescriptor(SharedImages.getImageDescriptor(languageID));
		setImageDescriptor(LanguageManager.sharedInstance().getFlag16x16ImageDescriptor(languageID));
	}

	@Override
	public void run()
	{
		// TODO: Maybe we should present here a list of registered languages first and then somehow set nl parameter
		// or maybe its even better to read the Locale from our config module and only fallback to the default locale
		// if none is set in the config moduel or if its invalid.
		Locale.setDefault(new Locale(languageID));
//		langMan.setCurrentLanguageID(languageID);
	}
	
	
}
