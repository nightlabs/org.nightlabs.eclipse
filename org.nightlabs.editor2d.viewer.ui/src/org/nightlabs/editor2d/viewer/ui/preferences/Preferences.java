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

package org.nightlabs.editor2d.viewer.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;

public class Preferences
{
	public static final String PREFERENCE_TIMER_DELAY = "TimerDelay"; //$NON-NLS-1$
	public static final String PREFERENCE_SCROLL_STEP = "ScrollStep"; //$NON-NLS-1$
	public static final String PREFERENCE_SCROLL_TOLERANCE = "ScrollTolerance"; //$NON-NLS-1$
	public static final String PREFERENCE_BUFFER_SCALE = "BufferScale"; //$NON-NLS-1$
	public static final String PREFERENCE_RENDERING = "Rendering";	 //$NON-NLS-1$
	public static final String PREFERENCE_QUALITY = "Quality"; //$NON-NLS-1$
	public static final String PREFERENCE_DEFAULT = "Default"; //$NON-NLS-1$
	public static final String PREFERENCE_SPEED = "Speed"; //$NON-NLS-1$
	public static final String PREFERENCE_HIT_TOLERANCE = "HitTolerance"; //$NON-NLS-1$

	public static IPreferenceStore getPreferenceStore()
	{
		initDefaultValues(ViewerPlugin.getDefault().getPreferenceStore());
		return ViewerPlugin.getDefault().getPreferenceStore();
	}

	public static void initDefaultValues(IPreferenceStore store)
	{
		store.setDefault(PREFERENCE_BUFFER_SCALE, 2);
		store.setDefault(PREFERENCE_TIMER_DELAY, 25);
		store.setDefault(PREFERENCE_SCROLL_STEP, 10);
		store.setDefault(PREFERENCE_SCROLL_TOLERANCE, 25);
		store.setDefault(PREFERENCE_HIT_TOLERANCE, 10);
		store.setDefault(PREFERENCE_RENDERING, PREFERENCE_DEFAULT);
	}
}
