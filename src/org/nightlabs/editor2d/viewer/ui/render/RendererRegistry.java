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

package org.nightlabs.editor2d.viewer.ui.render;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.util.ImageUtil;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.RenderModeDescriptor;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.Renderer;
import org.osgi.framework.Bundle;

public class RendererRegistry
extends AbstractEPProcessor
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.editor2d.viewer.ui.renderModeRegistry"; //$NON-NLS-1$
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(RendererRegistry.class.getName());
	
	private static RendererRegistry _sharedInstance;
	public static RendererRegistry sharedInstance()
	{
		if (_sharedInstance == null) {
			_sharedInstance = new RendererRegistry();
		}
		return _sharedInstance;
	}
	
	protected RendererRegistry() {
		super();
	}

	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	public static final String ELEMENT_REGISTRY = "registry"; //$NON-NLS-1$
	public static final String ELEMENT_RENDER_CONTEXT = "renderContext"; //$NON-NLS-1$
	
	public static final String ATTRIBUTE_MODE = "mode"; //$NON-NLS-1$
	public static final String ATTRIBUTE_RENDERER = "renderer"; //$NON-NLS-1$
	public static final String ATTRIBUTE_DRAWCOMPONENT_CLASS = "drawComponentClass"; //$NON-NLS-1$
	public static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String ATTRIBUTE_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	
	public static final String ATTRIBUTE_RENDER_CONTEXT_TYPE = "renderContextType"; //$NON-NLS-1$
	public static final String ATTRIBUTE_RENDER_CONTEXT = "renderContext"; //$NON-NLS-1$
		
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		if (renderModeManager == null)
			renderModeManager = new RenderModeManager();
		
		if (element.getName().equalsIgnoreCase(ELEMENT_REGISTRY))
		{
			try {
				String mode = element.getAttribute(ATTRIBUTE_MODE);
				String renderMode = RenderConstants.DEFAULT_MODE;
				if (checkString(mode))
					renderMode = mode;
				else
					throw new IllegalArgumentException("Attribute mode must not be null nor empty for " + //$NON-NLS-1$
							"DrawComponentClass "+element.getAttribute(ATTRIBUTE_DRAWCOMPONENT_CLASS) + " and " + //$NON-NLS-1$ //$NON-NLS-2$
							"Renderer "+element.getAttribute(ATTRIBUTE_RENDERER)); //$NON-NLS-1$

				String dcClassName = element.getAttribute(ATTRIBUTE_DRAWCOMPONENT_CLASS);
//				Class dcClass = null;
//				try {
//					dcClass = Platform.getBundle(extension.getNamespaceIdentifier()).loadClass(dcClassName);
//				} catch (ClassNotFoundException e) {
//					logger.error("Could not load class "+dcClass+" !"); //$NON-NLS-1$ //$NON-NLS-2$
//					return;
//				}
												
				String name = element.getAttribute(ATTRIBUTE_NAME);
				String description = element.getAttribute(ATTRIBUTE_DESCRIPTION);
				String icon = element.getAttribute(ATTRIBUTE_ICON);
				
				RenderModeDescriptor desc = null;
				if (checkString(name) && checkString(description) && checkString(icon))
				{
					Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
					ImageDescriptor imgDesc = ImageDescriptor.createFromURL(bundle.getEntry(icon));
					desc = new RenderModeDescriptor(renderMode, name, description,
							ImageUtil.convertToAWT(imgDesc.getImageData()));
				}
				else if (checkString(name) && checkString(description)) {
					desc = new RenderModeDescriptor(renderMode, name, description);
				}
				else if (checkString(name)) {
					desc = new RenderModeDescriptor(renderMode, name);
				}
				Renderer r = null;
				if (desc != null) {
//					r = renderModeManager.addRenderModeDescriptor(desc, dcClass);
					r = renderModeManager.addRenderModeDescriptor(desc, dcClassName);
				}
				if (r != null) {
					IConfigurationElement[] children = element.getChildren(ELEMENT_RENDER_CONTEXT);
					for (int i=0; i<children.length; i++) {
						processRenderContexts(children[i], r);
					}
				}
			} catch (Exception e) {
				throw new EPProcessorException(e);
			}
		}
	}
	
	protected void processRenderContexts(IConfigurationElement element, Renderer r)
	throws EPProcessorException
	{
		if (element.getName().equalsIgnoreCase(ELEMENT_RENDER_CONTEXT))
		{
			try {
				String renderContextType = element.getAttribute(ATTRIBUTE_RENDER_CONTEXT_TYPE);
				if (!checkString(renderContextType))
					throw new IllegalArgumentException("Attribute renderContextType must not be null nor empty"); //$NON-NLS-1$
								
				Object renderContext = element.createExecutableExtension(ATTRIBUTE_RENDER_CONTEXT);
				if (!(renderContext instanceof RenderContext))
					throw new IllegalArgumentException("Attribute renderContext must implement "+RenderContext.class.getName()+" "+renderContext.getClass().getName()+" does not!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				RenderContext rc = (RenderContext) renderContext;
				
				r.addRenderContext(rc);
			} catch (Exception e) {
				throw new EPProcessorException(e);
			}
		}
	}
	
	private RenderModeManager renderModeManager;
	public RenderModeManager getRenderModeManager()
	{
		checkProcessing();
//		renderModeManager.logRegisteredRenderer(RenderConstants.DEFAULT_MODE);
		
		return renderModeManager;
	}
	
}
