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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.language.LanguageCf;

public abstract class AbstractSimpleLanguageChooser 
implements LanguageChooser
{
	protected List<LanguageChangeListener> languageChangeListeners = new LinkedList<LanguageChangeListener>();
	protected LanguageCf oldLanguage = null;
	
	public AbstractSimpleLanguageChooser() {
		super();
	}

	public LanguageCf getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addLanguageChangeListener(LanguageChangeListener l) {
		languageChangeListeners.add(l);
	}

	public void removeLanguageChangeListener(LanguageChangeListener l) {
		languageChangeListeners.remove(l);
	}

	public void fireLanguageChangeEvent() 
	{
		if (languageChangeListeners.size() < 1)
			return;

		LanguageChangeEvent languageChangeEvent = new LanguageChangeEvent(this, oldLanguage, getLanguage());
		for (Iterator it = languageChangeListeners.iterator(); it.hasNext(); ) {
			((LanguageChangeListener)it.next()).languageChanged(languageChangeEvent);
		}

		oldLanguage = getLanguage();		
	}

}
