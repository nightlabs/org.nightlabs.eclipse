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

package org.nightlabs.editor2d.viewer.event;

/**
 * An MouseEvent Wrapper to give the opportunity to handle both
 * SWT as well AWT MouseEvents  
 * 
 * <p> Project: org.nightlabs.editor2d.viewer </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 21.12.2005 </p>
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class MouseEvent 
{ 	
//	public static final int BUTTON1 = 1;
//	public static final int BUTTON2 = 2;
//	public static final int BUTTON3 = 3;
//	public static final int NOBUTTON = 0;
	public static final int BUTTON1 = java.awt.event.MouseEvent.BUTTON1;
	public static final int BUTTON2 = java.awt.event.MouseEvent.BUTTON2;
	public static final int BUTTON3 = java.awt.event.MouseEvent.BUTTON3;
	public static final int NOBUTTON = java.awt.event.MouseEvent.NOBUTTON;	
	
	private int button = NOBUTTON;
	public int getButton() {
		return button;
	}
	public void setButton(int buttonMask) {
		this.button = buttonMask;
	}
	
	private int x = 0;
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	
	private int y = 0;
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}	
}
