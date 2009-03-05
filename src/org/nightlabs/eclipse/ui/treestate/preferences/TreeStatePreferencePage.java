package org.nightlabs.eclipse.ui.treestate.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class TreeStatePreferencePage 
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{
	private BooleanFieldEditor enableState; 
	private IntegerFieldEditor relativeTime;
	private IntegerFieldEditor absoluteTime;
	
	public TreeStatePreferencePage() {
		super(GRID);
		setPreferenceStore(Preferences.getPreferenceStore());
		setTitle("Title");
	}
	
	@Override
	protected void createFieldEditors() {
		enableState = new BooleanFieldEditor(Preferences.PREFERENCE_ENABLE_STATE,
				"Enable",
				getFieldEditorParent());
		enableState.setEnabled(Preferences.getPreferenceStore().getBoolean(Preferences.PREFERENCE_ENABLE_STATE)
				, getFieldEditorParent());

		relativeTime = new IntegerFieldEditor(Preferences.PREFERENCE_RELATIVE_TIME,
				"Relative Time",
				getFieldEditorParent());
		relativeTime.setValidRange(1, 100);
		relativeTime.setTextLimit(3);
		relativeTime.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_RELATIVE_TIME));
	
		absoluteTime = new IntegerFieldEditor(Preferences.PREFERENCE_RELATIVE_TIME,
				"Relative Time",
				getFieldEditorParent());
		absoluteTime.setValidRange(1, 100);
		absoluteTime.setTextLimit(3);
		absoluteTime.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_RELATIVE_TIME));
		
		addField(enableState);
		addField(relativeTime);
		addField(absoluteTime);
	}

	@Override
	public void init(IWorkbench w) {}
}
