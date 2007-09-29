/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.base.entity.tree;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.nightlabs.base.entity.EntityEditorRegistry;
import org.nightlabs.base.tree.AbstractTreeComposite;
import org.nightlabs.base.tree.TreeContentProvider;
import org.nightlabs.base.util.RCPUtil;

/**
 * Displays a tree of {@link IEntityTreeCategory}s regsitered to 
 * a given viewID and and opens the registered
 * editor for them upon user request.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EntityTree 
extends AbstractTreeComposite 
implements IOpenListener, DisposeListener, IEntityTreeCategoryContentConsumer
{
	/**
	 * Logger used by this class.
	 */
	private static final Logger logger = Logger.getLogger(EntityTree.class);

//	/**
//	 * This is the id the categories to display with this composite
//	 * should be registered to.
//	 */
//	private String viewID;
	
	/**
	 * Member to hold the categories bindings for this tree
	 */
	private IEntityTreeCategoryBinding[] categoryBindings;
	
	/**
	 * This map is used to find out wich tree object belongs 
	 * to wich category for asking the category for labels
	 * and icons.
	 */
	private Map<Object, IEntityTreeCategoryBinding> childBindings;

	public EntityTree(Composite parent, String viewID, int treeStyle) {
		super(parent, treeStyle, true, false, false);
		init();
		categoryBindings = EntityEditorRegistry.sharedInstance().getViewBindings(viewID);
		getTreeViewer().setInput(categoryBindings);
		getTreeViewer().addOpenListener(this);
		getTree().setHeaderVisible(false);
		addDisposeListener(this);
	}
		
	/**
	 * @param parent
	 */
	public EntityTree(Composite parent, String viewID) {
		this(parent, viewID, AbstractTreeComposite.DEFAULT_STYLE_SINGLE);
	}

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 * @param init
	 * @param headerVisible
	 */
	public EntityTree(Composite parent, int style,
			boolean setLayoutData, boolean init, boolean headerVisible) {
		super(parent, style, setLayoutData, init, headerVisible);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.tree.AbstractTreeComposite#createTreeColumns(org.eclipse.swt.widgets.Tree)
	 */
	@Override
	public void createTreeColumns(Tree tree) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.tree.AbstractTreeComposite#setTreeProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new EntityTreeContentProvider());
		treeViewer.setLabelProvider(new EntityTreeLabelProvider());
	}

	/**
	 * The content provider for the entity tree. This provider
	 * will use data obtained by calling methods defined in
	 * {@link IEntityTreeCategory}.
	 */
	protected class EntityTreeContentProvider extends TreeContentProvider 
	{
		private Map<IEntityTreeCategoryBinding, ITreeContentProvider> contentProviders = new HashMap<IEntityTreeCategoryBinding, ITreeContentProvider>();

		private ITreeContentProvider getContentProvider(IEntityTreeCategoryBinding categoryBinding) {
			if (categoryBinding == null)
				return null;
			if (contentProviders.containsKey(categoryBinding))
				return contentProviders.get(categoryBinding);
			ITreeContentProvider result = categoryBinding.createContentProvider(EntityTree.this);
			contentProviders.put(categoryBinding, result);
			return result;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			// either the input is set to a IEntityTreeCategory[]
			if (inputElement instanceof IEntityTreeCategoryBinding[])
				return (IEntityTreeCategoryBinding[])inputElement;
			// or it is something the delegate content provider has to deal with (using node-objects one still could have go-into)
			else {
				ITreeContentProvider contentProvider = getContentProvider(getChildBinding(inputElement));
				if (contentProvider != null)
					return contentProvider.getElements(inputElement);
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.tree.TreeContentProvider#getChildren(java.lang.Object)
		 */
		@Override
		public Object[] getChildren(Object parentElement)
		{
			IEntityTreeCategoryBinding parentBinding = null;
			// check the parent category. Its either the element itself (as published so in getElements)
			if (parentElement instanceof IEntityTreeCategoryBinding)
				parentBinding = (IEntityTreeCategoryBinding)parentElement;
			// or it is somewhere up the tree
			else 
				parentBinding = getChildBinding(parentElement);
			Object[] children = null;
			ITreeContentProvider contentProvider = getContentProvider(parentBinding);
			if (contentProvider != null)
				// if the direct parent is a category call getElements on its ContentProvider
				if (parentElement instanceof IEntityTreeCategoryBinding)
					children = contentProvider.getElements(parentElement);
				// if we're deeper check the getChildren method
				else 
					children = contentProvider.getChildren(parentElement);
			
			if (children != null)
				for (Object object : children)
					addChildBinding(object, parentBinding);
			return children;
			
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.tree.TreeContentProvider#hasChildren(java.lang.Object)
		 */
		@Override
		public boolean hasChildren(Object element)
		{
			// if direct categoryBinding, baby, say yeah
			if (element instanceof IEntityTreeCategoryBinding)				
				return true;
			// else delegate
			else {
				IEntityTreeCategoryBinding binding = getChildBinding(element);
				ITreeContentProvider contentProvider = binding != null ? getContentProvider(binding) : null;
				return (contentProvider != null) ? contentProvider.hasChildren(element) : false;
			}
		}
		
		@Override
		public Object getParent(Object element) {
			// here as well delegate when not at top level 
			if (element instanceof IEntityTreeCategoryBinding)
				return null;
			ITreeContentProvider contentProvider = getContentProvider(getChildBinding(element));
			if (contentProvider != null)
				return contentProvider.getParent(element);
			return super.getParent(element);
		}

		@Override
		public void dispose()
		{
			for (ITreeContentProvider cp : contentProviders.values())
				cp.dispose();

			super.dispose();
		}
	}

	/**
	 * The label provider for the entity tree. This provider
	 * will use data obtained by calling methods defined in
	 * {@link IEntityTreeCategory}.
	 */
	protected class EntityTreeLabelProvider extends LabelProvider implements ITableLabelProvider 
	{
		private Map<IEntityTreeCategoryBinding, ITableLabelProvider> labelProviders = new HashMap<IEntityTreeCategoryBinding, ITableLabelProvider>();
		
		private ITableLabelProvider getLabelProvider(IEntityTreeCategoryBinding binding) {
			if (binding == null)
				return null;			
			if (labelProviders.containsKey(binding))
				return labelProviders.get(binding);
			ITableLabelProvider labelProvider = binding.createLabelProvider();
			labelProviders.put(binding, labelProvider);
			return labelProvider;			
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element)
		{
			return getColumnImage(element, 0);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			return getColumnText(element, 0);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIdx) {
			if(element instanceof IEntityTreeCategoryBinding)
				return ((IEntityTreeCategoryBinding)element).getImage();
			else {
				IEntityTreeCategoryBinding binding = getChildBinding(element);
				ITableLabelProvider labelProvider = getLabelProvider(binding);
				Image image = null;
				if(labelProvider != null)
					image = labelProvider.getColumnImage(element, columnIdx);
				if (image != null)
					return image;
				return binding.getImage();
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIdx) {
			if(element instanceof IEntityTreeCategoryBinding)
				return ((IEntityTreeCategoryBinding)element).getName();
			else {
				ITableLabelProvider labelProvider = getLabelProvider(getChildBinding(element));
				if(labelProvider != null)
					return labelProvider.getColumnText(element, columnIdx);				
				return super.getText(element);
			}
		}
	}

	/**
	 * Add a child for a category. This is used internally
	 * to find out wich tree object belongs to wich category
	 * for asking the category for labels and icons.
	 * @param child The child element to add
	 * @param category The category the child belongs to
	 */
	protected void addChildBinding(Object child, IEntityTreeCategoryBinding categoryBinding)
	{
		if(childBindings == null)
			childBindings = new HashMap<Object, IEntityTreeCategoryBinding>();
		childBindings.put(child, categoryBinding);
	}

	/**
	 * Returns the category for the given entity element.
	 * @param child The child element to get the category for
	 * @return The category for the child element or <code>null</code>
	 * 		if the child is unknown.
	 */
	protected IEntityTreeCategory getChildCategory(Object child)
	{
		IEntityTreeCategoryBinding binding = childBindings == null ? null : childBindings.get(child);
		return binding == null ? null : binding.getEntityTreeCategory();
	}
	
	/**
	 * Returns the category binding for the given entity element.
	 * @param child The child element to get the binding for
	 * @return The category binding for the child element or <code>null</code>
	 * 		if the child is unknown.
	 */
	protected IEntityTreeCategoryBinding getChildBinding(Object child) {
		return childBindings == null ? null : childBindings.get(child);
	}
	
	/**
	 * Returns the {@link IEntityTreeCategory} of the given element
	 * regardless whether the element is itself a categroy or
	 * a child provided by a category.
	 * 
	 * @param element The element to search the category for.
	 * @return The {@link IEntityTreeCategory} of the given element
	 */
	public IEntityTreeCategory getElementCategory(Object element) {
		if (element instanceof IEntityTreeCategoryBinding)
			return ((IEntityTreeCategoryBinding)element).getEntityTreeCategory();
		return getChildCategory(element);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Expand/collapse all categories in the selection
	 * and open the registered editors for all selected
	 * category elements.
	 */
	public void open(OpenEvent event)
	{
		final ISelection _selection = event.getSelection();
		if(_selection.isEmpty())
			return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Object[] selectedElements = ((IStructuredSelection)_selection).toArray();
				for (Object element : selectedElements) {
					if(element instanceof IEntityTreeCategoryBinding) {
						if(getTreeViewer().getExpandedState(element))
							getTreeViewer().collapseToLevel(element, 1);
						else
							getTreeViewer().expandToLevel(element, 1);
					} else {
						IEntityTreeCategoryBinding binding = getChildBinding(element);
						if(binding != null) {
							IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(binding.getEditorID());
							if (editorDescriptor == null) {
								logger.warn("Could not obtain editorDescriptor to open editor for category "+binding.getName()+" and editorID "+binding.getEditorID()); //$NON-NLS-1$ //$NON-NLS-2$
								return;
							}
							try {
								IWorkbenchPage page = RCPUtil.getActiveWorkbenchPage();
								if (page != null)
									page.openEditor(binding.createEditorInput(element), editorDescriptor.getId());
								else
									logger.warn("Could not get active workbench page, no editor was opened."); //$NON-NLS-1$
							} catch (PartInitException e) {
								throw new RuntimeException("Opening editor failed", e); //$NON-NLS-1$
							}
						} else {
							logger.warn("Unknown entity category for element "+element); //$NON-NLS-1$
						}
					}
				}
			}
		});
	}

	public void widgetDisposed(DisposeEvent arg0) {
		getTreeViewer().removeOpenListener(this);
//		for (IEntityTreeCategory category : categories) {
//			category.removeEntityTreeCategoryChangeListener(this);
//		}
	}

	public void contentChanged(final ContentChangedEvent event)
	{
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getTreeViewer().refresh(event.getEntityTreeCategoryBinding());
			}
		});
	}

}
