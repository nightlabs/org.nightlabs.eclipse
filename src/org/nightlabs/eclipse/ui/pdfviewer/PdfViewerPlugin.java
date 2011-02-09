/* ********************************************************************
 * NightLabs PDF Viewer - http://www.nightlabs.org/projects/pdfviewer *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.eclipse.ui.pdfviewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfSimpleNavigatorComposite;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 *
 * @version $Revision$ - $Date$
 * @author frederik löser - frederik at nightlabs dot de
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.nightlabs.eclipse.ui.pdfviewer"; //$NON-NLS-1$

	// The shared instance
	private static PdfViewerPlugin plugin;

	/**
	 * The constructor
	 */
	public PdfViewerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// See: https://sourceforge.net/projects/pdfviewer/forums/forum/866211/topic/4097829
		new java.awt.Frame(""); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static PdfViewerPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
	    super.initializeImageRegistry(reg);

	    reg.put(
	    		PdfSimpleNavigatorComposite.ImageKey.gotoFirstPageButton_enabled.name(),
	    		ImageDescriptor.createFromURL(
	    				plugin.getBundle().getEntry("icons/internal/PdfSimpleNavigatorComposite-gotoFirstPageButton-enabled.16x16.png") //$NON-NLS-1$
	    		)
	    );

	    reg.put(
	    		PdfSimpleNavigatorComposite.ImageKey.gotoPreviousPageButton_enabled.name(),
	    		ImageDescriptor.createFromURL(
	    				plugin.getBundle().getEntry("icons/internal/PdfSimpleNavigatorComposite-gotoPreviousPageButton-enabled.16x16.png") //$NON-NLS-1$
	    		)
	    );

	    reg.put(
	    		PdfSimpleNavigatorComposite.ImageKey.gotoNextPageButton_enabled.name(),
	    		ImageDescriptor.createFromURL(
	    				plugin.getBundle().getEntry("icons/internal/PdfSimpleNavigatorComposite-gotoNextPageButton-enabled.16x16.png") //$NON-NLS-1$
	    		)
	    );

	    reg.put(
	    		PdfSimpleNavigatorComposite.ImageKey.gotoLastPageButton_enabled.name(),
	    		ImageDescriptor.createFromURL(
	    				plugin.getBundle().getEntry("icons/internal/PdfSimpleNavigatorComposite-gotoLastPageButton-enabled.16x16.png") //$NON-NLS-1$
	    		)
	    );
	}
}
