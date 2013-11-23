package org.eclipse.jface.viewers;

import org.eclipse.swt.widgets.Event;

public abstract class OwnerDrawLabelProvider extends CellLabelProvider {
	protected abstract void measure(Event event, Object element);
	protected abstract void paint(Event event, Object element);
	protected abstract void erase(Event event, Object element);
	public static void setUpOwnerDraw(ColumnViewer columnViewer) {
	}
	public void update(ViewerCell cell) {
	}
}
