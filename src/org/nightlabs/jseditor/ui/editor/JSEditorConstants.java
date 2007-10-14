/* *****************************************************************************
 *  Copyright (c) 2006 NightLabs, Germany                                      *
 *                                                                             *
 *  All rights reserved. This program and the accompanying materials           *
 *  are made available under the terms of the Eclipse Public License v1.0      *
 *  which accompanies this distribution, and is available at                   *
 *  http://www.eclipse.org/legal/epl-v10.html                                  *
 *                                                                             *
 *  Contributors:                                                              *
 *   Alexander Bieber, NightLabs - initial API and implementation              *
 ******************************************************************************/

package org.nightlabs.jseditor.ui.editor;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Chairat Kongrarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class JSEditorConstants {

	public static final String JS_CONTENT_TYPE_RESULTS = "jsResults";
	public static final String JS_CONTENT_TYPE_CANDIDATE_CLASS = "jsCandidateClass";
	public static final String JS_CONTENT_TYPE_FILTER = "jsFilter";
	public static final String JS_CONTENT_TYPE_GROUP_BY = "jsGroupBy";
	public static final String JS_CONTENT_TYPE_IMPORTS = "jsImports";
	public static final String JS_CONTENT_TYPE_VARIABLES = "jsVariables";
	public static final String JS_CONTENT_TYPE_PARAMETERS = "jsParameters";
	public static final String JS_CONTENT_TYPE_OPERATORS = "jsParameters";

	public static final String[] JS_CONTENT_TYPES = {
		JS_CONTENT_TYPE_CANDIDATE_CLASS, JS_CONTENT_TYPE_RESULTS,
		JS_CONTENT_TYPE_FILTER, JS_CONTENT_TYPE_GROUP_BY,
		JS_CONTENT_TYPE_IMPORTS,	JS_CONTENT_TYPE_VARIABLES,
		JS_CONTENT_TYPE_PARAMETERS, JS_CONTENT_TYPE_OPERATORS
	};
	
	
	public static final String JS_CONTENT_TYPE_SINGLELINE_COMMENT = "jsSingleLineComment";
	public static final String JS_CONTENT_TYPE_MULTILINE_COMMENT = "jsMultiLineComment";
	
	public static final String[] CONTENT_TYPE_STARTER = {"from", "select", "where", "group by", "imports", "variables", "parameters"};
	public static final Map<String, String> CONTENT_TYPE_STARTER_TO_CONTENT_TYPES = new HashMap<String, String>();

	public final static String[] JS_KEYWORDS=
	{ "cell", "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", 
	"continue", "default", "do", "double", "else", "extends", "false", "final", 
	"finally", "float", "for", "if", "implements", "import", "instanceof", "int", 
	"interface", "long", "native", "new", "null", "package", "private", "protected", 
	"public", "return", "short", "static", "super", "switch", "synchronized", "this", 
	"throw", "throws", "transient", "true", "try", "void", "volatile", "while" };
	
	public final static String[] JS_TYPES = { "void", "boolean", "char", "byte", "short",
	"int", "long", "float", "double" }; //$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-2$
	
	public final static String[] JS_CONSTANTS= { "false", "null", "true" }; //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
	
	public final static char[] JS_OPERATORS= { '+', '-', '*' , '/'}; //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
}
