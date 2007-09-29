package org.nightlabs.base.entity.editor;

/**
 * These listeners can be added to {@link IEntityEditorPageController}s
 * in order to monitor changes in the objects they control.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public interface IEntityEditorPageControllerModifyListener {
	
	/**
	 * Notifies the listener that an object that the associated 
	 * controller manages has changed.
	 * 
	 * @param modifyEvent The old value of the object.
	 */
	void controllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent);
}
