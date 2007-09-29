package org.nightlabs.base.action;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.nightlabs.base.composite.XComboComposite;

public class CComboContributionItem<T> 
extends XContributionItem
{
	private String name;
	public CComboContributionItem(String name, List<T> types, ILabelProvider labelProvider) {
		super();
		this.name = name;
		this.types = types;
		this.labelProvider = labelProvider;
	}
	
	public CComboContributionItem(String id, String name, List<T> types, ILabelProvider labelProvider) {
		super(id);
		this.name = name;
		this.types = types;
		this.labelProvider = labelProvider;		
	}
	
	private List<T> types;
	private ILabelProvider labelProvider;		
	private XComboComposite<T> comboComposite; 
	protected XComboComposite<T> getControl() {
		return comboComposite;
	}
	
  /**
   * Creates and returns the control for this contribution item
   * under the given parent composite.
   *
   * @param parent the parent composite
   * @return the new control
   */
  protected Control createControl(Composite parent) 
  {
  	comboComposite = new XComboComposite<T>(parent, XComboComposite.getDefaultWidgetStyle(parent), 
  			(String)null, labelProvider);
  	comboComposite.setInput(types);
  	return comboComposite;
  }	
  
	private ToolItem toolitem = null;	  
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
		toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
		Control control = createControl(parent);
		toolitem.setControl(control);	
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

  private CoolItem coolItem;
	@Override
	public void fill(CoolBar parent, int index) 
	{
		coolItem = new CoolItem(parent, SWT.SEPARATOR, index);
		Control control = createControl(parent);
		coolItem.setControl(control);
	}
	
	private MenuItem menuItem;
	@Override
	public void fill(Menu menu, int index) 
	{
		menuItem = new MenuItem(menu, SWT.CASCADE, index);
		menuItem.setText(name);
		for (int i=0; i<types.size(); i++) 
		{
			Object element = types.get(i);
			// TODO create MenuItems for each entry
//			MenuItem item = new MenuItem();
		}
	}		
  
	protected void setSize() 
	{
		if (toolitem != null) 
			toolitem.setWidth(computeWidth(getControl()));
		
		if (coolItem != null)
			coolItem.setSize(computeWidth(getControl()), computeHeight(getControl()));
		
		if (comboComposite != null)
			getControl().setSize(computeWidth(getControl()), computeHeight(getControl()));
	}  
}
