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
package org.nightlabs.editor2d.viewer.ui.descriptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.resource.Messages;

/**
 * the Implementation of the {@link IDrawComponentDescriptor} Interface
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class DrawComponentDescriptor
implements IDrawComponentDescriptor
{
	public DrawComponentDescriptor(DrawComponent dc)
	{
		super();
		setDrawComponent(dc);
	}
	
	protected DrawComponent dc = null;
	
	/**
	 * @see IDrawComponentDescriptor#getDrawComponent()
	 */
	public DrawComponent getDrawComponent() {
		return dc;
	}
	
	/**
	 * resets all entries for the new DrawComponent
	 * @see IDrawComponentDescriptor#setDrawComponent(DrawComponent)
	 */
	public void setDrawComponent(DrawComponent dc)
	{
		this.dc = dc;
		if (dc != null) {
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.id"), String.valueOf(getDrawComponent().getId()));			 //$NON-NLS-1$
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.name"), getDrawComponent().getName()); //$NON-NLS-1$
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.x"), String.valueOf(getDrawComponent().getX()));		 //$NON-NLS-1$
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.y"), String.valueOf(getDrawComponent().getY()));		 //$NON-NLS-1$
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.width"), String.valueOf(getDrawComponent().getWidth()));		 //$NON-NLS-1$
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.height"), String.valueOf(getDrawComponent().getHeight()));		 //$NON-NLS-1$
			addEntry(Messages.getString("org.nightlabs.editor2d.viewer.ui.descriptor.DrawComponentDescriptor.rotation"), String.valueOf((int)getDrawComponent().getRotation()));					 //$NON-NLS-1$
		}
	}
	
	protected List<String> names = new LinkedList<String>();
	public List<String> getNames() {
		return names;
	}
		
	protected Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * @see IDrawComponentDescriptor#getProperties()
	 */
	public Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * @see IDrawComponentDescriptor#addEntry(String, String)
	 */
	public void addEntry(String name, String value)
	{
		if (!names.contains(name))
			names.add(name);
		properties.put(name, value);
	}
	
	protected StringBuffer sb = new StringBuffer();
	
	/**
	 * @see IDrawComponentDescriptor#getEntriesAsString(boolean)
	 */
	public String getEntriesAsString(boolean lineWrap)
	{
		sb = new StringBuffer();
		if (!lineWrap)
		{
			int lastIndex = names.size()-1;
			for (int i=0; i<lastIndex; i++)
			{
				String name = names.get(i);
				String value = properties.get(name);
				sb.append(name);
				sb.append(" = "); //$NON-NLS-1$
				sb.append(value);
				if (i != lastIndex)
					sb.append(", "); //$NON-NLS-1$
			}
		}
		else {
			for (Iterator<String> it = names.iterator(); it.hasNext(); )
			{
				String name = it.next();
				String value = properties.get(name);
				sb.append(name);
				sb.append(" = "); //$NON-NLS-1$
				sb.append(value);
				sb.append("\n"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}
	
}
