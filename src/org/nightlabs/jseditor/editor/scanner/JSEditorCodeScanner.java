package org.nightlabs.jseditor.editor.scanner;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.nightlabs.jseditor.editor.JSEditorConstants;
import org.nightlabs.jseditor.editor.JSEditorWhitespaceDetector;
import org.nightlabs.jseditor.editor.colorprovider.JSEditorColorProvider;
import org.nightlabs.jseditor.editor.detector.JSEditorWordDetector;

/**
 * A JScript code scanner.
 */
public class JSEditorCodeScanner extends RuleBasedScanner {
	public final static String JS_MULTILINE_COMMENT= "__java_multiline_comment"; //$NON-NLS-1$
	public final static String JS_DOC= "__java_javadoc"; //$NON-NLS-1$
	public final static String[] JS_PARTITION_TYPES= new String[] { JS_MULTILINE_COMMENT, JS_DOC };
	/**
	 * Creates a JScript code scanner with the given color provider.
	 * 
	 * @param provider the color provider
	 */
	public JSEditorCodeScanner(JSEditorColorProvider provider) {

		IToken keywordToken = new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.KEYWORD), null, SWT.BOLD));
		IToken typeToken = new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.TYPE)));
		IToken stringToken = new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.STRING)));
		IToken commentToken = new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.SINGLE_LINE_COMMENT)));
		IToken operatorToken = new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.DEFAULT), null, SWT.BOLD));
		IToken otherToken = new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.DEFAULT)));
		
		List<IRule> rules= new ArrayList<IRule>();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", commentToken)); //$NON-NLS-1$
		rules.add(new EndOfLineRule("/", new Token(new TextAttribute(provider.getColor(JSEditorColorProvider.SPECIAL), null, SWT.BOLD)))); //$NON-NLS-1$

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", stringToken, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", stringToken, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new JSEditorWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule= new WordRule(new JSEditorWordDetector(), otherToken);
		for (int i= 0; i < JSEditorConstants.JS_KEYWORDS.length; i++)
			wordRule.addWord(JSEditorConstants.JS_KEYWORDS[i], keywordToken);
		for (int i= 0; i < JSEditorConstants.JS_TYPES.length; i++)
			wordRule.addWord(JSEditorConstants.JS_TYPES[i], typeToken);
		for (int i= 0; i < JSEditorConstants.JS_CONSTANTS.length; i++)
			wordRule.addWord(JSEditorConstants.JS_CONSTANTS[i], typeToken);
		for (int i= 0; i < JSEditorConstants.JS_OPERATORS.length; i++)
			wordRule.addWord(String.valueOf(JSEditorConstants.JS_OPERATORS[i]), operatorToken);
		rules.add(wordRule);

		
		IRule[] result= new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
}
