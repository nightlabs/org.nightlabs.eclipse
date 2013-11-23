package org.nightlabs.eclipse.compatibility;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class CompatibleFormToolkit {
	public static void paintBordersFor(FormToolkit toolkit, Composite parent) {
		toolkit.paintBordersFor(parent);
	}
}
