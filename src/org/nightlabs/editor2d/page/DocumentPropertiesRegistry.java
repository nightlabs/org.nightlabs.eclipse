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
package org.nightlabs.editor2d.page;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.print.page.PredefinedPageEP;
import org.nightlabs.config.Config;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.config.DocumentConfigModule;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.print.page.IPredefinedPage;
import org.osgi.framework.Bundle;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class DocumentPropertiesRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.editor2d.documentProperties"; //$NON-NLS-1$
	
	public static final String ELEMENT_DOCUMENT_PROPERTIES = "documentProperties"; //$NON-NLS-1$
	public static final String ATTRIBUTE_EDITOR_CLASS = "editorClass"; //$NON-NLS-1$
	public static final String ATTRIBUTE_EDITOR_ID = "editorID";	 //$NON-NLS-1$
	public static final String ATTRIBUTE_PREDEFINED_PAGE = "predefinedPageID"; //$NON-NLS-1$
	public static final String ATTRIBUTE_RESOLUTION_UNIT = "resolutionUnit"; //$NON-NLS-1$
	public static final String ATTRIBUTE_RESOLUTION = "resolution"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ORIENTATION = "orientation"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ORIENTATION_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ORIENTATION_VERTICAL = "vertical"; //$NON-NLS-1$

	private static DocumentPropertiesRegistry sharedInstance;
	public static DocumentPropertiesRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (DocumentPropertiesRegistry.class) {
				if (sharedInstance == null)
					sharedInstance = new DocumentPropertiesRegistry();
			}
		}
		return sharedInstance;
	}	
	
	protected DocumentPropertiesRegistry() {

	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_DOCUMENT_PROPERTIES)) 
		{
			String editorClassName = element.getAttribute(ATTRIBUTE_EDITOR_CLASS);
			Class editorClass = null;
			if (checkString(editorClassName)) {
				try {
					Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
					if (bundle != null)
						editorClass = bundle.loadClass(editorClassName);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}				
			}
			
			String pageID = element.getAttribute(ATTRIBUTE_PREDEFINED_PAGE);
			IPredefinedPage page = null;
			if (checkString(pageID)) {
				page = PredefinedPageEP.sharedInstance().getPageRegistry().getPage(pageID);
			}
			
			String resolutionUnitString = element.getAttribute(ATTRIBUTE_RESOLUTION_UNIT);
			IResolutionUnit resolutionUnit = null;
			if (checkString(resolutionUnitString)) {
				try {
					resolutionUnit = (IResolutionUnit) element.createExecutableExtension(ATTRIBUTE_RESOLUTION_UNIT);
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
			}
			
			String resolutionString = element.getAttribute(ATTRIBUTE_RESOLUTION);
			double resolution = 72;
			if (checkString(resolutionString)) {
				try {
					resolution = Double.parseDouble(resolutionString);
				} catch (NumberFormatException e) {
					
				}
			}
			
			String orientationString = element.getAttribute(ATTRIBUTE_ORIENTATION);
			int orientation = PageDrawComponent.ORIENTATION_VERTICAL;
			if (checkString(orientationString)) {
				if (orientationString.equalsIgnoreCase(ATTRIBUTE_ORIENTATION_HORIZONTAL))
					orientation = PageDrawComponent.ORIENTATION_HORIZONTAL;
				else if (orientationString.equalsIgnoreCase(ATTRIBUTE_ORIENTATION_VERTICAL))
					orientation = PageDrawComponent.ORIENTATION_VERTICAL;
			}
			
			String editorID = element.getAttribute(ATTRIBUTE_EDITOR_ID);
			if (checkString(editorID)) {
				getDocumentConfigModule().getEditorClass2EditorID().put(editorClass, editorID);
			}
			
			DocumentProperties documentProperties = new DocumentProperties(page, orientation,
					resolutionUnit, resolution);
			
			getDocumentConfigModule().getEditorClass2DocumentProperties().put(editorClass, documentProperties);
		}
	}

	private DocumentConfigModule documentConfigModule = null;
	protected DocumentConfigModule getDocumentConfigModule() {
		if (documentConfigModule == null)
			documentConfigModule = (DocumentConfigModule) Config.sharedInstance().createConfigModule(DocumentConfigModule.class);
		return documentConfigModule;
	}
		
	public DocumentConfigModule getDocumentConfModule() {
		checkProcessing();
		return getDocumentConfigModule();
	}
}
