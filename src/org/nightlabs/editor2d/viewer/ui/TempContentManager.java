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

package org.nightlabs.editor2d.viewer.ui;

import java.util.Collection;
import java.util.HashSet;

public class TempContentManager
implements ITempContentManager
{
	public TempContentManager() { }

	private Collection<Object> tempContent = new HashSet<Object>();
	private Collection<Object> readOnlyTempContent = null;

	/**
	 * @see ITempContentManager#getTempContent()
	 */
	public Collection<Object> getTempContent()
	{
		if (readOnlyTempContent == null) {
			synchronized (tempContent) {
//				readOnlyTempContent = Collections.unmodifiableCollection(new HashSet(tempContent));
				readOnlyTempContent = new HashSet<Object>(tempContent);
			}
		}
		return readOnlyTempContent;
	}

	/**
	 * @see ITempContentManager#removeFromTempContent(Object)
	 */
	public boolean removeFromTempContent(Object _object)
	{
		synchronized (tempContent) {
			boolean removed = tempContent.remove(_object);
			if (removed) {
				readOnlyTempContent = null;
			}
			return removed;
		}
	}

	/**
	 * @see ITempContentManager#removeFromTempContent(Collection)
	 */
	public boolean removeManyFromTempContent(Collection<?> c)
	{
		synchronized (tempContent) {
			boolean removed = tempContent.removeAll(c);
			if (removed) {
				readOnlyTempContent = null;
			}
			return removed;
		}
	}

	public boolean contains(Object o) {
		synchronized (tempContent) {
			return tempContent.contains(o);
		}
	}

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
	public void addToTempContent(Collection<Object> c)
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
