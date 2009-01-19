/**
 *
 */
package org.nightlabs.base.ui.table;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * This is used by the {@link GenericInvertViewerSorter} to obtain the
 * {@link Comparator} of a column in a {@link TableViewer} or {@link TreeViewer}.
 * If the label-provider of the Viewer implements this interface
 * its {@link #getColumnComparator(Object, int)} method is used to get the
 * {@link Comparator} for each element object, otherwise the
 * <code>getColumnText()</code> or <code>getText()</code> method is used.
 *
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public interface IColumnComparatorProvider {
//	/**
//	 * Return the Comparable that should be used to sort the column with the given columnIndex.
//	 *
//	 * @param element The element in the table/tree the Comparable should be obtained for.
//	 * @param columnIndex The index of the column to get the Comparable for.
//	 * @return The Comparable that should be used to sort the column with the given columnIndex.
//	 */
//	Comparable<?> getColumnComparable(Object element, int columnIndex);

	/**
	 * Return the {@link Comparator} that should be used to sort the column with the given columnIndex.
	 *
	 * @param element The element in the table/tree the Comparable should be obtained for.
	 * @param columnIndex The index of the column to get the Comparator for.
	 * @return The Comparator that should be used to sort the column with the given columnIndex.
	 */
	Comparator<?> getColumnComparator(Object element, int columnIndex);
}
