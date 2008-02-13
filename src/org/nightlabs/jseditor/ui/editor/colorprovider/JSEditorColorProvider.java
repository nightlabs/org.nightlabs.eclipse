package org.nightlabs.jseditor.ui.editor.colorprovider;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Manager for colors used in the JS editor
 */
public class JSEditorColorProvider {

	public static final RGB MULTI_LINE_COMMENT= new RGB(128, 0, 0);
	public static final RGB SINGLE_LINE_COMMENT= new RGB(128, 128, 0);
	public static final RGB KEYWORD= new RGB(86, 0, 191);
	public static final RGB TYPE= new RGB(0, 0, 128);
	public static final RGB STRING= new RGB(0, 0, 128);
	public static final RGB DEFAULT= new RGB(0, 0, 0);
	public static final RGB JSDOC_KEYWORD= new RGB(0, 128, 0);
	public static final RGB JSDOC_TAG= new RGB(128, 128, 128);
	public static final RGB JSDOC_LINK= new RGB(128, 128, 128);
	public static final RGB JSDOC_DEFAULT= new RGB(0, 128, 128);
	public static final RGB SPECIAL= new RGB(255, 0, 0);

	protected Map<RGB, Color> fColorTable= new HashMap<RGB, Color>(10);

	/**
	 * Release all of the color resources held onto by the receiver.
	 */
	public void dispose() {
		Iterator e= fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}
	
	/**
	 * Return the color that is stored in the color table under the given RGB
	 * value.
	 * 
	 * @param rgb the RGB value
	 * @return the color stored in the color table for the given RGB value
	 */
	public Color getColor(RGB rgb) {
		Color color= fColorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
