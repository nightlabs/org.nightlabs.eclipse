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

package org.nightlabs.base.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.composite.XComposite.LayoutMode;

/**
 * This is a subclass of the {@link FormToolkit} which allows also to 
 * use "ordinary" Composites as well.
 * When you create all GUI-Elements with the help of this class
 * you can easily switch the complete GUI-Style from Forms to normal
 * Composites
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 * @deprecated not needed anymore. Simply use {@link XComposite}
 */
public class XFormToolkit 
extends FormToolkit 
{	
	public static enum TOOLKIT_MODE {
		COMPOSITE, FORM;
	}
	
	protected TOOLKIT_MODE currentMode = TOOLKIT_MODE.FORM;
	public TOOLKIT_MODE getCurrentMode() {
		return currentMode;
	}
	public void setCurrentMode(TOOLKIT_MODE mode) {
		this.currentMode = mode;
	}

	/**
	 * @param display
	 */
	public XFormToolkit(Display display) {
		super(display);
	}

	/**
	 * @param colors
	 */
	public XFormToolkit(FormColors colors) {
		super(colors);
	}

	protected Composite createInternalComposite(Composite parent, int style) 
	{
//		return new XComposite(parent, style);
		return new Composite(parent, style);
	}
	
	protected int defaultStyle = SWT.NONE;
	
	@Override
	public Button createButton(Composite parent, String text, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createButton(parent, text, style);
			case COMPOSITE:
				Button b = new Button(parent, style);
				b.setText(text);
				return b;
			default:
				return super.createButton(parent, text, style);
		}			
	}
	
	public Composite createXComposite(Composite parent, int style, 
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		switch (currentMode) 
		{
			case FORM: 
				Composite c = super.createComposite(parent, style);
				c.setLayout(XComposite.getLayout(layoutMode));
				XComposite.setLayoutDataMode(layoutDataMode, c);
				return c;
			case COMPOSITE:
				return new XComposite(parent, style, layoutMode, layoutDataMode);
			default:
				c = super.createComposite(parent, style);
				c.setLayout(XComposite.getLayout(layoutMode));
				XComposite.setLayoutDataMode(layoutDataMode, c);
				return c;
		}					
	}
	
	public Composite createXComposite(Composite parent, int style) 
	{
		return createXComposite(parent, style, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA);
	}	
	
	public Group createGroup(Composite parent, int style, String text) 
	{
		Group g = new Group(parent, style);
		g.setText(text);				
		switch (currentMode) 
		{
			case FORM:
				g.setBackground(getBackground());
				paintBorderFor(g);
				return g;
			case COMPOSITE:
				return g;
			default:
				g.setBackground(getBackground());
				paintBorderFor(g);
				return g;			
		}				 
	}
	
	public Spinner createSpinner(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				Spinner s = new Spinner(parent, style);
				paintBorderFor(s);
				return s;
			case COMPOSITE:
				return new Spinner(parent, style);
			default:
				s = new Spinner(parent, style);
				paintBorderFor(s);
				return s;			
		}		
	}
	
	@Override
	public Composite createComposite(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createComposite(parent, style);
			case COMPOSITE:
				Composite c = createInternalComposite(parent, style);
				return c;
			default:
				return super.createComposite(parent, style);
		}			
	}
	
	@Override
	public Composite createComposite(Composite parent) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createComposite(parent);
			case COMPOSITE:
				return createInternalComposite(parent, defaultStyle);
			default:
				return super.createComposite(parent);
		}			
	}

	@Override
	public ExpandableComposite createExpandableComposite(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createExpandableComposite(parent, style);
			case COMPOSITE:
				return new ExpandableComposite(parent, style);				
			default:
				return super.createExpandableComposite(parent, style);
		}			
	}

	@Override
	public Label createLabel(Composite parent, String text, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createLabel(parent, text, style);
			case COMPOSITE:
				Label l = new Label(parent, style);
				l.setText(text);
				return l;				
			default:
				return super.createLabel(parent, text, style);
		}			
	}

	@Override
	public Label createLabel(Composite parent, String text) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createLabel(parent, text);
			case COMPOSITE:
				Label l = new Label(parent, defaultStyle);
				l.setText(text);
				return l;								
			default:
				return super.createLabel(parent, text);
		}			
	}

	@Override
	public Table createTable(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createTable(parent, style);
			case COMPOSITE:
				return new Table(parent, style);
			default:
				return super.createTable(parent, style);
		}			
	}

	@Override
	public Text createText(Composite parent, String text, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createText(parent, text, style);
			case COMPOSITE:
				Text t = new Text(parent, style);
				t.setText(text);
				return t;
			default:
				return super.createText(parent, text, style);
		}			
	}

	@Override
	public Text createText(Composite parent, String text) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createText(parent, text);
			case COMPOSITE:
				Text t = new Text(parent, defaultStyle);
				t.setText(text);
				return t;
			default:
				return super.createText(parent, text);
		}	
	}

	@Override
	public Tree createTree(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createTree(parent, style);
			case COMPOSITE:
				return new Tree(parent, style);
			default:
				return super.createTree(parent, style);
		}	
	}

	@Override
	public ScrolledForm createScrolledForm(Composite parent) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createScrolledForm(parent);
			case COMPOSITE:
				return super.createScrolledForm(parent);
				// TODO: make XScrolledForm work
//				XScrolledForm form = new XScrolledForm(parent); 
//				form.setExpandHorizontal(true);
//				form.setExpandVertical(true);
//				return form;
			default:
				return super.createScrolledForm(parent);
		}			
	}

	@Override
	public Label createSeparator(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createSeparator(parent, style);
			case COMPOSITE:
				return new Label(parent, SWT.SEPARATOR | style);
			default:
				return super.createSeparator(parent, style);
		}	
	}

	@Override
	public Section createSection(Composite parent, int sectionStyle) 
	{
		switch (currentMode) 
		{
			case FORM:
				return super.createSection(parent, sectionStyle);
			case COMPOSITE:
				return new XSection(parent, sectionStyle);
			default:
				return super.createSection(parent, sectionStyle);
		}	
	}	
	
	@Override
	public Form createForm(Composite parent) 
	{		
		switch (currentMode) 
		{
			case FORM:
				return super.createForm(parent);
			case COMPOSITE:
				return new XForm(parent, defaultStyle);
			default:
				return super.createForm(parent);
		}	
	}	
	
	@Override
	public void adapt(Composite composite) {
		// TODO Auto-generated method stub
		super.adapt(composite);
	}

	@Override
	public void adapt(Control control, boolean trackFocus, boolean trackKeyboard) {
		// TODO Auto-generated method stub
		super.adapt(control, trackFocus, trackKeyboard);
	}

	@Override
	public Composite createCompositeSeparator(Composite parent) {
		// TODO Auto-generated method stub
		return super.createCompositeSeparator(parent);
	}

	@Override
	public FormText createFormText(Composite parent, boolean trackFocus) {
		// TODO create XFormText
		return super.createFormText(parent, trackFocus);
	}

	@Override
	public Hyperlink createHyperlink(Composite parent, String text, int style) {
		// TODO create XHyperlink
		return super.createHyperlink(parent, text, style);
	}

	@Override
	public ImageHyperlink createImageHyperlink(Composite parent, int style) {
		// TODO create XImageHyperLink
		return super.createImageHyperlink(parent, style);
	}

	@Override
	public ScrolledPageBook createPageBook(Composite parent, int style) {
		// TODO create XScrolledPageBook
		return super.createPageBook(parent, style);
	}
		
	@Override
	public HyperlinkGroup getHyperlinkGroup() {
		// TODO create XHyperlinkGroup
		return super.getHyperlinkGroup();
	}

	@Override
	public void paintBordersFor(Composite parent) 
	{
		switch (currentMode) 
		{
			case FORM:
				super.paintBordersFor(parent);
			case COMPOSITE:
				// Does nothing
			default:
				super.paintBordersFor(parent);
		}	
	}

	public static void paintBorderFor(Widget w) 
	{
		w.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);		
	}
	
	public Color getBackground() 
	{
		switch (currentMode) 
		{
			case FORM:
				return getColors().getBackground();
			case COMPOSITE:
				// TODO: return standard Composite background
				 return null;
			default:
				return getColors().getBackground();
		}			
	}
	
	public Combo createCombo(Composite parent, int style) 
	{
		switch (currentMode) 
		{
			case FORM:
				Combo c = null;
				if ((style & SWT.BORDER) != 0) {					
					c = new Combo(parent, style ^ SWT.BORDER);
					paintBorderFor(c);					
					paintBorderFor(parent);
				} else {
					c = new Combo(parent, style);					
				} 					
				return c;
			case COMPOSITE:
				 return new Combo(parent, style);
			default:
				c = new Combo(parent, style);
				paintBorderFor(c);
				return c;
		}					
	}
}
