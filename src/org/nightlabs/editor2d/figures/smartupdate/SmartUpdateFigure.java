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

package org.nightlabs.editor2d.figures.smartupdate;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.nightlabs.editor2d.figures.RendererFigure;

/**
 * 
 * @author Alexander Bieber <alex[at]nightlabs[DOT]de>
 *
 */
public class SmartUpdateFigure 
extends Figure
implements ISmartUpdateFigure
{
	/**
	 * Inner tile class. 
	 */
	private static class Tile implements FigureTile {
		
		private Rectangle bounds;
		private String key;
		private List<IFigure> figures = new ArrayList<IFigure>();
		
		public Tile(Rectangle bounds) {
			this.bounds = bounds;
		}
		
		public String getKey() {
			if (key == null)
				key = getTileKey(bounds.x, bounds.y);
			return key;
		}		

		/**
		 * @see org.nightlabs.editor2d.figures.smartupdate.FigureTile#intersects(org.eclipse.draw2d.geometry.Rectangle)
		 */
		public boolean intersects(Rectangle rect) {
			return bounds.intersects(rect);
		}

		/**
		 * @see org.nightlabs.editor2d.figures.smartupdate.FigureTile#getBounds()
		 */
		public Rectangle getBounds() {
			return bounds;
		}
		
		/**
		 * @see org.nightlabs.editor2d.figures.smartupdate.FigureTile#getTileFigures()
		 */
		public Collection getTileFigures() {
			return figures;
		}

		/**
		 * @see org.nightlabs.editor2d.figures.smartupdate.FigureTile#addFigure(org.eclipse.draw2d.Figure)
		 */
		public void addFigure(IFigure figure) {
			if (!figures.contains(figure))
				figures.add(figure);
		}

		/**
		 * @see org.nightlabs.editor2d.figures.smartupdate.FigureTile#removeFigure(org.eclipse.draw2d.Figure)
		 */
		public void removeFigure(IFigure figure) {
			figures.remove(figure);
		}

		/**
		 * @see org.nightlabs.editor2d.figures.smartupdate.FigureTile#getIntersectionFigures(org.eclipse.draw2d.geometry.Rectangle)
		 */
		public Collection getIntersectingFigures(Rectangle rect) {
			List result = new ArrayList();
			for (Iterator iter = figures.iterator(); iter.hasNext();) {
				Figure figure = (Figure) iter.next();
				if (figure.intersects(rect)) {
					result.add(figure);
				}
			}
			return result;
		}
		
	}
	
	/**
	 * key: String: (x,y) upper left tile corner <br/>
	 * value: FigureTile: The FigureTile for this tile 
	 */
	private Map tiles = new HashMap();
	
	/**
	 * key Figure: childfigure <br/>
	 * value: Set of FigureTiles
	 */
	private Map figuresInTiles = new HashMap();

	/**
	 * The tile dimension calculated in {@link #rebuildTiles()} 
	 */
	private Dimension tileDimension;
	
	/**
	 * EveryTime on rebuild this method is
	 * consulted for the new tile dimensions.
	 * This implementation returns 
	 * x = bounds.x/round(sqrt(childCount)) and
	 * y = bounds.y/round(sqrt(childCount)) 
	 * May be overridden.
	 *  
	 * @return Optimal? tile dimensions for this Figure
	 */
	protected Dimension calculateTileDimension() {
		Dimension dim = new Dimension();
		int fraction = (int)Math.round(Math.sqrt(getChildren().size()));
		if (fraction == 0) {
			dim.width = 0;
			dim.height = 0;
		}
		else {
			dim.width = getBounds().width / fraction;
			dim.height = getBounds().height / fraction;
		}
		return dim;
	}
	
	public static String getTileKey(int x, int y) {
		return "("+x+","+y+")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Retrieve the FigureTile at the given Position.
	 * Autocreate if not existent.
	 */
	protected FigureTile getTile(int x, int y) {
		String tileKey = getTileKey(x, y);
		FigureTile figureTile = (FigureTile)tiles.get(tileKey);
		if (figureTile == null) {
			figureTile = new Tile(new Rectangle(x, y, tileDimension.width, tileDimension.height));
			tiles.put(tileKey, figureTile);
		}
		return figureTile;
	}
	
	/**
	 * Get all tiles this figure is registered in.
	 * 
	 * @param figure The figure
	 * @return The figures tiles.
	 */
	protected Set getTilesForFigure(IFigure figure) {
		Set figureTiles = (Set)figuresInTiles.get(figure);
		if (figureTiles == null) {
			figureTiles = new HashSet();
			figuresInTiles.put(figure, figureTiles);
		}
		return figureTiles;
	}
	
	/**
	 * Get the top left Position of the
	 * tile this point is in.
	 * 
	 * @param x The points x
	 * @param y The points y
	 */
	protected Point getTileTopLeft(int x, int y) {
		return new Point(
				(x/tileDimension.width) * tileDimension.width,
				(y/tileDimension.height) * tileDimension.height
			);
	}
	
	/**
	 * Integrates the given figure in the current tile build. 
	 * Makes sure that the registrations in the tile maps are
	 * correct after execution.
	 * 
	 * @param figure The figure to integrate
	 */	
	protected void integrateFigure(IFigure figure) {
		Rectangle figureBounds = figure.getBounds();
		Point topLefTilePosition = getTileTopLeft(figureBounds.x, figureBounds.y);
		Point bottomRightTilePosition = getTileTopLeft(figureBounds.x + figureBounds.width, figureBounds.y+figureBounds.height);
		
		// clear registration in current build
		Set figureTiles = getTilesForFigure(figure);
		for (Iterator iter = figureTiles.iterator(); iter.hasNext();) {
			FigureTile figureTile = (FigureTile) iter.next();
			figureTile.removeFigure(figure);
		}
		figureTiles.clear();
		
		// reintegrate in build
		for (int row = topLefTilePosition.y; row <= bottomRightTilePosition.y; row += tileDimension.height) {
			for (int col = topLefTilePosition.x; col <= bottomRightTilePosition.x; col += tileDimension.width) {
				FigureTile figureTile = getTile(col, row);
				figureTile.addFigure(figure);
				figureTiles.add(figureTile);
			}
		}
	}
	
	
	private boolean tilesBuild = false;
	/**
	 * Clears and rebuilds the tiles
	 *
	 */
	protected void rebuildTiles() {
		tiles.clear();
		figuresInTiles.clear();
		tileDimension = calculateTileDimension();
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			integrateFigure(figure);
		}
	}
	
	/**
	 * @see org.nightlabs.editor2d.figures.smartupdate.ISmartUpdateFigure#paintRegion(Graphics, Rectangle)
	 */
	public void paintRegion(Graphics graphics, Rectangle region) {
		internalPaintRegion(graphics, region);
	}

	/**
	 * @see org.nightlabs.editor2d.figures.smartupdate.ISmartUpdateFigure#paintRegion(java.awt.Graphics2D, org.eclipse.draw2d.geometry.Rectangle)
	 */
	public void paintRegion(Graphics2D graphics, Rectangle region) {
		internalPaintRegion(graphics, region);
	}

	private void internalPaintRegion(Object graphics, Rectangle region) {
		if (!tilesBuild)
			rebuildTiles();
		tilesBuild = true;
		internalPaintSelf(graphics, region);		
		if (getChildren().size() == 0)
			return;
		Rectangle regionToPaint = getBounds().getIntersection(region);	
		if (regionToPaint.width == 0 && regionToPaint.height == 0)
			return;
		Point topLefTilePosition = getTileTopLeft(regionToPaint.x, regionToPaint.y);
		Point bottomRightTilePosition = getTileTopLeft(regionToPaint.x + regionToPaint.width, regionToPaint.y+regionToPaint.height);
		
		for (int row = topLefTilePosition.y; row <= bottomRightTilePosition.y; row += tileDimension.height) {
			for (int col = topLefTilePosition.x; col <= bottomRightTilePosition.x; col += tileDimension.width) {
				FigureTile figureTile = getTile(col, row);
				for (Iterator iter = figureTile.getIntersectingFigures(regionToPaint).iterator(); iter.hasNext();) {
					Figure figure = (Figure) iter.next();
					internalPaintChild(figure, graphics, regionToPaint);
				}
			}
		}		
	}
	
	private void internalPaintSelf(Object graphics, Rectangle region) {
		if (graphics instanceof Graphics) {
			Graphics d2dGraphics = (Graphics)graphics;
			this.paint(d2dGraphics);
		}
		else if (graphics instanceof Graphics2D) {
			Graphics2D j2dGraphics = (Graphics2D)graphics;
			if (this instanceof ISmartUpdateFigure)
				((ISmartUpdateFigure)this).paintRegion(j2dGraphics, region);
			else if (this instanceof RendererFigure)
				((RendererFigure)this).paint(j2dGraphics);
		}
	}
	
	private void internalPaintChild(Figure child, Object graphics, Rectangle regionToPaint) {
		if (graphics instanceof Graphics) {
			Graphics d2dGraphics = (Graphics)graphics;
			if (child instanceof ISmartUpdateFigure)
				((ISmartUpdateFigure)child).paintRegion(d2dGraphics, regionToPaint);
			else 
				child.paint(d2dGraphics);
		}
		else if (graphics instanceof Graphics2D) {
			Graphics2D j2dGraphics = (Graphics2D)graphics;
			if (child instanceof ISmartUpdateFigure)
				((ISmartUpdateFigure)child).paintRegion(j2dGraphics, regionToPaint);
			else if (child instanceof RendererFigure)
				((RendererFigure)child).paint(j2dGraphics);
		}
	}


	/**
	 * @see org.nightlabs.editor2d.figures.smartupdate.ISmartUpdateFigure#refresh()
	 */
	public void refresh() {
		tilesBuild = false;
	}

	/**
	 * @see ISmartUpdateFigure#refresh(IFigure)
	 */
	public void refresh(IFigure figure) {
		if (!tilesBuild)
			rebuildTiles();
		tilesBuild = true;
		integrateFigure(figure);
	}
	
}
