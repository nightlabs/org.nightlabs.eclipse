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
package org.nightlabs.editor2d.config;

import java.util.HashMap;
import java.util.Map;

import org.nightlabs.config.ConfigModule;
import org.nightlabs.config.InitException;
import org.nightlabs.editor2d.Editor;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.page.DocumentProperties;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.print.page.A4Page;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DocumentConfigModule 
extends ConfigModule 
{
	private Map<Class, DocumentProperties> editorClass2DocumentProperties = null;

	/**
	 * @return Returns the editorClass2DocumentProperties.
	 */
	public Map<Class, DocumentProperties> getEditorClass2DocumentProperties() {
		return editorClass2DocumentProperties;
	}

	/**
	 * @param editorClass2DocumentProperties The editorClass2DocumentProperties to set.
	 */
	public void setEditorClass2DocumentProperties(
			Map<Class, DocumentProperties> editorClass2DocumentProperties) {
		this.editorClass2DocumentProperties = editorClass2DocumentProperties;
		setChanged();
	}

	private Map<Class, String> editorClass2EditorID = null;
	
	/**
	 * @return Returns the editorClass2EditorID.
	 */
	public Map<Class, String> getEditorClass2EditorID() {
		return editorClass2EditorID;
	}

	/**
	 * @param editorClass2EditorID The editorClass2EditorID to set.
	 */
	public void setEditorClass2EditorID(Map<Class, String> editorClass2EditorID) {
		this.editorClass2EditorID = editorClass2EditorID;
	}

	@Override
	public void init() 
	throws InitException 
	{
		super.init();
		if (editorClass2DocumentProperties == null) {
			editorClass2DocumentProperties = new HashMap<Class, DocumentProperties>();
			DocumentProperties documentProperties = new DocumentProperties(new A4Page(), 
					PageDrawComponent.ORIENTATION_VERTICAL, IResolutionUnit.dpiUnit, 762);
			editorClass2DocumentProperties.put(Editor.class, documentProperties);
		}
		if (editorClass2EditorID == null) {
			editorClass2EditorID = new HashMap<Class, String>();
			editorClass2EditorID.put(Editor.class, "org.nightlabs.editor2d.Editor"); //$NON-NLS-1$
		}
	}

}
