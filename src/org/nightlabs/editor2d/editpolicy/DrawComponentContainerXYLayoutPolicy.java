/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.editpolicy;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.graphics.Color;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.command.ChangeGuideCommand;
import org.nightlabs.editor2d.command.CreateDrawComponentCommand;
import org.nightlabs.editor2d.command.CreateImageCommand;
import org.nightlabs.editor2d.command.CreateShapeCommand;
import org.nightlabs.editor2d.command.CreateTextCommand;
import org.nightlabs.editor2d.command.RotateCenterCommand;
import org.nightlabs.editor2d.command.RotateCommand;
import org.nightlabs.editor2d.command.SetConstraintCommand;
import org.nightlabs.editor2d.command.ShearCommand;
import org.nightlabs.editor2d.command.shape.EditShapeCommand;
import org.nightlabs.editor2d.edit.ShapeDrawComponentEditPart;
import org.nightlabs.editor2d.figures.AbstractShapeFigure;
import org.nightlabs.editor2d.figures.FeedbackShapeFigure;
import org.nightlabs.editor2d.figures.ShapeFigure;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.request.EditorBoundsRequest;
import org.nightlabs.editor2d.request.EditorCreateShapeRequest;
import org.nightlabs.editor2d.request.EditorEditShapeRequest;
import org.nightlabs.editor2d.request.EditorLocationRequest;
import org.nightlabs.editor2d.request.EditorRequestConstants;
import org.nightlabs.editor2d.request.EditorRotateCenterRequest;
import org.nightlabs.editor2d.request.EditorRotateRequest;
import org.nightlabs.editor2d.request.EditorShearRequest;
import org.nightlabs.editor2d.request.ImageCreateRequest;
import org.nightlabs.editor2d.request.LineCreateRequest;
import org.nightlabs.editor2d.request.TextCreateRequest;
import org.nightlabs.editor2d.resource.Messages;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.util.J2DUtil;
import org.nightlabs.editor2d.util.feedback.FeedbackUtil;

public class DrawComponentContainerXYLayoutPolicy 
extends XYLayoutEditPolicy 
implements EditorRequestConstants
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(DrawComponentContainerXYLayoutPolicy.class);
  
  public DrawComponentContainerXYLayoutPolicy(XYLayout layout) {
  	this(layout, true, true, true);
  }  

  public DrawComponentContainerXYLayoutPolicy(XYLayout layout, boolean rotation, 
  		boolean scale) 
  {
  	this(layout, rotation, scale, rotation);
  }
  
  public DrawComponentContainerXYLayoutPolicy(XYLayout layout, boolean rotation, 
  		boolean scale, boolean rotationCenter) 
  {
    super();
    setXyLayout(layout);
    this.rotation = rotation;
    this.scale = scale;
    this.rotationCenter = rotationCenter;
  }
  
  private boolean rotation = true;
  private boolean scale = true;
  private boolean rotationCenter = true;
  
  /**
   * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
   */
  protected Command createAddCommand(EditPart child, Object constraint) 
  {
    return null;
  }
  
  protected Command createChangeConstraintCommand(ChangeBoundsRequest request, 
      EditPart child, Object constraint) 
  {
		SetConstraintCommand cmd = new SetConstraintCommand();
		DrawComponent part = (DrawComponent)child.getModel();
		cmd.setPart(part);
		cmd.setBounds(J2DUtil.toAWTRectangle((Rectangle)constraint));
		Command result = cmd;
		
		if ((request.getResizeDirection() & PositionConstants.NORTH_SOUTH) != 0) 
		{
			Integer guidePos = (Integer)request.getExtendedData().get(SnapToGuides.KEY_HORIZONTAL_GUIDE);
			if (guidePos != null) {
			  result = chainGuideAttachmentCommand(request, part, result, true);
			} 
			else if (part.getHorizontalGuide() != null) 
			{
				// SnapToGuides didn't provide a horizontal guide, but this part is attached
				// to a horizontal guide.  Now we check to see if the part is attached to
				// the guide along the edge being resized.  If that is the case, we need to
				// detach the part from the guide; otherwise, we leave it alone.
				int alignment = part.getHorizontalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.NORTH) != 0)
				  edgeBeingResized = -1;
				else
				  edgeBeingResized = 1;
				if (alignment == edgeBeingResized)
				  result = result.chain(new ChangeGuideCommand(part, true));
			}
		}
		
		if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) 
		{
		  Integer guidePos = (Integer)request.getExtendedData().get(SnapToGuides.KEY_VERTICAL_GUIDE);
		  if (guidePos != null) {
		    	result = chainGuideAttachmentCommand(request, part, result, false);
		  } 
		  else if (part.getVerticalGuide() != null) 
		  {
				int alignment = part.getVerticalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.WEST) != 0)
				edgeBeingResized = -1;
				else
				edgeBeingResized = 1;
				if (alignment == edgeBeingResized)
				  result = result.chain(new ChangeGuideCommand(part, false));
			}
		}
		
		if (request.getType().equals(REQ_MOVE_CHILDREN)
		|| request.getType().equals(REQ_ALIGN_CHILDREN)) 
		{
			result = chainGuideAttachmentCommand(request, part, result, true);
			result = chainGuideAttachmentCommand(request, part, result, false);
			result = chainGuideDetachmentCommand(request, part, result, true);
			result = chainGuideDetachmentCommand(request, part, result, false);
		}
		
		return result;
  }  
  
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {	  
		return null;
	}	
			
  protected EditPolicy createChildEditPolicy(EditPart child) 
  {  	
//  	return new DrawComponentResizeEditPolicy();
  	return new DrawComponentResizeEditPolicy(rotation, scale);  	
  } 
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
   */
  protected Command getDeleteDependantCommand(Request request) {
    return null;
  }

  protected Command getOrphanChildrenCommand(Request request) {
  	return null;
  }
      
  protected Command getAddCommand(Request generic) 
  {    
//    LOGGER.debug("getAddCommand()");
    
  	ChangeBoundsRequest request = (ChangeBoundsRequest)generic;
  	List editParts = request.getEditParts();
  	CompoundCommand command = new CompoundCommand();
  	command.setDebugLabel("Add in ConstrainedLayoutEditPolicy");//$NON-NLS-1$
  	GraphicalEditPart childPart;
  	Rectangle r;
  	Object constraint;

  	for (int i = 0; i < editParts.size(); i++) 
  	{
  		childPart = (GraphicalEditPart)editParts.get(i);
  		r = childPart.getFigure().getBounds().getCopy();
  		int oldWidth = r.width;
  		int oldHeight = r.height;
  		//convert r to absolute from childpart figure
  		childPart.getFigure().translateToAbsolute(r);
  		r = request.getTransformedRectangle(r);
  		//convert this figure to relative 
  		getLayoutContainer().translateToRelative(r);
  		
  		// WORKAROUND: reason = size changes when moving, 
  		// solution = check old size before transforming, if size changed set old size
  		if ((r.width != oldWidth || r.height != oldHeight) &&
  		    request.getSizeDelta().equals(0,0))
  		  r.setSize(oldWidth, oldHeight);
  		  
  		getLayoutContainer().translateFromParent(r);  		
  		r.translate(getLayoutOrigin().getNegated());
  		constraint = getConstraintFor(r);  		
  		command.add(createAddCommand(generic, childPart,
  			translateToModelConstraint(constraint)));
  	}
  	return command.unwrap();
  }
  
	protected Command chainGuideAttachmentCommand(Request request, DrawComponent part, Command cmd, boolean horizontal) 
	{
		Command result = cmd;
		
		// Attach to guide, if one is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
				                : SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos != null) {
			int alignment = ((Integer)request.getExtendedData()
					.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_ANCHOR
					                : SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
			ChangeGuideCommand cgm = new ChangeGuideCommand(part, horizontal);
			cgm.setNewGuide(findGuideAt(guidePos.intValue(), horizontal), alignment);
			result = result.chain(cgm);
		}

		return result;
	}	
	
	protected Command chainGuideDetachmentCommand(Request request, DrawComponent part,
			Command cmd, boolean horizontal) {
		Command result = cmd;
		
		// Detach from guide, if none is given
		Integer guidePos = (Integer)request.getExtendedData()
				.get(horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
				                : SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos == null)
			result = result.chain(new ChangeGuideCommand(part, horizontal));

		return result;
	}	
  
	protected EditorGuide findGuideAt(int pos, boolean horizontal) 
	{
		RulerProvider provider = ((RulerProvider)getHost().getViewer().getProperty(
				horizontal ? RulerProvider.PROPERTY_VERTICAL_RULER 
				: RulerProvider.PROPERTY_HORIZONTAL_RULER));
		return (EditorGuide)provider.getGuideAt(pos);
	}		
	
	/**
	 * Generates a draw2d constraint for the given <code>EditorCreateShapeRequest</code>. If the
	 * EditorCreateShapeRequest has a size, {@link #getConstraintFor(Rectangle)} is called with a
	 * Rectangle of that size and the result is returned. This is used during size-on-drop
	 * creation. Otherwise, {@link #getConstraintFor(Point)} is returned.
	 * <P>
	 * The EditorCreateShapeRequest location is relative the Viewer. The location is made
	 * layout-relative before calling one of the methods mentioned above.
	 * @param request the EditorCreateShapeRequest
	 * @return a draw2d constraint
	 */
	protected Object getConstraintFor(CreateRequest request) 
	{
		IFigure figure = getLayoutContainer();
		Point where = request.getLocation().getCopy();
		Dimension size = request.getSize();
				
		figure.translateToRelative(where);
		figure.translateFromParent(where);
		where.translate(getLayoutOrigin().getNegated());			
		
		if (size == null || size.isEmpty())
			return getConstraintFor(where);
		else 
		{
			//$TODO Probably should use PrecisionRectangle at some point instead of two 
			// geometrical objects
			size = size.getCopy();
			figure.translateToRelative(size);
			figure.translateFromParent(size);
			
			return getConstraintFor(new Rectangle(where, size));			
		}		
	}
	
	private static final Dimension DEFAULT_SIZE = new Dimension(-1, -1);
	
  public Rectangle getConstraintRectangleFor(Point point) 
  {
    Point p = point.getCopy();
		IFigure figure = getLayoutContainer();
		figure.translateToRelative(p);
		figure.translateFromParent(p);
		p.translate(getLayoutOrigin().getNegated());				
		return new Rectangle(p, DEFAULT_SIZE);
  }
  
  public Point getConstraintPointFor(Point point) 
  {
    Point p = point.getCopy();
		IFigure figure = getLayoutContainer();
		figure.translateToRelative(p);
		figure.translateFromParent(p);
		p.translate(getLayoutOrigin().getNegated());				
		return p;
  }  
  
  public Rectangle getConstraintRectangleFor(Rectangle rectangle) 
  {
    Rectangle r = rectangle.getCopy();
		IFigure figure = getLayoutContainer();
		figure.translateToRelative(r);
		figure.translateFromParent(r);
    r.translate(getLayoutOrigin().getNegated());
    return r;
  }	
	
  public Command getCommand(Request request) 
  {  	
    if (request instanceof TextCreateRequest)
      return getCreateTextCommand((TextCreateRequest)request);
    
    if (request instanceof ImageCreateRequest)
      return getCreateImageCommand((ImageCreateRequest)request);
    
    if (rotation) 
    {
      if (request instanceof EditorRotateRequest)
        return getRotateCommand((EditorRotateRequest)request);
    }

    if (rotationCenter)
    {      
      if (request instanceof EditorRotateCenterRequest)
        return getRotateCenterCommand((EditorRotateCenterRequest)request);          	
    }
    
  	if (REQ_RESIZE_CHILDREN.equals(request.getType())) {
  		if (scale)
    		return getResizeChildrenCommand((ChangeBoundsRequest)request);
  		else
  			return null;
  	}

  	if (REQ_EDIT_SHAPE.equals(request.getType()))
  		return getEditShapeCommand((EditorEditShapeRequest)request);

//	  if (request instanceof EditorShearRequest)
//	  	return getShearCommand((EditorShearRequest) request);
  	
  	return super.getCommand(request);
  }  
  
  protected Command getShearCommand(EditorShearRequest request) 
  {
    ShearCommand cmd = new ShearCommand();
    cmd.setEditParts(request.getEditParts());
    cmd.setAffineTransform(request.getAffineTransform());
    return cmd;
  }
  
  protected Command getRotateCenterCommand(EditorRotateCenterRequest request) 
  {
    RotateCenterCommand cmd = new RotateCenterCommand(request);
    Point rotationCenter = request.getRotationCenter().getCopy();
    rotationCenter = EditorUtil.toAbsolute(getHost(), rotationCenter.x, rotationCenter.y);
    cmd.setRotationCenter(rotationCenter);    
    logger.debug("cmd.rotationCenter = "+rotationCenter); //$NON-NLS-1$
    return cmd;
  }
  
  protected Command getRotateCommand(EditorRotateRequest request) 
  {
    RotateCommand cmd = new RotateCommand(request);
    double rotation = request.getRotation();
    cmd.setRotation(rotation);
//    LOGGER.debug("getRotateCommand().rotation = "+rotation);
    return cmd;
  }
  
  public Command getCreateTextCommand(TextCreateRequest request) 
  {
    // TODO: Optimize Command (dont create each time a new Command)
    CreateTextCommand create = new CreateTextCommand(request);                 
    create.setParent(getDrawComponentContainer());    
      
    Rectangle constraint = new Rectangle();
    constraint = (Rectangle)getConstraintFor((EditorBoundsRequest)request);    
    create.setBounds(constraint);
    
    return create;
  }
  
  public Command getCreateImageCommand(ImageCreateRequest request) 
  {
    CreateImageCommand create = new CreateImageCommand();
    create.setFileName(request.getFileName());    
    DrawComponent newPart = (DrawComponent)request.getNewObject();
    create.setChild(newPart);     
    create.setParent(getDrawComponentContainer());    
    Rectangle constraint = (Rectangle)getConstraintFor(request);
    create.setBounds(constraint);  
    return create;
  }
  
  /**
   * Returns the command contribution for the given edit shape request. 
   * By default, the request is redispatched to the host's parent as a {@link
   * org.nightlabs.editor2d.request.EditorRequestConstants#REQ_EDIT_SHAPE}.  
   * The parent's editpolicies determine how to perform the resize based on the layout manager in use.
   * @param request the edit shape request
   * @return the command contribution obtained from the parent
   */
  protected Command getEditShapeCommand(EditorEditShapeRequest request) 
  {
    EditShapeCommand editShapeCommand = null;
    if (editShapeCommand == null) 
    {
    	editShapeCommand = new EditShapeCommand();
    	ShapeDrawComponentEditPart sdcEP = (ShapeDrawComponentEditPart) request.getTargetEditPart();
    	ShapeDrawComponent sdc = sdcEP.getShapeDrawComponent();
    	editShapeCommand.setShapeDrawComponent(sdc);
    	editShapeCommand.setPathSegmentIndex(request.getPathSegmentIndex());
    	editShapeCommand.setLabel(Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentContainerXYLayoutPolicy.command.label.editShape"));       //$NON-NLS-1$
    }
  	Point modelPoint = getConstraintPointFor(request.getLocation());
  	editShapeCommand.setLocation(modelPoint); 
		return editShapeCommand;		
  } 
      		
  protected Color outlineColor = ColorConstants.black;
  protected Color getOutlineColor() {
  	return outlineColor;
  }
  
	protected IFigure createSizeOnDropFeedback(EditorCreateShapeRequest editorRequest) 
	{
    Rectangle constrainedBounds = (Rectangle)getConstraintFor((EditorBoundsRequest)editorRequest);	    
    
    if (editorRequest.isUseShape()) 
    {
      Shape shape = editorRequest.getShape();
      if (shape != null) 
      {
        shape.setBounds(constrainedBounds); 
        shape.setForegroundColor(getOutlineColor());
        addFeedback(shape);
        return shape;
      }	            
    }
    else 
    {
      GeneralShape gp = editorRequest.getGeneralShape();
	    if (gp != null) 
	    {
	      if (editorRequest.getMode() == EditorCreateShapeRequest.BOUNDS_FIX_MODE) 
	      {
			    ShapeFigure shapeFigure = new AbstractShapeFigure();			    			    
			    shapeFigure.setGeneralShape(gp);			    
			    shapeFigure.setFill(false);
			    shapeFigure.setForegroundColor(getOutlineColor());
			    addFeedback(shapeFigure);		   		    		    
			    return shapeFigure;	        
	      }
	      else 
	      {
			    FeedbackShapeFigure shapeFigure = new FeedbackShapeFigure();			    			    
			    shapeFigure.setGeneralShape(gp);			    
			    shapeFigure.setFill(false);
			    shapeFigure.setForegroundColor(getOutlineColor());			    
			    addFeedback(shapeFigure);		   		    		    
			    return shapeFigure;	      		      	        
	      }
	    }
    }
    return null;
  } 
	  	
	/**
	 * Generates a draw2d constraint for the given <code>EditorBoundsRequest</code>. If the
	 * EditorBoundsRequest has a size, {@link #getConstraintFor(Rectangle)} is called with a
	 * Rectangle of that size and the result is returned. This is used during size-on-drop
	 * creation. Otherwise, {@link #getConstraintFor(Point)} is returned.
	 * <P>
	 * The EditorBoundsRequest location is relative the Viewer. The location is made
	 * layout-relative before calling one of the methods mentioned above.
	 * @param request the EditorCreateShapeRequest
	 * @return a draw2d constraint
	 */
	protected Object getConstraintFor(EditorBoundsRequest request) 
	{	  
		IFigure figure = getLayoutContainer();
		Point where = request.getLocation().getCopy();
		Dimension size = request.getSize();
				
		figure.translateToRelative(where);
		figure.translateFromParent(where);
		where.translate(getLayoutOrigin().getNegated());		

		if (size == null || size.isEmpty())
			return getConstraintFor(where);
		else {
			//TODO Probably should use PrecisionRectangle at some point instead of two 
			// geometrical objects
			size = size.getCopy();
			figure.translateToRelative(size);
			figure.translateFromParent(size);									
			return getConstraintFor(new Rectangle(where, size));
		}		
	}
	
  /**
   * Generates a draw2d constraint (the Point relative to the zomm)
   * for the given <code>EditorLocationRequest</code>. 
   * {@link #getConstraintPointFor(Point)} is returned.
   * 
   * The EditorLocationRequest location is relative the Viewer. The location is made
   * layout-relative before calling one of the methods mentioned above.
   * @param request the EditorCreateShapeRequest
   * @return a draw2d constraint
   */
  protected Point getConstraintFor(EditorLocationRequest request) 
  {   
    return getConstraintPointFor(request.getLocation());    
  }	
  	
  public DrawComponentContainer getDrawComponentContainer() 
  {
  	// TODO: why current layer and not just the container
    DrawComponentContainer container = (DrawComponentContainer)getHost().getModel();
    return container.getRoot().getCurrentLayer();    
  }

  public RootDrawComponent getModelRoot() 
  {
  	if (getHost().getModel() instanceof DrawComponent) {
  		DrawComponent dc = (DrawComponent) getHost().getModel();
  		return dc.getRoot();
  	}
  	return null;
  }  
  
	protected Command createAddCommand(Request request, EditPart childEditPart, 
			Object constraint) 
	{	  
	  DrawComponent part = (DrawComponent)childEditPart.getModel();
		Rectangle rect = (Rectangle)constraint;

		// TODO: maybe uncomment for Container related things
//		AddDrawComponentCommand add = new AddDrawComponentCommand();
//		add.setParent((RootDrawComponent)getHost().getModel());
//		add.setChild(part);
//		add.setLocation(rect);
//		add.setLabel(EditorPlugin.getResourceString("command_add_command"));
//		add.setDebugLabel("MLDC_XYEP add drawComponent");//$NON-NLS-1$

		SetConstraintCommand setConstraint = new SetConstraintCommand();
		setConstraint.setBounds(J2DUtil.toAWTRectangle(rect));
		setConstraint.setPart(part);
		setConstraint.setLabel(Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentContainerXYLayoutPolicy.command.label.addObject")); //$NON-NLS-1$
		
//		Command cmd = add.chain(setConstraint);
		Command cmd = setConstraint;
		cmd = chainGuideAttachmentCommand(request, part, cmd, true);
		cmd = chainGuideAttachmentCommand(request, part, cmd, false);
		cmd = chainGuideDetachmentCommand(request, part, cmd, true);
		return chainGuideDetachmentCommand(request, part, cmd, false);
	}
	
	protected Command getCreateCommand(CreateRequest request) 
	{ 
	  if (request instanceof EditorCreateShapeRequest) 
	  {	      
	    EditorCreateShapeRequest req = (EditorCreateShapeRequest) request;	    
	    return getEditorCreateCommand(req);
	  } 
	  else {
	    CreateDrawComponentCommand create = new CreateDrawComponentCommand();
			DrawComponent newPart = (DrawComponent)request.getNewObject();
			create.setChild(newPart);			
			create.setParent(getDrawComponentContainer());
			
			Rectangle constraint = (Rectangle)getConstraintFor(request);
			create.setBounds(constraint);			
			
			Command cmd = chainGuideAttachmentCommand(request, newPart, create, true);
			return chainGuideAttachmentCommand(request, newPart, cmd, false);			
	  }		
	}
	
	protected Command getEditorCreateCommand(EditorCreateShapeRequest request) 
	{
	  // TODO: Optimize Command (dont create each time a new Command)
    CreateShapeCommand create = new CreateShapeCommand();	    
    create.setGeneralShape(request.getGeneralShape());
    ShapeDrawComponent newPart = (ShapeDrawComponent)request.getNewObject();
    create.setChild(newPart);				     
		create.setParent(getDrawComponentContainer());    
			
		Rectangle constraint = new Rectangle();
		if (request instanceof LineCreateRequest) {
		  LineCreateRequest lineRequest = (LineCreateRequest) request;
		  if (lineRequest.getCreationBounds() != null)
		    constraint = getConstraintRectangleFor(lineRequest.getCreationBounds());
		}
		else 
		  constraint = (Rectangle)getConstraintFor((EditorBoundsRequest)request);
		
		create.setBounds(constraint);
    
		Command cmd = chainGuideAttachmentCommand(request, newPart, create, true);
		return chainGuideAttachmentCommand(request, newPart, cmd, false);				      			    	  
	}
		
	/**
	 * Places the feedback Polyline where the User indicated.
	 * @see LayoutEditPolicy#showSizeOnDropFeedback(CreateRequest)
	 */
	protected void showSizeOnDropFeedback(EditorCreateShapeRequest request) 
	{		  	  	  
		Point p = request.getLocation().getCopy();
		Dimension size = request.getSize().getCopy();
		IFigure feedback = getSizeOnDropFeedback(request);		
		p.translate(getScrollOffset());
										
		Rectangle newBounds = new Rectangle(p, size).expand(getCreationFeedbackOffset(request));								
    feedback.setBounds(newBounds);    
	}
	
  protected Point getScrollOffset() {
    return EditorUtil.getScrollOffset(getHost());
  }	
        
	/**
	 * Lazily creates and returns the Figure to use for size-on-drop feedback.
	 * @return the size-on-drop feedback figure
	 */
	protected IFigure createDefaultSizeOnDropFeedback() 
	{
		IFigure	sizeOnDropFeedbackDefault = new RectangleFigure();
		sizeOnDropFeedbackDefault.setBackgroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getBackgroundColor());
		sizeOnDropFeedbackDefault.setForegroundColor(FeedbackUtil.DEFAULT_PAINT_DESCRIPTOR.getForegroundColor());
		if (sizeOnDropFeedbackDefault instanceof Shape) 
		{
			Shape s = (Shape) sizeOnDropFeedbackDefault;
			s.setLineStyle(Graphics.LINE_SOLID);
			s.setFillXOR(true);
			s.setOutlineXOR(true);  			
		}
		addFeedback(sizeOnDropFeedbackDefault);
		return sizeOnDropFeedbackDefault;
	}    
  
	/**
	 * Override to provide custom feedback figure for the given create request.
	 * @param createRequest the Editor create request
	 * @return custom feedback figure
	 */
	protected IFigure createSizeOnDropFeedback(CreateRequest createRequest) 
	{	  
	  logger.debug("createSizeOnDropFeedback!"); //$NON-NLS-1$
	  
	  if (createRequest instanceof EditorCreateShapeRequest) 
	  {
	    EditorCreateShapeRequest editorRequest = (EditorCreateShapeRequest) createRequest;
	    return createSizeOnDropFeedback(editorRequest);
	  }
	  else
	  	return createDefaultSizeOnDropFeedback();	  	  
	}	  
			
//************************* BEGIN Text Feedback ******************************
	protected void eraseSizeOnDropFeedback(Request request) 
	{
		logger.debug("eraseSizeOnDropFeedback!"); //$NON-NLS-1$
		
		super.eraseSizeOnDropFeedback(request);								
		if (showFeedbackText) {
			eraseFeedbackText();
		}		
	}	
	
	protected void showSizeOnDropFeedback(CreateRequest request) 
	{
	  if (request instanceof EditorCreateShapeRequest)
	    showSizeOnDropFeedback((EditorCreateShapeRequest)request);
	  else
	    super.showSizeOnDropFeedback(request);
	  
	  if (showFeedbackText) {
	  	showFeedbackText(request);
	  }
	}	
		
	protected boolean showFeedbackText = true;	
  protected Label feedbackText;
  
  protected Label getFeedbackTextFigure()
  {
  	if (feedbackText == null)
  		feedbackText = createFeedbackTextFigure(""); //$NON-NLS-1$
  	return feedbackText;
  }
  
  protected Rectangle getInitialFeedbackBounds() 
  {
  	return Rectangle.SINGLETON;
  }
  
  protected Label createFeedbackTextFigure(String text) 
  {       	
    Label l = new Label(text);
    l.setForegroundColor(getOutlineColor());
  	l.setBounds(getInitialFeedbackBounds());
  	addFeedback(l);
  	return l;
  }  
  
  protected void showFeedbackText(CreateRequest request) 
  {
  	Label feedbackText = getFeedbackTextFigure();  	
  	feedbackText.setText(getText(request));
  	feedbackText.setLocation(getFeedbackTextLocation(request));
  	feedbackText.setSize(100, 20);
  	  	
  	getFeedbackLayer().repaint();  	
  }
  
  protected Point getFeedbackTextLocation(CreateRequest request) 
  {
  	// TODO: set location always on mouse location 
  	Point loc = request.getLocation();
  	Dimension size = request.getSize();
//  	Rectangle feedbackBounds = getSizeOnDropFeedback().getBounds();
  	Point location = new Point(loc.x + size.width, loc.y + size.height);
  	location.translate(EditorUtil.getScrollOffset(getHost()));
  	return location;  	
  }
    
  protected String getText(CreateRequest request) 
  {
  	Point relativeSize = new Point(request.getSize().width, request.getSize().height);
  	Point absoluteSize = EditorUtil.toAbsolute(getHost(), relativeSize.x, relativeSize.y);
  	  	
  	String width = Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentContainerXYLayoutPolicy.label.shortWidth"); //$NON-NLS-1$
  	String height = Messages.getString("org.nightlabs.editor2d.editpolicy.DrawComponentContainerXYLayoutPolicy.label.shortHeight"); //$NON-NLS-1$
  	StringBuffer sb = new StringBuffer();
  	sb.append(width+" "); //$NON-NLS-1$
  	sb.append(absoluteSize.x);
  	sb.append(", "); //$NON-NLS-1$
  	sb.append(height+" "); //$NON-NLS-1$
  	sb.append(absoluteSize.y);
  	return sb.toString();
  }
  
  protected void eraseFeedbackText() 
  {
    if (feedbackText != null)
      removeFeedback(feedbackText);
    
    feedbackText = null;
  }  
    
//************************* END Text Feedback ******************************  
}
