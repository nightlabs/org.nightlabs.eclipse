package org.nightlabs.tableprovider.ui;

import java.util.Set;

import org.eclipse.swt.graphics.Image;

/**
 * Emulates the specificity and instructions on how a entry in a table's column is labelled.
 *
 * @author khaireel at nightlabs dot de
 */
public interface TableLabelProvider<JDOObjectID, JDOObject> {
	/**
	 * Returns a a text for the given element which corresponds to any of the given types in the given scope
	 *
	 * @param types the types (or fieldNames) to return a text for.
	 * @param element the element to return a text for the given types.
	 * @param scope the scope for which texts should be returned
	 * @return a text for the given element which corresponds to the given types
	 */
	public String getText(Set<String> types, JDOObject element, String scope);

	/**
	 * @param types the types to return a text for.
	 * @param element the element to return a text for the given types.
	 * @param scope the scope for which texts should be returned
	 * @return the Image answering to the above parameters.
	 */
	public Image getColumnImage(Set<String> types, JDOObject element, String scope);
}
