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

package org.nightlabs.editor2d.viewer.ui.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.nightlabs.base.ui.action.XContributionItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.custom.XCombo;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.RenderModeDescriptor;
import org.nightlabs.editor2d.render.RenderModeListener;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.ShapeRenderer;
import org.nightlabs.editor2d.render.StringRenderer;
import org.nightlabs.editor2d.viewer.ui.ViewerPlugin;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

public class RenderModeContributionItem
extends XContributionItem
{
	public static final String ID = RenderModeContributionItem.class.getName();
	private String initString = "RenderMode TEST"; //$NON-NLS-1$
	private Button showString;
	private Button showFillColor;
	private XCombo combo;
	private ToolItem toolitem;
	private IWorkbenchPage page;
	private Map<String, Integer> entry2Index = new HashMap<String, Integer>();
	private Map<Integer, String> index2RenderMode = new HashMap<Integer, String>();
  private Map<String, String> string2RenderMode = new HashMap<String, String>();
  private Map<String, String> renderMode2String = new HashMap<String, String>();
	private RenderModeManager renderModeMan;
  private Collection<Renderer> currentRenderers = null;
  private boolean showCheckBoxes = true;
  
	public RenderModeContributionItem(IWorkbenchPage page, boolean showCheckBoxes)
	{
		super(ID);
		this.page = page;
		this.showCheckBoxes = showCheckBoxes;
		this.page.addPartListener(partListener);
	}
		
	public RenderModeContributionItem(RenderModeManager renderModeMan, boolean showCheckBoxes)
	{
		super(ID);
		this.showCheckBoxes = showCheckBoxes;
		setRenderModeMan(renderModeMan);
	}
	
	private IPartListener partListener = new IPartListener()
	{
		public void partActivated(IWorkbenchPart part)
		{
		  Object adapter = part.getAdapter(RenderModeManager.class);
		  if (adapter != null && adapter instanceof RenderModeManager) {
		  	setRenderModeMan((RenderModeManager)adapter);
		  }
		}
		public void partOpened(IWorkbenchPart part) {
			
		}
		public void partDeactivated(IWorkbenchPart part) {
			
		}
		public void partClosed(IWorkbenchPart part) {
			
		}
		public void partBroughtToTop(IWorkbenchPart part) {
			
		}
	};
			
  protected void refresh(boolean repopulateCombo)
  {
  	if (combo == null || combo.isDisposed())
  		return;
  	
		if (renderModeMan == null) {
			combo.setEnabled(false);
			if (showCheckBoxes) {
				showString.setEnabled(false);
				showFillColor.setEnabled(false);				
			}
		}
		
		if (renderModeMan != null)
		{
			checkRenderers(getCurrentRenderers());
			
			if (repopulateCombo)
			{
				combo.remove(0);
				populateMaps(renderModeMan);
				int counter = 0;
				for (Iterator<String> it = string2RenderMode.keySet().iterator(); it.hasNext(); )
				{
					String entry = it.next();
					combo.add(null, entry, counter);
					entry2Index.put(entry, counter);
					index2RenderMode.put(counter, getRenderMode(entry));
					counter++;
				}
			}
			String currentRenderMode = renderModeMan.getCurrentRenderMode();
			String entry = getEntry(currentRenderMode);
			combo.select(getIndex(entry));
			combo.setEnabled(true);
		}
  }
    
  /**
   * Computes the width required by control
   * @param control The control to compute width
   * @return int The width required
   */
  @Override
	protected int computeWidth(Control control)
  {
  	int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
  	return width;
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
  	Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
  	int size = 3;
  	if (!showCheckBoxes)
  		size = 1;
  	GridLayout layout = new GridLayout(size, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
  	comp.setLayout(layout);
  	
  	combo = new XCombo(comp, SWT.DROP_DOWN | SWT.BORDER);
  	combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
  	combo.addSelectionListener(comboSelectionListener);
  	combo.addFocusListener(comboFocusListener);
  	combo.addDisposeListener(comboDisposeListener);
  	
  	// Initialize width of combo
  	combo.add(null, initString, 0);
  	
  	if (showCheckBoxes) {
    	showString = new Button(comp, SWT.CHECK);
    	showString.setText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.RenderModeContributionItem.text.showText")); //$NON-NLS-1$
    	showString.setToolTipText(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.RenderModeContributionItem.tooltip.showText")); //$NON-NLS-1$
    	showString.addSelectionListener(showStringSelectionListener);
    	showString.addDisposeListener(showStringDisposeListener);
    	
    	showFillColor = new Button(comp, SWT.CHECK);
    	showFillColor.setText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.RenderModeContributionItem.text.showFillColor"))); //$NON-NLS-1$
    	showFillColor.setToolTipText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.ui.action.RenderModeContributionItem.tooltip.showFillColor"))); //$NON-NLS-1$
    	showFillColor.addSelectionListener(showShapeSelectionListener);
    	showFillColor.addDisposeListener(showFillColorDisposeListener);  		
  	}
  	  	
  	if (toolitem != null)
  		toolitem.setWidth(computeWidth(comp));
  	
  	refresh(true);
  	  	
  	return comp;
  }
  		
	public RenderModeManager getRenderModeMan() {
		return renderModeMan;
	}
	public void setRenderModeMan(RenderModeManager rm)
	{
  	if (renderModeMan == rm)
  		return;
  	if (renderModeMan != null)
  		renderModeMan.removeRenderModeListener(renderModeListener);

  	renderModeMan = rm;
  	refresh(true);

  	if (renderModeMan != null)
  		renderModeMan.addRenderModeListener(renderModeListener);
	}
	
	 /**
   * The control item implementation of this <code>IContributionItem</code>
   * method calls the <code>createControl</code> framework method to
   * create a control under the given parent, and then creates
   * a new tool item to hold it.
   * Subclasses must implement <code>createControl</code> rather than
   * overriding this method.
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
   * Subclasses must implement <code>createControl</code> rather than
   * overriding this method.
   * 
   * @param parent The parent of the control to fill
   */
  @Override
	public final void fill(Composite parent) {
  	createControl(parent);
  }

  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method throws an exception since controls cannot be added to menus.
   * 
   * @param parent The menu
   * @param index Menu index
   */
  @Override
	public final void fill(Menu parent, int index) {
  	Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
  }
  
  /**
   * @see org.eclipse.jface.action.ContributionItem#dispose()
   */
  @Override
	public void dispose()
  {
  	if (partListener != null && page != null)
  		page.removePartListener(partListener);
  	
  	if (renderModeMan != null) {
  		renderModeMan.removeRenderModeListener(renderModeListener);
  		renderModeMan = null;
  	}
  	if (combo != null) {
    	combo = null;
  	}
  	partListener = null;
  }
    
  protected void populateMaps(RenderModeManager rmm)
  {
  	for (Iterator<String> it = rmm.getRenderModes().iterator(); it.hasNext(); )
  	{
  		String renderMode = it.next();
  		RenderModeDescriptor desc = rmm.getRenderModeDescriptor(renderMode);
  		String s = null;
  		if (desc != null) {
  			s = desc.getLocalizedText();
  		} else {
  			s = renderMode;
  		}
  		string2RenderMode.put(s, renderMode);
  		renderMode2String.put(renderMode, s);
  	}
  }
  
  protected String getRenderMode(String entry)
  {
  	String renderMode = string2RenderMode.get(entry);
  	return renderMode;
  }
  
  protected String getEntry(String renderMode) {
  	return renderMode2String.get(renderMode);
  }
  
  protected int getIndex(String entry)
  {
  	Integer i = entry2Index.get(entry);
  	if (i != null)
    	return i.intValue();
  	else
  		return 0;
  }
  
  protected String getRenderMode(int index) {
  	return index2RenderMode.get(index);
  }
   
  protected Collection<Renderer> getCurrentRenderers() {
  	return currentRenderers;
  }
  
	private RenderModeListener renderModeListener = new RenderModeListener()
	{
		public void renderModeChanges(String renderMode) {
			refresh(false);
			currentRenderers = renderModeMan.getRenderers(renderMode);
		}
	};
	
	private SelectionListener comboSelectionListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e)
		{
			int index = combo.getSelectionIndex();
			String renderMode = getRenderMode(index);
			renderModeMan.setCurrentRenderMode(renderMode);
		}
	};
	
	private FocusListener comboFocusListener = new FocusListener()
	{
		public void focusLost(FocusEvent e) {
			refresh(false);
		}
		public void focusGained(FocusEvent e) {
			// do nothing
		}
	};
	
	private DisposeListener comboDisposeListener = new DisposeListener()
	{
		public void widgetDisposed(DisposeEvent e)
		{
  		combo.removeSelectionListener(comboSelectionListener);
  		combo.removeFocusListener(comboFocusListener);
		}
	};

	private DisposeListener showStringDisposeListener = new DisposeListener()
	{
		public void widgetDisposed(DisposeEvent e) {
			if (showCheckBoxes)
				showString.removeSelectionListener(showStringSelectionListener);
		}
	};
	
	private DisposeListener showFillColorDisposeListener = new DisposeListener()
	{
		public void widgetDisposed(DisposeEvent e) {
			if (showCheckBoxes)
				showFillColor.removeSelectionListener(showShapeSelectionListener);
		}
	};
		
	private SelectionListener showStringSelectionListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			boolean selection = b.getSelection();
			setStringRenderer(getCurrentRenderers(), selection);
		}
	};
	
	private SelectionListener showShapeSelectionListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			boolean selection = b.getSelection();
			setShapeRenderer(getCurrentRenderers(), selection);
		}
	};
	
	protected void setStringRenderer(Collection<Renderer> renderer, boolean showString)
	{
		if (renderer != null)
		{
			String renderContextType = renderModeMan.getCurrentRenderContextType();
			for (Iterator<Renderer> it = renderer.iterator(); it.hasNext(); ) {
				Renderer r = it.next();
				RenderContext rc = r.getRenderContext(renderContextType);
				if (rc instanceof StringRenderer) {
					StringRenderer stringRenderer = (StringRenderer) rc;
					stringRenderer.setShowString(showString);
				}
			}
			updateViewer();
		}
	}

	protected void setShapeRenderer(Collection<Renderer> renderer, boolean showFillColor)
	{
		if (renderer != null)
		{
			String renderContextType = renderModeMan.getCurrentRenderContextType();
			for (Iterator<Renderer> it = renderer.iterator(); it.hasNext(); ) {
				Renderer r = it.next();
				RenderContext rc = r.getRenderContext(renderContextType);
				if (rc instanceof ShapeRenderer) {
					ShapeRenderer shapeRenderer = (ShapeRenderer) rc;
					shapeRenderer.setShowFillColor(showFillColor);
				}
			}
			updateViewer();
		}
	}
		
	protected void checkRenderers(Collection<Renderer> renderer)
	{
		if (renderer != null)
		{
			boolean stringRendererContained = false;
			boolean shapeRendererContained = false;
			boolean showStringActive = true;
			boolean showFillColorActive = true;
			for (Iterator<Renderer> it = renderer.iterator(); it.hasNext(); ) {
				Renderer r = it.next();
				Object renderContext = r.getRenderContext(renderModeMan.getCurrentRenderContextType());
				if (renderContext instanceof ShapeRenderer) {
					ShapeRenderer shapeRenderer = (ShapeRenderer) renderContext;
					showFillColorActive = shapeRenderer.isShowFillColor();
					shapeRendererContained = true;
				}
				if (renderContext instanceof StringRenderer) {
					StringRenderer stringRenderer = (StringRenderer) renderContext;
					showStringActive = stringRenderer.isShowString();
					stringRendererContained = true;
				}
			}
			if (showCheckBoxes) {
				showFillColor.setEnabled(shapeRendererContained);
				showString.setEnabled(stringRendererContained);
				
				// remove SelectionListener first to avoid repaint
				showFillColor.removeSelectionListener(showShapeSelectionListener);
				showString.removeSelectionListener(showStringSelectionListener);
				
				showFillColor.setSelection(showFillColorActive);
				showString.setSelection(showStringActive);
				
				showFillColor.addSelectionListener(showShapeSelectionListener);
				showString.addSelectionListener(showStringSelectionListener);				
			}
		}
	}
		
	protected void updateViewer()
	{
		renderModeMan.setCurrentRenderMode(renderModeMan.getCurrentRenderMode());
	}
}
