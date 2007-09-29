/**
 * 
 */
package org.nightlabs.base.entity.editor;

import java.util.EventObject;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EntityEditorPageControllerModifyEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private Object oldObject;
	private Object newObject;
	
	/**
	 * @param source The source.
	 */
	public EntityEditorPageControllerModifyEvent(Object source) {
		super(source);
	}

	
	/**
	 * Create a new {@link EntityEditorPageControllerModifyEvent}.
	 * 
	 * @param source The source.
	 * @param oldObject The old object.
	 * @param newObject The new object.
	 */
	public EntityEditorPageControllerModifyEvent(Object source, Object oldObject, Object newObject) {
		super(source);
		this.oldObject = oldObject;
		this.newObject = newObject;
	}

	/**
	 * @return the newObject
	 */
	public Object getNewObject() {
		return newObject;
	}

	/**
	 * @return the oldObject
	 */
	public Object getOldObject() {
		return oldObject;
	}
}
