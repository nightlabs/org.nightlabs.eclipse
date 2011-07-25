package org.eclipse.core.resources;

public interface IResourceChangeEvent {
	public static Object POST_CHANGE = null;

	IResourceDelta getDelta();
	
}
