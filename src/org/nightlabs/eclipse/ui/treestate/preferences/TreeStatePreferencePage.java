package org.nightlabs.eclipse.ui.treestate.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.eclipse.ui.treestate.resource.Messages;

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
				Messages.getString("org.nightlabs.eclipse.ui.treestate.preferences.TreeStatePreferencePage.button.enable.text"), //$NON-NLS-1$
				getFieldEditorParent());

		relativeTime = new IntegerFieldEditor(Preferences.PREFERENCE_RELATIVE_TIME,
				Messages.getString("org.nightlabs.eclipse.ui.treestate.preferences.TreeStatePreferencePage.field.relativeTime.text"), //$NON-NLS-1$
				getFieldEditorParent());
		relativeTime.setValidRange(1, 100000);
		relativeTime.setTextLimit(6);
		relativeTime.setStringValue(Preferences.getPreferenceStore().getString(
				Preferences.PREFERENCE_RELATIVE_TIME));

		absoluteTime = new IntegerFieldEditor(Preferences.PREFERENCE_ABSOLUTE_TIME,
				Messages.getString("org.nightlabs.eclipse.ui.treestate.preferences.TreeStatePreferencePage.field.absoluteTime.text"), //$NON-NLS-1$
				getFieldEditorParent());
		absoluteTime.setValidRange(1, 100000);
		absoluteTime.setTextLimit(6);
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
			setErrorMessage(Messages.getString("org.nightlabs.eclipse.ui.treestate.preferences.TreeStatePreferencePage.errorMessage.relativeTimeBiggerThanAbsoluteTime"));        //$NON-NLS-1$
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
