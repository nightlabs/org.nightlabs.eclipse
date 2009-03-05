package org.nightlabs.eclipse.ui.treestate.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.nightlabs.eclipse.ui.treestate.TreeStatePlugin;


public class Preferences
{
	public static final String PREFERENCE_RELATIVE_TIME = "RelativeTime"; //$NON-NLS-1$
	public static final String PREFERENCE_ABSOLUTE_TIME = "AbsoluteTime"; //$NON-NLS-1$
	public static final String PREFERENCE_ENABLE_STATE = "EnableState"; //$NON-NLS-1$

	public static IPreferenceStore getPreferenceStore()
	{
		initDefaultValues(TreeStatePlugin.getDefault().getPreferenceStore());
		return TreeStatePlugin.getDefault().getPreferenceStore();
	}

	public static void initDefaultValues(IPreferenceStore store)
	{
		store.setDefault(PREFERENCE_RELATIVE_TIME, 20);
		store.setDefault(PREFERENCE_ABSOLUTE_TIME, 1);
		store.setDefault(PREFERENCE_ENABLE_STATE, false);
	}
}
