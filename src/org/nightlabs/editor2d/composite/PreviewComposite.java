/**
 * <p> Project: org.nightlabs.crossticket.smd.ui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 10.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorActionBarContributor;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.viewer.ui.action.RenderModeContributionItem;
import org.nightlabs.i18n.unit.resolution.DPIResolutionUnit;

public class PreviewComposite 
extends Composite
{
	/**
	 * creates a Preview Composite for displaying drawComponents 
	 * 
	 * @param parent the Composite parent
	 * @param style the style
	 * @param dc the drawComponent to display
	 * 	 
	 */
	public PreviewComposite(Composite parent, int style, DrawComponent dc) {
		this(parent, style, dc, true, true, true, null);
	}

	/**
	 * creates a Preview Composite for displaying drawComponents 
	 * 
	 * @param parent the Composite parent
	 * @param style the style
	 * @param dc the drawComponent to display
	 * @param renderModeMan a optional RenderModeMan to fill the renderModeCombo,
	 * if renderModeMan is null, the renderModeMan of the drawComponent is taken 
	 */
	public PreviewComposite(Composite parent, int style, DrawComponent dc, 
			RenderModeManager renderModeMan) 
	{
		this(parent, style, dc, true, true, true, renderModeMan);
	}
		
	/**
	 * creates a Preview Composite for displaying drawComponents 
	 * 
	 * @param parent the Composite parent
	 * @param style the style
	 * @param dc the drawComponent to display
	 * @param zoomButtons determines if the composite shows zoomButtons
	 * @param resizeCanvas 
	 * @param renderModes determines if the composite shows a renderModeCombo
	 * @param renderModeMan a optional RenderModeMan to fill the renderModeCombo,
	 * if renderModeMan is null, the renderModeMan of the drawComponent is taken 
	 */
	public PreviewComposite(Composite parent, int style, DrawComponent dc, boolean zoomButtons,
			boolean resizeCanvas, boolean renderModes, RenderModeManager renderModeMan) 
	{
		super(parent, style);				
		this.drawComponent = dc;
		this.renderModeMan = renderModeMan != null ? renderModeMan : drawComponent.getRenderModeManager(); 
		this.resizeCanvas = resizeCanvas;		
		this.zoomButtons = zoomButtons;
		this.renderModes = renderModes;
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL_BOTH));
		createCanvas(this);		
		
		zoom = getResolutionFactor();
		applyZoom(zoom);
		updateZoomLabel();			
	}
			
	protected boolean renderModes;
	protected boolean zoomButtons = false;
	protected boolean resizeCanvas = true;
	protected DrawComponentPaintable paintable;
	protected Button zoomInButton;
	protected Button zoomOutButton;
	protected Label zoomLabel;
	
	protected double getResolutionFactor() 
	{
		double screenResolutionDPI = 72;
		double documentResolutionDPI = getDrawComponent().getRoot().getResolution().
			getResolutionX(new DPIResolutionUnit());
		double factor = screenResolutionDPI / documentResolutionDPI;
		return factor;
	}
	
	protected DrawComponent drawComponent;	
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	public void setDrawComponent(DrawComponent drawComponent) {
		this.drawComponent = drawComponent;
	}	
	
	protected RenderModeManager renderModeMan = null;
	public RenderModeManager getRenderModeMan() {
		return renderModeMan;
	}
	public void setRenderModeMan(RenderModeManager renderModeMan) {
		this.renderModeMan = renderModeMan;
	}		
	
	protected J2DCanvas canvas;
	public J2DCanvas getCanvas() {
		return canvas;
	}
		
	protected Color backgroundColor = new Color(null, 255, 255, 255);	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
		
	protected J2DCanvas createCanvas(Composite parent) 
	{
		paintable = new DrawComponentPaintable(getDrawComponent());
		if (zoomButtons) {
			Group group = new Group(parent, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_BOTH));
			group.setText(Messages.getString("org.nightlabs.editor2d.composite.PreviewComposite.group.preview.text")); //$NON-NLS-1$
			group.setLayout(new GridLayout(1, true));
			
			Composite buttonBar = new XComposite(group, SWT.NONE);
			buttonBar.setLayout(new GridLayout(5, false)); 
			buttonBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite beginComp = new XComposite(buttonBar, SWT.NONE);
			beginComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			zoomInButton = new Button(buttonBar, SWT.PUSH);
			zoomInButton.setImage(SharedImages.getSharedImage(EditorPlugin.getDefault(), 
					EditorActionBarContributor.class, "ZoomIn"));			 //$NON-NLS-1$
			zoomInButton.addSelectionListener(zoomListener);
			zoomLabel = new Label(buttonBar, SWT.NONE);
			updateZoomLabel();
			zoomOutButton = new Button(buttonBar, SWT.PUSH);
			zoomOutButton.setImage(SharedImages.getSharedImage(EditorPlugin.getDefault(), 
					EditorActionBarContributor.class, "ZoomOut"));			 //$NON-NLS-1$
			zoomOutButton.addSelectionListener(zoomListener);
			
			if (renderModes) {
				RenderModeContributionItem renderModeCI = new RenderModeContributionItem(getRenderModeMan());
				renderModeCI.fill(buttonBar);				
			}
			
			Composite endComp = new XComposite(buttonBar, SWT.NONE);
			endComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
										
			canvas = new J2DCanvas(group, SWT.BORDER, paintable);
			if (resizeCanvas)
				canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
			canvas.setBackground(getBackgroundColor());
			canvas.repaint();
			return canvas;			
		}
		else {
			canvas = new J2DCanvas(parent, SWT.BORDER, paintable);
			if (resizeCanvas)
				canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
			canvas.setBackground(getBackgroundColor());
			canvas.repaint();
			return canvas;						
		}
	}	
	
	protected double zoom = 1;
	private double initalZoomStep = 0.2d;
	protected double getZoomStep() {
		return initalZoomStep * getResolutionFactor();
	}
	
	protected SelectionListener zoomListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			if (e.getSource().equals(zoomInButton)) {
				zoom += getZoomStep();				
				applyZoom(zoom);
				updateZoomLabel();
				updateCanvas();
			}
			else if (e.getSource().equals(zoomOutButton)) {
				zoom -= getZoomStep();				
				applyZoom(zoom);
				updateZoomLabel();				
				updateCanvas();				
			}
		}	
	};
	
	protected void updateZoomLabel() {
		double z = Math.floor(zoom * 100) / getResolutionFactor();		
		zoomLabel.setText(z + "%"); //$NON-NLS-1$
	}	
	
	public void updateCanvas() 
	{
		if (canvas != null) {
			paintable = new DrawComponentPaintable(getDrawComponent());
			canvas.setPaintable(paintable);
			canvas.repaint();			
		}
	}
	
	public void applyZoom(double zoomFactor) {
		canvas.getPaintableManager().setScale(zoomFactor);
	}
}
