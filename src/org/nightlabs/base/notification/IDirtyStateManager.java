/**
 * 
 */
package org.nightlabs.base.notification;

/**
 * Interface that can be used to notify implementors of 
 * changes in the lifecycle of some object.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IDirtyStateManager {
	
	/**
	 * Call this to notify that the implementor should 
	 * be brought to dirty state. 
	 */
	public void markDirty();
	
	/**
	 * Call this to notify that the implementor should 
	 * be brought to clean state. 
	 */
	public void markUndirty();
	
	/**
	 * Returns whether the reciever is in dirty state.
	 * @return Whether the reciever is in dirty state
	 */
	public boolean isDirty();
}
