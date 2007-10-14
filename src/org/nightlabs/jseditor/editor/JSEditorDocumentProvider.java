package org.nightlabs.jseditor.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.nightlabs.jseditor.JSEditorPlugin;
import org.nightlabs.jseditor.editor.scanner.JSEditorPartitionScanner;

/**
 * 
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class JSEditorDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			configureDocument(document);
		}		
		return document;
	}
	
	
	public static void configureDocument(IDocument document) {
		if (document != null) {
			IDocumentPartitioner partitioner= new FastPartitioner(JSEditorPlugin.getDefault().getJSEditorPartitionScanner(), JSEditorPartitionScanner.JS_PARTITION_TYPES);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}		
	}
	
}