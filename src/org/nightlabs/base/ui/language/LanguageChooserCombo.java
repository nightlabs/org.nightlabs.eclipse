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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.custom.XCombo;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.language.LanguageCf;
import org.nightlabs.util.NLLocale;

/**
 * This is an implementation of LanguageChooser showing all registered languages
 * in a combo box and automatically selecting the one from NLLocale.getDefault().getLanguage()
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class LanguageChooserCombo
	extends AbstractLanguageChooser
{
	/**
	 * LOG4J logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(LanguageChooserCombo.class);

	private XCombo combo;
	private List<LanguageCf> languages = new ArrayList<LanguageCf>();

	public static enum Mode {
		iconOnly,
		textOnly,
		iconAndText
	}

	public LanguageChooserCombo(Composite parent)
	{
		this(parent, Mode.iconAndText);
	}

	public LanguageChooserCombo(Composite parent, Mode mode)
	{
		super(parent, SWT.NONE, true);
		((GridData)getLayoutData()).grabExcessVerticalSpace = false;
//		((GridData)getLayoutData()).grabExcessHorizontalSpace = grabExcessHorizontalSpace;
		((GridData)getLayoutData()).grabExcessHorizontalSpace = false;
		combo = new XCombo(this, getBorderStyle() | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectionEvent)
					{
						logger.debug("new language: "+getLanguage().getLanguageID()); //$NON-NLS-1$
						fireLanguageChangeEvent();
					}
				});

		try {
			// TODO We should load the languages only once into the client!
			// They usually don't change...

		  String userLanguageID = NLLocale.getDefault().getLanguage();
		  int languageIdx = -1;
		  for (Iterator<LanguageCf> it = LanguageManager.sharedInstance().getLanguages().iterator(); it.hasNext(); ) {
		  	LanguageCf language = it.next();
		  	if (userLanguageID.equals(language.getLanguageID()))
		  		languageIdx = languages.size();
		  	languages.add(language);
//		  	combo.add(language.getNativeName());
		  	Image image = LanguageManager.sharedInstance().getFlag16x16Image(language.getLanguageID());
		  	switch (mode) {
					case iconAndText:
						combo.add(image, language.getName().getText());
					break;
					case iconOnly:
						combo.add(image, ""); //$NON-NLS-1$
					break;
					case textOnly:
						combo.add(null, language.getName().getText());
					break;
					default:
						throw new IllegalStateException("Invalid mode: " + mode); //$NON-NLS-1$
				}
		  }

//		  if (languageIdx < 0)
//				throw new IllegalStateException("The user's language \""+userLanguageID+"\" is not registered in the server!"); //$NON-NLS-1$ //$NON-NLS-2$

		  if (languageIdx < 0)
			  languageIdx = 0;

		  combo.select(languageIdx);
		} catch (RuntimeException e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw e;
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.LanguageChooser#getLanguage()
	 */
	@Override
	public LanguageCf getLanguage()
	{
		return languages.get(combo.getSelectionIndex());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.language.LanguageChooser#getLanguages()
	 */
	@Override
	public List<LanguageCf> getLanguages()
	{
		return languages;
	}
}
