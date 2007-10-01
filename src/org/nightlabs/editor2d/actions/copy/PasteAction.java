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
package org.nightlabs.editor2d.actions.copy;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.actions.EditorActionConstants;
import org.nightlabs.editor2d.command.CloneDrawComponentCommand;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class PasteAction 
extends AbstractEditorAction
{
//	public static final String ID = PasteAction.class.getName();
	public static final String ID = ActionFactory.PASTE.getId();	
	
	protected boolean enabled = false;  	
	private boolean copy = false;
	private boolean cut = false;	
	private Collection<DrawComponent> clipBoardContent = null;
		
	/**
	 * @param part
	 */
	public PasteAction(AbstractEditor part) {
		super(part);
	}

	/**
	 * @param part
	 * @param style
	 */
	public PasteAction(AbstractEditor part, int style) {
		super(part, style);
	}

  protected void init() 
  {
  	setText(Messages.getString("org.nightlabs.editor2d.actions.copy.PasteAction.text")); //$NON-NLS-1$
  	setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.copy.PasteAction.tooltip")); //$NON-NLS-1$
  	setId(ID);
//  	setActionDefinitionId(ID);
  	setActionDefinitionId("org.eclipse.ui.edit.paste");  	 //$NON-NLS-1$
//  	setAccelerator(SWT.CTRL | 'P');
  } 
		
	protected boolean calculateEnabled() {
		return enabled;
	}	
	
	public void run() 
	{
		CompoundCommand cmd = new CompoundCommand();
		if (clipBoardContent != null) {
			for (Iterator<DrawComponent> it = clipBoardContent.iterator(); it.hasNext(); ) {
				DrawComponent dc = it.next();
				CloneDrawComponentCommand cloneCmd = new CloneDrawComponentCommand(dc, getCurrentLayer());
				if (copy)
					cloneCmd.setCloneName(dc.getName() + getCopyString());
				if (cut)
					cloneCmd.setCloneName(dc.getName());
				cmd.add(cloneCmd);
			}
			execute(cmd);					
		}
	}
			
	public IPropertyChangeListener copyListener = new IPropertyChangeListener() {	
		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(EditorActionConstants.PROP_COPY_TO_CLIPBOARD)) {
				copy = true;
				cut = false;
				Object content = event.getNewValue();
				if (!content.equals(EditorActionConstants.EMPTY_CLIPBOARD_CONTENT)) {
					enabled = true;
					if (content instanceof Collection) {
						clipBoardContent = (Collection<DrawComponent>) content;
					}
				}
			}			
		}	
	};
				
	public IPropertyChangeListener cutListener = new IPropertyChangeListener() {	
		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(EditorActionConstants.PROP_COPY_TO_CLIPBOARD)) {
				cut = true;
				copy = false;
				Object content = event.getNewValue();
				if (!content.equals(EditorActionConstants.EMPTY_CLIPBOARD_CONTENT)) {
					enabled = true;
					if (content instanceof Collection) {
						clipBoardContent = (Collection<DrawComponent>) content;
					}
				}
			}			
		}	
	};	
	
	protected String getCopyString() {
		return " ("+Messages.getString("org.nightlabs.editor2d.actions.copy.PasteAction.copy.text")+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}	
}
