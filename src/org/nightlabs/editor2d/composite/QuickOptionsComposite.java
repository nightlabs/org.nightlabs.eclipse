/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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
package org.nightlabs.editor2d.composite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.form.XFormToolkit;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.config.QuickOptionsConfigModule;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class QuickOptionsComposite 
extends XComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public QuickOptionsComposite(Composite parent, int style) 
	{
		super(parent, style);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public QuickOptionsComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		init();
	}

	protected void init() 
	{
		initConfigModule();
		initLayout();
		createComposite(this);
	}
		
	protected void initLayout() 
	{
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		setLayout(layout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	private QuickOptionsConfigModule confMod = null;
	protected QuickOptionsConfigModule getConfigModule() 
	{
		if (confMod == null)
			initConfigModule();
		
		return confMod;
	}
	
	protected void initConfigModule() 
	{
		try {
			confMod = (QuickOptionsConfigModule) Config.sharedInstance().createConfigModule(QuickOptionsConfigModule.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		} 
	}
	
//	private FormToolkit toolkit = null;
	private NightlabsFormsToolkit toolkit = null;
	private ScrolledForm form = null;	
	protected void createComposite(Composite parent) 
	{
//		toolkit = new XFormToolkit(parent.getDisplay());
		toolkit = new NightlabsFormsToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);		
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Section sectionDuplicate = toolkit.createSection(form.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.EXPANDED);
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		sectionDuplicate.setLayoutData(td);		
		
		sectionDuplicate.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
		  }
		});		
		sectionDuplicate.setText(Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.section.duplicate.text")); //$NON-NLS-1$
		sectionDuplicate.setDescription(Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.section.duplicate.description")); //$NON-NLS-1$
		Composite sectionClientDuplicate = toolkit.createComposite(sectionDuplicate);	
		sectionClientDuplicate.setLayout(new GridLayout(2, false));				
		createEntry(sectionClientDuplicate, Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.label.distanceX"), CLONE_X); //$NON-NLS-1$
		createEntry(sectionClientDuplicate, Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.label.distanceY"), CLONE_Y); //$NON-NLS-1$
		sectionDuplicate.setClient(sectionClientDuplicate);
		
		Section sectionMove = toolkit.createSection(form.getBody(), 
			  Section.DESCRIPTION|Section.TITLE_BAR|
			  Section.TWISTIE|Section.EXPANDED);
		TableWrapData tdMove = new TableWrapData(TableWrapData.FILL_GRAB);
		sectionMove.setLayoutData(tdMove);
		
		sectionMove.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
		  }
		});		
		sectionMove.setText(Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.section.move.text")); //$NON-NLS-1$
		sectionMove.setDescription(Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.section.move.description")); //$NON-NLS-1$
		Composite sectionClientMove = toolkit.createComposite(sectionMove);
		sectionClientMove.setLayout(new GridLayout(2, false));				
		createEntry(sectionClientMove, Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.label.distanceX"), MOVE_X); //$NON-NLS-1$
		createEntry(sectionClientMove, Messages.getString("org.nightlabs.editor2d.composite.QuickOptionsComposite.label.distanceY"), MOVE_Y); //$NON-NLS-1$
		sectionMove.setClient(sectionClientMove);
	}
	
	private static final int CLONE_X = 1; 
	private static final int CLONE_Y = 2;
	private static final int MOVE_X = 3;
	private static final int MOVE_Y = 4;	
	private Map<Spinner, Integer> spinner2Identifier = new HashMap<Spinner, Integer>();
	
	protected void createEntry(Composite parent, String labelText, int identifier) 
	{
		toolkit.paintBordersFor(parent);
		
		Label l = toolkit.createLabel(parent, labelText);		
		Spinner spinner = new Spinner(parent, SWT.NONE);
		spinner.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		spinner.setSelection(getValue(identifier));				
		spinner.addSelectionListener(textListener);
		spinner.addFocusListener(focusListener);
		spinner.addDisposeListener(disposeListener);
		
		spinner2Identifier.put(spinner, new Integer(identifier));		
	}	
	
	private SelectionListener textListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			Spinner t = (Spinner) e.getSource();
			int identifier = ((Integer) spinner2Identifier.get(t)).intValue();
			int value = t.getSelection();	
			setValue(identifier, value);
		}	
	};
		
	protected void setValue(int identifier, int value) 
	{
		switch (identifier) 
		{
			case (CLONE_X):
				getConfigModule().setCloneDistanceX(value);
				break;
			case (CLONE_Y):
				getConfigModule().setCloneDistanceY(value);
				break;
			case (MOVE_X):
				getConfigModule().setMoveTranslationX(value);
				break;
			case (MOVE_Y):
				getConfigModule().setMoveTranslationY(value);
				break;								
		}
	}
	
	protected int getValue(int identifier) 
	{
		switch (identifier) 
		{
			case (CLONE_X):
				return getConfigModule().getCloneDistanceX();
			case (CLONE_Y):
				return getConfigModule().getCloneDistanceY();
			case (MOVE_X):
				return getConfigModule().getMoveTranslationX();
			case (MOVE_Y):
				return getConfigModule().getMoveTranslationY();
		}
		return 0;
	}
	
	private FocusListener focusListener = new FocusListener()
	{	
		public void focusLost(FocusEvent e) 
		{
//			Spinner t = (Spinner) e.getSource();
//			int identifier = ((Integer) spinner2Identifier.get(t)).intValue();
//			int value = t.getSelection();	
//			setValue(identifier, value);			
		}	
		public void focusGained(FocusEvent e) {
			
		}	
	};
	
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			Spinner t = (Spinner) e.getSource();
			t.removeSelectionListener(textListener);
			t.removeFocusListener(focusListener);
		}	
	};
	
}