package org.nightlabs.editor2d.util;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;

public class OrderUtil 
{
	/**
	 * 
	 * @param container the DrawComponentContainer to get the last index from
	 * @return the last index of the the given DrawComponentContainer
	 * @see DrawComponentContainer
	 */
	public static int getLastIndex(DrawComponentContainer container) 
	{
		return container.getDrawComponents().size() - 1;
	}
	
	/**
	 * 
	 * @param dc the DrawComponent to determine the index of
	 * @return the index of the DrawComponent in the drawComponents-List of its parent 
	 */
	public static int indexOf(DrawComponent dc) 
	{
		return dc.getParent().getDrawComponents().indexOf(dc);
	}
}
