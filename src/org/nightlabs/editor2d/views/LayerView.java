/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.form.NightlabsFormsToolkit;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.command.CreateLayerCommand;
import org.nightlabs.editor2d.command.DeleteLayerCommand;
import org.nightlabs.editor2d.command.DrawComponentReorderCommand;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.OrderUtil;

public class LayerView 
extends ViewPart 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(LayerView.class);
	
  public static final String ID_VIEW = LayerView.class.getName();  
  
  public static final Image DELETE_ICON = SharedImages.DELETE_16x16.createImage();  
  public static final Image NEW_ICON = SharedImages.ADD_16x16.createImage();
  public static final Image EYE_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Eye");     //$NON-NLS-1$
  public static final Image EYE_INVISIBLE_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Eye-Invisible");   //$NON-NLS-1$
  public static final Image UP_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Up"); //$NON-NLS-1$
  public static final Image DOWN_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Down"); //$NON-NLS-1$
  public static final Image LOCK_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Lock"); //$NON-NLS-1$
  public static final Image UNLOCK_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Unlocked");   //$NON-NLS-1$
      
  private RootDrawComponent root;
  private AbstractEditor editor;
  private Map<Control, Layer> button2Layer = new HashMap<Control, Layer>();
  private Button buttonUp;
  private Button buttonDown;
  private Button buttonNew;
  private Button buttonDelete;
  
  private Color currentLayerColor; 
  protected Color getCurrentLayerColor() 
  {
  	if (currentLayerColor == null)
  		currentLayerColor = new Color(Display.getCurrent(), 232, 236, 240);  	
  	return currentLayerColor;
  }

  private Color toolButtonColor;
  protected Color getToolButtonColor() 
  {
  	if (toolButtonColor == null)
  		toolButtonColor = new Color(Display.getCurrent(), 0, 0, 0);  	
  	return toolButtonColor;
  }
    
  private ISelectionListener selectionListener = new ISelectionListener()
  {
    public void selectionChanged(IWorkbenchPart part, ISelection selection) 
    {
      if (part instanceof AbstractEditor) {
      	start(part);
      }
      else if (!(getSite().getPage().getActiveEditor() instanceof AbstractEditor)) {
      	clear();
      }    
    }
	};  

	private void start(IWorkbenchPart part)
	{
    if (part instanceof AbstractEditor) 
    {
    	if (!part.equals(editor)) {
      	removePropertyChangeListener(root);
        editor = (AbstractEditor) part;
        root = editor.getRootDrawComponent();
        addPropertyChangeListener(root);          
        refresh();              		
    	}
    } 
	}
	
	private void clear() 
	{
  	removePropertyChangeListener(root);
    editor = null;
    root = null;
    deactivateTools(false);
    refresh();		
	}
	    
	protected void init() 
	{
    getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
    if (getSite().getWorkbenchWindow().getActivePage() != null)
    	getSite().getWorkbenchWindow().getActivePage().addPartListener(partListener);
    else
    	getSite().getWorkbenchWindow().addPageListener(pageListener);
	}
	
//	private XFormToolkit toolkit = null;
//	protected XFormToolkit getToolkit() {
//		return toolkit;
//	}
	private NightlabsFormsToolkit toolkit = null;
	protected NightlabsFormsToolkit getToolkit() {
		return toolkit;
	}
	
	private Composite parent;
	protected Composite getParent() {
		return parent;
	}
	
	private ScrolledForm form = null;	
	protected ScrolledForm getForm() 
	{
		if (form == null || form.isDisposed()) 
		{
			if (!getParent().isDisposed()) {
				form = getToolkit().createScrolledForm(getParent());
				form.setLayout(new GridLayout());
				form.setLayoutData(new GridData(GridData.FILL_BOTH));		
				form.getBody().setLayout(new GridLayout());
				form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
				toolkit.paintBordersFor(form.getBody());									
			}
			else
				return null;
		}
		return form;
	}
	
	private Composite layerComposite;
		  
  /**
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent) 
  {   	  	
  	init();
        
  	this.parent = parent;    
//  	this.parent = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
    parent.setLayout(XComposite.getLayout(LayoutMode.ORDINARY_WRAPPER));    
    parent.setLayoutData(new GridData(GridData.FILL_BOTH));    
    
		toolkit = new NightlabsFormsToolkit(parent.getDisplay());    
//		toolkit = new XFormToolkit(parent.getDisplay());
//		toolkit.setCurrentMode(TOOLKIT_MODE.COMPOSITE);				
//		parent.setBackground(getToolkit().getBackground());
		parent.setBackground(getToolkit().getColors().getBackground());
				
		form = getForm();
		createComposites();
						
		createTools(parent);				
    refresh();    
    deactivateTools(false);
  }
  
  protected void createLayerEntry(Composite parent, Layer l) 
	{							
		int buttonStyle = SWT.TOGGLE;
				
		Composite parentComposite = getToolkit().createComposite(parent, SWT.NONE);
		parentComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		
		getToolkit().paintBordersFor(parent);
		getToolkit().paintBordersFor(parentComposite);		
		if (!parentComposite.isDisposed()) 
		{
			parentComposite.setLayout(new GridLayout(2, false));
			parentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			// create Visible-Button
			Button buttonVisible = getToolkit().createButton(parentComposite, "", buttonStyle);			 //$NON-NLS-1$
			buttonVisible.setSelection(!l.isVisible());
			if (l.isVisible()) {
				buttonVisible.setImage(EYE_ICON);				
				buttonVisible.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.invisible.tooltip"));				 //$NON-NLS-1$
			}
			else {
				buttonVisible.setImage(EYE_INVISIBLE_ICON);
				buttonVisible.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.visible.tooltip"));				 //$NON-NLS-1$
			}			
			buttonVisible.setLayoutData(new GridData(GridData.BEGINNING));
			buttonVisible.addSelectionListener(visibleListener);		
			
//			// create Editable-Button
//			Button buttonEditble = getToolkit().createButton(parentComposite, EditorPlugin.getResourceString("layerView.buttonLocked.text"), buttonStyle);		
//			buttonEditble.setSelection(!l.isEditable());		
////			buttonEditble.setImage(LOCK_ICON);
//			if (l.isEditable()) {
//				buttonEditble.setImage(UNLOCK_ICON);				
//				buttonEditble.setToolTipText(EditorPlugin.getResourceString("layerView.buttonLocked.tooltip.locked"));
//			}
//			else {				
//				buttonEditble.setImage(LOCK_ICON);				
//				buttonEditble.setToolTipText(EditorPlugin.getResourceString("layerView.buttonLocked.tooltip.unlocked"));				
//			}					
//			buttonEditble.setLayoutData(new GridData(GridData.CENTER));		
//			buttonEditble.addSelectionListener(editableListener);		
			
			// create Name-Text		
			Text text = getToolkit().createText(parentComposite, Messages.getString("org.nightlabs.editor2d.views.LayerView.text.layerName.default")); //$NON-NLS-1$
			if (l.getName() != null)
				text.setText(l.getName());		
			text.setEditable(true);
			text.setLayoutData(new GridData(GridData.FILL_BOTH));		  								
			text.addSelectionListener(textListener);
			text.addFocusListener(focusListener);
			
			// add newLayer to button2Layer
			button2Layer.put(buttonVisible, l);
//			button2Layer.put(buttonEditble, l);
			button2Layer.put(text, l);

			// set bgColor of currentLayer
			if (l.equals(root.getCurrentLayer())) {		
				text.setBackground(getCurrentLayerColor());				
			}
		}
	}
	
  protected void createTools(Composite parent) 
	{	  	  	  		
		int buttonStyle = SWT.PUSH;
		
		Composite toolsComposite = getToolkit().createComposite(parent);		
		toolsComposite.setLayout(new GridLayout(5, false));
		toolsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		toolsComposite.setBackground(getToolkit().getBackground());
		
		// spacer Label 
		Label spacerLabel = getToolkit().createLabel(toolsComposite, ""); //$NON-NLS-1$
		spacerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		// create Buttons		
		buttonUp = getToolkit().createButton(toolsComposite, Messages.getString("org.nightlabs.editor2d.views.LayerView.button.up.text"),  //$NON-NLS-1$
				buttonStyle);		
//		buttonUp.setImage(UP_ICON);
		buttonUp.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.up.tooltip")); //$NON-NLS-1$
		buttonUp.addSelectionListener(upListener);
		
		buttonDown = getToolkit().createButton(toolsComposite, Messages.getString("org.nightlabs.editor2d.views.LayerView.button.down.text"),  //$NON-NLS-1$
				buttonStyle);
//		buttonDown.setImage(DOWN_ICON);
		buttonDown.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.down.tooltip")); //$NON-NLS-1$
		buttonDown.addSelectionListener(downListener);
		
		buttonNew = getToolkit().createButton(toolsComposite, Messages.getString("org.nightlabs.editor2d.views.LayerView.button.new.text"),  //$NON-NLS-1$
				buttonStyle);
//		buttonNew.setImage(NEW_ICON);
		buttonNew.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.new.tooltip"));		 //$NON-NLS-1$
		buttonNew.addSelectionListener(newListener);
		
		buttonDelete = getToolkit().createButton(toolsComposite, Messages.getString("org.nightlabs.editor2d.views.LayerView.button.delete.text"),  //$NON-NLS-1$
				buttonStyle);
//		buttonDelete.setImage(DELETE_ICON);
		buttonDelete.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.delete.tooltip"));				 //$NON-NLS-1$
		buttonDelete.addSelectionListener(deleteListener);				
	}
	
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus() 
  {
  	getForm().setFocus();
  }

  private FocusAdapter focusListener = new FocusAdapter() 
  {
    public void focusGained(FocusEvent e) 
    {
			Text t = (Text) e.getSource();
			if (button2Layer.containsKey(t)) 
			{
				Layer currLayer = (Layer) button2Layer.get(t);
				if (!root.getCurrentLayer().equals(currLayer)) {
					root.setCurrentLayer(currLayer);
					logger.debug("Layer "+currLayer.getName()+" = CurrentLayer"); //$NON-NLS-1$ //$NON-NLS-2$
					refresh();				  
				}
			} else {
			  throw new IllegalStateException("There is no such Layer registered!"); //$NON-NLS-1$
			}      
    }
  }; 
  
  private SelectionListener textListener = new SelectionAdapter() 
	{ 
		public void widgetDefaultSelected(SelectionEvent e) 
		{    
      if (e.getSource() instanceof Text) 
      {
        Text text = (Text) e.getSource();
        String layerName = text.getText();
        Layer l = (Layer) button2Layer.get(text);
        l.setName(layerName);        
      }		  
		}
	};  
   	
	private SelectionListener visibleListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
			logger.debug("VISIBLE widgetSelected()"); //$NON-NLS-1$
			
			Button b = (Button) e.getSource();
			if (button2Layer.containsKey(b)) {
				Layer l = (Layer) button2Layer.get(b);
				if (!b.getSelection()) {
					l.setVisible(true);
					b.setImage(EYE_ICON);
					b.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.invisible.tooltip")); //$NON-NLS-1$
				} else {
					l.setVisible(false);
					b.setImage(EYE_INVISIBLE_ICON);
					b.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.visible.tooltip")); //$NON-NLS-1$
				}				
				updateViewer();
			}
			else {
				throw new IllegalStateException("There is no such Layer registered!"); //$NON-NLS-1$
			}			
		}
	};  

	private SelectionListener editableListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  logger.debug("EDITABLE widgetSelected()"); //$NON-NLS-1$
			
			Button b = (Button) e.getSource();
			if (button2Layer.containsKey(b)) {
				Layer l = (Layer) button2Layer.get(b);
				if (b.getSelection()) {
					l.setEditable(true);
					b.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.lock.tooltip")); //$NON-NLS-1$
					b.setImage(UNLOCK_ICON);					
				} else {
					l.setEditable(false);
					b.setToolTipText(Messages.getString("org.nightlabs.editor2d.views.LayerView.button.unlock.tooltip")); //$NON-NLS-1$
					b.setImage(LOCK_ICON);
				}
				updateViewer();
			}
			else {
				throw new IllegalStateException("There is no such Layer registered!"); //$NON-NLS-1$
			}			
		}
	};  
		
	protected void updateViewer() 
	{
		editor.updateViewer();
//		editor.getEditPartViewer().getRootEditPart().refresh();
//		editor.getEditorSite().getShell().update();
	}
	
	private SelectionListener newListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  logger.debug("NEW widgetSelected()"); //$NON-NLS-1$
		  CreateLayerCommand addLayer = new CreateLayerCommand(root.getCurrentPage(), editor.getModelFactory());		  
		  executeCommand(addLayer);	
		  refresh();
		}
	};

	private SelectionListener deleteListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  logger.debug("DELETE widgetSelected()"); //$NON-NLS-1$
		  Layer currentLayer = root.getCurrentLayer();
		  DeleteLayerCommand layerCommand = new DeleteLayerCommand(
		  		root.getCurrentPage(), currentLayer);
		  executeCommand(layerCommand);		
		  refresh();
		}
	};

	protected Layer getCurrentLayer() 
	{
		return root.getCurrentLayer();
	}
	
	private SelectionListener upListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  logger.debug("UP widgetSelected()"); //$NON-NLS-1$
		  int oldIndex = OrderUtil.indexOf(getCurrentLayer());
		  int lastIndex = OrderUtil.getLastIndex(getCurrentLayer().getParent());
		  int newIndex = oldIndex + 1;
		  if (lastIndex >= newIndex) 
		  {
			  DrawComponentReorderCommand cmd = new DrawComponentReorderCommand(
			  		getCurrentLayer(), getCurrentLayer().getParent(), newIndex);
			  executeCommand(cmd);
			  refresh();
		  }
		}
	};
	
	private SelectionListener downListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  logger.debug("DOWN widgetSelected()");  //$NON-NLS-1$
		  int oldIndex = OrderUtil.indexOf(getCurrentLayer());
		  int firstIndex = 0;
		  int newIndex = oldIndex - 1;
		  if (firstIndex <= newIndex) 
		  {
			  DrawComponentReorderCommand cmd = new DrawComponentReorderCommand(
			  		getCurrentLayer(), getCurrentLayer().getParent(), newIndex);
			  executeCommand(cmd);	
			  refresh();
		  }
		}
	};
	
	protected void executeCommand(Command cmd) 
	{
		editor.getOutlineEditDomain().getCommandStack().execute(cmd);
	}
	  	  
  protected void deactivateTools(boolean newButton) 
	{
	  if (!buttonUp.isDisposed())
	    buttonUp.setEnabled(false);
	  if (!buttonDown.isDisposed())
	    buttonDown.setEnabled(false);
	  if (!buttonDelete.isDisposed())
	    buttonDelete.setEnabled(false);
	  if (!buttonNew.isDisposed())
	    buttonNew.setEnabled(newButton);
	}
	
	protected void activateTools() 
	{
	  if (!buttonUp.isDisposed()) 
	    buttonUp.setEnabled(true);
	  if (!buttonDown.isDisposed())	  
	    buttonDown.setEnabled(true);
	  if (!buttonDelete.isDisposed())	  
	    buttonDelete.setEnabled(true);
	  if (!buttonNew.isDisposed())
	    buttonNew.setEnabled(true);
	}
  
  protected void createComposites() 
  {
		if (layerComposite != null)
			layerComposite.dispose();		
		if (getForm() != null) {
			layerComposite = getToolkit().createComposite(getForm().getBody(), SWT.NONE);		
			layerComposite.setLayout(new GridLayout());
			layerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));		
			layerComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
			getToolkit().paintBordersFor(getForm().getBody());					
		}
  }
    				
	public void refresh()
	{			
		if (layerComposite != null || !layerComposite.isDisposed()) 		
		{						
			createComposites();
			button2Layer.clear();			
			if (root != null) {
				for (int i = root.getCurrentPage().getDrawComponents().size()-1; i >= 0; --i) {
					DrawComponent dc  = (DrawComponent) root.getCurrentPage().getDrawComponents().get(i);
					if (dc instanceof Layer) {
					  Layer l = (Layer) dc; 
					  createLayerEntry(layerComposite, l); 
					} else {
					  logger.debug("dc NOT instanceof Layer, but instanceof "+dc.getClass()); //$NON-NLS-1$
					}
				}				
				if (root.getCurrentPage().getDrawComponents().size() <= 1)			  
				  deactivateTools(true);			  
				else 
				  activateTools();
				
				getForm().reflow(true);
			}				  
		}
		else
			logger.debug("layerComposite == null or isDisposed()"); //$NON-NLS-1$
	}  
    
  private PropertyChangeListener currentPageListener = new PropertyChangeListener()
  {	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (evt.getPropertyName().equals(RootDrawComponent.PROP_CURRENT_PAGE)) 
			{
				PageDrawComponent oldPage = (PageDrawComponent) evt.getOldValue();
				oldPage.removePropertyChangeListener(layerListener);
				PageDrawComponent newPage = (PageDrawComponent) evt.getNewValue();
				newPage.addPropertyChangeListener(layerListener);
				refresh();
			}
		}	
	};

  private PropertyChangeListener layerListener = new PropertyChangeListener()
  {	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (evt.getSource() instanceof PageDrawComponent && 
					(evt.getPropertyName().equals(DrawComponentContainer.CHILD_ADDED) ||
					evt.getPropertyName().equals(DrawComponentContainer.CHILD_REMOVED)) ) 
			{
				logger.debug("layer listener"); //$NON-NLS-1$
				refresh();
			}
		}	
	};
		
	private void removePropertyChangeListener(RootDrawComponent root) 
	{
		if (root != null) {
	    root.removePropertyChangeListener(currentPageListener);
	    root.getCurrentPage().removePropertyChangeListener(layerListener);   			
		}
	}
	
	private void addPropertyChangeListener(RootDrawComponent mldc) 
	{
		if (mldc != null) {
	    mldc.addPropertyChangeListener(currentPageListener);
	    mldc.getCurrentPage().addPropertyChangeListener(layerListener);   			
		}
	}
	
	@Override
  public void dispose() 
  {
    getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
    if (getSite().getWorkbenchWindow().getActivePage() != null)
    	getSite().getWorkbenchWindow().getActivePage().removePartListener(partListener);
    getSite().getWorkbenchWindow().removePageListener(pageListener);
    removePropertyChangeListener(root);    
    super.dispose();
  }
  
  private IPartListener partListener = new IPartListener() 
  {
		public void partActivated(IWorkbenchPart part) {
			start(part);
		}
		public void partBroughtToTop(IWorkbenchPart p) { }
		public void partClosed(IWorkbenchPart p) {
			clear();
		}
		public void partDeactivated(IWorkbenchPart p) { }
		public void partOpened(IWorkbenchPart p) { }
	};
	
  private IPageListener pageListener = new IPageListener()
  {	
		public void pageOpened(IWorkbenchPage page) {
			
		}	
		public void pageClosed(IWorkbenchPage page) {
			page.removePartListener(partListener);
		}	
		public void pageActivated(IWorkbenchPage page) {
			page.addPartListener(partListener);
		}	
	};
	
//private CommandStackListener commandStackListener = new CommandStackListener() 
//{
//  /* (non-Javadoc)
//   * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
//   */
//  public void commandStackChanged(EventObject event) 
//  {      
//		editor.getEditPartViewer().getRootEditPart().refresh();
//		refresh();
//  }
//};

//private EditPartListener layerListener = new EditPartListener.Stub() 
//{
//  /**
//   * @see org.eclipse.gef.EditPartListener#childAdded(org.eclipse.gef.EditPart, int)
//   */
//  public void childAdded(EditPart child, int index) 
//  {
//    if (child instanceof LayerEditPart) {
//      RootDrawComponentEditPart parent = (RootDrawComponentEditPart) child.getParent();
//      root = (RootDrawComponent) parent.getModel();
//      refresh();
//    }
//  }
//
//  /**
//   * @see org.eclipse.gef.EditPartListener#removingChild(org.eclipse.gef.EditPart, int)
//   */
//  public void removingChild(EditPart child, int index) 
//  {
//    if (child instanceof LayerEditPart) {
//      RootDrawComponentEditPart parent = (RootDrawComponentEditPart) child.getParent();
//      root = (RootDrawComponent) parent.getModel();
//      refresh();        
//    }
//  }
//};	
}
