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

package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.form.NightlabsFormsToolkit;
import org.nightlabs.base.toolkit.IToolkit;

public class XComposite extends Composite
{
	private ChildStatusController childStatusController = new ChildStatusController();

	/**
	 * Enum for different (Grid)Layout types.
	 */
	public static enum LayoutMode {
		/**
		 * No layout
		 */
		NONE, 
		/**
		 * Wrapping with normal indents and spacing
		 */
		ORDINARY_WRAPPER,
		/**
		 * Wrapping with zero-indents at the margins, but normal spacing within
		 */
		TIGHT_WRAPPER,
		/**
		 * Wrapping with zero intents at top and bottom, normal spacing
		 */
		TOP_BOTTOM_WRAPPER, 
		/**
		 * Wrapping with zero intents at left and right, normal spacing
		 */
		LEFT_RIGHT_WRAPPER,
		/**
		 * Wrapping with zero-indents and spacing
		 */
		TOTAL_WRAPPER
	}

	/**
	 * Enum for Layout data configurations
	 */
	public static enum LayoutDataMode {
		/**
		 * No layout data
		 */
		NONE, 
		/**
		 * GridData filling both
		 */
		GRID_DATA,
		/**
		 * GridData filling horizontal
		 */
		GRID_DATA_HORIZONTAL
	}

	/**
	 * returns a GridLayout for the given layoutMode
	 * @return a GridLayout for the given layoutMode
	 */
	public static GridLayout getLayout(LayoutMode layoutMode)
	{
		return getLayout(layoutMode, null);
	}
	
	public static GridLayout getLayout(LayoutMode layoutMode, GridLayout layout)
	{
		return getLayout(layoutMode, layout, 1);
	}
	
	/**
	 * Configures the given GridLayout to the appropriate layout mode.
	 * 
	 * @param layoutMode The layout mode to apply
	 * @param layout The layout to configure
	 */
	public static void configureLayout(LayoutMode layoutMode, GridLayout layout) {
		switch (layoutMode) 
		{
			case NONE:
			case ORDINARY_WRAPPER:
				return;
			case TIGHT_WRAPPER:
//				layout.horizontalSpacing = 0;
//				layout.verticalSpacing = 0;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				layout.marginLeft = 0;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.marginBottom = 0;
				return;
			case TOP_BOTTOM_WRAPPER:
//				layout.verticalSpacing = 0;
				layout.marginHeight = 0;
				layout.marginTop = 0;
				layout.marginBottom = 0;
				return;
			case LEFT_RIGHT_WRAPPER:
//				layout.horizontalSpacing = 0;
				layout.marginWidth = 0;
				layout.marginLeft = 0;
				layout.marginRight = 0;
				return;		
			case TOTAL_WRAPPER:
				layout.horizontalSpacing = 0;
				layout.verticalSpacing = 0;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				layout.marginLeft = 0;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.marginBottom = 0;
				return;				
			default:
				throw new IllegalArgumentException("layoutMode = " + layoutMode + " is unknown!"); //$NON-NLS-1$ //$NON-NLS-2$
		}		
	}
	
	/**
	 * Modifies a GridLayout to the appropriate {@link LayoutMode}.
	 * If the given layout is <code>null</code> a new one will
	 * be created.
	 * 
	 * @param layoutMode the layoutMode to set
	 * @param layout the GridLayout to modify
	 * @return the modified GridLayout
	 * @see #configureLayout(org.nightlabs.base.composite.XComposite.LayoutMode, GridLayout)
	 */
	public static GridLayout getLayout(LayoutMode layoutMode, GridLayout layout, int cols)
	{
		if (LayoutMode.NONE == layoutMode)
			return null;

		if (layout == null)
			layout = new GridLayout(cols, false);
		configureLayout(layoutMode, layout);
		return layout;
	}	
	
	public static void setLayoutDataMode(LayoutDataMode layoutDataMode, Control c) 
	{
		switch (layoutDataMode) 
		{
			case NONE:
				// nothing
				break;
			case GRID_DATA:
				GridData gridData = new GridData(GridData.FILL_BOTH);
				c.setLayoutData(gridData);
				break;
			case GRID_DATA_HORIZONTAL:
				GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
				c.setLayoutData(gridData2);
				break;				
			default:
				throw new IllegalArgumentException("layoutDataMode = " + layoutDataMode + " is unknown!"); //$NON-NLS-1$ //$NON-NLS-2$
		}	 
	}
	
	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode)} with
	 * <code>layoutMode = </code> {@link LayoutMode#ORDINARY_WRAPPER}
	 */
	public XComposite(Composite parent, int style)
	{
		this(parent, style, LayoutMode.ORDINARY_WRAPPER);
	}

	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode, LayoutDataMode)}
	 * with <code>layoutDataMode = </code> {@link LayoutDataMode#GRID_DATA}
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * 
	 * @see LayoutMode
	 * @see LayoutDataMode
	 */
	public XComposite(Composite parent, int style, LayoutMode layoutMode)
	{
		this(parent, style, layoutMode, LayoutDataMode.GRID_DATA);
	}
	
	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode, LayoutDataMode)}
	 * with <code>layoutDataMode = </code> {@link LayoutDataMode#GRID_DATA}
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * @param cols the number of columns this Gridlayout shall have.
	 * 
	 * @see LayoutMode
	 * @see LayoutDataMode
	 */
	public XComposite(Composite parent, int style, LayoutMode layoutMode, int cols) {
		this(parent, style, layoutMode, LayoutDataMode.GRID_DATA, cols);
	}

	/**
	 * Calls {@link #XComposite(Composite, int, LayoutMode, LayoutDataMode)}
	 * with <code>layoutMode = </code> {@link LayoutMode#ORDINARY_WRAPPER}
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutDataMode the LayoutDataMode to set
	 *
	 * @see LayoutMode
	 * @see LayoutDataMode 
	 */
	public XComposite(Composite parent, int style, LayoutDataMode layoutDataMode)
	{
		this(parent, style, LayoutMode.ORDINARY_WRAPPER, layoutDataMode);
	}

	/**
	 * creates a Composite with the appropriate layoutMode and layoutDataMode
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * @param layoutDataMode the LayoutDataMode to set
	 * 
	 * @see LayoutMode
	 * @see LayoutDataMode  
	 */
	public XComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		this(parent, style, layoutMode, layoutDataMode, 1);
	}
	
	/**
	 * creates a Composite with the appropriate layoutMode and layoutDataMode and the specified number of columns.
	 * 
	 * @param parent the parent Composite
	 * @param style the SWT style flag
	 * @param layoutMode the layoutMode to set
	 * @param layoutDataMode the LayoutDataMode to set
	 * @param cols the number of columns of the grid layout
	 * 
	 * @see LayoutMode
	 * @see LayoutDataMode
	 */
	public XComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols)
	{
		super(parent, style);

		this.setForeground(parent.getForeground());
		this.setBackground(parent.getBackground());
//	 force all XComposites to have the same toolkit as the highest one
		toolkit = XComposite.retrieveToolkit(parent);

		setLayout(getLayout(layoutMode, null, cols));
		setLayoutDataMode(layoutDataMode, this);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled)
	{
		if (enabled == isEnabled())
			return;
		childStatusController.setEnabled(this, enabled);
		super.setEnabled(enabled);
	}

	/**
	 * This is a convenience method.
	 *
	 * @return Returns the same instance as <tt>Composite.getLayout()</tt>, but you don't need to cast anymore.
	 * @see Composite#getLayout()
	 */
	public GridLayout getGridLayout()
	{
		return (GridLayout)getLayout();
	}

	/**
	 * This is a convenience method.
	 *
	 * @return Returns the same instance as <tt>Control.getLayoutData()</tt>, but you don't need to cast anymore.
	 * @see org.eclipse.swt.widgets.Control#getLayoutData()
	 */
	public GridData getGridData()
	{
		if (getLayoutData() == null)
			setLayoutData(new GridData());
		
		return (GridData) getLayoutData();
	}
	
	protected IToolkit toolkit;
	
	public IToolkit getToolkit() {
		return getToolkit(false);
	}
	
	public IToolkit getToolkit(boolean createIfNotSet) 
	{
		if (toolkit != null)
			return toolkit;
		
		if (createIfNotSet)
			toolkit = new NightlabsFormsToolkit(Display.getDefault());
		
		return toolkit;
	}
	
	/**
	 * Assigns this composite a toolkit.
	 */
	public void setToolkit(IToolkit toolkit) {
		this.toolkit = toolkit;
	}
	
	@Override
	public void layout(boolean ignoreCachedInformation, boolean recurseDown) {
		if (toolkit != null) { 
			adaptToToolkit();
		}
		super.layout(ignoreCachedInformation, recurseDown);
		super.redraw();
	}
	
	public void adaptToToolkit() {
		if (toolkit != null)
			adaptComposite(this, toolkit, true);
	}
	
	private void adaptComposite(Composite comp, IToolkit toolkit, boolean checkChildrenForBorders) {
		toolkit.adapt(comp);
		
		for (Control child : comp.getChildren()) {
			boolean paintBorder = false;
			if (checkChildrenForBorders)
			 paintBorder = toolkit.checkForBorders(child);

			// stop at XComposite children, since they 
			// will adapt everything beneath themselves through the call to child.layout(bool, bool)
			// #layout(boolean, boolean) isn't called for every XComposite, in fact in nearly none... damn
			// TODO: But since it is called few times the adaption is done too often and too many border painters
			// 			 are added. This needs some performance improvements!
//			if (child instanceof XComposite) {
//				return;
//			}
			
			if (child instanceof Composite) {
//			 if a painter has been added and child is an own widget -> adapt elements beneath child but 
//				don't draw additional borders beneath child
				adaptComposite((Composite)child, toolkit, !paintBorder);
			}
			else
				toolkit.adapt(child, false, false);
		}
	}
	
	/**
	 * Returns the boarder flag according to the context this composite is used in;
	 * Forms => SWT.NONE, since the toolkit draws one if needed
	 * Other => SWT.Border <b>
	 * 
	 * <p>This method should be called if you want to create a border in any context but don't want to 
	 * have a double border in the Form context.</p>
	 * 
	 * <p>The check of being in a form or not is done by checking the assigned Toolkit and if
	 * necessary all the way up to the root of the composite tree.</p>
	 * 
	 * @return the boarder flag according to the context this composite is used in; Forms => SWT.NONE, 
	 * since the toolkit draws one if needed; Other => SWT.Border
	 */
	public int getBorderStyle() {
		return XComposite.getBorderStyle(this);
	}
	
	/**
	 * @see #getBorderStyle()
	 * @param comp the composite starting from which we traverse the composite tree upwards. 
	 * @return  the boarder flag according to the context this composite is used in; Forms => SWT.NONE, 
	 * since the toolkit draws one if needed; Other => SWT.Border
	 */
	public static int getBorderStyle(Composite comp) {
		// walk up the composite tree and check the toolkit 
		IToolkit toolkit = retrieveToolkit(comp);
		if (toolkit != null)
			return toolkit.getBorderStyle();
			
		// if no Xcomposite in the tree above this one has a toolkit set 
		// => assume we're in no FormPage context
		return SWT.BORDER;
	}
	
	private static IToolkit retrieveToolkit(Composite comp) {
		Composite tmp = comp;
		while( tmp != null ) {
			if (tmp instanceof XComposite) {
					return ((XComposite) tmp).toolkit;
			}
			tmp = tmp.getParent();
		} // walk up the composite tree
		
		return null;
	}
	
}
