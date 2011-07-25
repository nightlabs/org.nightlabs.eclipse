package org.eclipse.swt.accessibility;

import org.eclipse.swt.widgets.Control;


public class CompatibleAccessible {
	public static void addAccessibleListener(Control aControl, AccessibleAdapter adapter) {
		aControl.getAccessible().addAccessibleListener(adapter);
	}
	
	public static void addAccessibleTextListener(Control aControl, AccessibleTextAdapter adapter) {
		aControl.getAccessible().addAccessibleTextListener(adapter);
	}
	
	public static void addAccessibleControlListener(Control aControl, AccessibleControlAdapter adapter) {
		aControl.getAccessible().addAccessibleControlListener(adapter);
	}
}
