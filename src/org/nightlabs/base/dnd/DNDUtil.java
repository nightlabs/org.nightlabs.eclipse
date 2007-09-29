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

package org.nightlabs.base.dnd;

public class DNDUtil
{

	public DNDUtil() {
		super();
	}
	
	public static final String DROP_NONE = "DROP_NONE"; //$NON-NLS-1$
	public static final String DROP_COPY = "DROP_COPY"; //$NON-NLS-1$
	public static final String DROP_MOVE = "DROP_MOVE"; //$NON-NLS-1$
	public static final String DROP_LINK = "DROP_LINK"; //$NON-NLS-1$
	public static final String DROP_TARGET_MOVE = "DROP_TARGET_MOVE"; //$NON-NLS-1$
	public static final String DROP_DEFAULT = "DROP_DEFAULT"; //$NON-NLS-1$
	
	public static String getStringForCode(int value) 
	{
		StringBuffer sb = new StringBuffer();
		
		if (value == 0)
			sb.append(DROP_NONE + " "); //$NON-NLS-1$
		else if (value == (1 << 0))
			sb.append(DROP_COPY + " "); //$NON-NLS-1$
		else if (value == (1 << 1))
			sb.append(DROP_MOVE + " "); //$NON-NLS-1$
		else if (value == (1 << 2))
			sb.append(DROP_LINK + " "); //$NON-NLS-1$
		else if (value == (1 << 3))
			sb.append(DROP_TARGET_MOVE + " "); //$NON-NLS-1$
		else if (value == (1 << 4))
			sb.append(DROP_DEFAULT + " "); //$NON-NLS-1$

		return sb.toString();
	}

}
