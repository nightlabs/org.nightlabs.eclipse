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

package org.nightlabs.editor2d.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.EditorContextMenuProvider;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;
import org.nightlabs.editor2d.outline.filter.FilterManager;
import org.nightlabs.editor2d.resource.Messages;


public class EditorOutlinePage 
extends ContentOutlinePage 
implements IAdaptable 
{
  static final int ID_OUTLINE  = 0;
  static final int ID_OVERVIEW = 1;
  static final int ID_FILTER 	 = 3;
  
  private AbstractEditor editor;
  private FilterManager filterMan;  
  private IAction showFilterAction;
  
  private PageBook pageBook;
  private Control outline;
  private IAction showOutlineAction; 
  private IAction showOverviewAction;
  private IAction filterOutlineAction;
  private DisposeListener disposeListener;
      
  public EditorOutlinePage(AbstractEditor editor, EditPartViewer viewer){
    super(viewer);
    this.editor = editor;
    this.filterMan = editor.getFilterManager();
    filterMan.addPropertyChangeListener(filterListener);
  }
    
  public void init(IPageSite pageSite)
  {
    super.init(pageSite);
    ActionRegistry registry = editor.getOutlineActionRegistry();
    IActionBars bars = pageSite.getActionBars();
    String id = ActionFactory.UNDO.getId();
    bars.setGlobalActionHandler(id, registry.getAction(id));
    id = ActionFactory.REDO.getId();
    bars.setGlobalActionHandler(id, registry.getAction(id));
    id = ActionFactory.DELETE.getId();
    bars.setGlobalActionHandler(id, registry.getAction(id));
    bars.updateActionBars();
  }

  protected void configureOutlineViewer()
  {
    getViewer().setEditDomain(editor.getOutlineEditDomain());
    getViewer().setEditPartFactory(editor.getOutlineEditPartFactory());
    ContextMenuProvider provider = editor.getContextMenuProvider();      
    getViewer().setContextMenu(provider);
    // TODO: ContextMenu ID Problem      
    getSite().registerContextMenu(
      EditorContextMenuProvider.CONTEXT_MENU_ID, //$NON-NLS-1$
      provider, getSite().getSelectionProvider());
    getViewer().setKeyHandler(editor.getCommonKeyHandler());
//    getViewer().addDropTargetListener(
//      new EditorTemplateTransferDropTargetListener(getViewer()));

    createActions();
        
    showPage(ID_OUTLINE);        
  }
    
  protected void createActions() 
  {
    IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
    
    // create Filter entries
    IMenuManager menuMan = getSite().getActionBars().getMenuManager();
    createFilterEntries(menuMan);    
    
    // Show Outline
    showOutlineAction = new Action() {
      public void run() {
        showPage(ID_OUTLINE);
      }
    };
    showOutlineAction.setImageDescriptor(SharedImages.getSharedImageDescriptor(
    		EditorPlugin.getDefault(), EditorOutlinePage.class, "Outline"));     //$NON-NLS-1$
    tbm.add(showOutlineAction);
    
    // Show Overview
    showOverviewAction = new Action() {
      public void run() {
        showPage(ID_OVERVIEW);
      }
    };
    showOverviewAction.setImageDescriptor(SharedImages.getSharedImageDescriptor(
    		EditorPlugin.getDefault(), EditorOutlinePage.class, "Overview")); //$NON-NLS-1$
    tbm.add(showOverviewAction);  	
    
//    // Add Page
//    newPageAction = new NewPageAction(editor);
//    tbm.add(newPageAction);
//    
//    // Delete Page
//    deletePageAction = new DeletePageAction(editor);
//    tbm.add(deletePageAction);
  }
  
//  private IAction newPageAction;
//  private IAction deletePageAction;  
  
  private IAction createFilterAction(final Class c) 
  {
		IAction filterAction = new Action() {
      public void run() {
        filterMan.setFilter(c);
      }  			
		};
		filterAction.setText(filterMan.getTypeName(c));		
		return filterAction;
  }
  
  private void createFilterEntries(IMenuManager menuMan) 
  {
		IAction filterNoneAction = new Action() {
      public void run() {
        filterMan.setAllFiltersAvailable();
      }  			
		};
		filterNoneAction.setText(Messages.getString("org.nightlabs.editor2d.outline.EditorOutlinePage.label.filterNone")); //$NON-NLS-1$
		menuMan.add(filterNoneAction);
		
  	for (Iterator it = filterMan.getAllFilters().iterator(); it.hasNext(); ) 
  	{
  		Class c = (Class) it.next();
			IAction filterAction = createFilterAction(c);
			menuMan.add(filterAction);
  	}  	
  }
  
  private Canvas overview;  
  private Thumbnail thumbnail;
//  private PreviewComposite overview;
  public void createControl(Composite parent){
    pageBook = new PageBook(parent, SWT.NONE);
    outline = getViewer().createControl(pageBook);
    
//    overview = new Canvas(pageBook, SWT.NONE);    
//    overview = new J2DCanvas(pageBook, SWT.NONE, new J2DSamplePaintable("TestMessage"));    
    overview = new J2DCanvas(pageBook, SWT.NONE, 
    		new DrawComponentPaintable(editor.getRootDrawComponent()));
    
//    RootEditPart rep = editor.getOutlineGraphicalViewer().getRootEditPart();
//    ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart) rep;
//    Viewport viewport = ((Viewport)root.getFigure());  
//    PreviewViewport previewViewport = new PreviewViewport(viewport);
//    overview = new PreviewComposite(editor.getRootDrawComponent(), previewViewport, pageBook, SWT.NONE);
    
    pageBook.showPage(outline);
    configureOutlineViewer();
    hookOutlineViewer();
    initializeOutlineViewer();    
  }
  
  public void dispose(){
    unhookOutlineViewer();
    if (thumbnail != null) {
      thumbnail.deactivate();
      thumbnail = null;
    }
    super.dispose();
//    outlinePage = null;
  }
  
//  public Object getAdapter(Class type) {
//    if (type == ZoomManager.class)
//      return editor.getOutlineGraphicalViewer().getProperty(ZoomManager.class.toString());
//    return null;
//  }
  public Object getAdapter(Class type) {
    return null;
  }

  public Control getControl() {
    return pageBook;
  }

  protected void hookOutlineViewer(){
    editor.getOutlineSelectionSynchronizer().addViewer(getViewer());
  }

  protected void initializeOutlineViewer(){
    setContents(editor.getRootDrawComponent());
    
//    if (getViewer() instanceof TreeViewer) {
//    	TreeViewer tv = (TreeViewer) getViewer();
//    	tv.getTreeViewer().addFilter(new ViewerFilter(){			
//				@Override
//				public boolean select(Viewer viewer, Object parentElement, Object element) 
//				{
//					if (element instanceof DrawComponentTreeEditPart) {
//						DrawComponentTreeEditPart dctep = (DrawComponentTreeEditPart) element;
//						if (dctep.getDrawComponent().isTemplate())
//							return false;
//					}
//					return true;
//				}			
//			});
//    }
  }
  
  protected void initializeOverview() 
  {
    LightweightSystem lws = new LightweightSystem(overview);
    RootEditPart rep = editor.getOutlineGraphicalViewer().getRootEditPart();
    if (rep instanceof ScalableFreeformRootEditPart) {
      ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart)rep;
      thumbnail = new ScrollableThumbnail((Viewport)root.getFigure());
      thumbnail.setBorder(new MarginBorder(3));
      thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
//      thumbnail.setSource(root.getFigure());      
      lws.setContents(thumbnail);
      disposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
          if (thumbnail != null) {
            thumbnail.deactivate();
            thumbnail = null;
          }
        }
      };
      editor.getEditor().addDisposeListener(disposeListener);
    }
  }
  
//  protected void initializeOverview() 
//  {
//    LightweightSystem lws = new J2DLightweightSystem(overview);
////  	LightweightSystem lws = new J2DLightweightSystem();
//    RootEditPart rep = editor.getOutlineGraphicalViewer().getRootEditPart();
//    if (rep instanceof J2DScalableFreeformRootEditPart) {
//    	J2DScalableFreeformRootEditPart root = (J2DScalableFreeformRootEditPart)rep;
////      thumbnail = new J2DScrollableThumbnail((Viewport)root.getFigure());
//      thumbnail = new ScrollableThumbnail((Viewport)root.getFigure());    	
//      thumbnail.setBorder(new MarginBorder(3));
//      thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
//      lws.setContents(thumbnail);
//      disposeListener = new DisposeListener() {
//        public void widgetDisposed(DisposeEvent e) {
//          if (thumbnail != null) {
//            thumbnail.deactivate();
//            thumbnail = null;
//          }
//        }
//      };
//      editor.getEditor().addDisposeListener(disposeListener);
//    }
//  }
  
  public void setContents(Object contents) {
    getViewer().setContents(contents);
  }
  
  protected void showPage(int id) {
    if (id == ID_OUTLINE) {
      showOutlineAction.setChecked(true);
      showOverviewAction.setChecked(false);
      pageBook.showPage(outline);
      if (thumbnail != null)
        thumbnail.setVisible(false);
    } 
    else if (id == ID_OVERVIEW) {
      if (thumbnail == null)
        initializeOverview();
      showOutlineAction.setChecked(false);
      showOverviewAction.setChecked(true);
      pageBook.showPage(overview);
      if (thumbnail != null)      
      	thumbnail.setVisible(true);
    }
  }
  
  protected void unhookOutlineViewer(){
    editor.getOutlineSelectionSynchronizer().removeViewer(getViewer());
    if (disposeListener != null && editor.getEditor() != null && !editor.getEditor().isDisposed())
      editor.getEditor().removeDisposeListener(disposeListener);
  }
 
  private PropertyChangeListener filterListener = new PropertyChangeListener() 
  {	
		public void propertyChange(PropertyChangeEvent pce)
		{
			if (pce.getPropertyName().equals(FilterManager.FILTER_CHANGED)) 
			{
				getViewer().setContents(editor.getRootDrawComponent());
			}	
			else if (pce.getPropertyName().equals(FilterManager.FILTER_ADDED)) 
			{				
				Object o = pce.getNewValue();
				if (o instanceof Class) {
					IMenuManager menuMan = getSite().getActionBars().getMenuManager();					
					Class c = (Class) o;
					menuMan.add(createFilterAction(c));
					getViewer().setContents(editor.getRootDrawComponent());					
				}
			}
			else if (pce.getPropertyName().equals(FilterManager.FILTERS_ADDED)) 
			{				
				Object o = pce.getNewValue();
				if (o instanceof Collection) {
					IMenuManager menuMan = getSite().getActionBars().getMenuManager();					
					Collection classes = (Collection) o;
					for (Iterator it = classes.iterator(); it.hasNext(); ) {
						Class c = (Class) it.next(); 
						menuMan.add(createFilterAction(c));						
					}
					getViewer().setContents(editor.getRootDrawComponent());					
				}
			}			
		}	
	};
	
}
