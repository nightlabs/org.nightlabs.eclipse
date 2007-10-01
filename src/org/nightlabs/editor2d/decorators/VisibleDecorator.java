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
package org.nightlabs.editor2d.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart;
import org.nightlabs.editor2d.resource.Messages;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class VisibleDecorator 
extends LabelProvider 
implements ILightweightLabelDecorator 
{
	public void decorate(Object element, IDecoration decoration) 
	{
		if(element instanceof DrawComponentTreeEditPart) 
		{	
			DrawComponentTreeEditPart dctep = (DrawComponentTreeEditPart) element;
			DrawComponent dc = dctep.getDrawComponent();
			if (!dc.isVisible()) {
				ImageDescriptor invisibleImage = SharedImages.getSharedImageDescriptor(
						EditorPlugin.getDefault(), 
						VisibleCompositeImage.class, "", ImageDimension._8x8, ImageFormat.gif); //$NON-NLS-1$
				
				decoration.addOverlay(invisibleImage);
				decoration.addSuffix(" ["+Messages.getString("org.nightlabs.editor2d.decorators.VisibleDecorator.invisible")+"]");				 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (dc.isTemplate()) {
				decoration.addSuffix(" ["+Messages.getString("org.nightlabs.editor2d.decorators.VisibleDecorator.template")+"]");				 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}
}
