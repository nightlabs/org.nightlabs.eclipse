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

package org.nightlabs.editor2d;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.editor2d.actions.DeleteAction;
import org.nightlabs.editor2d.actions.EditShapeAction;
import org.nightlabs.editor2d.actions.RepaintRetargetAction;
import org.nightlabs.editor2d.actions.RotateAction;
import org.nightlabs.editor2d.actions.copy.CopyAction;
import org.nightlabs.editor2d.actions.copy.CutAction;
import org.nightlabs.editor2d.actions.copy.PasteAction;
import org.nightlabs.editor2d.actions.group.GroupAction;
import org.nightlabs.editor2d.actions.group.UnGroupAction;
import org.nightlabs.editor2d.actions.order.ChangeOrderOneDown;
import org.nightlabs.editor2d.actions.order.ChangeOrderOneUp;
import org.nightlabs.editor2d.actions.order.ChangeOrderToLocalBack;
import org.nightlabs.editor2d.actions.order.ChangeOrderToLocalFront;
import org.nightlabs.editor2d.actions.preferences.ShowFigureToolTipAction;
import org.nightlabs.editor2d.actions.preferences.ShowStatusLineAction;
import org.nightlabs.editor2d.actions.shape.ConvertToShapeAction;
import org.nightlabs.editor2d.actions.shape.ShapeExclusiveOrAction;
import org.nightlabs.editor2d.actions.shape.ShapeIntersectAction;
import org.nightlabs.editor2d.actions.shape.ShapeSubtractAction;
import org.nightlabs.editor2d.actions.shape.ShapeUnionAction;
import org.nightlabs.editor2d.actions.zoom.ZoomAllAction;
import org.nightlabs.editor2d.actions.zoom.ZoomAllRetargetAction;
import org.nightlabs.editor2d.actions.zoom.ZoomPageAction;
import org.nightlabs.editor2d.actions.zoom.ZoomPageRetargetAction;
import org.nightlabs.editor2d.actions.zoom.ZoomSelectionAction;
import org.nightlabs.editor2d.actions.zoom.ZoomSelectionRetargetSelection;
import org.nightlabs.editor2d.custom.EditorZoomComboContributionItem;
import org.nightlabs.editor2d.print.EditorPrintAction;
import org.nightlabs.editor2d.print.EditorPrintSetupAction;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.viewer.ui.action.RenderModeContributionItem;
import org.nightlabs.editor2d.viewer.ui.render.RendererRegistry;


public class EditorActionBarContributor 
extends ActionBarContributor 
{
  protected void buildActions() 
  {
  	// Undo / Redo
  	addRetargetAction(new UndoRetargetAction());
  	addRetargetAction(new RedoRetargetAction());
  	
  	// Delete
//  	addRetargetAction(new DeleteRetargetAction());
  	addRetargetAction(new RetargetAction(DeleteAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.delete")));  	 //$NON-NLS-1$
  	  	
  	// Cut / Copy / Paste
  	addRetargetAction(new RetargetAction(CopyAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.copy"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(PasteAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.paste"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(CutAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.cut"))); //$NON-NLS-1$
  	
  	// Group
  	addRetargetAction(new RetargetAction(GroupAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.group"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(UnGroupAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.ungroup"))); //$NON-NLS-1$
  	
  	// Alignment
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));
  	addRetargetAction(new MatchWidthRetargetAction());
  	addRetargetAction(new MatchHeightRetargetAction());
  	
  	// Order
  	addRetargetAction(new RetargetAction(ChangeOrderToLocalFront.ID,
  		Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.bringToFront"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ChangeOrderOneUp.ID,
    	Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.moveOneUp"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ChangeOrderOneDown.ID,
    	Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.moveOneDown"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ChangeOrderToLocalBack.ID,
    	Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.bringToBack"))); //$NON-NLS-1$
  	
  	// Ruler / Grid
  	addRetargetAction(new RetargetAction(
  			GEFActionConstants.TOGGLE_RULER_VISIBILITY, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.menu.showRuler"), IAction.AS_CHECK_BOX));  	 //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(
  			GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.menu.snapToGeometry"), IAction.AS_CHECK_BOX)); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.menu.showGrid"), IAction.AS_CHECK_BOX)); //$NON-NLS-1$

  	// Zoom
  	addRetargetAction(new ZoomInRetargetAction());
  	addRetargetAction(new ZoomOutRetargetAction());  	
  	addRetargetAction(new ZoomAllRetargetAction());
  	addRetargetAction(new ZoomSelectionRetargetSelection());
  	addRetargetAction(new ZoomPageRetargetAction());
  	  	
  	addRetargetAction(new RetargetAction(ShowFigureToolTipAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.showToolTips"),  //$NON-NLS-1$
  			IAction.AS_CHECK_BOX));  	
  	addRetargetAction(new RetargetAction(ShowStatusLineAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.showStatusLineInfo"),  //$NON-NLS-1$
  			IAction.AS_CHECK_BOX));
  	
  	addRetargetAction(new RepaintRetargetAction());
  	
  	// Print
  	addRetargetAction(new RetargetAction(EditorPrintAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.print")));  	 //$NON-NLS-1$
//  	addRetargetAction(new RetargetAction(EditorPrintPreviewAction.ID, 
//  			"PrintPreview"));  	  	
  	addRetargetAction(new RetargetAction(EditorPrintSetupAction.ID, 
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.pageSetup")));  	 //$NON-NLS-1$
  	
  	// Shape Actions
  	addRetargetAction(new RetargetAction(ConvertToShapeAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.convertToShape"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(EditShapeAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.editShape"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ShapeUnionAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.shape.union"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ShapeSubtractAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.shape.subtract"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ShapeIntersectAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.shape.intersect"))); //$NON-NLS-1$
  	addRetargetAction(new RetargetAction(ShapeExclusiveOrAction.ID,
  			Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.shape.exclusiveOr")));  	 //$NON-NLS-1$
  }

  public static final String ID_VIEW_MENU = IWorkbenchActionConstants.M_VIEW;
  public static final String ID_EDIT_MENU = IWorkbenchActionConstants.M_EDIT;
  public static final String ID_FILE_MENU = IWorkbenchActionConstants.M_FILE;
  public static final String ID_SHAPE_MENU = Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.menu.shape"); //$NON-NLS-1$
  
  /**
   * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(IMenuManager)
   */
  public void contributeToMenu(IMenuManager menubar) 
  {
  	super.contributeToMenu(menubar);  	
  	 
  	IContributionItem fm = RCPUtil.getMenuItem(ID_FILE_MENU, menubar);
  	if (fm instanceof IMenuManager)  
  		fileMenu = (IMenuManager) fm;
  	if (fileMenu != null) 
  	{
  		IContributionItem printMenu = RCPUtil.getMenuItem(EditorPrintAction.ID, fileMenu);
  		if (printMenu == null)
  			fileMenu.insertBefore(ActionFactory.QUIT.getId(), getAction(EditorPrintAction.ID));
  		
//			fileMenu.insertAfter(EditorPrintAction.ID, getAction(EditorPrintPreviewAction.ID));
//  		fileMenu.insertAfter(EditorPrintPreviewAction.ID, getAction(EditorPrintSetupAction.ID));
//    	fileMenu.insertAfter(EditorPrintSetupAction.ID, new Separator());
			fileMenu.insertAfter(EditorPrintAction.ID, getAction(EditorPrintSetupAction.ID));
    	fileMenu.insertAfter(EditorPrintSetupAction.ID, new Separator());  				  		  		
  	}

  	// Edit
  	editMenu = new MenuManager(Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.menu.edit"), ID_EDIT_MENU); //$NON-NLS-1$
  	
  	// Undo / Redo
  	editMenu.add(getAction(ActionFactory.UNDO.getId()));
  	editMenu.add(getAction(ActionFactory.REDO.getId()));
  	editMenu.add(new Separator());
  	
  	// Copy / Cut / Paste
  	editMenu.add(getAction(CopyAction.ID));
  	editMenu.add(getAction(PasteAction.ID));
  	editMenu.add(getAction(CutAction.ID));
  	editMenu.add(new Separator());  	
  	
  	// Delete
//  	editMenu.add(getAction(ActionFactory.DELETE.getId()));
  	editMenu.add(getAction(DeleteAction.ID));  	
  	editMenu.add(new Separator());  	
  	
  	// Group
  	editMenu.add(getAction(GroupAction.ID));
  	editMenu.add(getAction(UnGroupAction.ID));
  	editMenu.add(new Separator());  	
  	
  	// Edit - Align  	
  	addAlignActions(editMenu);  	
  	  	
  	// Edit - Order  	
  	editMenu.add(new Separator());
  	addOrderActions(editMenu);  	
  	
  	menubar.insertAfter(IWorkbenchActionConstants.M_FILE, editMenu);  	
  	
  	// View
  	viewMenu = new MenuManager(Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.menu.view"), ID_VIEW_MENU); //$NON-NLS-1$
  	addZoomActions(viewMenu);
  	
  	viewMenu.add(new Separator());
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
  	  	
  	viewMenu.add(new Separator());
  	viewMenu.add(getAction(ShowFigureToolTipAction.ID));
  	viewMenu.add(getAction(ShowStatusLineAction.ID));
  	
  	menubar.insertAfter(ID_EDIT_MENU, viewMenu);
  	
  	// Shape
  	shapeMenu = new MenuManager(Messages.getString("org.nightlabs.editor2d.EditorActionBarContributor.submenu.shape"), ID_SHAPE_MENU); //$NON-NLS-1$
  	shapeMenu.add(getAction(ConvertToShapeAction.ID));
  	shapeMenu.add(getAction(EditShapeAction.ID));
  	shapeMenu.add(new Separator());
  	addBooleanShapeActions(shapeMenu);
  	
  	menubar.insertAfter(ID_VIEW_MENU, shapeMenu);  	
  }
  
  private IMenuManager viewMenu = null;
  protected IMenuManager getViewMenu() {
  	return viewMenu;
  }
  
  private IMenuManager editMenu = null;
  protected IMenuManager getEditMenu() {
  	return editMenu;
  }
  
  private IMenuManager fileMenu = null;
  protected IMenuManager getFileMenu() {
  	return fileMenu;
  }

  private IMenuManager shapeMenu = null;
  protected IMenuManager getShapeMenu() {
  	return shapeMenu;
  }
  
  protected void addOrderActions(IContributionManager cm) 
  {
  	cm.add(getAction(ChangeOrderToLocalFront.ID));
  	cm.add(getAction(ChangeOrderOneUp.ID));  	
  	cm.add(getAction(ChangeOrderOneDown.ID));  	
  	cm.add(getAction(ChangeOrderToLocalBack.ID));  	  	
  }
  
  protected void addAlignActions(IContributionManager cm)
  {
  	cm.add(getAction(GEFActionConstants.ALIGN_LEFT));
  	cm.add(getAction(GEFActionConstants.ALIGN_CENTER));
  	cm.add(getAction(GEFActionConstants.ALIGN_RIGHT));  	
  	cm.add(new Separator());
  	cm.add(getAction(GEFActionConstants.ALIGN_TOP));
  	cm.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
  	cm.add(getAction(GEFActionConstants.ALIGN_BOTTOM));  	
  	cm.add(new Separator());	
  	cm.add(getAction(GEFActionConstants.MATCH_WIDTH));
  	cm.add(getAction(GEFActionConstants.MATCH_HEIGHT));  	  	
  }
  
  protected void addZoomActions(IContributionManager cm) 
  {
  	IAction zoomInAction = getAction(GEFActionConstants.ZOOM_IN);
  	zoomInAction.setImageDescriptor(SharedImages.getSharedImageDescriptor(
  			EditorPlugin.getDefault(), EditorActionBarContributor.class, "ZoomIn")); //$NON-NLS-1$
  	cm.add(zoomInAction);

  	IAction zoomOutAction = getAction(GEFActionConstants.ZOOM_OUT);
  	zoomOutAction.setImageDescriptor(SharedImages.getSharedImageDescriptor(
  			EditorPlugin.getDefault(), EditorActionBarContributor.class, "ZoomOut"));  	 //$NON-NLS-1$
  	cm.add(zoomOutAction);

  	cm.add(getAction(ZoomAllAction.ID));
  	cm.add(getAction(ZoomSelectionAction.ID));
  	cm.add(getAction(ZoomPageAction.ID));  	
  }

  protected void addBooleanShapeActions(IContributionManager cm) 
  {
  	cm.add(getAction(ShapeUnionAction.ID));
  	cm.add(getAction(ShapeIntersectAction.ID));  	
  	cm.add(getAction(ShapeSubtractAction.ID));
  	cm.add(getAction(ShapeExclusiveOrAction.ID));  	
  }
  
  private EditorZoomComboContributionItem zoomContributionItem = null;
  public EditorZoomComboContributionItem getEditorZoomComboContributionItem() {
  	return zoomContributionItem;
  }
  
  /**
   * Add actions to the given toolbar.
   * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
   */
  public void contributeToToolBar(IToolBarManager tbm) 
  {
  	super.contributeToToolBar(tbm);
  	// Undo / Redo
  	tbm.add(getAction(ActionFactory.UNDO.getId()));
  	tbm.add(getAction(ActionFactory.REDO.getId()));  	  	  	
  	tbm.add(new Separator());
  	
  	// Align
  	addAlignActions(tbm);  	
  	tbm.add(new Separator());
  	
  	// Zoom
  	String[] zoomStrings = new String[] {	ZoomManager.FIT_ALL, 
  											ZoomManager.FIT_HEIGHT, 
  											ZoomManager.FIT_WIDTH	};
  	zoomContributionItem = new EditorZoomComboContributionItem(getPage(), zoomStrings); 
  	tbm.add(zoomContributionItem);
  	addZoomActions(tbm);  	
  	tbm.add(new Separator());
  	
  	// RenderModes
  	RenderModeManager renderMan = RendererRegistry.sharedInstance().getRenderModeManager();
  	if (renderMan.getRenderModes().size() > 1)
  		tbm.add(new RenderModeContributionItem(getPage()));  	  		  	
  	tbm.add(new Separator());
  	
//  	tbm.add(getAction(RepaintAction.ID));
//  	tbm.add(getAction(ViewerAction.ID));
  }  
    
  /**
   * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
   */
  protected void declareGlobalActionKeys() 
  {
  	addGlobalActionKey(ActionFactory.PRINT.getId());
  	addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
  	addGlobalActionKey(ActionFactory.PASTE.getId());    
  	addGlobalActionKey(ActionFactory.COPY.getId());  	
  	addGlobalActionKey(GEFActionConstants.ZOOM_IN);
  	addGlobalActionKey(GEFActionConstants.ZOOM_OUT);
  	addGlobalActionKey(ActionFactory.SAVE.getId());  	
  	addGlobalActionKey(ActionFactory.SAVE_AS.getId());
  	addGlobalActionKey(ActionFactory.UNDO.getId());
  	addGlobalActionKey(ActionFactory.REDO.getId());

  	addGlobalActionKey(ZoomAllAction.ID);
  	addGlobalActionKey(ZoomSelectionAction.ID);
  	addGlobalActionKey(ZoomPageAction.ID);
  	addGlobalActionKey(RotateAction.ID);
  	addGlobalActionKey(EditShapeAction.ID);
  	addGlobalActionKey(ChangeOrderOneUp.ID);
  	addGlobalActionKey(ChangeOrderOneDown.ID);
  	addGlobalActionKey(ChangeOrderToLocalBack.ID);
  	addGlobalActionKey(ChangeOrderToLocalFront.ID);  	
  }

	public void dispose() 
	{
		super.dispose();
		
		// TODO: workaround because printActions are not removed automaticly
		IContributionItem fm = RCPUtil.getMenuItem(ID_FILE_MENU, getActionBars().getMenuManager());
		if (fm instanceof IMenuManager) {
			IMenuManager fileMenu = (IMenuManager) fm;
			removeActionMenu(EditorPrintAction.ID, fileMenu);			
//			removeActionMenu(EditorPrintPreviewAction.ID, fileMenu);
			removeActionMenu(EditorPrintSetupAction.ID, fileMenu);
		}		
	}
  
	protected void removeActionMenu(String actionID, IMenuManager menuMan) 
	{
		IContributionItem ci = RCPUtil.getMenuItem(actionID, menuMan);
		if (ci != null) {
			menuMan.remove(ci);
		}
	}
	 
}
