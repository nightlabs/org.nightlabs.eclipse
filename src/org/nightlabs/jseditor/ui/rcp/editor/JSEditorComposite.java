package org.nightlabs.jseditor.ui.rcp.editor;

import java.util.IdentityHashMap;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jseditor.ui.DocumentEvent;
import org.nightlabs.jseditor.ui.IDocumentListener;
import org.nightlabs.jseditor.ui.IJSEditor;
import org.nightlabs.jseditor.ui.rcp.JSEditorPlugin;
import org.nightlabs.jseditor.ui.rcp.editor.colorprovider.JSEditorColorProvider;

/**
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class JSEditorComposite extends XComposite implements IJSEditor {
	private SourceViewer sourceViewer;

	private JSEditorConfiguration jsEditorConfiguration;
	private JSEditorColorProvider jsColorProvider = JSEditorPlugin.getDefault().getJSEditorColorProvider();

	private Document document;

//	public JSEditorComposite(IWorkbenchWindow wPart, Composite parent) {
//		this(wPart, parent, null);
//	}
//
//	public JSEditorComposite(IWorkbenchWindow wPart, Composite parent, String title) {
//		this(wPart, parent, title, SWT.NONE);
//	}
//
//	public JSEditorComposite(IWorkbenchWindow wPart, Composite parent, String title, int style) {
//		this(parent, title, style);
//	}

	public JSEditorComposite(Composite parent) {
		this(parent, SWT.NONE);
	}

//	public JSEditorComposite(Composite parent, String title) {
//		this(parent, title, SWT.NONE);
//	}

	public JSEditorComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		setLayout(layout);

		/******************************
		 * Prepare Ruler
		 ******************************/
		CompositeRuler ruler = new CompositeRuler( );
//		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
//		ruler.addDecorator( 0, lineNumbers );

		/******************************
		 * Prepare Source Viewer & Configuration
		 ******************************/
		sourceViewer = new SourceViewer( this, ruler, SWT.H_SCROLL | SWT.V_SCROLL );
		jsEditorConfiguration = new JSEditorConfiguration(jsColorProvider);
		sourceViewer.configure(jsEditorConfiguration);

		/******************************
		 * Prepare Document
		 ******************************/
		document = new Document();
		JSEditorDocumentProvider.configureDocument(document);

		sourceViewer.setDocument(document);
		sourceViewer.getTextWidget().setFont(JFaceResources.getTextFont());

		GridData data = new GridData(GridData.FILL_BOTH);
		sourceViewer.getControl().setLayoutData(data);

		//Add listener to the text component...
		sourceViewer.getTextWidget( ).addKeyListener( new KeyListener( ) {
			public void keyPressed( KeyEvent e )
			{
				if ( isUndoKeyPress( e ) )
				{
					sourceViewer.doOperation( ITextOperationTarget.UNDO );
				}
				else if ( isRedoKeyPress( e ) )
				{
					sourceViewer.doOperation( ITextOperationTarget.REDO );
				}
			}
			private boolean isUndoKeyPress( KeyEvent e )
			{
				// CTRL + z
				return ( ( e.stateMask & SWT.CONTROL ) > 0 )
				&& ( ( e.keyCode == 'z' ) || ( e.keyCode == 'Z' ) );
			}
			private boolean isRedoKeyPress( KeyEvent e )
			{
				// CTRL + y
				return ( ( e.stateMask & SWT.CONTROL ) > 0 )
				&& ( ( e.keyCode == 'y' ) || ( e.keyCode == 'Y' ) );
			}
			public void keyReleased( KeyEvent e )
			{
				// do nothing
			}
		} );
	}

	private IdentityHashMap<IDocumentListener, org.eclipse.jface.text.IDocumentListener> jsEditorDocumentListenerToJFaceDocumentListener = new IdentityHashMap<IDocumentListener, org.eclipse.jface.text.IDocumentListener>();

	@Override
	public void addDocumentListener(final IDocumentListener documentListener)
	{
		if (documentListener == null)
			throw new IllegalArgumentException("documentListener == null");

		org.eclipse.jface.text.IDocumentListener jfaceDocumentListener = new org.eclipse.jface.text.IDocumentListener()
		{
			@Override
			public void documentChanged(org.eclipse.jface.text.DocumentEvent paramDocumentEvent) {
				documentListener.documentChanged(new DocumentEvent(JSEditorComposite.this));
			}

			@Override
			public void documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent paramDocumentEvent) { }
		};

		jsEditorDocumentListenerToJFaceDocumentListener.put(documentListener, jfaceDocumentListener);
		sourceViewer.getDocument().addDocumentListener(jfaceDocumentListener);
	}

	@Override
	public void removeDocumentListener(IDocumentListener documentListener)
	{
		org.eclipse.jface.text.IDocumentListener jfaceDocumentListener = jsEditorDocumentListenerToJFaceDocumentListener.get(documentListener);
		if (jfaceDocumentListener == null)
			return;

		sourceViewer.getDocument().removeDocumentListener(jfaceDocumentListener);
		jsEditorDocumentListenerToJFaceDocumentListener.remove(documentListener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.editor.IJSEditor#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	@Override
	public void addFocusListener(FocusListener listener)
	{
		sourceViewer.getTextWidget().addFocusListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.editor.IJSEditor#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	@Override
	public void removeFocusListener(FocusListener listener)
	{
		sourceViewer.getTextWidget().removeFocusListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.editor.IJSEditor#setDocumentText(java.lang.String)
	 */
	public void setDocumentText(String text) {
		if (document == null)
			throw new IllegalStateException("The document is null!"); //$NON-NLS-1$
		document.set(text);
		sourceViewer.invalidateTextPresentation();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.editor.IJSEditor#getDocumentText()
	 */
	public String getDocumentText() {
		if (document == null)
			throw new IllegalStateException("The document is null!"); //$NON-NLS-1$
		return document.get();
	}

	public IDocument getDocument(){
		return document;
	}

	public SourceViewer getSourceViewer(){
		return sourceViewer;
	}

	// This should be in compliance with the current FocusListener settings, which direct this JSEditorComposite's
	// focus to the sourceViewer. Kai 2009-11-19.
	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.editor.IJSEditor#addKeyListener(org.eclipse.swt.events.KeyListener)
	 */
	@Override
	public void addKeyListener(KeyListener listener) {
		sourceViewer.getTextWidget().addKeyListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jseditor.ui.editor.IJSEditor#removeKeyListener(org.eclipse.swt.events.KeyListener)
	 */
	@Override
	public void removeKeyListener(KeyListener listener) {
		sourceViewer.getTextWidget().removeKeyListener(listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		sourceViewer.getTextWidget().addMouseListener(listener);
	}

	@Override
	public void removeMouseListener(MouseListener listener) {
		sourceViewer.getTextWidget().removeMouseListener(listener);
	}

	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public Menu createContextMenu(MenuManager menuManager) {
		Menu menu = menuManager.createContextMenu(sourceViewer.getTextWidget());
		sourceViewer.getTextWidget().setMenu(menu);
		return menu;
	}
}
