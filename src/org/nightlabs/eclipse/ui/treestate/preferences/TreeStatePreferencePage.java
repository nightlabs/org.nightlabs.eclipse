package org.nightlabs.eclipse.ui.treestate.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
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
	}

	@Override
	protected void createFieldEditors() {
		enableState = new BooleanFieldEditor(Preferences.PREFERENCE_ENABLE_STATE,
				"Enable",
				getFieldEditorParent());

		relativeTime = new IntegerFieldEditor(Preferences.PREFERENCE_RELATIVE_TIME,
				"Relative time(1-100000 milliseconds)",
				getFieldEditorParent());
		relativeTime.setValidRange(1, 100);
		relativeTime.setTextLimit(3);
		relativeTime.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_RELATIVE_TIME));

		absoluteTime = new IntegerFieldEditor(Preferences.PREFERENCE_ABSOLUTE_TIME,
				"Absolute time(1-100000 milliseconds)",
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
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.VALUE)) { 
			if (event.getSource() == absoluteTime 
					|| event.getSource() == relativeTime
					|| event.getSource() == enableState) 
				checkState(); 
		} 
	}
	@Override
	protected void checkState() {
		super.checkState();
		if (absoluteTime.getIntValue() < relativeTime.getIntValue()) {
			setErrorMessage("The absolute time should have value greater than the relative time.");       
			setValid(false); 
		} 
		else { 
			setErrorMessage(null); 
			setValid(true); 
		} 
	}

	@Override
	public void init(IWorkbench workbench) {}
}
