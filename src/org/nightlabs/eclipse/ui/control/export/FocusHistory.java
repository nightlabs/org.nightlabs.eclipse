package org.nightlabs.eclipse.ui.control.export;

import java.util.LinkedList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;

public class FocusHistory {
	private static volatile FocusHistory _sharedInstance = null;
	public static FocusHistory sharedInstance() {
		if (_sharedInstance == null) {
			synchronized (FocusHistory.class) {
				if (_sharedInstance == null)
					_sharedInstance = new FocusHistory();
			}
		}

		return _sharedInstance;
	}

	private static final int MAX_HISTORY_ITEM_COUNT = 5;

	private LinkedList<FocusHistoryItem> items = new LinkedList<FocusHistoryItem>();

	protected FocusHistory() { }

	protected static void assertUIThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!!!");
	}

	public void addFocusedWidget(Widget widget) {
		assertUIThread();

		// if it's not a table or tree, we silently ignore
		if (!((widget instanceof Table) || (widget instanceof Tree)))
			return;

		if (items.size() > MAX_HISTORY_ITEM_COUNT) {
			items.removeFirst();
		}

		items.add(new FocusHistoryItem(widget));
	}

	public LinkedList<FocusHistoryItem> getItems() {
		return items;
	}
}
