package org.nightlabs.jseditor.ui.editor;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jseditor.ui.JSEditorPlugin;
import org.nightlabs.jseditor.ui.editor.colorprovider.JSEditorColorProvider;

/**
 * 
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class JSEditorComposite extends XComposite{
	private SourceViewer sourceViewer;

	private JSEditorConfiguration jsEditorConfiguration;
	private JSEditorColorProvider jsColorProvider = JSEditorPlugin.getDefault().getJSEditorColorProvider();

	private Document document;

	private IWorkbenchWindow wPart;

	public JSEditorComposite(IWorkbenchWindow wPart, Composite parent) {
		this(wPart, parent, null);
	}

	public JSEditorComposite(IWorkbenchWindow wPart, Composite parent, String title) {
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.wPart = wPart;

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

	@Override
	public void addFocusListener(FocusListener listener)
	{
		sourceViewer.getTextWidget().addFocusListener(listener);
	}

	@Override
	public void removeFocusListener(FocusListener listener)
	{
		sourceViewer.getTextWidget().removeFocusListener(listener);
	}

	public void setDocumentText(String text) {
		if (document == null)
			throw new IllegalStateException("The document is null!"); //$NON-NLS-1$
		document.set(text);
		sourceViewer.invalidateTextPresentation();
	}

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
}
