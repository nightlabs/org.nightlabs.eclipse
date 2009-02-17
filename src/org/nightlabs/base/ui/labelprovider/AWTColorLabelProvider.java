/* *****************************************************************************
 * org.nightlabs.base.ui - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.ui.labelprovider;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.util.ImageUtil;

/**
 * @author unascribed (probably Daniel Mazurek)
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class AWTColorLabelProvider
//implements ILabelProvider
extends LabelProvider
{
//	private LinkedList<Image> images = new LinkedList<Image>();
	private Map<Color, Image> color2image = new HashMap<Color, Image>();

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof Color) {
			Color color = (Color)element;
			Image image = color2image.get(color);
			if (image == null) {
				image = ImageUtil.createColorImage(color);
				color2image.put(color, image);
			}
//			images.add(image);
			return image;
		}

		return null;
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof Color)
		{
			Color color = (Color) element;
			return (
					"("+color.getRed()+","+ //$NON-NLS-1$ //$NON-NLS-2$
					color.getGreen()+","+ //$NON-NLS-1$
					color.getBlue()+")" //$NON-NLS-1$
			);
		}
		return element == null ? "" : element.toString(); //$NON-NLS-1$
	}

	@Override
	public void dispose()
	{
		for (Image image : color2image.values()) {
			image.dispose();
		}

		color2image.clear();

		super.dispose();
	}
}
