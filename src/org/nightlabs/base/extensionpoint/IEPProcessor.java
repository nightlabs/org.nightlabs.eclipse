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

package org.nightlabs.base.extensionpoint;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

/**
 * Interface for processors of an extension-point.
 * 
 * @author Alexander Bieber
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public interface IEPProcessor
{
	/**
	 * Returns the extension-point id that should be processed by this processor.
	 * 
	 * @return The extension-point id that should be processed by this processor.
	 */
	String getExtensionPointID();

	/**
	 * Processes a single element that is a direct child of the extension to the
	 * extension-point this processor operates on.
	 *  
	 * @param extension The extension the element belongs to.
	 * @param element The element that should be processed. It is always a direct child node of the extension.
	 * @throws Exception If something fails while processing the element.
	 */
	void processElement(IExtension extension, IConfigurationElement element) throws Exception;

	/**
	 * Finds all extensions to the extension-point defined by {@link #getExtensionPointID()}
	 * and lets them being processed by a call to {@link #processElement(IExtension, IConfigurationElement)}.
	 * <p>
	 * Note that this method will catch the exceptions thrown by {@link #processElement(IExtension, IConfigurationElement)},
	 * continue the procedure with the next extension and only will log an error.
	 * </p> 
	 */
	void process();
}
