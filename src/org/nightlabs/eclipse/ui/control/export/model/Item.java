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

	public boolean isEmpty() {
		for (Cell cell : cells) {
			if (cell.getImageData() != null || (cell.getText() != null && !cell.getText().isEmpty()))
				return false;
		}
		return true;
	}

	public int getLevel()
	{
		int level = 0;
		Item parent = this.getParentItem();
		while (parent != null) {
			++level;
			parent = parent.getParentItem();
		}
		return level;
	}
}