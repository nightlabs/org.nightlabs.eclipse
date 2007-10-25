package org.nightlabs.jseditor.ui.editor.contentassist;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;

/**
 * JS completion processor.
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class JSCompletionProcessor implements IContentAssistProcessor {

	public JSCompletionProcessor(){
		
	}
	
	/**
	 * Simple content assist tip closer. The tip is valid in a range
	 * of 5 characters around its popup location.
	 */
	protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

		protected int jsInstallOffset;

		/*
		 * @see IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int offset) {
			return Math.abs(jsInstallOffset - offset) < 5;
		}

		/*
		 * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer, int offset) {
			jsInstallOffset= offset;
		}
		
		/*
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			return false;
		}
	}

//	private String[] appandedContentProposals;
//	public void appandClassContentProposal(Class c){
//		ArrayList<String> result = new ArrayList<String>();
//		Method[] methods = c.getMethods();
//		
//		for(Method m : methods){
//			String mSig = m.toGenericString();
//			result.add(mSig.substring(mSig.indexOf(" ") + 1));
//		}//for
//		
//		this.appandedContentProposals = (String[])result.toArray(new String[0]);
//	}
	
	protected IContextInformationValidator jsValidator= new Validator();

	public HashMap<ImageDescriptor, Image>imageCache = new HashMap<ImageDescriptor, Image>();

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		ArrayList<ICompletionProposal> result= new ArrayList<ICompletionProposal>();
		AbstractContentAssistantVariable ac = new AbstractContentAssistantVariable(){
			@Override
			public void preparedWordList() {
			}
		};
		for(String s : ac.getWordList()){
			IContextInformation info= new ContextInformation(s, MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.Proposal.ContextInfo.pattern"), new Object[] { s })); //$NON-NLS-1$
			result.add(new CompletionProposal(s, documentOffset, 0, s.length(), null/*Image*/, s, info, MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.Proposal.hoverinfo.pattern"), new Object[] { s}))); //$NON-NLS-1$
		}//for
//		for (int i= 0; i < JSEditorConstants.JS_KEYWORDS.length; i++) {
//			IContextInformation info= new ContextInformation(JSEditorConstants.JS_KEYWORDS[i], MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.Proposal.ContextInfo.pattern"), new Object[] { JSEditorConstants.JS_KEYWORDS[i] })); //$NON-NLS-1$
//			result.add(new CompletionProposal(JSEditorConstants.JS_KEYWORDS[i], documentOffset, 0, JSEditorConstants.JS_KEYWORDS[i].length(), null/*Image*/, JSEditorConstants.JS_KEYWORDS[i], info, MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.Proposal.hoverinfo.pattern"), new Object[] { JSEditorConstants.JS_KEYWORDS[i]}))); //$NON-NLS-1$
//		}//for
		
//		if(appandedContentProposals != null && appandedContentProposals.length > 0){
//			for(String s : appandedContentProposals){
//				//obtain the cached image corresponding to the descriptor
//				ImageDescriptor descriptor = JSEditorPlugin.getImageDescriptor("icons/sample.gif");
//				Image image = (Image)imageCache.get(descriptor);
//				if (image == null && descriptor != null) {
//					image = descriptor.createImage();
//					imageCache.put(descriptor, image);
//				}//if
//				IContextInformation info= new ContextInformation(s, MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.Proposal.ContextInfo.pattern"), new Object[] { s })); //$NON-NLS-1$
//				result.add(new CompletionProposal(s, documentOffset, 0, s.length(), image, s, info, MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.Proposal.hoverinfo.pattern"), new Object[] {s}))); //$NON-NLS-1$
//			}//for
//		}//if
		
		return result.toArray(new ICompletionProposal[0]);
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		IContextInformation[] result= new IContextInformation[5];
		for (int i= 0; i < result.length; i++)
			result[i]= new ContextInformation(
				MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.ContextInfo.display.pattern"), new Object[] { new Integer(i), new Integer(documentOffset) }),  //$NON-NLS-1$
				MessageFormat.format(JSEditorMessages.getString("CompletionProcessor.ContextInfo.value.pattern"), new Object[] { new Integer(i), new Integer(documentOffset - 5), new Integer(documentOffset + 5)})); //$NON-NLS-1$
		return result;
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.', '(' };
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '#' };
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return jsValidator;
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public String getErrorMessage() {
		return null;
	}
}
