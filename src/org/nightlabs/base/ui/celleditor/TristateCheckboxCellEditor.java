package org.nightlabs.base.ui.celleditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 *
 */
public class TristateCheckboxCellEditor
		extends CellEditor
{
  private Boolean state;

  /**
	 *
	 */
	public TristateCheckboxCellEditor()
	{
	}

	/**
	 * @param parent
	 */
	public TristateCheckboxCellEditor(Composite parent)
	{
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public TristateCheckboxCellEditor(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
  protected Control createControl(Composite parent)
  {
//  	Control checkboxControl = super.createControl(parent);
//	   checkbox.addSelectionListener(new SelectionAdapter()
//	   {
//	  	 @Override
//	  	 public void widgetSelected(SelectionEvent e)
//	  	 {
//	  		 Boolean newState;
//	  		 if (checkbox.isEnabled())
//	  			 newState = checkbox.getSelection();
//	  		 else
//	  			 newState = Boolean.FALSE;
//
//	  		 if (checkbox.isEnabled())
//	  		 {
//	  			 if (!state && newState)
//	  				 state = newState;
//	  			 else if (state && ! newState)
//	  			 {
//	  				 checkbox.setEnabled(false);
//	  				 state = null;
//	  			 }
//	  		 }
//	  		 else
//	  		 {
//	  			 checkbox.setEnabled(true);
//	  			 state = Boolean.FALSE;
//	  		 }
//	  	 }
//	   });
	   return null;
  }

	@Override
	protected Object doGetValue()
	{
		return state;
	}

	@Override
	protected void doSetValue(Object value)
	{
		state = (Boolean) value;
	}

	@Override
	protected void doSetFocus()
	{
	}

	@Override
	public void activate()
	{
		if (state == null)
			state = Boolean.FALSE;
		else if (!state)
			state = !state;
		else
			state = null;

		fireApplyEditorValue();
	}

}
