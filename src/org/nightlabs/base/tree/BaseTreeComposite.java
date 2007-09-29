/**
 * 
 */
package org.nightlabs.base.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class BaseTreeComposite extends AbstractTreeComposite {

	/**
	 * @param parent
	 */
	public BaseTreeComposite(Composite parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param init
	 */
	public BaseTreeComposite(Composite parent, boolean init) {
		super(parent, init);
	}

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 * @param init
	 * @param headerVisible
	 */
	public BaseTreeComposite(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new BaseTreeContentProvider());
		treeViewer.setLabelProvider(new BaseTreeLabelProvider());
	}

}
