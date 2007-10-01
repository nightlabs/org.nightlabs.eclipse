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

package org.nightlabs.editor2d.viewer.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.pref.DoubleFieldEditor;
import org.nightlabs.editor2d.viewer.BufferManager;
import org.nightlabs.editor2d.viewer.ViewerPlugin;
import org.nightlabs.editor2d.viewer.resource.Messages;

public class BufferPreferencePage 
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{
	public BufferPreferencePage() 
	{
		super(GRID);
		setPreferenceStore(ViewerPlugin.getDefault().getPreferenceStore());		
		setTitle(Messages.getString("org.nightlabs.editor2d.viewer.preferences.BufferPreferencePage.title.buffer")); //$NON-NLS-1$
	}
	
	protected DoubleFieldEditor bufferScale = null;
	protected void createFieldEditors() 
	{
		bufferScale = new DoubleFieldEditor(Preferences.PREFERENCE_BUFFER_SCALE,
				Messages.getString("org.nightlabs.editor2d.viewer.preferences.BufferPreferencePage.label.bufferScale"), //$NON-NLS-1$
				getFieldEditorParent());
		bufferScale.setValidRange(1, 10);
		addField(bufferScale);
	}
	
	
	protected void performDefaults() 
	{
		Preferences.initDefaultValues(Preferences.getPreferenceStore());
		super.performDefaults();		
	}
		
	public boolean performOk() 
	{
		BufferManager.sharedInstance().setBufferScaleFactor(bufferScale.getDoubleValue());
		return super.performOk();
	}

	public void init(IWorkbench workbench) {
	}
	
}
