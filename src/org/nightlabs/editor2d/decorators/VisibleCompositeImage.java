/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
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

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.EditorPlugin;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VisibleCompositeImage 
extends CompositeImageDescriptor 
{
	public VisibleCompositeImage(Image image) {
		this.image = image;
	}

	private Image image;
	
	@Override
	protected void drawCompositeImage(int width, int height) 
	{
		drawImage(image.getImageData(), 0, 0);
		Image invisibleImage = SharedImages.getSharedImage(EditorPlugin.getDefault(), 
				VisibleCompositeImage.class, "", ImageDimension._8x8, ImageFormat.gif); //$NON-NLS-1$
		drawImage(invisibleImage.getImageData(), 0, 8);
	}

	@Override
	protected Point getSize() {
		return new Point(16, 16);
	}
}
