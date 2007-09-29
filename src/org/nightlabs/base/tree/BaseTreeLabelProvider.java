/**
 * 
 */
package org.nightlabs.base.tree;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.table.TableLabelProvider;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class BaseTreeLabelProvider extends TableLabelProvider {

	/**
	 * 
	 */
	public BaseTreeLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof TreeNode)
			return ((TreeNode)element).getColumnText(columnIndex);
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public String getText(Object element) {
		return getColumnText(element, 0);
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof TreeNode)
			return ((TreeNode)element).getColumnImage(columnIndex);
		return super.getColumnImage(element, columnIndex);
	}

	@Override
	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}
	
}
