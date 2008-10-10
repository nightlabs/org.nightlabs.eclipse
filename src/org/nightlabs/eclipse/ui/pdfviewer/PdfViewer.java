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

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.internal.ContextElementRegistry;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfViewerComposite;

/**
 * The raw viewing area without any additional elements. Use this, if you want to
 * compose a custom viewer. You can add additional elements - if desired -
 * (e.g. a {@link PdfSimpleNavigator}) to your custom viewer wherever you want.
 *
 * @version $Revision$ - $Date$
 * @author marco schulze - marco at nightlabs dot de
 * @author frederik loeser - frederik at nightlabs dot de
 */
public class PdfViewer
{
	private PdfDocument pdfDocument;
	private PdfViewerComposite pdfViewerComposite;
	private ContextElementRegistry contextElementRegistry = new ContextElementRegistry();
	private int zoomFactorPerMill = 1000;

	private boolean updateCurrentPageOnScrolling = true;
	private AutoZoom autoZoom = AutoZoom.pageWidth;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

//	/**
//	 * When auto-zooming to page size/width/height, use this factor to determine, how much of the visible space
//	 * shall be used as reference size. The value 1.0 means, the page's width, for example, will completely
//	 * cover the panel's width.
//	 */
//	private double autoZoomFactor = 1.0d;

	/**
	 * When auto-zooming to page size/width/height, this margin (in real coordinates - i.e. DOT) defines how much the visible
	 * space is reduced on the left and the right side (i.e. twice the value specified here).
	 */
	private int autoZoomHorizontalMargin = 3;

	/**
	 * When auto-zooming to page size/width/height, this margin (in real coordinates - i.e. DOT) defines how much the visible
	 * space is reduced on the top and the bottom side (i.e. twice the value specified here).
	 */
	private int autoZoomVerticalMargin = 3;

	/**
	 * Constant used by the {@link PropertyChangeListener}s for modifications of the view-origin,
	 * i.e. the view area's left top point in the real coordinate system. This is triggered on scrolling
	 * or on zooming (if the zoom change causes a change of the view origin - which is more or less always
	 * the case, because zooming is done to the center of the view area).
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the new view origin (an instance of {@link Point2D})
	 * and {@link PropertyChangeEvent#getOldValue()} returns the view origin before the change happened
	 * (an instance of {@link Point2D}, too).
	 * </p>
	 *
	 * @see #getViewOrigin()
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_VIEW_ORIGIN = "viewOrigin"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s for modifications of the view-dimension,
	 * i.e. the view size in the real coordinate system. This is triggered on zooming or when the
	 * view area is resized.
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the new view dimension (an instance of {@link Dimension2D})
	 * and {@link PropertyChangeEvent#getOldValue()} returns the view dimension before the change happened
	 * (an instance of {@link Dimension2D}, too).
	 * </p>
	 *
	 * @see #getViewDimension()
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_VIEW_DIMENSION = "viewDimension"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s for modifications of the zoom factor.
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the new zoom factor (an instance of {@link Integer})
	 * and {@link PropertyChangeEvent#getOldValue()} returns the zoom factor before the change happened
	 * (an instance of {@link Integer}, too). The zoom factor returned is the value in &permil; [per mill] (e.g. a value of 1000 means
	 * 100% = 1.0).
	 * </p>
	 *
	 * @see #getZoomFactorPerMill()
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_ZOOM_FACTOR = "zoomFactor"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s when a {@link PdfDocument} has been assigned to this
	 * <code>PdfViewer</code>.
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the new {@link PdfDocument} (or <code>null</code>, since this is a valid
	 * value for {@link #setPdfDocument(PdfDocument)}); {@link PropertyChangeEvent#getOldValue()} returns the {@link PdfDocument}
	 * that was assigned before (or <code>null</code>, if there was none).
	 * </p>
	 *
	 * @see #getPdfDocument()
	 * @see #setPdfDocument(PdfDocument)
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_PDF_DOCUMENT = "pdfDocument"; //$NON-NLS-1$

	public static final String PROPERTY_CURRENT_PAGE = "currentPage"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s when a {@link ContextElement} has been registered
	 * for this <code>PdfViewer</code>.
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the {@link ContextElement} (never <code>null</code>);
	 * {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 */
	public static final String PROPERTY_REGISTER_CONTEXT_ELEMENT = "registerContextElement"; //$NON-NLS-1$
	/**
	 * Constant used by the {@link PropertyChangeListener}s when a {@link ContextElement} has been unregistered
	 * for this <code>PdfViewer</code>.
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the {@link ContextElement} (never <code>null</code>);
	 * {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 */
	public static final String PROPERTY_UNREGISTER_CONTEXT_ELEMENT = "unregisterContextElement"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s when the user clicked somewhere in the viewing area.
	 * A click is defined as pressing and releasing a mouse button.
	 *
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns an instance of {@link org.nightlabs.eclipse.ui.pdfviewer.MouseEvent}
	 * and {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 *
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_MOUSE_CLICKED = "mouseClicked"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s when the user pressed a mouse button somewhere in the viewing area.
	 *
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns an instance of {@link org.nightlabs.eclipse.ui.pdfviewer.MouseEvent}
	 * and {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 *
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_MOUSE_PRESSED = "mousePressed"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s whenever the mouse pointer is moved.
	 *
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns an instance of {@link org.nightlabs.eclipse.ui.pdfviewer.MouseEvent}
	 * and {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 *
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_MOUSE_MOVED = "mouseMoved"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s when the user released a mouse button
	 * (that was pressed before - see {@link #PROPERTY_MOUSE_PRESSED}).
	 *
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns an instance of {@link org.nightlabs.eclipse.ui.pdfviewer.MouseEvent}
	 * and {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 *
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_MOUSE_RELEASED = "mouseReleased"; //$NON-NLS-1$

	/**
	 * Constant used by the {@link PropertyChangeListener}s whenever the mouse pointer is
	 * dragged - i.e. moved while a mouse button is down (after pressed and before released).
	 *
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns an instance of {@link org.nightlabs.eclipse.ui.pdfviewer.MouseEvent}
	 * and {@link PropertyChangeEvent#getOldValue()} returns <code>null</code>.
	 * </p>
	 *
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_MOUSE_DRAGGED = "mouseDragged"; //$NON-NLS-1$

//	public static final String PROPERTY_COMPONENT_RESIZED = "componentResized"; //$NON-NLS-1$


	public PdfViewer() { }

	/**
	 * Assign a context-element. This method should be called by the context-element itself
	 * when it is created/assigned a <code>PdfViewer</code>.
	 *
	 * @param contextElement the context-element. Must not be <code>null</code>.
	 */
	public void registerContextElement(ContextElement<?> contextElement)
	{
		contextElementRegistry.registerContextElement(contextElement);
		propertyChangeSupport.firePropertyChange(PROPERTY_REGISTER_CONTEXT_ELEMENT, null, contextElement);
	}

	/**
	 * Remove a context-element's registration.
	 *
	 * @param contextElementType the type of the <code>contextElement</code> as specified by {@link ContextElement#getContextElementType()} when it was added.
	 * @param contextElementId the identifier or <code>null</code> as specified by {@link ContextElement#getContextElementId()} when it was added.
	 */
	@SuppressWarnings("unchecked")
	public void unregisterContextElement(ContextElementType<?> contextElementType, String contextElementId)
	{
		ContextElement<?> contextElement = contextElementRegistry.getContextElement((ContextElementType)contextElementType, contextElementId);
		contextElementRegistry.unregisterContextElement(contextElementType, contextElementId);
		propertyChangeSupport.firePropertyChange(PROPERTY_UNREGISTER_CONTEXT_ELEMENT, null, contextElement);
	}

	/**
	 * Remove a context-element's registration.
	 *
	 * @param contextElement the element to be removed.
	 */
	public void unregisterContextElement(ContextElement<?> contextElement)
	{
		contextElementRegistry.unregisterContextElement(contextElement.getContextElementType(), contextElement.getContextElementId());
	}

	/**
	 * Get a context-element that was registered before via {@link #registerContextElement(ContextElement)}
	 * or <code>null</code> if none is known for the given <code>contextElementType</code> and <code>id</code>.
	 *
	 * @param contextElementType the type of the <code>contextElement</code> as passed to {@link #registerContextElement(ContextElement)} before.
	 * @param id the identifier of the context-element as specified in {@link #registerContextElement(ContextElement)} - can be <code>null</code>.
	 * @return the appropriate context-element or <code>null</code>.
	 */
	public <T extends ContextElement<T>> T getContextElement(ContextElementType<T> contextElementType, String id) {
		return contextElementRegistry.getContextElement(contextElementType, id);
	}

	/**
	 * Get all {@link ContextElement}s that are registered for the given <code>contextElementType</code>.
	 *
	 * @param contextElementType
	 *          the type of the <code>contextElement</code> as passed to {@link #registerContextElement(ContextElement)} before.
	 * @return a <code>Collection</code> containing the previously registered context-elements; never <code>null</code> (instead, an empty
	 *         <code>Collection</code> is returned). This <code>Collection</code> is not backed by the registry and can be safely iterated
	 *         while the registry is modified.
	 */
	public <T extends ContextElement<T>> Collection<T> getContextElements(ContextElementType<T> contextElementType) {
		Collection<T> result = contextElementRegistry.getContextElements(contextElementType);
		return result;
	}

	/**
	 * Get all {@link ContextElement}s that are registered.
	 *
	 * @return an immutable <code>Collection</code> containing all {@link ContextElement}s. This <code>Collection</code> is not backed by
	 *         the registry and can be safely iterated while the registry is modified.
	 */
	public Collection<? extends ContextElement<?>> getContextElements() {
		Collection<? extends ContextElement<?>> result = contextElementRegistry.getContextElements();
		return result;
	}

	/**
	 * Checks if a given method is called on the SWT UI thread.
	 */
	private static void assertValidThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
	}

	public Control createControl(Composite parent, int style) {
		assertValidThread();

		if (this.pdfViewerComposite != null) {
			this.pdfViewerComposite.dispose();
		}

		this.pdfViewerComposite = new PdfViewerComposite(parent, style, this);

		this.pdfViewerComposite.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				assertValidThread(); // just to make sure the PdfViewerComposite is implemented correctly.

				// We handle some values BEFORE the event is propagated to the outside (the API clients) in order to
				// ensure that the local copies of these values are already updated.
				if (PROPERTY_VIEW_ORIGIN.equals(event.getPropertyName())) {
					viewOrigin = (Point2DDouble) event.getNewValue();
					if (!viewOrigin.isReadOnly()) {
						throw new IllegalStateException("Why the hell is this viewOrigin not immutable???!!!"); //$NON-NLS-1$
					}
				}
				else if (PROPERTY_ZOOM_FACTOR.equals(event.getPropertyName())) {
					zoomFactorPerMill = ((Integer) event.getNewValue()).intValue();
				}

				propertyChangeSupport.firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
			}
		});

		this.pdfViewerComposite.setViewOrigin(viewOrigin);
		this.pdfViewerComposite.setZoomFactorPerMill(zoomFactorPerMill);
		this.pdfViewerComposite.setPdfDocument(pdfDocument); // just in case the document was set before this method.

		for (Object listener : paintToBufferListeners.getListeners())
			this.pdfViewerComposite.addPaintToBufferListener((PaintListener) listener);

		for (Object listener : paintToViewListeners.getListeners())
			this.pdfViewerComposite.addPaintToViewListener((PaintListener) listener);

		return this.pdfViewerComposite;
	}

	public Control getControl() {
		assertValidThread();

		return this.pdfViewerComposite;
	}

	/**
	 * Get the current {@link PdfDocument}, which can be <code>null</code>.
	 *
	 * @return the current {@link PdfDocument} or <code>null</code>.
	 * @see #setPdfDocument(PdfDocument)
	 */
	public PdfDocument getPdfDocument() {
		assertValidThread();

		return pdfDocument;
	}

	/**
	 * Set the current {@link PdfDocument} or <code>null</code>. This will cause
	 * a {@link PropertyChangeEvent} to be propagated for the property
	 * {@link #PROPERTY_PDF_DOCUMENT}.
	 *
	 * @param pdfDocument the new {@link PdfDocument}.
	 * @see #getPdfDocument()
	 * @see #PROPERTY_PDF_DOCUMENT
	 */
	public void setPdfDocument(PdfDocument pdfDocument) {
		assertValidThread();

		PdfDocument oldPdfDocument = this.pdfDocument;
		this.pdfDocument = pdfDocument;
		if (pdfViewerComposite != null)
			pdfViewerComposite.setPdfDocument(pdfDocument);

		propertyChangeSupport.firePropertyChange(PROPERTY_PDF_DOCUMENT, oldPdfDocument, this.pdfDocument);

		if (pdfDocument != null && pdfDocument.getPdfFile().getNumPages() > 0 && pdfViewerComposite != null)
			setCurrentPage(1);
	}

	private Point2DDouble viewOrigin;
	{
		viewOrigin = new Point2DDouble();
		viewOrigin.setReadOnly();
	}

	/**
	 * Get the view origin, i.e. the left top position of the view area in the
	 * {@link PdfDocument} (in real coordinates).
	 *
	 * @return the current view origin.
	 * @see #setViewOrigin(Point2D)
	 */
	public Point2D getViewOrigin() {
		assertValidThread();

		return viewOrigin;
	}

	/**
	 * Get the size of the view area in real coordinates (i.e. what is visible
	 * in the {@link PdfDocument}). Together with {@link #getViewOrigin()},
	 * this information tells you what area is currently visible.
	 * <p>
	 * This value changes whenever the zoom is modified
	 * (see {@link #getZoomFactorPerMill()}) or when the view area is resized,
	 * but stays the same during scrolling.
	 * </p>
	 * <p>
	 * If you want to get notified about changes, you should register a
	 * {@link PropertyChangeListener} for {@link #PROPERTY_VIEW_DIMENSION}.
	 * </p>
	 *
	 * @return the size of the view area in real coordinates.
	 * @see #getViewOrigin()
	 * @see #getZoomFactorPerMill()
	 * @see #PROPERTY_VIEW_DIMENSION
	 */
	public Dimension2D getViewDimension()
	{
		assertValidThread();

		if (pdfViewerComposite == null)
			return null;

		return pdfViewerComposite.getViewDimension();
	}

	/**
	 * Set the new view origin, i.e. the left top position of the view area in the
	 * {@link PdfDocument} (in real coordinates). This will cause
	 * a {@link PropertyChangeEvent} to be propagated for the property
	 * {@link #PROPERTY_VIEW_ORIGIN}.
	 *
	 * @param viewOrigin the new view origin.
	 * @see #getViewOrigin()
	 * @see #PROPERTY_VIEW_ORIGIN
	 */
	public void setViewOrigin(Point2D viewOrigin) {
		assertValidThread();

		if (pdfViewerComposite != null)
			pdfViewerComposite.setViewOrigin(viewOrigin);
		else {
			Point2DDouble newViewOrigin = new Point2DDouble(viewOrigin);
			newViewOrigin.setReadOnly();
			this.viewOrigin = newViewOrigin;
		}
	}

	/**
	 * Add a <code>PropertyChangeListener</code> in order to react on changes.
	 *
	 * @param propertyName the property - one of {@link #PROPERTY_VIEW_ORIGIN}, {@link #PROPERTY_ZOOM_FACTOR} or another <code>PROPERTY_*</code> constant.
	 * @param listener the listener to be added.
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a <code>PropertyChangeListener</code> that was added via {@link #addPropertyChangeListener(String, PropertyChangeListener)}
	 * before.
	 *
	 * @param propertyName the property.
	 * @param listener the listener to be removed.
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Add a <code>PropertyChangeListener</code> in order to react on changes. You might consider
	 * instead using {@link #addPropertyChangeListener(String, PropertyChangeListener)} in order
	 * to specify what events you are interested in.
	 *
	 * @param listener the listener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a <code>PropertyChangeListener</code> that was added via {@link #addPropertyChangeListener(PropertyChangeListener)}
	 * before.
	 *
	 * @param listener the listener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Get the zoom factor in &permil; (1/1000). A zoom of 1000 &permil; (= 1.0) means that
	 * a document is shown in the same size as it is in real life (e.g. when printed).
	 * This is done by taking the screen resolution into account and calculating a
	 * zoom-correction-factor (see {@link #getZoomScreenResolutionFactor()}
	 * and {@link #getZoomScreenResolutionFactor()}) that's multiplied with the zoom factor
	 * whenever a calculation containing the zoom is done.
	 *
	 * @return the zoom factor in &permil;.
	 * @see #setZoomFactorPerMill(int)
	 */
	public int getZoomFactorPerMill() {
		assertValidThread();

		return zoomFactorPerMill;
	}

	/**
	 * Set the zoom factor in &permil; (1/1000). This will cause
	 * a {@link PropertyChangeEvent} to be propagated for the property
	 * {@link #PROPERTY_ZOOM_FACTOR}.
	 *
	 * @param zoomFactorPerMill the zoom factor in &permil;.
	 * @see #getZoomFactorPerMill()
	 * @see #PROPERTY_ZOOM_FACTOR
	 */
	public void setZoomFactorPerMill(int zoomFactorPerMill) {
		assertValidThread();

		if (pdfViewerComposite != null)
			pdfViewerComposite.setZoomFactorPerMill(zoomFactorPerMill);
		else
			this.zoomFactorPerMill = zoomFactorPerMill;
	}

	public int getCurrentPage() {
		if (pdfViewerComposite == null)
			throw new IllegalStateException("Currently, this method can only be called when a control has already been created (i.e. after PdfViewer.createControl() has been called)!"); // TODO use local mirror variable //$NON-NLS-1$

		return pdfViewerComposite.getCurrentPage();
	}

	public void setCurrentPage(int currentPage) {
		if (pdfViewerComposite != null)
			pdfViewerComposite.setCurrentPage(currentPage);
		else
			throw new IllegalStateException("Currently, this method can only be called when a control has already been created (i.e. after PdfViewer.createControl() has been called)!"); // TODO use local mirror variable //$NON-NLS-1$
	}

	/**
	 * Get the zoom correction factor (horizontal and vertical) to make <code>zoomFactor=100%</code> mean real-life size.
	 * This, of course, works only, if your operating system indicates the correct screen resolution.
	 * In other words, with GNU/Linux, it works very well; with Windows, it probably doesn't.
	 * <p>
	 * Currently, this method can only be called after {@link #createControl(Composite, int)} was called (because
	 * it uses the control's {@link Display} to find out the screen resolution).
	 * </p>
	 *
	 * @return the zoom correction factor based on the screen resolution.
	 * @see #getZoomFactorPerMill()
	 */
	public Point2D getZoomScreenResolutionFactor()
	{
		if (pdfViewerComposite != null)
			return pdfViewerComposite.getZoomScreenResolutionFactor();
		else
			throw new IllegalStateException("Currently, this method can only be called when a control has already been created (i.e. after PdfViewer.createControl() has been called)!"); //$NON-NLS-1$
	}

	public AutoZoom getAutoZoom() {
		return autoZoom;
	}

	public void setAutoZoom(AutoZoom autoZoom) {
		assertValidThread();

		this.autoZoom = autoZoom;
		if (pdfViewerComposite != null)
			pdfViewerComposite.onAutoZoomChange();
	}

	private boolean mouseWheelZoomEnabled = true;

	public void setMouseWheelZoomEnabled(boolean zoomIsAllowed) {
		this.mouseWheelZoomEnabled = zoomIsAllowed;
	}

	public boolean isMouseWheelZoomEnabled() {
		return mouseWheelZoomEnabled;
	}

	private ListenerList paintToBufferListeners = new ListenerList();
	private ListenerList paintToViewListeners = new ListenerList();

	/**
	 * Add a listener for custom painting into the buffer.
	 *
	 * @param listener the listener to be added.
	 * @see #removePaintToBufferListener(PaintListener)
	 * @see #addPaintToViewListener(PaintListener)
	 */
	public void addPaintToBufferListener(PaintListener listener)
	{
		paintToBufferListeners.add(listener);
		if (pdfViewerComposite != null)
			pdfViewerComposite.addPaintToBufferListener(listener);
	}
	/**
	 * Remove a listener that was previously added by {@link #addPaintToBufferListener(PaintListener)}.
	 *
	 * @param listener the listener to be removed.
	 * @see #addPaintToBufferListener(PaintListener)
	 */
	public void removePaintToBufferListener(PaintListener listener)
	{
		paintToBufferListeners.remove(listener);
		if (pdfViewerComposite != null)
			pdfViewerComposite.removePaintToBufferListener(listener);
	}

	/**
	 * Add a listener for custom painting into the view area.
	 *
	 * @param listener the listener to be added.
	 * @see #removePaintToViewListener(PaintListener)
	 * @see #addPaintToBufferListener(PaintListener)
	 */
	public void addPaintToViewListener(PaintListener listener)
	{
		paintToViewListeners.add(listener);
		if (pdfViewerComposite != null)
			pdfViewerComposite.addPaintToViewListener(listener);
	}
	/**
	 * Remove a listener that was previously added by {@link #addPaintToViewListener(PaintListener)}.
	 *
	 * @param listener the listener to be removed.
	 * @see #addPaintToViewListener(PaintListener)
	 */
	public void removePaintToViewListener(PaintListener listener)
	{
		paintToViewListeners.remove(listener);
		if (pdfViewerComposite != null)
			pdfViewerComposite.removePaintToViewListener(listener);
	}

	public void setUpdateCurrentPageOnScrolling(boolean updateCurrentPageOnScrolling) {
		assertValidThread();

		this.updateCurrentPageOnScrolling = updateCurrentPageOnScrolling;
	}
	public boolean isUpdateCurrentPageOnScrolling() {
		return updateCurrentPageOnScrolling;
	}

//	public double getAutoZoomFactor() {
//		return autoZoomFactor;
//	}
//	public void setAutoZoomFactor(double autoZoomFactor) {
//		assertValidThread();
//
//		this.autoZoomFactor = autoZoomFactor;
//		if (pdfViewerComposite != null)
//			pdfViewerComposite.onAutoZoomChange();
//	}

	public int getAutoZoomHorizontalMargin() {
		return autoZoomHorizontalMargin;
	}
	public void setAutoZoomHorizontalMargin(int autoZoomHorizontalMargin) {
		this.autoZoomHorizontalMargin = autoZoomHorizontalMargin;
		if (pdfViewerComposite != null)
			pdfViewerComposite.onAutoZoomChange();
	}
	public int getAutoZoomVerticalMargin() {
		return autoZoomVerticalMargin;
	}
	public void setAutoZoomVerticalMargin(int autoZoomVerticalMargin) {
		this.autoZoomVerticalMargin = autoZoomVerticalMargin;
		if (pdfViewerComposite != null)
			pdfViewerComposite.onAutoZoomChange();
	}
}
