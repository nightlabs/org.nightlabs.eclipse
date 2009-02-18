package org.nightlabs.eclipse.ui.control.export;

import org.eclipse.swt.widgets.Widget;

public class FocusHistoryItem {
	private Widget widget;

	public FocusHistoryItem(Widget widget) {
		this.widget = widget;
	}

	public Widget getWidget() {
		return widget;
	}
}
