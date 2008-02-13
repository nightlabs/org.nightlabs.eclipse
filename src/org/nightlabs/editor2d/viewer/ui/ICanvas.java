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


public interface ICanvas
{
	/**
	 * scales the Canvas
	 * @param scale the scaleFactor
	 */
	void setScale(double scale);
	
	/**
	 * 
	 * @return the scaleFactor of the Canvas
	 */
	double getScale();
	
	/**
	 * translates the Canvas in X-Direction
	 * @param translateX the value of the translation
	 */
	void translateX(float translateX);
//	float getTranslateX();
	
	/**
	 * translates the Canvas in Y-Direction
	 * @param translateY the value of the translation
	 */
	void translateY(float translateY);
//	float getTranslateY();
		
	/**
	 * 
	 * repaints the Canvas
	 */
	void repaint();
	
	/**
	 * sets the Background Color of the Canvas as RGB which consists of three values
	 * 
	 * @param red the Red-Value of the Color (0-255)
	 * @param green the Green-Value of the Color (0-255)
	 * @param blue the Blue-Value of the Color (0-255)
	 */
	void setBackground(int red, int green, int blue);
	
	void dispose();
}
