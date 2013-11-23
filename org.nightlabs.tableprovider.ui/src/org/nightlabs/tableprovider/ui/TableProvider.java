package org.nightlabs.tableprovider.ui;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.nightlabs.progress.ProgressMonitor;

/**
 * Interface for obtaining information in a table like manner for certain elements.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 */
public interface TableProvider<ElementID, Element>
extends TableLabelProvider<ElementID, Element>, Serializable
{
	/**
	 * Returns the elements for the given id objects, which contain the necessary information which should be
	 * displayed by this {@link TableProvider}.
	 *
	 * @param objectIDs the ID objects of the elements to return
	 * @param scope the scope to return the necessary data for
	 * @param monitor the {@link ProgressMonitor} to display the progress
	 * @return the Collection of elements for the given id objects with all the information needed to display in the given scope
	 */
	Map<ElementID, Element> getObjects(Collection<ElementID> objectIDs, String scope, ProgressMonitor monitor);

	/**
	 * Returns the amount of columns as types in the given scope.
	 * These types correspond to the types in {@link #getText(Set, Object, Scope)}.
	 *
	 * @return the amount of columns as types in the given scope
	 */
	String[] getTypes(String scope);

	/**
	 * Returns a descriptive name for the given type. Usually this name is the column name.
	 *
	 * @param type the type to get a name for
	 * @return a descriptive name for the given type. Usually this name is the column name.
	 */
	String getTypeName(String type);

	/**
	 * Returns whether this {@link TableProvider} is compatible for the given element in the given scope or not.
	 *
	 * @param element the element to check for.
	 * @param scope the scope.
	 * @return true if the tableProvider is compatible with the given type in the given scope or false if not.
	 */
	boolean isCompatible(Element element, String scope);

	/**
	 *
	 * @return true if this is a generic implementation and should be asked
	 * only if no other special implementation is existing
	 */
	boolean isGeneric();
}
