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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class AutoScrollPreferencePage 
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{	
	public AutoScrollPreferencePage() 
	{
		super(GRID);
		setPreferenceStore(ViewerPlugin.getDefault().getPreferenceStore());
		setTitle(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.AutoScrollPreferencePage.title"))); //$NON-NLS-1$
	}
			
	protected IntegerFieldEditor scrollStep;
	protected IntegerFieldEditor timerDelay;	
	protected IntegerFieldEditor scrollTolerance;
//protected IntegerFieldEditor bufferScale;	
	
	protected void createFieldEditors()
	{
		scrollStep = new IntegerFieldEditor(Preferences.PREFERENCE_SCROLL_STEP,
				ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.AutoScrollPreferencePage.label.autoScroll")), //$NON-NLS-1$
				getFieldEditorParent());
		scrollStep.setValidRange(1, 25);
		scrollStep.setTextLimit(2);
		scrollStep.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_SCROLL_STEP));

		scrollTolerance = new IntegerFieldEditor(Preferences.PREFERENCE_SCROLL_TOLERANCE,
				ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.AutoScrollPreferencePage.label.scrollTollerance")), //$NON-NLS-1$
				getFieldEditorParent());
		scrollTolerance.setValidRange(1, 100);
		scrollTolerance.setTextLimit(3);
		scrollTolerance.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_SCROLL_TOLERANCE));
				
		timerDelay = new IntegerFieldEditor(Preferences.PREFERENCE_TIMER_DELAY,
				ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.AutoScrollPreferencePage.label.timerDelay")), //$NON-NLS-1$
				getFieldEditorParent());
		timerDelay.setValidRange(1, 100);
		timerDelay.setTextLimit(3);
		timerDelay.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_TIMER_DELAY));
		
//		bufferScale = new IntegerFieldEditor(Preferences.PREFERENCE_BUFFER_SCALE,
//				ViewerPlugin.getResourceString("preferences.buffer.label.scaleFactor"),
//				getFieldEditorParent());
//		bufferScale.setValidRange(1, 5);
//		bufferScale.setTextLimit(1);

		addField(scrollStep);
		addField(scrollTolerance);
		addField(timerDelay);
	}
	
	protected void performDefaults() {
		Preferences.initDefaultValues(Preferences.getPreferenceStore());
		super.performDefaults();		
	}

	public boolean performOk() 
	{
		getPreferenceStore().setValue(Preferences.PREFERENCE_SCROLL_STEP, scrollStep.getIntValue());		
		getPreferenceStore().setValue(Preferences.PREFERENCE_SCROLL_TOLERANCE, scrollTolerance.getIntValue());		
		getPreferenceStore().setValue(Preferences.PREFERENCE_TIMER_DELAY, timerDelay.getIntValue());		
//		return super.performOk();
		return true;
	}	
	
	public void init(IWorkbench workbench) {}			
}
