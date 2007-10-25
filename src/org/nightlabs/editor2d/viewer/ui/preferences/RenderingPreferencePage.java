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

package org.nightlabs.editor2d.viewer.ui.preferences;

import java.awt.RenderingHints;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.editor2d.util.RenderingHintsManager;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class RenderingPreferencePage 
extends PreferencePage
implements IWorkbenchPreferencePage
{		
	public RenderingPreferencePage() 
	{
		super();
		setPreferenceStore(ViewerPlugin.getDefault().getPreferenceStore());
		setTitle(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.title"))); //$NON-NLS-1$
	}
	
	protected Button buttonQuality;
	protected Button buttonSpeed;
	protected Button buttonDefault;
//	protected Button buttonCustom;
	
	@Override
	protected Control createContents(Composite parent) 
	{		
		Composite comp = new XComposite(parent, SWT.NONE);
		GridData compData = new GridData(GridData.FILL_BOTH);
		comp.setLayoutData(compData);
		GridLayout compLayout = new GridLayout(2, false);
		comp.setLayout(compLayout);
		
		buttonDefault = new Button(comp, SWT.RADIO);
		buttonDefault.addSelectionListener(buttonListener);
		buttonDefault.addDisposeListener(disposeListener);
		Label labelDefault = new Label(comp, SWT.NONE);
		labelDefault.setText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.default"))); //$NON-NLS-1$
		labelDefault.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		buttonQuality = new Button(comp, SWT.RADIO);
		buttonQuality.addSelectionListener(buttonListener);
		buttonQuality.addDisposeListener(disposeListener);
		Label labelQuality = new Label(comp, SWT.NONE);
		labelQuality.setText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.quality"))); //$NON-NLS-1$
		labelQuality.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		

		buttonSpeed = new Button(comp, SWT.RADIO);
		buttonSpeed.addSelectionListener(buttonListener);
		buttonSpeed.addDisposeListener(disposeListener);				
		Label labelSpeed = new Label(comp, SWT.NONE);
		labelSpeed.setText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.preferences.RenderingPreferencePage.speed")));		 //$NON-NLS-1$
		labelSpeed.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		init();
		
//		buttonCustom = new Button(comp, SWT.RADIO);
//		buttonCustom.addSelectionListener(buttonListener);
//		buttonCustom.addDisposeListener(disposeListener);						
//		Label labelCustom = new Label(comp, SWT.NONE);
//		labelCustom.setText(ViewerPlugin.getResourceString("preferences.rendering.label.custom"));		
//		labelCustom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
//		ExpandableWrapperComposite detailComp = new ExpandableWrapperComposite(parent, SWT.NONE);
//		detailComp.setText(ViewerPlugin.getResourceString("preferences.rendering.label.details"));
//		GridLayout detailLayout = new GridLayout(4, false);
//		detailComp.setLayout(detailLayout);
//		
//		createDetailEntry(parent, ViewerPlugin.getResourceString("preferences.rendering.label.antiAliasing"),
//				ViewerPlugin.getResourceString("preferences.rendering.label.default"), 
//				ViewerPlugin.getResourceString("preferences.rendering.label.on"),
//				ViewerPlugin.getResourceString("preferences.rendering.label.off"));
//		
//		createDetailEntry(parent, ViewerPlugin.getResourceString("preferences.rendering.label.textAntiAliasing"),
//				ViewerPlugin.getResourceString("preferences.rendering.label.default"), 
//				ViewerPlugin.getResourceString("preferences.rendering.label.on"),
//				ViewerPlugin.getResourceString("preferences.rendering.label.off"));
//
//		createDetailEntry(parent, ViewerPlugin.getResourceString("preferences.rendering.label.rendering"),
//				ViewerPlugin.getResourceString("preferences.rendering.label.default"), 
//				ViewerPlugin.getResourceString("preferences.rendering.label.quality"),
//				ViewerPlugin.getResourceString("preferences.rendering.label.speed"));
				
		return parent;
	}

	public void init(IWorkbench workbench) {}	
	
	@Override
	public IPreferenceStore getPreferenceStore() {
		return ViewerPlugin.getDefault().getPreferenceStore();
	}
	
//	protected void createDetailEntry(Composite parent, String entry, 
//			String defaultString, String qualityString, String speedString) 
//	{
//		Composite comp = new Composite(parent, SWT.NONE);
//		GridLayout compLayout = new GridLayout(7, false);
//		
//		Label l = new Label(parent, SWT.NONE);
//		l.setText(entry);
//		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		Button defaultButton = new Button(parent, SWT.RADIO);		
//		Label defaultLabel = new Label(comp, SWT.NONE);
//		defaultLabel.setText(defaultString);
//		
//		Button qualityButton = new Button(parent, SWT.RADIO);
//		Label qualityLabel = new Label(comp, SWT.NONE);
//		qualityLabel.setText(qualityString);
//		
//		Button speedButton = new Button(parent, SWT.RADIO);
//		Label speedLabel = new Label(comp, SWT.NONE);
//		speedLabel.setText(speedString);		
//	}
	
	protected RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);;
	public RenderingHints getRenderingHints() {
		return renderHints;
	}
	
	protected String selection = Preferences.PREFERENCE_DEFAULT;
	
	protected SelectionListener buttonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			if (e.getSource() instanceof Button) {
				Button b = (Button) e.getSource();
				if (b.getSelection() == true) 
				{
					if (b.equals(buttonDefault)) {
						selection = Preferences.PREFERENCE_DEFAULT;
					}
					else if (b.equals(buttonQuality)) {
						selection = Preferences.PREFERENCE_QUALITY;						
					}
					else if (b.equals(buttonSpeed)) {
						selection = Preferences.PREFERENCE_SPEED;						
					}
				}
			}
		}	
	};
	
	protected DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(buttonListener);
		}	
	};
		
	protected void setSelection(String selection) 
	{
		if (selection.equals(Preferences.PREFERENCE_DEFAULT)) {
			RenderingHintsManager.setDefaultRenderMode(getRenderingHints());
			buttonDefault.setSelection(true);
		}
		else if (selection.equals(Preferences.PREFERENCE_QUALITY)) {
			RenderingHintsManager.setQualityRenderMode(getRenderingHints());
			buttonQuality.setSelection(true);			
		}
		else if (selection.equals(Preferences.PREFERENCE_SPEED)) {
			RenderingHintsManager.setSpeedRenderMode(getRenderingHints());
			buttonSpeed.setSelection(true);			
		}
	}

	@Override
	public boolean performOk() 
	{
		setSelection(selection);
		getPreferenceStore().setValue(Preferences.PREFERENCE_RENDERING, selection);
		return true;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		selection = Preferences.PREFERENCE_DEFAULT;
	}			
	
	protected void init() {
		selection = Preferences.getPreferenceStore().getString(Preferences.PREFERENCE_RENDERING);
		setSelection(selection);
	}
}
