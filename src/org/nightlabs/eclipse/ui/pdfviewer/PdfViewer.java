package org.nightlabs.eclipse.ui.pdfviewer;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.eclipse.ui.pdfviewer.internal.PdfViewerComposite;

/**
 * The raw viewing area without any additional elements. Use this, if you want to
 * compose a custom viewer. You can add additional elements - if desired -
 * (e.g. a {@link PdfSimpleNavigator}) to your custom viewer wherever you want.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
public class PdfViewer
{
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public static final String PROPERTY_VIEW_ORIGIN = "viewOrigin";

	/**
	 * Constant used by the {@link PropertyChangeListener}s for modifications of the zoom factor.
	 * <p>
	 * {@link PropertyChangeEvent#getNewValue()} returns the new zoom factor (an instance of {@link Integer})
	 * and {@link PropertyChangeEvent#getOldValue()} returns the view origin before the change happened
	 * (an instance of {@link Integer}, too). The zoom factor returned is the value in per mill (e.g. a value of 1000 means
	 * 100% = 1.0).
	 * </p>
	 */
	public static final String PROPERTY_ZOOM_FACTOR = "zoomFactor";

	private PdfDocument pdfDocument;
	private PdfViewerComposite pdfViewerComposite;

	private Map<ContextElementType, Map<String, Object>> contextElementType2id2contextElement = new HashMap<ContextElementType, Map<String,Object>>();

	/**
	 * Assign a context-element. This method should be called by the context-element itself
	 * when it is created/assigned a <code>PdfViewer</code>.
	 *
	 * @param contextElementType the type of the <code>contextElement</code>. This should be the base class - e.g. when subclassing {@link PdfSimpleNavigator}, you should still pass <code>PdfSimpleNavigator.class</code> and not the subclass' type. Must <b>not</b> be <code>null</code>!
	 * @param id the identifier of the context-element. Can be <code>null</code>.
	 * @param contextElement the context-element. Can be <code>null</code> to remove a previous entry.
	 */
	public void setContextElement(ContextElementType contextElementType, String id, Object contextElement)
	{
		assertValidThread();
		if (contextElementType == null)
			throw new IllegalArgumentException("contextElementType must not be null!");

		Map<String, Object> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null && contextElement != null) {
			id2contextElement = new HashMap<String, Object>();
			contextElementType2id2contextElement.put(contextElementType, id2contextElement);
		}

		if (contextElement == null) {
			if (id2contextElement != null)
				id2contextElement.remove(id);
		}
		else
			id2contextElement.put(id, contextElement);

		if (id2contextElement != null && id2contextElement.isEmpty())
			contextElementType2id2contextElement.remove(contextElementType);
	}

	/**
	 * Get a context-element that was registered before via {@link #setContextElement(ContextElementType, String, Object)}
	 * or <code>null</code> if none is known for the given <code>contextElementType</code> and <code>id</code>.
	 *
	 * @param contextElementType the type of the <code>contextElement</code> as passed to {@link #setContextElement(ContextElementType, String, Object)} before.
	 * @param id the identifier of the context-element as specified in {@link #setContextElement(ContextElementType, String, Object)} - can be <code>null</code>.
	 * @return the appropriate context-element or <code>null</code>.
	 */
	public Object getContextElement(ContextElementType contextElementType, String id) {
		Map<String, Object> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null)
			return null;

		return id2contextElement.get(id);
	}

	/**
	 * @param contextElementType the type of the <code>contextElement</code> as passed to {@link #setContextElement(ContextElementType, String, Object)} before.
	 * @return a <code>Collection</code> containing the previously registered context-elements; never <code>null</code> (instead, an empty <code>Collection</code> is returned).
	 */
	public Collection<?> getContextElements(ContextElementType contextElementType)
	{
		Map<String, Object> id2contextElement = contextElementType2id2contextElement.get(contextElementType);
		if (id2contextElement == null)
			return Collections.emptySet();

		return Collections.unmodifiableCollection(id2contextElement.values());
	}

	private static void assertValidThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!");
	}

	public PdfViewer() { }

	public Control createControl(Composite parent)
	{
		assertValidThread();
		if (this.pdfViewerComposite != null)
			this.pdfViewerComposite.dispose();

		this.pdfViewerComposite = new PdfViewerComposite(parent);
		this.pdfViewerComposite.setViewOrigin(viewOrigin);
		this.pdfViewerComposite.setZoomFactorPerMill(zoomFactorPerMill);
		this.pdfViewerComposite.setPdfDocument(pdfDocument); // just in case, the document was set before this method.
//		viewOrigin.setLocation(this.pdfViewerComposite.getViewOrigin());

		this.pdfViewerComposite.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				assertValidThread(); // just to make sure the PdfViewerComposite is implemented correctly.

				// We handle some values BEFORE the event is propagated to the outside (the API clients) in order to
				// ensure that the local copies of these values are already updated.
				if (PROPERTY_VIEW_ORIGIN.equals(event.getPropertyName()))
					viewOrigin.setLocation((Point2D) event.getNewValue());
				else if (PROPERTY_ZOOM_FACTOR.equals(event.getPropertyName()))
					zoomFactorPerMill = ((Integer) event.getNewValue()).intValue();

				propertyChangeSupport.firePropertyChange(event.getPropertyName(), event.getOldValue(), event.getNewValue());
			}
		});

		return this.pdfViewerComposite;
	}

	public Control getControl() {
		assertValidThread();

		return this.pdfViewerComposite;
	}

	public PdfDocument getPdfDocument() {
		assertValidThread();

		return pdfDocument;
	}

	public void setPdfDocument(PdfDocument pdfDocument) {
		assertValidThread();

		this.pdfDocument = pdfDocument;
		if (pdfViewerComposite != null)
			pdfViewerComposite.setPdfDocument(pdfDocument);
	}

	private Point2D viewOrigin = new Point2D.Double();
	private int zoomFactorPerMill = 1000;

	public Point2D getViewOrigin() {
		assertValidThread();

		return viewOrigin;
	}

	public void setViewOrigin(Point2D viewOrigin) {
		assertValidThread();

		this.viewOrigin.setLocation(viewOrigin);

		if (pdfViewerComposite != null)
			pdfViewerComposite.setViewOrigin(viewOrigin);
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

	public int getZoomFactorPerMill() {
		assertValidThread();

		return zoomFactorPerMill;
	}

	public void setZoomFactorPerMill(int zoomFactorPerMill) {
		assertValidThread();

		this.zoomFactorPerMill = zoomFactorPerMill;

		if (pdfViewerComposite != null)
			pdfViewerComposite.setZoomFactorPerMill(zoomFactorPerMill);
	}

	// TODO more API, like:
	// - get the zoom (in per mill)

	// - set the zoom
	// - get visible dimension (i.e. width + height) of the view panel in real coordinates
}
