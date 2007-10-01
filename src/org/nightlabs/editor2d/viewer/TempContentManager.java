/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework														 *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org										*
 *																																						 *
 * This library is free software; you can redistribute it and/or							 *
 * modify it under the terms of the GNU Lesser General Public									*
 * License as published by the Free Software Foundation; either								*
 * version 2.1 of the License, or (at your option) any later version.					*
 *																																						 *
 * This library is distributed in the hope that it will be useful,						 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of							*
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU					 *
 * Lesser General Public License for more details.														 *
 *																																						 *
 * You should have received a copy of the GNU Lesser General Public						*
 * License along with this library; if not, write to the											 *
 *		 Free Software Foundation, Inc.,																				 *
 *		 51 Franklin St, Fifth Floor,																						*
 *		 Boston, MA	02110-1301	USA																						 *
 *																																						 *
 * Or get it online :																													*
 *		 http://www.gnu.org/copyleft/lesser.html																 *
 *																																						 *
 *																																						 *
 ******************************************************************************/

package org.nightlabs.editor2d.viewer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TempContentManager 
implements ITempContentManager
{
	public TempContentManager() { }

	private Collection tempContent = new HashSet();
	private Collection readOnlyTempContent = null;

	/**
	 * @see ITempContentManager#getTempContent()
	 */	
	public Collection getTempContent() 
	{
		if (readOnlyTempContent == null) {
			synchronized (tempContent) {
				readOnlyTempContent = Collections.unmodifiableCollection(new HashSet(tempContent));
			}
		}
		return readOnlyTempContent;
	}

	/**
	 * @see ITempContentManager#removeFromTempContent(Object)
	 */	
	public void removeFromTempContent(Object _object) 
	{
		synchronized (tempContent) {
			if (tempContent.remove(_object))
				readOnlyTempContent = null;
		}
	}

	/**
	 * @see ITempContentManager#removeFromTempContent(Collection)
	 */	
	public void removeFromTempContent(Collection c) 
	{
		synchronized (tempContent) {
			if (tempContent.removeAll(c))
				readOnlyTempContent = null;
		}
	}
		
	public boolean contains(Object o) {
		synchronized (tempContent) {
			return tempContent.contains(o);
		}
	}

//	/**
//	 * @see ITempContentManager#addToTempContent(Object)
//	 */
//	public void addToTempContent(Object _object)
//	{
//		if (_object instanceof DrawComponent || _object instanceof Component) {
//			if (tempContent.contains(_object)) {
//				return;
//			}
//			tempContent.add(_object);
//			readOnlyTempContent = null;
//		}
//		else {
//			throw new IllegalArgumentException("_object is neither a instance of DrawComponent nor Component!");
//		}
//	}
	/**
	 * @see ITempContentManager#addToTempContent(Object)
	 */
	public void addToTempContent(Object _object)
	{
		synchronized (tempContent) {
			tempContent.add(_object);
			readOnlyTempContent = null;
		}
	}
	
	/**
	 * @see ITempContentManager#addToTempContent(Collection)
	 */	
	public void addToTempContent(Collection c) 
	{
		synchronized (tempContent) {
			tempContent.addAll(c);
			readOnlyTempContent = null;
		}
	}
	
	public boolean isEmpty() 
	{
		synchronized (tempContent) {
			return tempContent.isEmpty();
		}
	}

	public void clear() {
		synchronized (tempContent) {
			tempContent.clear();
			readOnlyTempContent = null;
		}
	}
}
