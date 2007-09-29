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

package org.nightlabs.base.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class FileEditorInputFactory implements IElementFactory {

	public static final String CLASSNAME_KEY = "org.nightlabs.base.io.fileeditorinput.classname"; //$NON-NLS-1$
	public static final String FILENAME_KEY = "org.nightlabs.base.io.fileeditorinput.fileName"; //$NON-NLS-1$
	
	public FileEditorInputFactory() {
		super();
	}

	public IAdaptable createElement(IMemento memento) {
		try {
			String className = memento.getString(CLASSNAME_KEY);
			Class clazz = Class.forName(className);
			Constructor constructor = clazz.getConstructor(new Class[] {File.class});
			String fileName = memento.getString(FILENAME_KEY);
			return (IAdaptable)constructor.newInstance(new Object[] {new File(fileName)});
			
		} catch (Throwable t) {			
			t.printStackTrace();
			
			// fallback to FileEditorInput
			String fileName = memento.getString(FILENAME_KEY);
//			if (fileName != null) {
			File file = new File(fileName);
			FileEditorInput input = new FileEditorInput(file);
			return input;
			
		}
	}

	public static void storeFileEditorInput(IMemento memento, FileEditorInput input) {
		if (input.getFile() == null) // || !input.getFile().exists())
			return;

		try {
			memento.putString(CLASSNAME_KEY, input.getClass().getName());
			memento.putString(FILENAME_KEY, input.getFile().getCanonicalPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
