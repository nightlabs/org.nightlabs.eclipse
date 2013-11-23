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

package org.nightlabs.editor2d.viewer.ui.tool;

import org.eclipse.swt.graphics.Image;

public interface IToolEntry
{
	/**
	 * 
	 * @return the tool for this entry
	 */
	ITool getTool();
	
	/**
	 * 
	 * @param tool the tool for this entry
	 */
	void setTool(ITool tool);
	
	/**
	 * 
	 * @return the name of the toolEntry
	 */
	String getName();
	
	/**
	 * 
	 * @param name the name of the toolEntry
	 */
	void setName(String name);
	
	/**
	 * 
	 * @return the toolTipText of the toolEntry
	 */
	String getToolTipText();
	
	/**
	 * 
	 * @param toolTipText the toolTipText of the toolEntry
	 */
	void setToolTipText(String toolTipText);
	
//	/**
//	 *
//	 * @return the Image of the ToolEntry
//	 */
//	BufferedImage getImage();
//
//	/**
//	 *
//	 * @param image the Image of the ToolEntry
//	 */
//	void setImage(BufferedImage image);
	
	/**
	 * 
	 * @return the Image of the ToolEntry
	 */
	Image getImage();
	
	/**
	 * 
	 * @param image the Image of the ToolEntry
	 */
	void setImage(Image image);
	
}
