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

package org.nightlabs.base.part;

import org.eclipse.swt.widgets.Composite;

/**
 * ControllableParts are used together with {@link org.nightlabs.base.part.PartController}s.
 * When implementing a ControllablePart register it (best in its constructor)
 * to a PartController. 
 * Delegate the {@link org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)}
 * method to this Controller by calling its method {@link org.nightlabs.base.part.PartController#createPartControl(ControllablePart, Composite)}.
 * Create the real contents of the part in the {@link #createPartContents(Composite)}
 * method that will be called by the PartController if this view can be displayed.
 * ControllableParts themselves control whether they might be displayed by {@link #canDisplayPart()}
 *   
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface ControllablePart  
//extends IWorkbenchPart
{

	/**
	 * Create the real contents of a {@link org.eclipse.ui.part.WorkbenchPart}
	 * here. Delegate the WorkbenchParts 
	 * {@link org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)}
	 * method to the {@link PartController#createPartControl(ControllablePart, Composite)}
	 * method of the PartController you have registered this WorkbenchPart to.
	 * 
	 * @param parent The parent to create the contents to.
	 */
	void createPartContents(Composite parent);
	
	/**
	 * Should check whether this WorkbenchPart can be displayed. If false is returned
	 * the associated PartController will hide or even dispose the 
	 * contents of this WorkbenchPart and display a default Composite.
	 * 
	 * @return Whether this Part can be displayed.
	 */
	boolean canDisplayPart();
		
//	void createPartControl(Composite parent);		
}
