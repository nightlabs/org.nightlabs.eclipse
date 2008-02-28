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

package org.nightlabs.editor2d.viewer.ui.tool;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.ui.event.MouseEvent;


public class SelectTool
//extends AbstractTool
extends RectangleTool
{
	public static final String ID = SelectTool.class.getName();

	private SelectionInterceptor selectionInterceptor = null;

	public SelectTool()
	{
		super();
		setID(ID);
		setShowRectangle(false);
//		setShowTooltip(false);
		setShowRollOver(true);
	}
				
	@Override
	public void mouseReleased(MouseEvent me)
	{
		super.mouseReleased(me);
		int currentX = getRelativeX(currentPoint.x);
		int currentY = getRelativeY(currentPoint.y);
		checkDrawComponents(currentX, currentY);			
		leftPressed = false;
		rightPressed = false;
	}

	public SelectionInterceptor getSelectionInterceptor() {
		return selectionInterceptor;
	}
	public void setSelectionInterceptor(SelectionInterceptor selectionInterceptor) {
		this.selectionInterceptor = selectionInterceptor;
	}

	/**
	 * This method is called by {@link #checkDrawComponents(int, int)}.
	 * If there is no {@link SelectionInterceptor} assigned, this method
	 * always returns <code>true</code>, otherwise the result of
	 * {@link SelectionInterceptor#canSelect(DrawComponent)}.
	 *
	 * @param dc The <code>DrawComponent</code> to check.
	 * @return Returns the result of {@link SelectionInterceptor#canSelect(DrawComponent)}
	 *		or <code>true</code>, if none assigned.
	 */
	protected boolean selectionInterceptor_canSelect(DrawComponent dc)
	{
		if (selectionInterceptor == null)
			return true;

		return selectionInterceptor.canSelect(dc);
	}
	
	protected void checkDrawComponents(int x, int y)
	{
		DrawComponent dc = getDrawComponent(x, y);
		if (dc != null)
		{
			if (!selectionInterceptor_canSelect(dc))
				return;

			if (getViewer().getSelectionManager().getSelectedDrawComponents().contains(dc))
				getViewer().getSelectionManager().removeSelectedDrawComponent(dc);
			else
				getViewer().getSelectionManager().addSelectedDrawComponent(dc);
		}
		else {
			getViewer().getSelectionManager().clearSelection(true);
		}
	}
	
}
