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

package org.nightlabs.editor2d.outline.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.nightlabs.editor2d.NameProvider;
import org.nightlabs.editor2d.RootDrawComponent;

/**
 * Used to set Filters in the Outline Page
 */
public class FilterManager 
{
  public static final String FILTER_CHANGED = "Filter changed"; //$NON-NLS-1$
  public static final String FILTER_ADDED = "Filter added"; //$NON-NLS-1$
  public static final String FILTERS_ADDED = "Filters added"; //$NON-NLS-1$
	    
  public String getTypeName(Class c) {
  	return nameProvider.getTypeName(c);
  }
  
  protected List<Class> filters;
  protected List<Class> allFilters;
  protected NameProvider nameProvider = null;  
  public FilterManager(NameProvider nameProvider) 
  {
    super();
    this.nameProvider = nameProvider;
  }
    
  public void addFilter(Class clazz) 
  {
  	if (clazz == null)
			throw new IllegalArgumentException("Param clazz must not be null!"); //$NON-NLS-1$
  	
    getFilters().add(clazz);
    getAllFilters().add(clazz);
    
    checkIgnore();    
    pcs.firePropertyChange(FILTER_ADDED, null, clazz);
  }
  
  public void addFilters(Collection classes) 
  {
  	if (classes == null)
			throw new IllegalArgumentException("Param classes must not be null!"); //$NON-NLS-1$
  	
    getFilters().addAll(classes);
    getAllFilters().addAll(classes); 
    
    checkIgnore();    
    pcs.firePropertyChange(FILTERS_ADDED, null, classes);    
  }
  
  public List<Class> getFilters() 
  {
    if (filters == null)
      filters = new ArrayList<Class>();
    
    return filters;
  }
  
  public List<Class> getAllFilters() 
  {
    if (allFilters == null)
      allFilters = new ArrayList<Class>();
    
    return allFilters;
  }
  
  public void setFilter(Class clazz) 
  {
  	if (clazz == null)
			throw new IllegalArgumentException("Param clazz must not be null!"); //$NON-NLS-1$
  	
    getFilters().clear();
    getFilters().add(clazz);
    pcs.firePropertyChange(FILTER_CHANGED, null, getFilters());
  }
  
  public void setFilter(Collection<Class> classes) 
  {
  	if (classes == null)
			throw new IllegalArgumentException("Param classes must not be null!"); //$NON-NLS-1$
  	
    getFilters().clear();
    getFilters().addAll(classes);
    pcs.firePropertyChange(FILTER_CHANGED, null, getFilters());    
  }
  
  public void setAllFiltersAvailable() {
    setFilter(getAllFilters());
  }
    
  public boolean isAllFilterSet() {
  	if (filters != null && allFilters != null) {
    	if (filters.size() == allFilters.size())
    		return true;
    	else
    		return false;
  	}
  	return true;
  }
  
  protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	}
	
	protected PropertyChangeListener newTypeListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			if (evt.getPropertyName().equals(RootDrawComponent.TYPE_ADDED)) {
				Class c = (Class) evt.getNewValue();
				addFilter(c);
			}
		}	
	};
	
	public PropertyChangeListener getTypeListener() {
		return newTypeListener;
	}
	
	protected List<Class> ignoreClasses = new ArrayList<Class>();
	public void ignoreClass(Class c) 
	{
		ignoreClasses.add(c);
		checkIgnore();
	}
	
	protected void checkIgnore() 
	{
		for (Iterator<Class> it = ignoreClasses.iterator(); it.hasNext(); ) {
			Class c = it.next();
			if (getAllFilters().contains(c)) {				
				getAllFilters().remove(c);
				if (getFilters().contains(c))
					getFilters().remove(c);
			}
		}		
	}
	
}
