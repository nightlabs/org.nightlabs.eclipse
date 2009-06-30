/**
 * 
 */
package org.nightlabs.base.ui.labelprovider;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class ColumnSpanLabelProvider extends OwnerDrawLabelProvider {

	private ColumnViewer columnViewer;
	
	/**
	 * Create a new {@link ColumnSpanLabelProvider} for the given {@link ColumnViewer}.
	 * Note that this constructor will cause Measure and Paint listeners to be added to 
	 * the given {@link ColumnViewer}.
	 * 
	 * @param columnViewer The {@link ColumnViewer} this labelProvider is for (it will be set as) 
	 */
	public ColumnSpanLabelProvider(ColumnViewer columnViewer) {
		if (columnViewer == null)
			throw new IllegalArgumentException("Parameter columnViewer must not be null");
		this.columnViewer = columnViewer;
		OwnerDrawLabelProvider.setUpOwnerDraw(columnViewer);
	}

	/**
	 * Get the span indices for the given element. I.e. an array of column-index-intervals 
	 * so this label-provider knows how to group the columns. Column-spanning can be 
	 * unique fore each element that's why it is passed as parameter to this method.
	 * Note that index of the interval found in the result of this method will be
	 * the parameter to {@link #getColumnText(Object, int)}.   
	 * 
	 * @param element The element the column-grouping should be determined for.
	 * @return The column-grouping for the given element given as array of intervals.
	 */
	protected abstract int[][] getColumnSpan(Object element);
	
	/**
	 * Get the text to display for the given element at the given column index.
	 * The index of the column here is the index in the array of intervals returned
	 * by {@link #getColumnSpan(Object)} for the same element.
	 * 
	 * @param element The element the text to display is queried for.
	 * @param spanColIndex The index of the column-group the text is queried for.
	 * @return The text to be displayed for the given column-group.
	 */
	protected abstract String getColumnText(Object element, int spanColIndex);
	
	/**
	 * Get the image to display for the given element at begining of the column with the given index.
	 * The index of the column here is the index in the array of intervals returned
	 * by {@link #getColumnSpan(Object)} for the same element.
	 * 
	 * @param element The element the image to display is queried for.
	 * @param spanColIndex The index of the column-group the image is queried for.
	 * @return The image to be displayed for the given column-group.
	 */
	protected Image getColumnImage(Object element, int spanColIndex) {
		return null;
	}
	
	@Override
	protected void measure(Event event, Object element) {
		int spanColIdx = getSpanColumnIndex(element, event.index);
		if (spanColIdx < 0) {
			event.setBounds(new Rectangle(event.x, event.y, event.width, event.height));
		} else {
			String string = getColumnText(element, spanColIdx);
			int[][] colSpans = internalGetColumnSpan(element);
			Point extent = event.gc.stringExtent(string);
			event.setBounds(new Rectangle(event.x , event.y, extent.x / colSpans[spanColIdx].length  , extent.y));
		}
	}

	@Override
	protected void paint(Event event, Object element) {
		if (event.index == 0) {
			firstColOffset = event.x;
		}
		if (firstColOffset < 0)
			firstColOffset = 0;
		int[][] spanCols = internalGetColumnSpan(element);
		int spanColIdx = getSpanColumnIndex(element, event.index);
        if (spanColIdx >= 0 & spanColIdx <= spanCols.length) {
            String string = getColumnText(element, spanColIdx);
            Image image = getColumnImage(element, spanColIdx);
            int imageWidth = 0; 
            if (image != null)
            	imageWidth = image.getBounds().width;
//            Point extent = event.gc.stringExtent(string);
//            int y = event.y + (event.height - extent.y) / 2;
            int y = event.y;
        	if (spanCols[spanColIdx].length > 0) {
        		if (spanCols[spanColIdx].length != 1) {
        			int offset = - (firstColOffset + imageWidth + 5);
        			if (event.index == 0) {
        				offset = offset + firstColOffset;
        			}
        			for (int i = event.index; i > spanCols[spanColIdx][0]; i--) {
        				if (i != 0) {
        					offset += getColumnWidth(i - 1);
        				}
        			}
        			if (event.index == spanCols[spanColIdx][0] && image != null) {
        				event.gc.drawImage(image, event.x, event.y);
        			}
        			event.gc.drawString(string, event.x - offset, y, true);
        		} else {
        			if (image != null)
        				event.gc.drawImage(image, event.x, event.y);
        			event.gc.drawString(string, event.x + 5 + imageWidth, y, true);
        		}
        	} else {
        		throw new IllegalStateException("Span column " + spanColIdx + " out of " + spanCols + " is empty");
        	}
        } else {
        	throw new IllegalStateException("Could not find span columns for element " + String.valueOf(element) + ". Column index is " + event.index + ". Spancolumn index is " + spanColIdx + ". Span columns are " + spanCols);
        }
	}
	
	private Object tmpElement;
	private int[][] tmpColSpans;
	private int firstColOffset = -1;
	
	protected int[][] internalGetColumnSpan(Object element) {
		if (tmpElement == element) {
			return tmpColSpans;
		}
		return getColumnSpan(element);
	}
	
	protected int getSpanColumnIndex(Object element, int columnIndex) {
		int[][] colSpans = null;
		if (tmpElement != element) {
			tmpElement = element;
			tmpColSpans = getColumnSpan(element);
			validateColumnSpan(tmpColSpans);
		}
		colSpans = tmpColSpans;
		for (int i = 0; i < colSpans.length; i++) {
			if (colSpans[i][0] <= columnIndex && colSpans[i][colSpans[i].length -1] >= columnIndex) {
				return i;
			}
		}
		return -1;
	}
	
	protected void validateColumnSpan(int[][] colSpans) {
		// validate the column group
		if (colSpans.length > 0) {
			if (colSpans[0].length < 1)
				throw new IllegalStateException("The first interval in the result of getColumnSpan(Object) does not have any elements.");
			if (colSpans[0][0] != 0) 
				throw new IllegalStateException("The first interval in the result of getColumnSpan(Object) must start with the first column-index (0), it starts with " + colSpans[0][0] + " though");
			int lastIdx = colSpans[0][colSpans[0].length - 1];
			for (int i = 1; i < colSpans.length; i++) {
				if (colSpans[i].length < 1)
					throw new IllegalStateException("Each interval in the result of getColumnSpan(Object) must have at least one entry, the interval with index " + i + " does not have an entry.");
				if (colSpans[i][0] != lastIdx + 1)
					throw new IllegalStateException("The intervals in the result of getColumnSpan(Object) must be continuous, however the interval with index " + i + " starts with " + colSpans[i][0] + ", " + (lastIdx + 1) + " was expected.");
				lastIdx = colSpans[i][colSpans[i].length - 1];
			} 
		}
	}
	
	
	protected int getColumnWidth(int columnIndex) {
		if (columnViewer instanceof TableViewer) {
			return ((TableViewer) columnViewer).getTable().getColumn(columnIndex).getWidth();
		} else if (columnViewer instanceof TreeViewer) {
			return ((TreeViewer) columnViewer).getTree().getColumn(columnIndex).getWidth();
		}
		throw new IllegalStateException("Unsupported ColumnViewer type: " + columnViewer.getClass().getName());
	}
	
	
}
