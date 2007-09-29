/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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
package org.nightlabs.base.search;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.resource.SharedImages.ImageDimension;
import org.nightlabs.base.resource.SharedImages.ImageFormat;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class SearchCompositeImage 
extends CompositeImageDescriptor 
{

	public SearchCompositeImage(Image image) {
		this.image = image;
	}
	
	private Image image;
	
	@Override
	protected void drawCompositeImage(int width, int height) 
	{		
		Image searchImage = SharedImages.getSharedImage(NLBasePlugin.getDefault(), 
				SearchContributionItem.class, null, ImageDimension._16x16, ImageFormat.png);		
		drawImage(searchImage.getImageData(), 0, 0);
		if (image != null) {
			int imageWidth = image.getBounds().width;
			int imageHeight = image.getBounds().height;
			drawImage(image.getImageData(), 16-imageWidth, 16-imageHeight);
//		drawImage(image.getImageData(), 8, 8);
		}
	}

	@Override
	protected Point getSize() {
		return new Point(16, 16);
	}

}
