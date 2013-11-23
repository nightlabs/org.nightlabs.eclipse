package org.nightlabs.base.ui.action;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComboComposite;

public class CComboContributionItem<T>
extends XContributionItem
{
	private String name;
	public CComboContributionItem(String name, List<T> elements, ILabelProvider labelProvider) {
		super();
		this.name = name;
		this.elements = elements;
		this.labelProvider = labelProvider;
	}

	public CComboContributionItem(String id, String name, List<T> elements, ILabelProvider labelProvider) {
		super(id);
		this.name = name;
		this.elements = elements;
		this.labelProvider = labelProvider;
	}

	protected void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	private List<T> elements;
	private ILabelProvider labelProvider;
	private XComboComposite<T> comboComposite;
	protected XComboComposite<T> getControl() {
		return comboComposite;
	}

	protected int getComboStyle(Composite parent)
	{
		return AbstractListComposite.getDefaultWidgetStyle(parent);
	}

  /**
   * Creates and returns the control for this contribution item
   * under the given parent composite.
   *
   * @param parent the parent composite
   * @return the new control
   */
  protected XComboComposite<T> createControl(Composite parent)
  {
  	comboComposite = new XComboComposite<T>(
  			parent,
  			getComboStyle(parent),
  			(String)null,
  			labelProvider
  	);
  	comboComposite.setInput(elements);
  	comboComposite.setEnabled(isEnabled());
  	return comboComposite;
  }

//	private ToolItem toolitem = null;
  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method calls the <code>createControl</code> framework method to
   * create a control with a combo under the given parent, and then creates
   * a new tool item to hold it.
   *
   * @param parent The ToolBar to add the new control to
   * @param index Index
   */
  @Override
  public void fill(ToolBar parent, int index)
  {
//		toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
//		Control control = createControl(parent);
//		toolitem.setControl(control);
		fillToolBar(parent, index);
  }

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (comboComposite != null)
			comboComposite.setEnabled(enabled);
	}

 /**
  * The control item implementation of this <code>IContributionItem</code>
  * method calls the <code>createControl</code> framework method.
  *
  * @param parent The parent of the control to fill
  */
  @Override
  public void fill(Composite parent) {
  	createControl(parent);
  }

  class ToolBarContributionItem extends ContributionItem
	{
		public ToolBarContributionItem(String id) {
			super(id);
		}

		@Override
		public void fill(ToolBar parent, int index) {
			final ToolItem toolItem = new ToolItem(parent, SWT.SEPARATOR);
			toolItem.setControl(createControl(parent));
			toolItem.setWidth(getWidth());
		}
	}

  protected int getWidth()
  {
//  	return 100;
  	return getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
  }

  private void fillToolBar(ToolBar parent, int index)
	{
//		final ToolItem toolItem = new ToolItem(parent, SWT.SEPARATOR);

		IContributionManager toolBarManager = getParent();
		String id1 = ToolBarContributionItem.class.getName();
		toolBarManager.remove(id1);
		toolBarManager.add(new ToolBarContributionItem(id1));

//		Control control = createControl(parent);
//		toolItem.setControl(control);
//		toolItem.setWidth(100);
//

//		toolItem.setData(new SubContributionItem(this));

//		final Menu menu = createMenu(new Menu(RCPUtil.getActiveShell(), SWT.POP_UP));
//		setSelectedItem(createSearchItem(toolBar, menu));
////		selectedItem.setData(new SubContributionItem(this));
//		getSelectedItem().addDisposeListener(new DisposeListener(){
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				logger.debug("selectedItem DISPOSE!!!"); //$NON-NLS-1$
//			}
//		});

		parent.pack();
	}

  private CoolItem coolItem;
	@Override
	public void fill(CoolBar parent, int index)
	{
//		coolItem = new CoolItem(parent, SWT.SEPARATOR, index);
//		Control control = createControl(parent);
//		coolItem.setControl(control);

		// TODO this method is currently not used - no idea whether it would work! Maybe remove it? I have no idea, how to test it. Marco.

//		final CoolBar coolBar = parent;
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP);

		fillToolBar(toolBar, index);
//
//		CoolItem coolItem = new CoolItem(coolBar, SWT.SEPARATOR);
//		coolItem.setControl(toolBar);
//
////		// FIXME: set size for contributionItem leads to strange layout problems when resetting perspective
//		Point size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//		Point coolSize = coolItem.computeSize(size.x, size.y);
//		int diffX = 0;
//		coolItem.setSize(coolSize.x - diffX, coolSize.y);
//		coolItem.setMinimumSize(coolSize.x - diffX, coolSize.y);
//		coolItem.setPreferredSize(coolSize.x - diffX, coolSize.y);
////		toolBar.layout(true, true);
////		coolBar.layout(true, true);
//
//		coolBar.pack();
		parent.pack();
	}

	private MenuItem menuItem;
	@Override
	public void fill(Menu menu, int index)
	{
		menuItem = new MenuItem(menu, SWT.CASCADE, index);
		menuItem.setText(name);
		for (int i=0; i<elements.size(); i++)
		{
//			Object element = elements.get(i);
			// TODO create MenuItems for each entry
//			MenuItem item = new MenuItem();
		}
	}

	public List<T> getElements() {
		return Collections.unmodifiableList(elements);
	}
	public void setElements(List<T> elements) {
		this.elements = elements;
		if (comboComposite != null)
			comboComposite.setInput(elements);
	}

//	protected void setSize()
//	{
//		if (toolitem != null)
//			toolitem.setWidth(computeWidth(getControl()));
//
//		if (coolItem != null)
//			coolItem.setSize(computeWidth(getControl()), computeHeight(getControl()));
//
//		if (comboComposite != null)
//			getControl().setSize(computeWidth(getControl()), computeHeight(getControl()));
//	}
}
