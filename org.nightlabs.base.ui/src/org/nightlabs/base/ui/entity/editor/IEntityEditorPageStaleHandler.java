/**
 * 
 */
package org.nightlabs.base.ui.entity.editor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * These handlers are used by the {@link EntityEditorStaleHandler}s of {@link EntityEditor}s
 * to display changed {@link IEntityEditorPageController}s to the user
 * and present him a choice of actions.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IEntityEditorPageStaleHandler extends Runnable {

	/**
	 * Get the {@link IEntityEditorPageController} associated to this handler.
	 * @return
	 */
	IEntityEditorPageController getPageController();
	/**
	 * Get the cellEditor for this handler.
	 * The tree the {@link IEntityEditorPageStaleHandler}s are displayed
	 * in create a {@link EditingSupport} for the second column
	 * and will use the handler to control it.
	 * <p>
	 * Not that this method is required to create the cellEditor lazily
	 * to the given parent and return this one on subsequent calls.
	 * </p>
	 * @param parent The parent to use.
	 * @param element The element to create the cell editor for (this instance of {@link IEntityEditorPageStaleHandler})
	 * @return The lazily created instance of the {@link CellEditor} for this handler.
	 */
	CellEditor getCellEditor(Composite parent, Object element);
	/**
	 * Get the value for the {@link CellEditor} of this handler.
	 * @param element The element to get the value for (this instance of {@link IEntityEditorPageStaleHandler})
	 * @return The value for this handler.
	 */
	Object getValue(Object element);
	/**
	 * Set the value for this handler as returned by the {@link CellEditor}.
	 * @param element The element to set the value for (this instance of {@link IEntityEditorPageStaleHandler}).
	 * @param value The value to set as returned by the {@link CellEditor}.
	 */
	void setValue(Object element, Object value);
	/**
	 * Get the LableProvider for this handler.
	 * It is responsible to return a value for the first and second row.
	 * The first row should describe the controller, the second is for
	 * the handler to retrieve user input.
	 * @return The LabelProvider.
	 */
	ITableLabelProvider getLabelProvider();
	
}
