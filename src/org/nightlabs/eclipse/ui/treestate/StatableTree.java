package org.nightlabs.eclipse.ui.treestate;

import org.eclipse.swt.widgets.Tree;

public interface StatableTree
{
	/**
	 * @return String identifier of the Tree
	 */
	String getID();

	/**
	 * @return Tree
	 */
	Tree getTree();
}
