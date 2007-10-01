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

package org.nightlabs.editor2d.properties;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart;
import org.nightlabs.editor2d.model.DrawComponentPropertySource;
import org.nightlabs.i18n.unit.IUnit;

public class EditorPropertyPage 
extends PropertySheetPage
{	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(EditorPropertyPage.class);
	
	public EditorPropertyPage(UnitManager unitManager) 
	{
		super();		
		this.unitManager = unitManager;		
	}
	
	private UnitManager unitManager = null;
	public UnitManager getUnitManager() {
		return unitManager;
	}
	public void setUnitManager(UnitManager unitManager) {
		this.unitManager = unitManager;
	}

	private UnitContributionItem unitContributionItem = null;
  public void makeContributions(IMenuManager menuManager,
      IToolBarManager toolBarManager, IStatusLineManager statusLineManager) 
  {
//  	LanguageContributionItem langContribution = new LanguageContributionItem();
//  	toolBarManager.add(langContribution);
  	
  	unitContributionItem = new UnitContributionItem(getUnitManager());
  	toolBarManager.add(unitContributionItem);
  	
  	super.makeContributions(menuManager, toolBarManager, statusLineManager);  	  	
  }
      		
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
		if (!listenerAdded) {
	  	unitContributionItem.getCombo().addSelectionListener(unitListener);
	  	listenerAdded = true;
		}			
		unitContributionItem.selectUnit(getUnitManager().getCurrentUnit());	  							
	  if (selection instanceof IStructuredSelection) {
	  	Object[] sel = ((IStructuredSelection) selection).toArray();
	  	this.selection = sel;
	  	for (int i=0; i<sel.length; i++) {
	  		setUnit(sel[i]);
	  	}
	  }
		super.selectionChanged(part, selection);	  
	}	
	
	private Object[] selection = null;
	private boolean listenerAdded = false;
	protected SelectionListener unitListener = new SelectionListener()
	{	
		public void widgetSelected(SelectionEvent e) 
		{
			logger.debug("unit changed!"); //$NON-NLS-1$
			if (selection != null) {
				for (int i = 0; i < selection.length; i++) {
					setUnit(selection[i]);					
				}
			}
			refresh();
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	};

	protected void setUnit(Object o) 
	{
		if (o != null) 
		{
			IPropertySource ps = null;
			if (o instanceof AbstractDrawComponentEditPart)
				ps = ((AbstractDrawComponentEditPart)o).getPropertySource();
			else if (o instanceof DrawComponentTreeEditPart)
				ps = ((DrawComponentTreeEditPart)o).getPropertySource();
			
			if (ps != null && ps instanceof DrawComponentPropertySource) 
			{			
				DrawComponentPropertySource dcps = (DrawComponentPropertySource) ps;
				IUnit currentUnit = getUnitManager().getCurrentUnit();
				dcps.setUnit(currentUnit);
			}			
		}
	}
							
//  protected Collection makeLanguageActions() 
//  {
//  	Collection languageActions = new ArrayList(langMan.getLanguages().size());
//  	for (Iterator it = langMan.getLanguages().iterator(); it.hasNext(); ) {
//  		LanguageCf language = (LanguageCf) it.next();
//  		LanguageAction action = new LanguageAction(langMan, language.getLanguageID());
//  		languageActions.add(action);  		
//  	}
//  	return languageActions;
//  }
  
//	protected LanguageChangeListener langListener = new LanguageChangeListener()
//	{	
//		public void languageChanged(LanguageChangeEvent event) 
//		{			
//			if (!getControl().isDisposed()) {
//				refresh();
//			}
//		}	
//	};
  
//	private PropertyChangeListener languageListener = new PropertyChangeListener()
//	{	
//		public void propertyChange(PropertyChangeEvent evt) 
//		{
//			if (!getControl().isDisposed()) {
//				refresh();
//			}			
//		}	
//	};
		
}
