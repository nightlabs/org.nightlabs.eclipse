/**
 * 
 */
package org.nightlabs.base.language;

/**
 * The Event object which is created and passed when an {@link ModificationFinishedListener} 
 * is triggered 
 * 
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 */
public class ModificationFinishedEvent 
{
	public ModificationFinishedEvent(II18nTextEditor editor) {
		this.editor = editor;
	}
	
	/**
	 * the {@link II18nTextEditor} which modification is finished  
	 */
	private II18nTextEditor editor;
	public II18nTextEditor getEditor() {
		return editor;
	}
}
