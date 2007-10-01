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
package org.nightlabs.editor2d.actions.order;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.actions.AbstractEditorSelectionAction;
import org.nightlabs.editor2d.command.DrawComponentReorderCommand;
import org.nightlabs.editor2d.util.OrderUtil;

/**
 * An Abstract Base Implementation of a generic reorder action  
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class AbstractChangeOrderSelectionAction 
extends AbstractEditorSelectionAction 
{

	/**
	 * @param editor
	 * @param style
	 */
	public AbstractChangeOrderSelectionAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public AbstractChangeOrderSelectionAction(AbstractEditor editor) {
		super(editor);
	}

	/**
	 *@return true, if objects are selected, except the RootEditPart or LayerEditParts
	 */
	protected boolean calculateEnabled() {
		return !getDefaultSelection(false).isEmpty();
	}
	
	/**
	 * executes a Command which changes the order, based on the newIndex and the 
	 * DrawComponentContainer for all selected Objects
	 */
	public void run() 
	{
		List<DrawComponent> dcs = getSelectionAsList(DrawComponent.class, true);
//		Collection<DrawComponent> dcs = getSelection(DrawComponent.class, true);		
		if (!dcs.isEmpty()) 
		{
			if (getPrimarySelected().getModel() instanceof DrawComponent) {
				primarySelected = (DrawComponent) getPrimarySelected().getModel();
			}
			// sort the selection dependend on their index
			Collections.sort(dcs, indexComparator);
			CompoundCommand compoundCmd = new CompoundCommand();
			for (Iterator it = dcs.iterator(); it.hasNext(); ) 
			{
				DrawComponent dc = (DrawComponent) it.next();
				Command cmd = changeOrder(dc, getContainer(), getNewIndex());
				compoundCmd.add(cmd);
			}
			compoundCmd.setLabel(getText());
			execute(compoundCmd);
		}
	}
	
	protected DrawComponent primarySelected = null;
	
	/**
	 * returns the primary selected DrawComponent
	 * @return the primary selected DrawComponent 
	 */
	public DrawComponent getPrimarySelectedDrawComponent() {
		return primarySelected;
	}
	
	/**
	 * compares the index of 2 DrawComponents
	 */
	protected Comparator indexComparator = new Comparator()
	{	
		public int compare(Object o1, Object o2) 
		{
			if (o1 instanceof DrawComponent && o2 instanceof DrawComponent) 
			{
				DrawComponent dc1 = (DrawComponent) o1;
				DrawComponent dc2 = (DrawComponent) o2;
				if (primarySelected != null) 
				{
					DrawComponentContainer primaryContainer = primarySelected.getParent();
					if (!dc1.getParent().equals(primaryContainer) || !dc2.getParent().equals(primaryContainer))
					{
						if (dc1.getParent().equals(primaryContainer) && !dc2.getParent().equals(primaryContainer))
							return 1;
						if (!dc1.getParent().equals(primaryContainer) && dc2.getParent().equals(primaryContainer))
							return -1;
						else {
							return compareIndexInDifferentContainer(dc1, dc2);
						}							
					}
					else
						return compareIndexInSameContainer(dc1, dc2);
				}
				else
					return compareIndexInSameContainer(dc1, dc2);
			}
			return 0;
		}	
	};
	
	/**
	 * compares the index of 2 DrawComponents if they are in the same container 
	 */
	protected int compareIndexInSameContainer(DrawComponent dc1, DrawComponent dc2) 
	{
		int index1 = dc1.getParent().getDrawComponents().indexOf(dc1);
		int index2 = dc2.getParent().getDrawComponents().indexOf(dc2);
		if (index1 > index2)
			return 1;
		if (index1 < index2)
			return -1;
		else
			return 0;							
	}

	/**
	 * compares the index of 2 DrawComponents if they are NOT in the same container, 
	 * then the index of the parents are compared   
	 */	
	protected int compareIndexInDifferentContainer(DrawComponent dc1, DrawComponent dc2) 
	{
		int parentIndex1 = dc1.getParent().getParent().getDrawComponents().indexOf(dc1.getParent());
		int parentIndex2 = dc2.getParent().getParent().getDrawComponents().indexOf(dc2.getParent());
		if (parentIndex1 > parentIndex2)
			return 1;
		if (parentIndex1 < parentIndex2)
			return -1;
		else
			return 0;									
	}	
	
	/**
	 * 
	 * @param dc the DrawComponent to reorder
	 * @param container the DrawComponentContainer the dc should be reordered to
	 * @param newIndex the newIndex in the List of the DrawComponentContainer 
	 * @return a DrawComponentReorderCommand which executes the reordering 
	 */
	public Command changeOrder(DrawComponent dc, DrawComponentContainer container, int newIndex) 
	{
		return new DrawComponentReorderCommand(dc, container, newIndex);		
	}
	
	public Layer getCurrentLayer() 
	{
		if (getPrimarySelected() != null) 
		{
			DrawComponent dc = (DrawComponent) getPrimarySelected().getModel();
			return dc.getRoot().getCurrentLayer();
		}
		return null;
	}
	
	public int getLastIndex(DrawComponentContainer container) 
	{
		return OrderUtil.getLastIndex(container);
	}
	
	public int indexOf(DrawComponent dc) 
	{
		return OrderUtil.indexOf(dc);
	}
	
	/**
	 * returns the newIndex of the List of DrawComponents in the
	 * given {@link DrawComponentContainer}
	 * 
	 * @return the newIndex of the List of DrawComponents in the
	 * given {@link DrawComponentContainer}
	 * 
	 * @see #getContainer()
	 */
	public abstract int getNewIndex();
	
	/**
	 * returns the {@link DrawComponentContainer} where the selected {@link DrawComponent}
	 * should be reordered to
	 * 
	 * @return the {@link DrawComponentContainer} where the selected {@link DrawComponent}
	 * should be reordered to
	 */
	public abstract DrawComponentContainer getContainer();
}
