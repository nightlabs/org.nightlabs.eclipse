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
package org.nightlabs.editor2d.print;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.GeomUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class J2DPrintDialog 
extends CenteredDialog 
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(J2DPrintDialog.class);
	
	public J2DPrintDialog(Shell parentShell, DrawComponent dc, PageFormat pageFormat) 
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);				
		this.drawComponent = dc;
		this.pageFormat = pageFormat;
		init();
	}

	protected void configureShell(Shell newShell) 
	{
		super.configureShell(newShell);		
		newShell.setText(Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.title")); //$NON-NLS-1$
		newShell.setSize(350, 325);		
	}	
	
	private PageFormat pageFormat; 
	public PageFormat getPageFormat() {
		return pageFormat;
	}
	private DrawComponent drawComponent;
	private J2DCanvas canvas;
	private IPaintable paintable;
			
	private Button alignHorizontal;
	private Button alignVertical;

	private int align = HORIZONTAL_ALIGNMENT;
	public static final int HORIZONTAL_ALIGNMENT = PageFormat.LANDSCAPE;
	public static final int VERTICAL_ALIGNMENT = PageFormat.PORTRAIT;
	
	private SelectionListener alignVerticalListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			align = VERTICAL_ALIGNMENT;
			refresh();
		}	
	};		

	private SelectionListener alignHorizontalListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			align = HORIZONTAL_ALIGNMENT;
			refresh();
		}	
	};		
		
	private Spinner marginTopWidget;
	private Spinner marginBottomWidget;
	private Spinner marginLeftWidget;
	private Spinner marginRightWidget;
	
	private double marginTop;
	private double marginBottom;
	private double marginLeft;
	private double marginRight;
	
	private SelectionListener marginListener = new SelectionAdapter()
	{	
		public void widgetSelected(SelectionEvent e) 
		{
			Spinner t = (Spinner) e.getSource(); 
			int value = t.getSelection();
			if (t.equals(marginTopWidget))
				setMarginValue(SWT.TOP, value);
			if (t.equals(marginBottomWidget))
				setMarginValue(SWT.BOTTOM, value);
			if (t.equals(marginLeftWidget))
				setMarginValue(SWT.LEFT, value);
			if (t.equals(marginRightWidget))
				setMarginValue(SWT.RIGHT, value);	
			
			refresh();
		}	
	};	
					
	protected void setMarginValue(int position, double value) 
	{
		Paper paper = pageFormat.getPaper();		
		switch (position) 
		{		
			case(SWT.TOP):
				marginTop = value;
				break;
			case(SWT.LEFT):
				marginLeft = value;
				break;
			case(SWT.RIGHT):
				marginRight = value;
				break;
			case(SWT.BOTTOM):
				marginBottom = value;
				break;				
		}
		if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
			paper.setImageableArea(marginLeft, marginTop, (paper.getWidth() - (marginLeft + marginRight)), 
					(paper.getHeight() - (marginTop + marginBottom)) );					
		} 
		else if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			paper.setImageableArea(marginTop, marginRight, (paper.getWidth() - (marginBottom + marginTop)), 
					(paper.getHeight() - (marginLeft + marginRight)) );						
		}
		pageFormat.setPaper(paper);		
	}		
	
	protected Control createDialogArea(Composite parent) 
	{
		Composite comp = new XComposite(parent, SWT.NONE);		
		comp.setLayout(new GridLayout(2, false));
		
		Composite prefComp = new Composite(comp, SWT.NONE);		
		prefComp.setLayout(new GridLayout(1, true));
		
//		// Fit		
//		Group fitGroup = new Group(prefComp, SWT.NONE);
//		fitGroup.setText("Details");
//		fitGroup.setLayout(new GridLayout(1, true));				
//		GridData fitData = new GridData();
//		fitData.verticalAlignment = SWT.BEGINNING;
//		fitData.horizontalAlignment = SWT.FILL;
//		fitGroup.setLayoutData(fitData);
//		
//		fitPage = new Button(fitGroup, SWT.RADIO);
//		fitPage.setText("Fit Page");
//		fitPage.addSelectionListener(fitPageSelectionListener);
//
//		fitWidth = new Button(fitGroup, SWT.RADIO);
//		fitWidth.setText("Fit Width");
//		fitWidth.addSelectionListener(fitWidthSelectionListener);
//
//		fitHeight = new Button(fitGroup, SWT.RADIO);
//		fitHeight.setText("Fit Height");
//		fitHeight.addSelectionListener(fitHeightSelectionListener);

		// Alignement
		Group alignmentGroup = new Group(prefComp, SWT.NONE);
		alignmentGroup.setText(Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.group.alignment")); //$NON-NLS-1$
		alignmentGroup.setLayout(new GridLayout(1, true));
		GridData alignData = new GridData();
//		alignData.verticalAlignment = SWT.CENTER;		
		alignData.verticalAlignment = SWT.BEGINNING;		
		alignData.horizontalAlignment = SWT.FILL;
		alignmentGroup.setLayoutData(alignData);
		
		alignHorizontal = new Button(alignmentGroup, SWT.RADIO);
		alignHorizontal.setText(Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.button.horizontal")); //$NON-NLS-1$
		alignVertical = new Button(alignmentGroup, SWT.RADIO);
		alignVertical.setText(Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.button.vertical"));		 //$NON-NLS-1$
		
		if (pageFormat.getOrientation() == PageFormat.LANDSCAPE) {
			alignHorizontal.setSelection(true);
			align = HORIZONTAL_ALIGNMENT;
		}
		if (pageFormat.getOrientation() == PageFormat.PORTRAIT) {
			align = VERTICAL_ALIGNMENT;
			alignVertical.setSelection(true);			
		}
		
		alignHorizontal.addSelectionListener(alignHorizontalListener);		
		alignVertical.addSelectionListener(alignVerticalListener);		
		
		// Margins
		Group marginsGroup = new Group(prefComp, SWT.NONE);
		marginsGroup.setLayout(new GridLayout(1, true));
		marginsGroup.setText(Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.group.margins")); //$NON-NLS-1$
		marginsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createMarginsEntry(marginsGroup, SWT.TOP, 
				Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.label.top"), marginTop); //$NON-NLS-1$
		createMarginsEntry(marginsGroup, SWT.BOTTOM, 
				Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.label.bottom"), marginBottom); //$NON-NLS-1$
		createMarginsEntry(marginsGroup, SWT.LEFT, 
				Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.label.left"), marginLeft); //$NON-NLS-1$
		createMarginsEntry(marginsGroup, SWT.RIGHT, 
				Messages.getString("org.nightlabs.editor2d.print.J2DPrintDialog.label.right"), marginRight);		 //$NON-NLS-1$
				
		// Preview 
		Composite canvasComp = new XComposite(comp, SWT.BORDER);
		GridLayout canvasLayout = new GridLayout();
		canvasComp.setLayout(canvasLayout);
		canvasComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas = new J2DCanvas(canvasComp, SWT.NONE, paintable);
		canvas.setBackground(new Color(comp.getDisplay(), 255, 255, 255));
		canvas.addControlListener(canvasResizeListener);		
		canvas.addPaintListener(canvasPaintListener);	
		
		refresh();
		
		return comp;
	}	
		
	protected Composite createMarginsEntry(Composite parent, int orientation, String label, double value) 
	{
		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;		
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Spinner t = new Spinner(comp, SWT.BORDER);
//		Text t = new Text(comp, SWT.BORDER);		
		switch (orientation) {
			case(SWT.TOP):
				marginTopWidget = t;
			case(SWT.BOTTOM):
				marginBottomWidget = t;
			case(SWT.LEFT):
				marginLeftWidget = t;
			case(SWT.RIGHT):
				marginRightWidget = t;
		}		
		t.setSelection((int)Math.rint(value));
		t.addSelectionListener(marginListener);
		t.setMaximum(200);
		Label l = new Label(comp, SWT.NONE);
		l.setText(label);
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return comp;		
	}
	
	protected void setCanvasZoom(double scale) {
		canvas.getPaintableManager().setScale(scale);		
	}

	protected void updateCanvas() {
		canvas.repaint();	
	}
		
	private ControlListener canvasResizeListener = new ControlAdapter() {	
		public void controlResized(ControlEvent e) {
//			refresh();
		}	
	};
		
	private Rectangle pageRectangle;
	private Rectangle imageablePageRectangle;
					
	protected void initPage(PageFormat pf) 
	{
		pageRectangle = new Rectangle(0, 0, (int)pf.getWidth(), (int)pf.getHeight());
		imageablePageRectangle = new Rectangle(
				(int)pf.getImageableX(), (int)pf.getImageableY(), 
				(int)pf.getImageableWidth(), (int)pf.getImageableHeight());

		marginTop = Math.rint(pf.getImageableY());
		marginBottom = Math.rint((pf.getHeight() - (pf.getImageableY() + pf.getImageableHeight())));
		marginLeft = Math.rint(pf.getImageableX());
		marginRight = Math.rint((pf.getWidth() - (pf.getImageableX() + pf.getImageableWidth())));		
										
		if (marginTopWidget != null)
			marginTopWidget.setSelection((int)marginTop);
		if (marginBottomWidget != null)
			marginBottomWidget.setSelection((int)marginBottom);
		if (marginLeftWidget != null)
			marginLeftWidget.setSelection((int)marginLeft);
		if (marginRightWidget != null)
			marginRightWidget.setSelection((int)marginRight);			
	}		
	
	private Rectangle dcBounds;
	private Rectangle canvasBounds;
	
	private double scale = 1;
//	private double scaleX = 1;
//	private double scaleY = 1;
		
	private double canvasScaleFactor = 4;
	protected void setCanvasSize() 
	{
		GridData canvasData = new GridData();
		int newWidth = (int) Math.rint((double)pageRectangle.width / canvasScaleFactor);
		int newHeight = (int) Math.rint((double)pageRectangle.height / canvasScaleFactor);		
		canvasData.widthHint = newWidth;
		canvasData.heightHint = newHeight; 
		canvas.setLayoutData(canvasData);
		canvas.setSize(newWidth, newHeight);						
	}
				
	protected void refresh() 
	{		
		logger.debug("refresh"); //$NON-NLS-1$
		
		switch (align) {
			case(HORIZONTAL_ALIGNMENT):
				pageFormat.setOrientation(PageFormat.LANDSCAPE);
				break;
			case(VERTICAL_ALIGNMENT):
				pageFormat.setOrientation(PageFormat.PORTRAIT);
				break;				
		}
		initPage(pageFormat);	
		setCanvasSize();

		Rectangle bounds = getBounds();
		dcBounds = GeomUtil.translateToOrigin(bounds);				
		canvasBounds = org.nightlabs.base.ui.util.GeomUtil.toAWTRectangle(canvas.getClientArea());										
		Rectangle shrinkedCanvasBounds = new Rectangle(0, 0, 
				(int)(pageFormat.getImageableWidth() / canvasScaleFactor), 
				(int)(pageFormat.getImageableHeight() / canvasScaleFactor) );		
		Point2D scales = GeomUtil.calcScale(dcBounds, shrinkedCanvasBounds);		
		
		scale = Math.min(scales.getX(), scales.getY());
		translateCanvas();
		
		if (logger.isDebugEnabled()) {
			logger.debug("canvasBounds = " + canvasBounds);			 //$NON-NLS-1$
			logger.debug("dcBounds = " + dcBounds); //$NON-NLS-1$
			logger.debug("shrinkedCanvasBounds = " + shrinkedCanvasBounds); //$NON-NLS-1$
			logger.debug("imageablePageRectangle = " + imageablePageRectangle); //$NON-NLS-1$
			logger.debug("scale = "+scale); //$NON-NLS-1$
			logger.debug("translateX = "+translateX);		 //$NON-NLS-1$
			logger.debug("translateY = "+translateY); //$NON-NLS-1$
			logger.debug("");			 //$NON-NLS-1$
		}				
		
		setCanvasZoom(scale);		
		updateCanvas();
	}			
	
	protected Rectangle getBounds() 
	{
		logger.debug("dcBounds = " + drawComponent.getBounds()); //$NON-NLS-1$
		drawComponent.clearBounds();
		logger.debug("dcBounds after clean = " + drawComponent.getBounds());				 //$NON-NLS-1$
		return drawComponent.getBounds();
	}
	
	private double translateX = 0; 
	private double translateY = 0;
	protected void translateCanvas() 
	{
		AffineTransform initalAT = canvas.getPaintableManager().getInitialTransform();
		AffineTransform at = new AffineTransform(initalAT);
		translateX = (((double)pageFormat.getImageableX() / canvasScaleFactor) - (drawComponent.getX() * scale ));
		translateY = (((double)pageFormat.getImageableY() / canvasScaleFactor) - (drawComponent.getY() * scale ));
		at.setToTranslation(translateX, translateY);		
		canvas.getPaintableManager().setInitialTransform(at);		
	}
		
	private PaintListener canvasPaintListener = new PaintListener()
	{	
		public void paintControl(PaintEvent e) 
		{
			GC gc = e.gc; 
			Transform transform = new Transform(gc.getDevice());			
			Rectangle canvasBounds = org.nightlabs.base.ui.util.GeomUtil.toAWTRectangle(canvas.getClientArea());			
			Point2D scales = GeomUtil.calcScale(pageRectangle, canvasBounds);			
			double gcScale = Math.min(scales.getX(), scales.getY());											
			transform.scale((float)gcScale, (float)gcScale);
			
			gc.setTransform(transform);			
			gc.setLineWidth(3);
			gc.setLineStyle(SWT.LINE_DASH);
			gc.drawRectangle(imageablePageRectangle.x, imageablePageRectangle.y, 
					imageablePageRectangle.width, imageablePageRectangle.height);						
		}	
	};
			
	protected void init() 
	{
		paintable = new DrawComponentPaintable(drawComponent);		
		initPage(pageFormat);		
	}
	
}
