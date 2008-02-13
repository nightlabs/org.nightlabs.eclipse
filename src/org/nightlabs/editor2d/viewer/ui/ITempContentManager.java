/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d.viewer.ui;

import java.util.Collection;

public interface ITempContentManager
{
	/**
	 * 
	 * @param o the Object to add to the Temp Content
	 */
	void addToTempContent(Object o);
	
	/**
	 * 
	 * @param c the Collection of Objects to add to the Temp Content
	 */
	void addToTempContent(Collection c);
	
	/**
	 * 
	 * @param o the Object to remove from the Temp Content
	 */
	void removeFromTempContent(Object o);
	
	/**
	 * 
	 * @param c the Collection of Objects to remove to the Temp Content
	 */
	void removeFromTempContent(Collection c);
	
	/**
	 * clears the Temp Content
	 */
	void clear();
	
	/**
	 * 
	 * @return a unmodifiable Collection which contains the Temp Content
	 */
	Collection getTempContent();
}
