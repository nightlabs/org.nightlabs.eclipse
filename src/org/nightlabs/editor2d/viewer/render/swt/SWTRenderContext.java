/**
 * 
 */
package org.nightlabs.editor2d.viewer.render.swt;

import org.eclipse.swt.graphics.GC;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.RenderContext;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface SWTRenderContext 
extends RenderContext<GC> 
{
	public static final String RENDER_CONTEXT_TYPE = "SWT"; //$NON-NLS-1$
	
	void paint(DrawComponent dc, GC gc);	
}
