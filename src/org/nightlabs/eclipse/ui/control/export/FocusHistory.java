package org.nightlabs.eclipse.ui.control.export;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!!!"); //$NON-NLS-1$
	}

	public void addFocusedWidget(Widget widget) {
		assertUIThread();

		// if it's not a table or tree, we silently ignore
		if (!((widget instanceof Table) || (widget instanceof Tree)))
			return;

		FocusHistoryItem focusHistoryItem = new FocusHistoryItem(widget);
		items.add(focusHistoryItem);
		
		widget.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				FocusHistoryItem disposedItem = null;
				for (FocusHistoryItem fi : items) {
					if (fi.getWidget() != null && fi.getWidget().equals(e.getSource())) {
						disposedItem = fi;
					}
				}
				
				items.remove(disposedItem);
			}
		});

		while (items.size() > MAX_HISTORY_ITEM_COUNT) {
			items.removeFirst();
		}
	}

	public List<FocusHistoryItem> getItems() {
		assertUIThread();
		return Collections.unmodifiableList(items);
	}

	public FocusHistoryItem getLastItem() {
		assertUIThread();
		return items.getLast();
	}
}
