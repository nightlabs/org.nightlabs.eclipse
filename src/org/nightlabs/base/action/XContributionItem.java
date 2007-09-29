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

package org.nightlabs.base.action;

import java.lang.reflect.Field;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

public class XContributionItem
extends ContributionItem
implements IXContributionItem
{
	public XContributionItem()
	{
	}

	public XContributionItem(String id)
	{
		super(id);
	}

	public void setId(String id)
	{
		try {
			Field field = ContributionItem.class.getDeclaredField("id"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(this, id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean enabled;

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
  /**
   * Computes the width required by control
   * 
   * @param control The control to compute width for
   * @return int The width required
   */
  protected int computeWidth(Control control) 
  {
  	int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
  	return width;
  }  	

  /**
   * Computes the height required by control
   * 
   * @param control The control to compute height for
   * @return int The height required
   */  
  protected int computeHeight(Control control) 
  {
  	int height = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
  	return height;
  }   
}
