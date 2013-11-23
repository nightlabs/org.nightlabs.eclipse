/**
 * 
 */
package org.nightlabs.base.ui.entity.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.tree.TreeContentProvider;

/**
 * A tree that displays {@link IEntityEditorPageStaleHandler}s with the pages
 * of their {@link IEntityEditorPageController}s as children.
 * <p>
 * The tree has two columns one to display the handler and the page
 * the other is managed by the {@link IEntityEditorPageStaleHandler} and
 * usually used to modify the action performed by the handler.
 * </p>
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EntityEditorStaleHandlerTree extends AbstractTreeComposite {
	
	/**
	 * ContentProvider used.
	 */
	class ContentProvider extends TreeContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection) {
				return ((Collection) inputElement).toArray();
			}
			return null;
		}
		
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof IEntityEditorPageStaleHandler) {
				IEntityEditorPageStaleHandler staleHandler = (IEntityEditorPageStaleHandler) parentElement;
				List<String> pageNames = new ArrayList<String>(staleHandler.getPageController().getPages().size());
				for (IFormPage page : staleHandler.getPageController().getPages()) {
					pageNames.add(page.getTitle());
				}
				return pageNames.toArray();
			}
			return super.getChildren(parentElement);
		}
		
		@Override
		public boolean hasChildren(Object element) {
			return element instanceof IEntityEditorPageStaleHandler;
		}
	}
	
	/**
	 * LabelProvider used. It will delegate the most to the LabelProvider
	 * of the {@link IEntityEditorPageStaleHandler}.
	 */
	class LabelProvider extends TableLabelProvider {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IEntityEditorPageStaleHandler) {
				return ((IEntityEditorPageStaleHandler) element).getLabelProvider().getColumnText(element, columnIndex);
			}
			if (columnIndex == 0)
				return String.valueOf(element);
			return ""; //$NON-NLS-1$
		}
	}
	
	/**
	 * EditingSupport used for the second column.
	 * It will delegate to the {@link IEntityEditorPageStaleHandler}.
	 */
	class HandlerEditingSupport extends EditingSupport {

		public HandlerEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(Object element) {
			return element instanceof IEntityEditorPageStaleHandler;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if (element instanceof IEntityEditorPageStaleHandler)
				return ((IEntityEditorPageStaleHandler) element).getCellEditor(getTree(), element);
			return null;
		}

		@Override
		protected Object getValue(Object element) {
			if (element instanceof IEntityEditorPageStaleHandler)
				return ((IEntityEditorPageStaleHandler) element).getValue(element);
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (element instanceof IEntityEditorPageStaleHandler) {
				((IEntityEditorPageStaleHandler) element).setValue(element, value);
				getViewer().update(element, null);
			}
		}
	}
	
	/**
	 * Create a new {@link EntityEditorStaleHandlerTree} for the given parent.
	 * @param parent The parent to use.
	 */
	public EntityEditorStaleHandlerTree(Composite parent) {
		super(parent);
	}

	/**
	 * Create a new {@link EntityEditorStaleHandlerTree} for the given parent.
	 * @param parent The parent to use.
	 * @param init Whether to init the table.
	 */
	public EntityEditorStaleHandlerTree(Composite parent, boolean init) {
		super(parent, init);
	}

	public EntityEditorStaleHandlerTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}

	public EntityEditorStaleHandlerTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible,
			boolean sortColumns) {
		super(parent, style, setLayoutData, init, headerVisible, sortColumns);
	}

	
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		TableLayout l = new TableLayout();
		TreeColumn tc = new TreeColumn(tree, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditorStaleHandlerTree.column.dirtyPage")); //$NON-NLS-1$
		TreeColumn tc2 = new TreeColumn(tree, SWT.LEFT);
		tc2.setText(Messages.getString("org.nightlabs.base.ui.entity.editor.EntityEditorStaleHandlerTree.column.action")); //$NON-NLS-1$
		TreeViewerColumn tvc = new TreeViewerColumn(getTreeViewer(), tc2);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IEntityEditorPageStaleHandler) {
					return ((IEntityEditorPageStaleHandler) element).getLabelProvider().getColumnText(element, 1);
				}
				return super.getText(element);
			}
		});
		tvc.setEditingSupport(new HandlerEditingSupport(getTreeViewer()));
		l.addColumnData(new ColumnWeightData(3));
		l.addColumnData(new ColumnWeightData(2));
		tree.setLayout(l);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
	}

}
