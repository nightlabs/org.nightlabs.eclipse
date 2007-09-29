/**
 * 
 */
package org.nightlabs.base.tree;

import org.eclipse.jface.viewers.Viewer;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class BaseTreeContentProvider extends TreeContentProvider {

	private TreeNode input;
	
	/**
	 * 
	 */
	public BaseTreeContentProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return ((TreeNode)inputElement).getChildren().toArray();
	}
	
	
	@Override
	public Object getParent(Object element) {
		return ((TreeNode)element).getParent();
	}
	
	@Override
	public boolean hasChildren(Object element) {
		return ((TreeNode)element).hasChildren();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return ((TreeNode)parentElement).getChildren().toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {		
		super.inputChanged(viewer, oldInput, newInput);
		if ((newInput != null) && (!(newInput instanceof TreeNode)))
			throw new IllegalArgumentException("Expected input of type "+TreeNode.class.getName()+" but found "+newInput.getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$
		this.input = (TreeNode)newInput;
	}
	
	public TreeNode getInput() {
		return input;
	}

}
