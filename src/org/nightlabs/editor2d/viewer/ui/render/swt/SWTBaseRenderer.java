/**
 * 
 */
package org.nightlabs.editor2d.viewer.ui.render.swt;

import java.util.Iterator;

import org.eclipse.swt.graphics.GC;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.render.RenderContext;
import org.nightlabs.editor2d.render.Renderer;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class SWTBaseRenderer
implements SWTRenderContext
{
	public SWTBaseRenderer()
	{
		super();
		init();
	}
	
  /**
	 * The Standard Implementation of the paint-Method
	 * does by default nothing.
	 * It checks if the given DrawComponent is a DrawComponentContainer
	 * and if so, it paints all its children,
	 * but only if it is not an {@link IVisible} which visible state is set to false
   */
  public void paint(DrawComponent dc, GC gc)
  {
  	if (dc instanceof DrawComponentContainer) {
  		DrawComponentContainer container = (DrawComponentContainer) dc;
  		if (container != null) {
  			if (container.isVisible()) {
    			for (Iterator<DrawComponent> it = container.getDrawComponents().iterator(); it.hasNext(); ) {
    				DrawComponent d = it.next();
    				if (d.isVisible()) {
      				Renderer r = d.getRenderer();
      				RenderContext rc = r.getRenderContext(SWTRenderContext.RENDER_CONTEXT_TYPE);
      				if (rc != null && rc instanceof SWTRenderContext) {
      					SWTRenderContext swtrc = (SWTRenderContext) rc;
      					swtrc.paint(d, gc);
      				}
    				}
    			}
  			}
  		}
  	}
  }
  
  /**
   * Inheritans of this class can override this Method
   * in order to initialize things, needed for rendering.
   *
   * By Default this Method is empty.
   * This Method will be called in the Constructor
   */
  protected void init()
  {
  };
  
  protected String renderContextType = SWTRenderContext.RENDER_CONTEXT_TYPE;
  public String getRenderContextType() {
  	return renderContextType;
  }
  public void setRenderContextType(String newRenderContextType) {
  	this.renderContextType = newRenderContextType;
  }

}
