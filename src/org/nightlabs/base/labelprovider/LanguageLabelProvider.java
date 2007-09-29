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

package org.nightlabs.base.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.i18n.I18nText;

public class LanguageLabelProvider 
extends LabelProvider
{
	protected I18nText text;
	public LanguageLabelProvider(I18nText text) {
		super();
		this.text = text;
	}
	
  public String getText(Object element) 
  {
    if (element == null)
        return ""; //$NON-NLS-1$

    if (element instanceof I18nText) {
    	I18nText i18nText = (I18nText) element;
    	if (i18nText != null)
    		return i18nText.getText(LanguageManager.sharedInstance().getCurrentLanguageID());
    }

    return ""; //$NON-NLS-1$
  }

  public Image getImage(Object element) 
  {
    if (element == null)
      return null; //$NON-NLS-1$

    return LanguageManager.sharedInstance().getFlag16x16Image(LanguageManager.sharedInstance().getCurrentLanguageID());    
 	}
  
//  protected String getLanguageID(String languageText) 
//  {
//  	for (Iterator it = text.getTexts().iterator(); it.hasNext(); ) {
//  		Map.Entry entry = (Map.Entry) it.next();
//  		String value = (String) entry.getValue();
//  		if (value.equals(languageText)) {
//  			String languageID = (String) entry.getKey();
//  			return languageID;
//  		}
//  	}
//  	return null;
//  }
}
