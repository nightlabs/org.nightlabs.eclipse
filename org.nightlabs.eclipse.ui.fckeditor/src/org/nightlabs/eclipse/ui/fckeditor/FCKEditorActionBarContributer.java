/* ************************************************************************
 * org.nightlabs.eclipse.ui.fckeditor - Eclipse RCP FCKeditor Integration *
 * Copyright (C) 2008 NightLabs - http://NightLabs.org                    *
 *                                                                        *
 * This library is free software; you can redistribute it and/or          *
 * modify it under the terms of the GNU Lesser General Public             *
 * License as published by the Free Software Foundation; either           *
 * version 2.1 of the License, or (at your option) any later version.     *
 *                                                                        *
 * This library is distributed in the hope that it will be useful,        *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of         *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU      *
 * Lesser General Public License for more details.                        *
 *                                                                        *
 * You should have received a copy of the GNU Lesser General Public       *
 * License along with this library; if not, write to the                  *
 *     Free Software Foundation, Inc.,                                    *
 *     51 Franklin St, Fifth Floor,                                       *
 *     Boston, MA  02110-1301  USA                                        *
 *                                                                        *
 * Or get it online:                                                      *
 *     http://www.gnu.org/copyleft/lesser.html                            *
 **************************************************************************/
package org.nightlabs.eclipse.ui.fckeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @version $Revision$ - $Date$
 */
public class FCKEditorActionBarContributer extends EditorActionBarContributor
{
	private static class EditorAction extends Action
	{
		private IFCKEditor editor;
		public void setEditor(IFCKEditor editor)
		{
			this.editor = editor;
		}
		@Override
		public void run()
		{
			if(editor != null)
				editor.print();
		}
	}
	
	EditorAction print = new EditorAction() {
	};

	public void setActiveEditor(IEditorPart part) {
		IActionBars bars= getActionBars();
		if (bars == null)
			return;
		if(!(part instanceof IFCKEditor))
			return;
		print.setEditor((IFCKEditor)part);
		bars.setGlobalActionHandler(ActionFactory.PRINT.getId(), print);
		bars.updateActionBars();
	}
}
