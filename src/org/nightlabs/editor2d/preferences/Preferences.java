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
package org.nightlabs.editor2d.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.i18n.unit.PointUnit;
import org.nightlabs.i18n.unit.resolution.DPIResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.print.page.A4Page;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class Preferences 
{
	public static final String PREF_DOCUMENT_RESOLUTION = "Document Resolution"; //$NON-NLS-1$
	public static final int PREF_DOCUMENT_RESOLUTION_DEFAULT = Resolution.DEFAULT_RESOLUTION_DPI;
	
	public static final String PREF_STANDARD_UNIT_ID = "Standard Unit"; //$NON-NLS-1$
	public static final String PREF_STANDARD_UNIT_ID_DEFAULT = PointUnit.UNIT_ID;	

	public static final String PREF_STANDARD_RESOLUTION_UNIT_ID = "Standard Resolution Unit"; //$NON-NLS-1$
	public static final String PREF_STANDARD_RESOLUTION_UNIT_ID_DEFAULT = DPIResolutionUnit.RESOLUTION_ID;

	public static final String PREF_PREDEFINED_PAGE_ID = "Predefined Page"; //$NON-NLS-1$
	public static final String PREF_PREDEFINED_PAGE_ID_DEFAULT = A4Page.PAGE_ID;

	public static final String PREF_PAGE_ORIENTATION_ID = "Page Orientation"; //$NON-NLS-1$
	public static final int PREF_PAGE_ORIENTATION_ID_DEFAULT = PageDrawComponent.ORIENTATION_VERTICAL;
	
  protected static void initializeDefaults(IPreferenceStore ps) 
  {
  	ps.setDefault(
  			Preferences.PREF_DOCUMENT_RESOLUTION, 
  			Preferences.PREF_DOCUMENT_RESOLUTION_DEFAULT);
  	
  	ps.setDefault(
  			Preferences.PREF_STANDARD_UNIT_ID,
  			Preferences.PREF_STANDARD_UNIT_ID_DEFAULT);
  	
  	ps.setDefault(
  			Preferences.PREF_STANDARD_RESOLUTION_UNIT_ID,
  			Preferences.PREF_STANDARD_RESOLUTION_UNIT_ID_DEFAULT);
  	
  	ps.setDefault(
  			Preferences.PREF_PREDEFINED_PAGE_ID,
  			Preferences.PREF_PREDEFINED_PAGE_ID_DEFAULT);
  	
  	ps.setDefault(
  			Preferences.PREF_PAGE_ORIENTATION_ID,
  			Preferences.PREF_PAGE_ORIENTATION_ID_DEFAULT);  	  	  	  	  	
  }

  protected static boolean defaultsSet = false;
	public static IPreferenceStore getPreferenceStore()
	{
		IPreferenceStore ps = EditorPlugin.getDefault().getPreferenceStore();
		if (!defaultsSet) {
			initializeDefaults(ps);
			defaultsSet = true;
		}
		return ps;
	}	
	
}
