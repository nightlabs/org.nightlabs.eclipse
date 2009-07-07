/**
 *
 */
package org.nightlabs.base.ui.labelprovider;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

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

	/**
	 * Get the alignment for the given column-group. If {@link SWT#NONE} is returned here,
	 * the alignment from the first real column in the column group will be used, otherwise
	 * one of the values returned here. Either {@link SWT#LEFT}, {@link SWT#RIGHT} or {@link SWT#CENTER}.
	 * <p>
	 * The default implementation returns {@link SWT#NONE} to indicate that the value from
	 * the column definition should be used. Override this method to change this behaviour.
	 * </p>
	 *
	 * @param element The element the column alignment is queried for.
	 * @param spanColIndex The index of the column-group the alignment is queried for.
	 * @return {@link SWT#NONE} to indicate that the columns (TreeColumn/TableColumn) value should be used. Return
	 * 		either {@link SWT#LEFT}, {@link SWT#RIGHT} or {@link SWT#CENTER} to override it.
	 */
	protected int getColumnAlignment(Object element, int spanColIndex) {
		return SWT.NONE;
	}


	@Override
	protected void measure(Event event, Object element) {
		internalIndexElement(event, element);
		int spanColIdx = internalGetSpanColumnIndex(element, event.index);
		if (spanColIdx < 0) {
			event.setBounds(new Rectangle(event.x, event.y, event.width, event.height));
		} else {
			int[][] colSpans = idxColSpans;
			Point extent = idxColTextExtends[spanColIdx];
			Image image = idxColImages[spanColIdx];
			int imageWidth = 0;
			if (image != null)
				imageWidth = image.getBounds().width;
			event.setBounds(new Rectangle(event.x , event.y, extent.x / colSpans[spanColIdx].length + imageWidth , extent.y));
		}
	}

	@Override
	protected void paint(Event event, Object element) {
		// index the data for the current element
		internalIndexElement(event, element);

		if (event.index == 0) {
			// the firstColOffset is used to honor the indentation in trees
			firstColOffset = event.x;
		}
		if (firstColOffset < 0)
			firstColOffset = 0;

		int[][] spanCols = internalGetColumnSpan(element);
		int spanColIdx = internalGetSpanColumnIndex(element, event.index);
        if (spanColIdx >= 0 && spanColIdx <= spanCols.length) {
            String string = idxColTexts[spanColIdx];
            Image image = idxColImages[spanColIdx];
            int imageWidth = 0;
            if (image != null)
            	imageWidth = image.getBounds().width;

            // If we have an alignment != SWT.LEFT we compute the offset based on the column width
            int alignmentOffset = 0;
            int colAlignment = idxColAlignments[spanColIdx];
            if (colAlignment == SWT.RIGHT) {
            	alignmentOffset = idxColWidths[spanColIdx] - (idxColTextExtends[spanColIdx].x + 5);
            } else if (colAlignment == SWT.CENTER) {
            	alignmentOffset = idxColWidths[spanColIdx] / 2 - idxColTextExtends[spanColIdx].x / 2;
            }

            int y = event.y;
        	if (spanCols[spanColIdx].length > 0) {
        		int offset = 0;

        		if (alignmentOffset != 0) {
        			// If an alignment-offset was set, we take this offset
        			offset = - alignmentOffset;
        			if (event.index == 0) {
        				offset = offset + firstColOffset;
        			}
        		} else {
        			// If we align left, we use a little space and the image width as offset
        			offset = - (firstColOffset + imageWidth + 5);
            		if ((event.index == 0 && spanCols[spanColIdx].length > 1) || (spanCols[spanColIdx].length == 1)) {
            			offset = offset + firstColOffset;
            		}
        		}

        		// If we are in a column of a group we substract the width of the previous columns from the offset
        		for (int i = event.index; i > spanCols[spanColIdx][0]; i--) {
        			if (i != 0) {
        				offset += internalGetColumnWidth(i - 1);
        			}
        		}

        		if (string != null)
        			event.gc.drawString(string, event.x - offset, y, true);

        		if (event.index == spanCols[spanColIdx][0] && image != null) {
        			event.gc.drawImage(image, event.x, event.y);
        		}
        	} else {
        		throw new IllegalStateException("Span column " + spanColIdx + " out of " + spanCols + " is empty");
        	}
        } else {
        	throw new IllegalStateException("Could not find span columns for element " + String.valueOf(element) + ". Column index is " + event.index + ". Spancolumn index is " + spanColIdx + ". Span columns are " + spanCols);
        }
	}

	/**
	 * We have refresh bugs, because our "idxElement" is changed, but the reference here stays the same. Hence,
	 * I introduce this constant temporarily until we find a way to clear our cache whenever TreeViewer.refresh() or TableViewer.refresh() is called.
	 * Marco.
	 */
	private static final boolean USE_CACHE = false;
	private Object idxElement;
	private int[][] idxColSpans;
	private int firstColOffset = -1;
	private int[] idxColAlignments = null;
	private String[] idxColTexts = null;
	private Image[] idxColImages = null;
	private int[] idxColWidths = null;
	private Point[] idxColTextExtends = null;

	private int[][] internalGetColumnSpan(Object element) {
		if (USE_CACHE && idxElement == element) {
			return idxColSpans;
		}
		return getColumnSpan(element);
	}

	private void internalIndexElement(Event event, Object element) {
		if (USE_CACHE && idxElement == element) {
			return;
		}
		idxElement = element;
		idxColSpans = getColumnSpan(element);
		internalValidateColumnSpan(idxColSpans);
		idxColAlignments = new int[idxColSpans.length];
		idxColTexts = new String[idxColSpans.length];
		idxColImages = new Image[idxColSpans.length];
		idxColTextExtends = new Point[idxColSpans.length];
		idxColWidths = new int[idxColSpans.length];
		for (int i = 0; i < idxColSpans.length; i++) {
			idxColAlignments[i] = internalValidateColumnAlignment(getColumnAlignment(element, i), idxColSpans[i]);
			idxColTexts[i] = getColumnText(element, i);
			idxColImages[i] = getColumnImage(element, i);
			if (idxColTexts[i] == null)
				idxColTexts[i] = "";
			idxColTextExtends[i] = event.gc.stringExtent(idxColTexts[i]);
			int colWidth = 0;
			if (idxColSpans[i].length == 1) {
				colWidth = internalGetColumnWidth(idxColSpans[i][0]);
			} else {
				for (int j = idxColSpans[i][0]; j <= idxColSpans[i][idxColSpans[i].length - 1]; j++) {
					colWidth += internalGetColumnWidth(j);
				}
			}
			idxColWidths[i] = colWidth;
		}
	}

	private int internalGetSpanColumnIndex(Object element, int columnIndex) {
		int[][] colSpans = idxColSpans;
		for (int i = 0; i < colSpans.length; i++) {
			if (colSpans[i][0] <= columnIndex && colSpans[i][colSpans[i].length -1] >= columnIndex) {
				return i;
			}
		}
		return -1;
	}

	private void internalValidateColumnSpan(int[][] colSpans) {
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

	private int internalValidateColumnAlignment(int alignment, int[] colSpan) {
		if ((alignment & (SWT.LEFT | SWT.RIGHT | SWT.CENTER)) > 0) {
			return alignment;
		} else {
			return internalGetColumnAlignment(colSpan[0]);
		}
	}

	private int internalGetColumnAlignment(int columnIndex) {
		if (columnViewer instanceof TableViewer) {
			Table table = ((TableViewer) columnViewer).getTable();
			if (columnIndex >= table.getColumnCount())
				return SWT.LEFT;

			return table.getColumn(columnIndex).getAlignment();
		} else if (columnViewer instanceof TreeViewer) {
			Tree tree = ((TreeViewer) columnViewer).getTree();
			if (columnIndex >= tree.getColumnCount())
					return SWT.LEFT;

			return tree.getColumn(columnIndex).getAlignment();
		}
		throw new IllegalStateException("Unsupported ColumnViewer type: " + columnViewer.getClass().getName());
	}

	private int internalGetColumnWidth(int columnIndex) {
		if (columnViewer instanceof TableViewer) {
			Table table = ((TableViewer) columnViewer).getTable();
			if (columnIndex >= table.getColumnCount())
				return 0;

			return table.getColumn(columnIndex).getWidth();
		} else if (columnViewer instanceof TreeViewer) {
			Tree tree = ((TreeViewer) columnViewer).getTree();
			if (columnIndex >= tree.getColumnCount())
					return 0;

			return tree.getColumn(columnIndex).getWidth();
		}
		throw new IllegalStateException("Unsupported ColumnViewer type: " + columnViewer.getClass().getName());
	}
}
