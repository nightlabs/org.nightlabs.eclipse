package org.nightlabs.jseditor.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;
import org.nightlabs.jseditor.ui.JSEditorPlugin;
import org.nightlabs.jseditor.ui.editor.colorprovider.JSEditorColorProvider;
import org.nightlabs.jseditor.ui.editor.contentassist.AbstractContentAssistantVariable;
import org.nightlabs.jseditor.ui.editor.contentassist.JSCompletionProcessor;
import org.nightlabs.jseditor.ui.editor.scanner.JSEditorCodeScanner;

/**
 * 
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class JSEditorConfiguration extends SourceViewerConfiguration {
	public JSEditorConfiguration(JSEditorColorProvider colorProvider) {
		super();
	}
	
	public JSEditorConfiguration(JSEditorColorProvider colorProvider, AbstractContentAssistantVariable abstractContentAssistantVariable){
		
	}
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		String[] result = new String[JSEditorConstants.JS_CONTENT_TYPES.length + 1];
		result[0] = IDocument.DEFAULT_CONTENT_TYPE;
		for (int i = 1; i < result.length; i++) {
			result[i] = JSEditorConstants.JS_CONTENT_TYPES[i-1];
		}
		return result;
	}

//	@Override
//	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
////		return super.getAutoEditStrategies(sourceViewer, contentType);
//		return new IAutoEditStrategy[]{new JSEditorAutoEditStrategy()};
//	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		JSEditorColorProvider provider = new JSEditorColorProvider();
		
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		JSEditorCodeScanner codeScanner = new JSEditorCodeScanner(provider);
		
		DefaultDamagerRepairer dRepairer = new DefaultDamagerRepairer(codeScanner);
		reconciler.setDamager(dRepairer, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dRepairer, IDocument.DEFAULT_CONTENT_TYPE);
		
		dRepairer = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(provider.getColor(JSEditorColorProvider.JSDOC_TAG))));
		reconciler.setDamager(dRepairer, JSEditorCodeScanner.JS_DOC);
		reconciler.setRepairer(dRepairer, JSEditorCodeScanner.JS_DOC);

		dRepairer = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(provider.getColor(JSEditorColorProvider.MULTI_LINE_COMMENT))));
		reconciler.setDamager(dRepairer, JSEditorCodeScanner.JS_MULTILINE_COMMENT);
		reconciler.setRepairer(dRepairer, JSEditorCodeScanner.JS_MULTILINE_COMMENT);

		return reconciler;
	}
	
	/**
	 * Single token scanner.
	 */
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant= new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		/**********************************************************/
		JSCompletionProcessor jscp = new JSCompletionProcessor();
//		jscp.appandClassContentProposal(String.class);
		/**********************************************************/
		assistant.setContentAssistProcessor(jscp, IDocument.DEFAULT_CONTENT_TYPE);
//		assistant.setContentAssistProcessor(new JavaDocCompletionProcessor(), JavaPartitionScanner.JAVA_DOC);

//		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setContextInformationPopupBackground(JSEditorPlugin.getDefault().getJSEditorColorProvider().getColor(new RGB(150, 150, 0)));
		return assistant;
	}
	
	
}
