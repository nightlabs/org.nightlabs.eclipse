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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.custom.XCombo;
import org.nightlabs.base.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.language.LanguageCf;

/**
 * @deprecated Please use the {@link LanguageChooserCombo} instead!
 */
public class LanguageChooserImageCombo 
extends AbstractLanguageChooser
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(LanguageChooserImageCombo.class);
	
	protected XCombo combo;
	protected List<LanguageCf> languages = new ArrayList<LanguageCf>();
	
	public LanguageChooserImageCombo(Composite parent) {
		this(parent, true, true);
	}
	
	protected SelectionListener comboSelectionListener = new SelectionAdapter() 
	{
		public void widgetSelected(SelectionEvent selectionEvent)
		{
			logger.debug("new language: "+getLanguage().getLanguageID()); //$NON-NLS-1$
			fireLanguageChangeEvent();
		}
	};
 
	public LanguageChooserImageCombo(Composite parent, boolean showImage, boolean showText)
	{
		super(parent, SWT.NONE, true);
		if (!showImage && !showText)
			throw new IllegalArgumentException("either showImage or showText must be true!"); //$NON-NLS-1$
		
		combo = new XCombo(this, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(comboSelectionListener);
		try {
			// TODO We should load the languages only once into the client!
			// They usually don't change...

		  String userLanguageID = Locale.getDefault().getLanguage();
		  int languageIdx = -1;
		  for (Iterator it = LanguageManager.sharedInstance().getLanguages().iterator(); it.hasNext(); ) {
		  	LanguageCf language = (LanguageCf) it.next();
		  	if (userLanguageID.equals(language.getLanguageID()))
		  		languageIdx = languages.size();
		  	languages.add(language);
		  	Image image = LanguageManager.sharedInstance().getFlag16x16Image(language.getLanguageID());
		  	if (showImage && showText)
		  		combo.add(image, language.getNativeName());
		  	else if (showImage && !showText)
		  		combo.add(image, ""); //$NON-NLS-1$
		  	else if (!showImage && showText)
		  		combo.add(null, language.getNativeName());
		  }

		  if (languageIdx < 0)
				throw new IllegalStateException("The user's language \""+userLanguageID+"\" is not registered in the server!"); //$NON-NLS-1$ //$NON-NLS-2$

			combo.select(languageIdx);
		} catch (RuntimeException e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw e;
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw new RuntimeException(e);
		}
	}

	public LanguageCf getLanguage() 
	{
		return (LanguageCf)languages.get(combo.getSelectionIndex());
	}

}
