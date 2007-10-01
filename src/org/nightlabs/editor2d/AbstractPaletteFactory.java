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

import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.model.IModelCreationFactory;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.tools.EditorSelectionToolEntry;
import org.nightlabs.editor2d.tools.EllipseToolEntry;
import org.nightlabs.editor2d.tools.ImageToolEntry;
import org.nightlabs.editor2d.tools.LineToolEntry;
import org.nightlabs.editor2d.tools.RectangleToolEntry;
import org.nightlabs.editor2d.tools.TextToolEntry;
import org.nightlabs.editor2d.tools.ZoomToolEntry;

public abstract class AbstractPaletteFactory 
{
  public AbstractPaletteFactory(Editor2DFactory factory) 
  {
    super();
    this.factory = factory;
  }
	
  protected Editor2DFactory factory = null;
  public Editor2DFactory getFactory() {
  	return factory;
  }
  
  /** Default palette size. */
  protected static final int DEFAULT_PALETTE_SIZE = 125;
  /** Preference ID used to persist the palette location. */
  protected static final String PALETTE_DOCK_LOCATION = "PaletteFactory.Location"; //$NON-NLS-1$
  /** Preference ID used to persist the palette size. */
  protected static final String PALETTE_SIZE = "PaletteFactory.Size"; //$NON-NLS-1$
  /** Preference ID used to persist the flyout palette's state. */
  protected static final String PALETTE_STATE = "PaletteFactory.State"; //$NON-NLS-1$
  
//  public abstract CreationFactory getCreationFactory(Class targetClass);
  public abstract IModelCreationFactory getCreationFactory(Class targetClass);  
  
  /**
   * Return a FlyoutPreferences instance used to save/load the preferences of a flyout palette.
   */
  public FlyoutPreferences createPalettePreferences() 
  {
    // set default flyout palette preference values, in case the preference store
    // does not hold stored values for the given preferences
    getPreferenceStore().setDefault(PALETTE_DOCK_LOCATION, -1);
    getPreferenceStore().setDefault(PALETTE_STATE, -1);
    getPreferenceStore().setDefault(PALETTE_SIZE, DEFAULT_PALETTE_SIZE);
    
    return new FlyoutPreferences() {
      public int getDockLocation() {
        return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
      }
      public int getPaletteState() {
        return getPreferenceStore().getInt(PALETTE_STATE);
      }
      public int getPaletteWidth() {
        return getPreferenceStore().getInt(PALETTE_SIZE);
      }
      public void setDockLocation(int location) {
        getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
      }
      public void setPaletteState(int state) {
        getPreferenceStore().setValue(PALETTE_STATE, state);
      }
      public void setPaletteWidth(int width) {
        getPreferenceStore().setValue(PALETTE_SIZE, width);
      }
    };
  } 
    
  /**
  * Returns the preference store for the EditorPlugin.
  * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore() 
  */
  protected IPreferenceStore getPreferenceStore() 
	{
  	return EditorPlugin.getDefault().getPreferenceStore();
	}
 
 protected PaletteContainer createShapesDrawer() 
 {
  PaletteDrawer componentsDrawer = new PaletteDrawer(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.category.shapes")); //$NON-NLS-1$

  // add Rectangle Tool
  ToolEntry component = createRectangleToolEntry();
  componentsDrawer.add(component);

  // add Ellipse Tool
  component = createEllipseToolEntry();
  componentsDrawer.add(component);

  // add Line Tool
  component = createLineToolEntry();
  componentsDrawer.add(component);
  
  // add Text Tool
  component = createTextToolEntry();   
  componentsDrawer.add(component);

  // add Image Tool
  component = createImageToolEntry();
  componentsDrawer.add(component);
  
  return componentsDrawer;
 } 
  
 /**
  * Creates the PaletteRoot and adds all palette elements.
  * Use this factory method to create a new palette for your graphical editor.
  * @return a new PaletteRoot
  */
 protected PaletteRoot createPalette() 
 {
  PaletteRoot palette = new PaletteRoot();
  palette.add(createToolsGroup(palette));
  palette.add(createShapesDrawer());
  return palette;
 }
 
 protected PaletteContainer createToolsGroup(PaletteRoot palette) 
 {
  PaletteGroup toolGroup = new PaletteGroup(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.category.tools")); //$NON-NLS-1$

  // Add a selection tool to the group
  ToolEntry tool = createEditorSelectionToolEntry();
  toolGroup.add(tool);
  palette.setDefaultEntry(tool);
  
//  // Add a marquee tool to the group
//  toolGroup.add(createMarqueeToolEntry());

  // Add a zoom tool to the group
  toolGroup.add(createZoomToolEntry());
  
//  // Add a (unnamed) separator to the group
//  toolGroup.add(new PaletteSeparator());

  return toolGroup;
 }
 
 protected ToolEntry createEditorSelectionToolEntry() {
	  return new EditorSelectionToolEntry();	 
 }
 
// protected ToolEntry createMarqueeToolEntry() 
// {
//	  return new MarqueeToolEntry();	 
// }

 protected ToolEntry createZoomToolEntry() 
 {
	 return new ZoomToolEntry(
			 EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.zoom.label")), //$NON-NLS-1$
		   EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.zoom.description")), //$NON-NLS-1$
		   SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Zoom"),     //$NON-NLS-1$
		   SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Zoom", //$NON-NLS-1$
		  		 ImageDimension._24x24, ImageFormat.png)
	 );	 
 }
 
 protected ToolEntry createRectangleToolEntry() 
 {
	 return new RectangleToolEntry (
	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.rectangle.label")), //$NON-NLS-1$
	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.rectangle.description")),  //$NON-NLS-1$
	    RectangleDrawComponent.class,
	    getCreationFactory(RectangleDrawComponent.class), 
	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Rectangle"),     //$NON-NLS-1$
	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Rectangle", //$NON-NLS-1$
	    		ImageDimension._24x24, ImageFormat.png)
	  );	 
 }
 
 protected ToolEntry createEllipseToolEntry() 
 {
	 return new EllipseToolEntry (
	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.ellipse.label")), //$NON-NLS-1$
	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.ellipse.description")),  //$NON-NLS-1$
	    EllipseDrawComponent.class,
	    getCreationFactory(EllipseDrawComponent.class), 
	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Ellipse"),         //$NON-NLS-1$
	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Ellipse", //$NON-NLS-1$
	    		ImageDimension._24x24, ImageFormat.png)
	  );	 
 	}
 
 	protected ToolEntry createLineToolEntry() 
 	{
 		return new LineToolEntry
 	  (
 	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.line.label")), //$NON-NLS-1$
 	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.line.description")),  //$NON-NLS-1$
 	    LineDrawComponent.class,
 	    getCreationFactory(LineDrawComponent.class), 
 	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Line"),     //$NON-NLS-1$
 	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Line", //$NON-NLS-1$
 	    		ImageDimension._24x24, ImageFormat.png)    
 	  ); 		
 	}
 	
 	protected ToolEntry createTextToolEntry() 
 	{
 		return new TextToolEntry
 	  (
 	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.text.label")), //$NON-NLS-1$
 	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.text.description")),  //$NON-NLS-1$
 	    TextDrawComponent.class,
 	    getCreationFactory(TextDrawComponent.class), 
 	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Text"),     //$NON-NLS-1$
 	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Text", //$NON-NLS-1$
 	    		ImageDimension._24x24, ImageFormat.png)    
 	  ); 		
 	}
 	
 	protected ToolEntry createImageToolEntry() 
 	{
 		return new ImageToolEntry
 	  (
 	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.image.label")), //$NON-NLS-1$
 	    EditorPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.AbstractPaletteFactory.tool.image.description")),  //$NON-NLS-1$
 	    ImageDrawComponent.class,
 	    getCreationFactory(ImageDrawComponent.class), 
 	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Image"),     //$NON-NLS-1$
 	    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Image", //$NON-NLS-1$
 	    		ImageDimension._24x24, ImageFormat.png)        
 	  ); 		
 	}
 	
}
