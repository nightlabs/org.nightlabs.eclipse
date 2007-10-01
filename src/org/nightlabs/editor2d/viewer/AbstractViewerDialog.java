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

package org.nightlabs.editor2d.viewer;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.FullScreenDialog;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.viewer.resource.Messages;

public abstract class AbstractViewerDialog 
extends FullScreenDialog 
{
	protected DrawComponent dc;
	public AbstractViewerDialog(Shell arg0, DrawComponent dc) {
		super(arg0);
		if (dc == null)
			throw new IllegalArgumentException("Param dc must not be null!"); //$NON-NLS-1$
		this.dc = dc;
	}

	public void create() 
	{		
		super.create();		
		getShell().setText(ViewerPlugin.getResourceString(Messages.getString("org.nightlabs.editor2d.viewer.AbstractViewerDialog.title"))); //$NON-NLS-1$
	}
		
	protected AbstractViewerComposite viewerComp;
	public AbstractViewerComposite getViewerComposite() {
		return viewerComp;
	}
	
	protected Control createDialogArea(Composite parent) 
	{
		viewerComp = initViewerComposite(parent);
		GridData viewerData = new GridData(GridData.FILL_BOTH);
		viewerComp.setLayoutData(viewerData);
		
		return viewerComp;
	}

	protected void okPressed() {
		super.okPressed();
	}

	protected abstract AbstractViewerComposite initViewerComposite(Composite parent);

}
