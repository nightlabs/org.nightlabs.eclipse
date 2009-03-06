package org.nightlabs.eclipse.ui.treestate;

import org.eclipse.swt.widgets.Tree;

/**
 * This interface is used for the {@link TreeStateController} that needs to get an identifier 
 * of the tree by calling {@link StatableTree#getID()} and the {@link StatableTree#getTree} is used
 * for getting the tree from the container that contains the tree.
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public interface StatableTree
{
	/**
	 * Returns the ID for identifying the tree (MyClass.getClass().getName()) for example).
	 * @return String identifier of the Tree
	 */
	String getID();

	/**
	 * Returns the tree.
	 * @return Tree
	 */
	Tree getTree();
}
