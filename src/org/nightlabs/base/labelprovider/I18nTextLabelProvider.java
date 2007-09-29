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

import java.util.Locale;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.i18n.I18nText;

public class I18nTextLabelProvider 
extends LabelProvider
{
	private String languageID;
	private boolean showImage = true;
	
	public I18nTextLabelProvider() {
		this(null, true);
	}

	public I18nTextLabelProvider(boolean showImage) {
		this(null, showImage);
	}
	
	public I18nTextLabelProvider(String languageID, boolean showImage) {
		super();
		this.languageID = languageID;
		this.showImage = showImage;
	}
	
  public String getText(Object element) 
  {
    if (element == null)
        return ""; //$NON-NLS-1$

    if (element instanceof I18nText) {
    	I18nText i18nText = (I18nText) element;
    	if (languageID != null)
    		return i18nText.getText(languageID);
    	
    	return i18nText.getText();
    }

    return super.getText(element);    	
  }
  
  public Image getImage(Object element) 
  {
  	if (showImage) {
    	if (languageID != null)
    		return LanguageManager.sharedInstance().getFlag16x16Image(languageID);
    	
    	return LanguageManager.sharedInstance().getFlag16x16Image(Locale.getDefault().getLanguage());  		
  	}
  	return null;
 	}
  
}
