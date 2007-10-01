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

package org.nightlabs.editor2d.viewer.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.event.ISelectionChangedListener;
import org.nightlabs.editor2d.viewer.ui.event.SelectionEvent;
import org.nightlabs.editor2d.viewer.ui.util.ToolUtil;

/**
 * The SelectionManager which handles the selection of the IViewer
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class SelectionManager 
//extends AbstractTypeSafeSelectionSupport 
{	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(SelectionManager.class);
	
	public SelectionManager(IViewer viewer)
	{
		this.viewer = viewer;
	}
	
	private IViewer viewer = null;
	
	/**
	 * 
	 * @return the Viewer for the SelectionManager
	 * @see IViewer
	 */
	public IViewer getViewer() {
		return viewer;
	}
	
	private List<DrawComponent> selectedDrawComponents = new LinkedList<DrawComponent>();
	private List<DrawComponent> readOnlySelectedDrawComponents = null;

	/**
	 * @return a unmodifiable List of the selected DrawComponents
	 */
	public List<DrawComponent> getSelectedDrawComponents() 
	{
		if (readOnlySelectedDrawComponents == null)
			readOnlySelectedDrawComponents = Collections.unmodifiableList(new ArrayList<DrawComponent>(selectedDrawComponents));

		return readOnlySelectedDrawComponents;
	}

	private Collection<ISelectionChangedListener> selectionListener = null;
	protected Collection<ISelectionChangedListener> getSelectionListener() 
	{
		if (selectionListener == null)
			selectionListener = new LinkedList<ISelectionChangedListener>();
		
		return selectionListener;
	}
	
	/**
	 * adds a ISelectionChangedListener to gets notified of selection changes
	 * 
	 * @param l the ISelectionChangedListener to add
	 * @see ISelectionChangedListener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener l) 
	{
		getSelectionListener().add(l);
	}
	
	/**
	 * removes a previously added ISelectionChangedListener
	 * 
	 * @param l the ISelectionChangedListener to remove
	 * @see ISelectionChangedListener
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener l) 
	{
		getSelectionListener().remove(l);
	}
	
	protected void fireSelectionChanged() 
	{
		SelectionEvent evt = new SelectionEvent(getSelectedDrawComponents());
		for (Iterator<ISelectionChangedListener> it = getSelectionListener().iterator(); it.hasNext(); )
		{
			ISelectionChangedListener l = it.next();
			l.selectionChanged(evt);
		}
		logger.debug("selection changed!"); //$NON-NLS-1$
	}
	
	private Map<DrawComponent, DrawComponent> dc2SelectionDC = new HashMap<DrawComponent, DrawComponent>();
	
	/**
	 * dc the DrawComponent to add to the selection and fire a selection change and 
	 * repaint the viewer
	 * 
	 * @param dc the DrawComponent to add to the selection
	 */
	public void addSelectedDrawComponent(DrawComponent dc)
	{
		addSelectedDrawComponent(dc, true);
	}
		
	/**
	 * add a DrawComponent to the selection and fire a selection change
	 * 
	 * @param dc the DrawComponent to add to the selection
	 * @param repaint determines if the viewer should be repainted
	 */
	public void addSelectedDrawComponent(DrawComponent dc, boolean repaint) 
	{
		addSelectedDrawComponent(dc, repaint, true);
	}
	
	/**
	 * add a DrawComponent to the selection
	 * 
	 * @param dc the DrawComponent to add to the selection
	 * @param repaint determines if the viewer should be repainted
	 * @param fireNotification determines if a selectionChange should be fired
	 */
	public void addSelectedDrawComponent(DrawComponent dc, boolean repaint, 
			boolean fireNotification) 
	{
		if (!selectedDrawComponents.contains(dc)) 
		{
			selectedDrawComponents.add(dc);
			if (fireNotification)
				fireSelectionChanged();
			
			DrawComponent selectionDC = createSelectionDrawComponent(dc);
			if (selectionDC != null) {
				dc2SelectionDC.put(dc, selectionDC);
				addToTempContent(selectionDC);				
			}
						
			if (repaint)
				getViewer().getBufferedCanvas().repaint();
			
			readOnlySelectedDrawComponents = null;
//			LOGGER.debug("dc "+dc.getName()+" selected");
		}
	}	
		
	/**
	 * removes a DrawComponent from the selection and fire a selection change and 
	 * repaints the viewer
	 * 
	 * @param dc the DrawComponent to remove from the selection
	 */
	public void removeSelectedDrawComponent(DrawComponent dc) 
	{
		removeSelectedDrawComponent(dc, true);
	}
	
	/**
	 * removes a DrawComponent from the selection and fires a selection change
	 * 
	 * @param dc the DrawComponent to remove from the selection
	 * @param repaint determines if the viewer should be repainted
	 */
	public void removeSelectedDrawComponent(DrawComponent dc, boolean repaint) 
	{
		removeSelectedDrawComponent(dc, repaint, true);
	}	
	
	/**
	 * removes a DrawComponent from the selection
	 * 
	 * @param dc the DrawComponent to remove from the selection
	 * @param repaint determines if the viewer should be repainted
	 * @param fireNotification determines if a selectionChange should be fired
	 */
	public void removeSelectedDrawComponent(DrawComponent dc, boolean repaint, 
			boolean fireNotification)
	{
		if (selectedDrawComponents.contains(dc)) 
		{
			selectedDrawComponents.remove(dc);
			if (fireNotification)
				fireSelectionChanged();
			
			DrawComponent selectionDC = dc2SelectionDC.get(dc);
			if (selectionDC != null) {
				removeTempContent(selectionDC); 
			} else {
				logger.debug("selectionDC for "+dc.getName()+" not in dc2SelectionDC"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			if (repaint)
				getViewer().getBufferedCanvas().repaint();
			
			readOnlySelectedDrawComponents = null;
//			LOGGER.debug("dc "+dc.getName()+" deselected");			
		}
	}

	/**
	 * 
	 * add a Collection of DrawComponents to the selection and fires a selection change 
	 * and repaints the viewer
	 * 
	 * @param dcs the Collection of DrawComponents to add to the selection
	 */
	public void addSelectedDrawComponents(Collection<DrawComponent> dcs)
	{
		addSelectedDrawComponents(dcs, true);
	}
		
	/**
	 * add a Collection of DrawComponents to the selection and fires a selection change
	 * 
	 * @param dcs the Collection of DrawComponents to add to the selection
	 * @param repaint determines if the viewer should be repainted
	 */
	public void addSelectedDrawComponents(Collection<DrawComponent> dcs, boolean repaint) 
	{
		addSelectedDrawComponents(dcs, repaint, true);
	}

	/**
	 * add a Collection of DrawComponents to the selection
	 * 
	 * @param dcs the Collection of DrawComponents to add to the selection
	 * @param repaint determines if the viewer should be repainted
	 * @param fireNotification determines if a selectionChange should be fired
	 */
	public void addSelectedDrawComponents(Collection<DrawComponent> dcs, boolean repaint,
			boolean fireNotification) 
	{
		selectedDrawComponents.addAll(dcs);
		
		for (Iterator<DrawComponent> it = dcs.iterator(); it.hasNext(); ) {
			DrawComponent dc = it.next();
			DrawComponent selectionDC = createSelectionDrawComponent(dc);
			dc2SelectionDC.put(dc, selectionDC);
			addToTempContent(selectionDC);			
		}
		
		if (repaint)
			getViewer().getBufferedCanvas().repaint();
		
		readOnlySelectedDrawComponents = null;

		if (fireNotification)
			fireSelectionChanged();
	}
		
	/**
	 * removes a Collection of DrawComponents from the selection and fires a selection change
	 * and repaints the viewer
	 * 
	 * @param dcs the Collection of DrawComponents to remove from the selection
	 */
	public void removeSelectedDrawComponents(Collection<DrawComponent> dcs)
	{
		removeSelectedDrawComponents(dcs, true);
	}
		
	/**
	 * removes a Collection of DrawComponents from the selection and fires a selection change
	 * 
	 * @param dcs the Collection of DrawComponents to remove from the selection
	 * @param repaint determines if the viewer should be repainted
	 */
	public void removeSelectedDrawComponents(Collection<DrawComponent> dcs, boolean repaint)
	{
		removeSelectedDrawComponents(dcs, repaint, true);
	}

	/**
	 * removes a Collection of DrawComponents from the selection
	 * 
	 * @param dcs the Collection of DrawComponents to remove from the selection
	 * @param repaint determines if the viewer should be repainted
	 * @param fireNotification determines if a selectionChange should be fired
	 */
	public void removeSelectedDrawComponents(Collection<DrawComponent> dcs, boolean repaint,
			boolean fireNotification)
	{
		selectedDrawComponents.removeAll(dcs);
		if (fireNotification)
			fireSelectionChanged();
		
		for (Iterator<DrawComponent> it = dcs.iterator(); it.hasNext(); ) {
			DrawComponent dc = it.next();
			DrawComponent selectionDC = dc2SelectionDC.get(dc);
			if (selectionDC != null) {
				removeTempContent(selectionDC); 
			} else {
				logger.debug("selectionDC for "+dc.getName()+" not in dc2SelectionDC"); //$NON-NLS-1$ //$NON-NLS-2$
			}			
		}
		
		if (repaint)
			getViewer().getBufferedCanvas().repaint();		
		
		readOnlySelectedDrawComponents = null;
	}
	
	/**
	 * clears the selection and fires a selection change and repaints the viewer
	 *
	 */
	public void clearSelection() 
	{
		clearSelection(true);
	}

	/**
	 * clears the selection and fires a selection change
	 * 
	 * @param repaint determines if the viewer should be repainted
	 */
	public void clearSelection(boolean repaint) 
	{
		clearSelection(repaint, true);
	}
		
	/**
	 * clears the selection
	 * 
	 * @param repaint determines if the viewer should be repainted
	 * @param fireNotification determines if a selectionChange should be fired
	 */
	public void clearSelection(boolean repaint, boolean fireNotification) 
	{		
		dc2SelectionDC.clear();
		selectedDrawComponents.clear();
		clearTempContent();
		
		if (fireNotification)
			fireSelectionChanged();
		
		if (repaint)
			getViewer().getBufferedCanvas().repaint();	
		
		readOnlySelectedDrawComponents = null;
	}
			
	public boolean contains(DrawComponent dc)
	{
		return selectedDrawComponents.contains(dc);
	}	
	
	protected DrawComponent createSelectionDrawComponent(DrawComponent dc)
	{
//		return ToolUtil.createFeedbackDrawComponent(dc, Color.YELLOW);
		return ToolUtil.createFeedbackDrawComponent(dc, Color.BLACK, 5);				
	}
	
	protected void clearTempContent() 
	{
		getViewer().getBufferedCanvas().getTempContentManager().clear();
	}
	
	protected void addToTempContent(DrawComponent dc)
	{
		getViewer().getBufferedCanvas().getTempContentManager().addToTempContent(dc);
	}
	
	protected void removeTempContent(DrawComponent dc)
	{
		getViewer().getBufferedCanvas().getTempContentManager().removeFromTempContent(dc);
	}
}
