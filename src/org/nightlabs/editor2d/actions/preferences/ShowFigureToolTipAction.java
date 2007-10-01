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
package org.nightlabs.editor2d.actions.preferences;

import java.util.Iterator;

import org.eclipse.gef.EditPart;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.actions.EditorCommandConstants;
import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ShowFigureToolTipAction 
extends PreferencesAction 
{
	public static final String ID = ShowFigureToolTipAction.class.getName();
	
	/**
	 * @param editor
	 * @param style
	 */
	public ShowFigureToolTipAction(AbstractEditor editor, int style) {
		super(editor, style | AS_CHECK_BOX);
	}

	/**
	 * @param editor
	 */
	public ShowFigureToolTipAction(AbstractEditor editor) {
		super(editor, AS_CHECK_BOX);
	}

	protected void init() 
	{
		setId(ID);
		setText(Messages.getString("org.nightlabs.editor2d.actions.preferences.ShowFigureToolTipAction.text")); //$NON-NLS-1$
		setToolTipText(Messages.getString("org.nightlabs.editor2d.actions.preferences.ShowFigureToolTipAction.tooltip")); //$NON-NLS-1$
		setActionDefinitionId(EditorCommandConstants.SHOW_FIGURE_TOOLTIPS_ID);
	}
	
	public boolean isChecked() 
	{
		return prefConfMod.isShowToolTips();
	}

	public void run() 
	{
		prefConfMod.setShowToolTips(!isChecked());
		EditPart modelRootEditPart = getEditPart(getRootDrawComponent());
		updateToolTips(modelRootEditPart);
	}

	protected void updateToolTips(EditPart ep) 
	{
		if (ep instanceof AbstractDrawComponentEditPart) {
			AbstractDrawComponentEditPart dcEP = (AbstractDrawComponentEditPart) ep;
			dcEP.updateTooltip();
		}
		
		for (Iterator<EditPart> it = ep.getChildren().iterator(); it.hasNext(); ) {
			EditPart child = it.next();
			updateToolTips(child);
		}
	}
}
