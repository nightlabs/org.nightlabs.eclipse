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
package org.nightlabs.editor2d.ui.config;

import org.nightlabs.config.ConfigModule;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class QuickOptionsConfigModule 
extends ConfigModule 
{

	public QuickOptionsConfigModule() {
		super();
	}

	public static final int DEFAULT_MOVE_TRANSLATION = 25;
	public static final int DEFAULT_CLONE_DISTANCE = 25;
	
	protected int moveTranslationX = DEFAULT_MOVE_TRANSLATION;
	public int getMoveTranslationX() {
		return moveTranslationX;
	}
	public void setMoveTranslationX(int moveTranslationX) {
		this.moveTranslationX = moveTranslationX;
		setChanged();
	}
	
	protected int moveTranslationY = DEFAULT_MOVE_TRANSLATION;
	public int getMoveTranslationY() {
		return moveTranslationY;
	}
	public void setMoveTranslationY(int moveTranslationY) {
		this.moveTranslationY = moveTranslationY;
		setChanged();
	}
	
	protected int cloneDistanceX = DEFAULT_CLONE_DISTANCE;
	public int getCloneDistanceX() {
		return cloneDistanceX;
	}
	public void setCloneDistanceX(int cloneDistanceX) {
		this.cloneDistanceX = cloneDistanceX;
		setChanged();
	}
	
	protected int cloneDistanceY = DEFAULT_CLONE_DISTANCE;
	public int getCloneDistanceY() {
		return cloneDistanceY;
	}
	public void setCloneDistanceY(int cloneDistanceY) {
		this.cloneDistanceY = cloneDistanceY;
		setChanged();
	}
	
	public void init() 
	{
		
	}
}
