package org.nightlabs.eclipse.compatibility;

import org.eclipse.swt.widgets.Text;

public class CompatibleText {
	public static void cut(Text text) {
		text.cut();
	}

	public static void copy(Text text) {
		text.copy();
	}

	public static void paste(Text text) {
		text.paste();
	}
	
	public static void showSelection(Text text) {
		text.showSelection();
	}

	public static boolean traverse(Text text, int traversal) {
		return text.traverse(traversal);
	}

	public static int getTabs(Text text) {
		return text.getTabs();
	}

	public static boolean getDoubleClickEnabled(Text text) {
		return text.getDoubleClickEnabled();
	}
}
