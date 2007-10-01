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

package org.nightlabs.editor2d.util;

import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.NameProvider;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.resource.Messages;

public class EditorNameProvider
implements NameProvider
{
	public EditorNameProvider() {
		super();
	}

	public String getTypeName(Class c) 
	{
//		if (DrawComponent.class.isAssignableFrom(c)) {
//			return EditorPlugin.getResourceString("model.drawComponent.name");
//		}
//		else if (ShapeDrawComponent.class.isAssignableFrom(c)) {
//			return EditorPlugin.getResourceString("model.shapeDrawComponent.name");
//		}
		if (ImageDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.image"); //$NON-NLS-1$
		}
		else if (RectangleDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.rectangle"); //$NON-NLS-1$
		}
		else if (EllipseDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.ellipse"); //$NON-NLS-1$
		}		
		else if (LineDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.line"); //$NON-NLS-1$
		}
		else if (TextDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.text"); //$NON-NLS-1$
		}
		else if (PageDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.page"); //$NON-NLS-1$
		}		
		else if (GroupDrawComponent.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.group"); //$NON-NLS-1$
		}				
		else if (Layer.class.isAssignableFrom(c)) {
			return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.layer"); //$NON-NLS-1$
		}				
		
		return Messages.getString("org.nightlabs.editor2d.util.EditorNameProvider.object"); //$NON-NLS-1$
	}
}
