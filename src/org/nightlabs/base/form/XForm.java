/**
 * Created Mar 16, 2006, 7:49:40 PM by nick
 */
package org.nightlabs.base.form;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;
import org.nightlabs.base.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.composite.XComposite.LayoutMode;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 * @deprecated
 */
public class XForm extends Form
{
	public XForm(Composite parent, int style)
	{
		this(parent, style, LayoutMode.ORDINARY_WRAPPER);
	}
	
	public XForm(Composite parent, int style, LayoutMode layoutMode)
	{
		this(parent, style, layoutMode, LayoutDataMode.GRID_DATA);
	}

	public XForm(Composite parent, int style, LayoutDataMode layoutDataMode)
	{
		this(parent, style, LayoutMode.ORDINARY_WRAPPER, layoutDataMode);
	}

	public XForm(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style);
	}
}
