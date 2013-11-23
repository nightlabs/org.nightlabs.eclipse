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

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.pref.DoubleFieldEditor;
import org.nightlabs.editor2d.util.RenderingHintsManager;
import org.nightlabs.editor2d.viewer.ui.BufferManager;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class ViewerPreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{
	private IntegerFieldEditor scrollStep;
	private IntegerFieldEditor timerDelay;
	private IntegerFieldEditor scrollTolerance;
	private DoubleFieldEditor bufferScale;
	private RadioGroupFieldEditor renderQualitySettings;
	private IntegerFieldEditor hitTolerance;

	public ViewerPreferencePage() {
		super(GRID);
//		setPreferenceStore(ViewerPlugin.getDefault().getPreferenceStore());
		setPreferenceStore(Preferences.getPreferenceStore());
		setTitle(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.ViewerPreferencePage.text"));
	}

	public void init(IWorkbench workbench) {}

	protected void setRenderSelection(String selection)
	{
		if (selection.equals(Preferences.PREFERENCE_DEFAULT)) {
			RenderingHintsManager.sharedInstance().setRenderMode(RenderingHintsManager.RENDER_MODE_DEFAULT);
		}
		else if (selection.equals(Preferences.PREFERENCE_QUALITY)) {
			RenderingHintsManager.sharedInstance().setRenderMode(RenderingHintsManager.RENDER_MODE_QUALITY);
		}
		else if (selection.equals(Preferences.PREFERENCE_SPEED)) {
			RenderingHintsManager.sharedInstance().setRenderMode(RenderingHintsManager.RENDER_MODE_SPEED);
		}
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		BufferManager.sharedInstance().setBufferScaleFactor(bufferScale.getDoubleValue());
		setRenderSelection(getPreferenceStore().getString(Preferences.PREFERENCE_RENDERING));

		IPersistentPreferenceStore store = (IPersistentPreferenceStore) getPreferenceStore();
		try {
			store.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return ok;
	}

	@Override
	protected void performDefaults()
	{
		Preferences.initDefaultValues(Preferences.getPreferenceStore());
		super.performDefaults();
	}

	@Override
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
		timerDelay.setValidRange(1, 1000);
		timerDelay.setTextLimit(3);
		timerDelay.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_TIMER_DELAY));
		addField(scrollStep);
		addField(scrollTolerance);
		addField(timerDelay);

		bufferScale = new DoubleFieldEditor(Preferences.PREFERENCE_BUFFER_SCALE,
				Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.BufferPreferencePage.label.bufferScale"), //$NON-NLS-1$
				getFieldEditorParent());
		bufferScale.setValidRange(1, 10);
		addField(bufferScale);

		hitTolerance = new IntegerFieldEditor(Preferences.PREFERENCE_HIT_TOLERANCE,
				"Hit Tolerance",
				getFieldEditorParent());
		hitTolerance.setValidRange(1, 100);
		hitTolerance.setStringValue(Preferences.getPreferenceStore().getString(Preferences.PREFERENCE_HIT_TOLERANCE));
		addField(hitTolerance);

		String labelText = ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.title")); //$NON-NLS-1$
		String labelDefault = ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.default")); //$NON-NLS-1$
		String labelQuality = ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.quality")); //$NON-NLS-1$
		String labelSpeed = ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.speed")); //$NON-NLS-1$
		String[][] labelAndValues = {
                { labelDefault, Preferences.PREFERENCE_DEFAULT },
                { labelQuality, Preferences.PREFERENCE_QUALITY },
                { labelSpeed, Preferences.PREFERENCE_SPEED } };
		renderQualitySettings = new RadioGroupFieldEditor(Preferences.PREFERENCE_RENDERING, labelText,
				1, labelAndValues, getFieldEditorParent(), true);
		addField(renderQualitySettings);
	}

}
