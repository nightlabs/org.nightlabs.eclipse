package org.nightlabs.eclipse.ui.control.export.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Item
{
	private Item parentItem;
	private List<Cell> cells = new ArrayList<Cell>();

	public Item(Item parentItem) {
		this.parentItem = parentItem;
	}

	public void addCell(Cell cell) {
		cells.add(cell);
	}

	public Item getParentItem() {
		return parentItem;
	}

	public List<Cell> getCells() {
		return Collections.unmodifiableList(cells);
	}
}