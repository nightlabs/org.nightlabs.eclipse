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
package org.nightlabs.base.search;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.i18n.I18nText;


/**
 * Factory which creates instances of {@link ISearchResultProvider}
 * and holds the describing data like name, image etc 
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public interface ISearchResultProviderFactory
extends IExecutableExtension
{
	public static final String WILDCARD_PERSPECTIVE_ID = "*"; //$NON-NLS-1$
	public static final int DEFAULT_PRIORITY = -1;

	/**
	 * returns the ISearchResultProvider
	 * @return the ISearchResultProvider
	 */
	ISearchResultProvider createSearchResultProvider();
	
	/**
	 * returns the multilanguage capable name of the type of search
	 * @return the multilanguage capable name of the type of search
	 * @see I18nText
	 */
	I18nText getName();
	
	/**
	 * @return the class of the results of the search 
	 */
	Class getResultTypeClass();
	
	/**
	 * returns the {@link ISearchResultActionHandler} which should be used 
	 * 
	 * @return {@link ISearchResultActionHandler}
	 */
	ISearchResultActionHandler getActionHandler();
	
	/**
	 * registers a ISearchResultActionHandler for a perspectiveID
	 * 
	 * @param actionHandler the ISearchResultActionHandler to add
	 * @param perspectiveID the perspectiveID of the ISearchResultActionHandler
	 */
	void addActionHandler(ISearchResultActionHandler actionHandler, String perspectiveID);
		
	/**
	 * returns the decorator image (usally 8x8 pixels)
	 * @return the decorator image
	 */
	Image getDecoratorImage();
	
	/**
	 * returns the image (usally 16x16 pixels)
	 * @return the image
	 */
	Image getImage();
	
	/**
	 * returns the id
	 * @return the id
	 */
	String getID();
	
	/**
	 * returns the priority
	 * @return the priority
	 */
	int getPriority();
	
	/**
	 * returns the composed decorator image {@link SearchCompositeImage}
	 * @return the composed decorator image
	 */
	Image getComposedDecoratorImage();
}
