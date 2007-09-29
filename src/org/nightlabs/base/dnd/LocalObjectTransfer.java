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

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * This Implementation of org.eclipse.swt.dnd.Transfer, 
 * is only for locale Transfer of Objects between Java-Applications,
 * as it uses internal {@link LocalTransferManager} which only keeps a refernce of Java-Objects,
 * which are not serialized. 
 * 
 * @author Daniel.Mazurek [AT] NightLabs [DOT] com
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LocalObjectTransfer 
extends ByteArrayTransfer
{
	
	public static final String TYPE_NAME = "local-object-transfer"; //$NON-NLS-1$
	public static int TYPE_ID = registerType(TYPE_NAME);	

	public LocalObjectTransfer() {
		super();
	}

	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	protected void javaToNative(Object object, TransferData transferData) 
	{
		String objectKey = LocalTransferManager.sharedInstance().addObject(object);
		super.javaToNative(objectKey.getBytes(), transferData); // TODO shouldn't we better use objectKey.getBytes(Utils.CHARSET_NAME_UTF_8) ?!!!!
	}

	protected Object nativeToJava(TransferData transferData) 
	{
		byte[] superResult = (byte[])super.nativeToJava(transferData);
		String transferKey = new String(superResult); // TODO shouldn't we better use new String(superResult, Utils.CHARSET_NAME_UTF_8) ?!!!!
		return LocalTransferManager.sharedInstance().popObject(transferKey);
	}
	
	protected boolean validate(Object object) {
		return true;
	}
	
	private static LocalObjectTransfer sharedInstance;
	
	public static LocalObjectTransfer sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new LocalObjectTransfer();
		return sharedInstance;
	}
	
	public static Transfer[] TRANSFERS = new Transfer[] { sharedInstance() };
	
}
