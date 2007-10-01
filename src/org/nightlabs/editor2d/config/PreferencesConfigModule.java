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
package org.nightlabs.editor2d.config;

import org.nightlabs.config.ConfigModule;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PreferencesConfigModule 
extends ConfigModule 
{

	public PreferencesConfigModule() {
		super();
	}

	protected boolean showToolTips = true;
	public boolean isShowToolTips() {
		return showToolTips;
	}
	public void setShowToolTips(boolean showToolTips) {
		this.showToolTips = showToolTips;
		setChanged(true);
	}
	
	protected boolean showStatusLine = false;
	public boolean isShowStatusLine() {
		return showStatusLine;
	}
	public void setShowStatusLine(boolean showStatusLine) {
		this.showStatusLine = showStatusLine;
		setChanged(true);
	}
		
	protected boolean showRollOver = false;
	public boolean isShowRollOver() {
		return showRollOver;
	}
	public void setShowRollOver(boolean showRollOver) {
		this.showRollOver = showRollOver;
		setChanged(true);
	}
	
}
