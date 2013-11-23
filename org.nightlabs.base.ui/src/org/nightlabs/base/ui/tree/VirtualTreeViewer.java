/**
 * 
 */
package org.nightlabs.base.ui.tree;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * This is a subclass of {@link TreeViewer} where the method {@link #replace(Object, int, Object)} as well
 * as {@link #setChildCount(Object, int)} are overridden, because the default implementation is too slow
 * under windows when having a huge amount of child elements. 
 *  
 * @author Daniel Mazurek
 */
public class VirtualTreeViewer extends TreeViewer 
{
	private static final Logger logger = Logger.getLogger(VirtualTreeViewer.class);
	
	public VirtualTreeViewer(Composite parent, int style) {
		super(parent, style);
	}

	public VirtualTreeViewer(Composite parent) {
		super(parent);
	}

	public VirtualTreeViewer(Tree tree) {
		super(tree);
	}

	/**
	 * Overridden original method and copied the code. 
	 * Only the selection dependent parts were commented out, because these parts take quite long
	 * under windows and are not urgently necessary.
	 * 
	 * @see org.eclipse.jface.viewers.TreeViewer#replace(java.lang.Object, int, java.lang.Object)
	 */
	@Override
	public void replace(Object parentElementOrTreePath, int index, Object element) 
	{
		long start = System.currentTimeMillis();
//		super.replace(parentElementOrTreePath, index, element);
		
//		Item[] selectedItems = getSelection(getControl());
//		TreeSelection selection = (TreeSelection) getSelection();
		Widget[] itemsToDisassociate;
		if (parentElementOrTreePath instanceof TreePath) {
			TreePath elementPath = ((TreePath) parentElementOrTreePath)
					.createChildPath(element);
			itemsToDisassociate = internalFindItems(elementPath);
		} else {
			itemsToDisassociate = internalFindItems(element);
		}
		if (internalIsInputOrEmptyPath(parentElementOrTreePath)) {
			if (index < getTree().getItemCount()) {
				TreeItem item = getTree().getItem(index);
//				selection = adjustSelectionForReplace(selectedItems, selection, item, element, getRoot());
				// disassociate any different item that represents the
				// same element under the same parent (the tree)
				for (int i = 0; i < itemsToDisassociate.length; i++) {
					if (itemsToDisassociate[i] instanceof TreeItem) {
						TreeItem itemToDisassociate = (TreeItem) itemsToDisassociate[i];
						if (itemToDisassociate != item
								&& itemToDisassociate.getParentItem() == null) {
							int indexToDisassociate = getTree().indexOf(
									itemToDisassociate);
							disassociate(itemToDisassociate);
							getTree().clear(indexToDisassociate, true);
						}
					}
				}
				Object oldData = item.getData();
				updateItem(item, element);
				if (!VirtualTreeViewer.this.equals(oldData, element)) {
					item.clearAll(true);
				}
			}
		} else {
			Widget[] parentItems = internalFindItems(parentElementOrTreePath);
			for (int i = 0; i < parentItems.length; i++) {
				TreeItem parentItem = (TreeItem) parentItems[i];
				if (index < parentItem.getItemCount()) {
					TreeItem item = parentItem.getItem(index);
//					selection = adjustSelectionForReplace(selectedItems, selection, item, element, parentItem.getData());
					// disassociate any different item that represents the
					// same element under the same parent (the tree)
					for (int j = 0; j < itemsToDisassociate.length; j++) {
						if (itemsToDisassociate[j] instanceof TreeItem) {
							TreeItem itemToDisassociate = (TreeItem) itemsToDisassociate[j];
							if (itemToDisassociate != item
									&& itemToDisassociate.getParentItem() == parentItem) {
								int indexToDisaccociate = parentItem
										.indexOf(itemToDisassociate);
								disassociate(itemToDisassociate);
								parentItem.clear(indexToDisaccociate, true);
							}
						}
					}
					Object oldData = item.getData();
					updateItem(item, element);
					if (!VirtualTreeViewer.this.equals(oldData, element)) {
						item.clearAll(true);
					}
				}
			}
		}
//		// Restore the selection if we are not already in a nested preservingSelection:
//		if (!preservingSelection) {
//			setSelectionToWidget(selection, false);
//			// send out notification if old and new differ
//			ISelection newSelection = getSelection();
//			if (!newSelection.equals(selection)) {
//				handleInvalidSelection(selection, newSelection);
//			}
//		}		
		
		if (logger.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - start;
			logger.debug(duration+" ms took replace("+parentElementOrTreePath+", "+index+", "+element+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	/**
	 * Overridden original method and copied the code. 
	 * Only the preserving selection and isBusy() parts were commented out, because these parts take quite long
	 * under windows and are not urgently necessary.
	 * @see org.eclipse.jface.viewers.TreeViewer#setChildCount(java.lang.Object, int)
	 */
	@Override
	public void setChildCount(Object elementOrTreePath, int count) {
		long start = System.currentTimeMillis();
//		super.setChildCount(elementOrTreePath, count);	
		
		if (internalIsInputOrEmptyPath(elementOrTreePath)) {
			getTree().setItemCount(count);
			return;
		}
		Widget[] items = internalFindItems(elementOrTreePath);
		for (int i = 0; i < items.length; i++) {
			TreeItem treeItem = (TreeItem) items[i];
			treeItem.setItemCount(count);
		}
		
		if (logger.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - start;
			logger.debug(duration+" ms took setChildCount("+elementOrTreePath+", "+count+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}		
	}

}
