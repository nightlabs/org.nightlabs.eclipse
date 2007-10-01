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
package org.nightlabs.editor2d;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.editor2d.command.SetConstraintCommand;
import org.nightlabs.editor2d.config.QuickOptionsConfigModule;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorViewerKeyHandler 
extends GraphicalViewerKeyHandler 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorViewerKeyHandler.class);
	
	/**
	 * @param viewer
	 */
	public EditorViewerKeyHandler(GraphicalViewer viewer) 
	{
		super(viewer);
		init();
	}

	protected void init() 
	{
		initConfigModule();		
		getViewer().addSelectionChangedListener(selectionListener);
		getViewer().getControl().addDisposeListener(disposeListener);		
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
			confMod = (QuickOptionsConfigModule) 
				Config.sharedInstance().createConfigModule(QuickOptionsConfigModule.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		} 		
	}
	
	protected CommandStack getCommandStack() {
		return getViewer().getEditDomain().getCommandStack();
	}
		
	private List<Object> selectedObjects = null;
	protected List<Object> getSelectedObjects() 
	{
		if (selectedObjects == null)
			selectedObjects = Collections.EMPTY_LIST;
		
		return selectedObjects;
	}
	
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener()
	{	
		public void selectionChanged(SelectionChangedEvent event) 
		{
			ISelection selection = event.getSelection();			
			if (!selection.isEmpty()) 
			{
				if (selection instanceof IStructuredSelection) 
				{
//					LOGGER.debug("Selection changed");
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					selectedObjects = structuredSelection.toList();
					return;
				}
			}
			selectedObjects = Collections.EMPTY_LIST;			
		}	
	};
	
	private DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			getViewer().removeSelectionChangedListener(selectionListener);
		}	
	};
	
	public boolean keyPressed(KeyEvent event) 
	{
//		LOGGER.debug("Key pressed");
		if (!getSelectedObjects().isEmpty()) 
		{
			if (acceptLeft(event)) {				
				translate(getSelectedObjects(), LEFT);
				return true;
			}
			if (acceptRight(event)) {				
				translate(getSelectedObjects(), RIGHT);
				return true;				
			}
			if (acceptUp(event)) {
				translate(getSelectedObjects(), UP);
				return true;				
			}
			if (acceptDown(event)) {				
				translate(getSelectedObjects(), DOWN);
				return true;				
			}						
		}		
		return super.keyPressed(event);
	}	
	
	public static final int LEFT = PositionConstants.LEFT;
	public static final int RIGHT = PositionConstants.RIGHT;
	public static final int UP = PositionConstants.TOP;
	public static final int DOWN = PositionConstants.BOTTOM;
		
	public int getTranslationX() {
		return getConfigModule().getMoveTranslationX();
	}
	
	public int getTranslationY() {
		return getConfigModule().getMoveTranslationY();
	}
					
	/**
	 * 
	 * @param editParts a List of EditParts to translate
	 * @param direction the translation direction 
	 */
	protected void translate(List editParts, int direction) 
	{
		CompoundCommand compoundCmd = new CompoundCommand();
		for (Iterator it = editParts.iterator(); it.hasNext(); ) 
		{
			EditPart ep = (EditPart) it.next();
			if (ep instanceof AbstractDrawComponentEditPart) 
			{
				AbstractDrawComponentEditPart dcep = (AbstractDrawComponentEditPart) ep;
				DrawComponent dc = dcep.getDrawComponent();
				SetConstraintCommand cmd = new SetConstraintCommand();
				cmd.setPart(dc);
				Rectangle dcBounds = new Rectangle(dc.getBounds());				
				switch (direction) 
				{				
					case(DOWN):
						dcBounds.y += getTranslationY();	
						break;
					case(UP):
						dcBounds.y -= getTranslationY();
						break;
					case(LEFT):
						dcBounds.x -= getTranslationX();
						break;					
					case(RIGHT):
						dcBounds.x += getTranslationX();			
						break;						
				}	
				cmd.setBounds(dcBounds);
				compoundCmd.add(cmd);				
			}			
		}
		if (!compoundCmd.getCommands().isEmpty()) {
			getCommandStack().execute(compoundCmd);
		}
		
		logger.debug("Translate Command executed");		 //$NON-NLS-1$
	}	
	
	/**
	 * checks if the LEFT ARROW has been pressed
	 * @return <code>true</code> if the keys pressed indicate translate selection left
	 */
	protected boolean acceptLeft(KeyEvent event) {
//		return ((event.stateMask & SWT.ALT) != 0) && (event.keyCode == SWT.ARROW_LEFT);
		return (event.keyCode == SWT.ARROW_LEFT);
	}
	
	/**
	 * checks if the RIGHT ARROW has been pressed
	 * @return <code>true</code> if the keys pressed indicate translate selection left
	 */
	protected boolean acceptRight(KeyEvent event) {
//		return ((event.stateMask & SWT.ALT) != 0) && (event.keyCode == SWT.ARROW_RIGHT);
		return (event.keyCode == SWT.ARROW_RIGHT);
	}	

	/**
	 * checks if the DOWN ARROW has been pressed
	 * @return <code>true</code> if the keys pressed indicate translate selection left
	 */
	protected boolean acceptDown(KeyEvent event) {
//		return ((event.stateMask & SWT.ALT) != 0) && (event.keyCode == SWT.ARROW_DOWN);
		return (event.keyCode == SWT.ARROW_DOWN);
	}	

	/**
	 * checks if the UP ARROW has been pressed
	 * @return <code>true</code> if the keys pressed indicate translate selection left
	 */
	protected boolean acceptUp(KeyEvent event) {
//		return ((event.stateMask & SWT.ALT) != 0) && (event.keyCode == SWT.ARROW_UP);
		return (event.keyCode == SWT.ARROW_UP);
	}	
	
}
