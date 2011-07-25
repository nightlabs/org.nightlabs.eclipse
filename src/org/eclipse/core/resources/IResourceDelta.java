package org.eclipse.core.resources;

public interface IResourceDelta {
	public static Object CHANGED = null;
	public static int TYPE = 0;
	public static int OPEN = 0;
	public static int SYNC = 0;
	public static int DESCRIPTION = 0;
	public static int REPLACED = 0;
	public static int ADDED = 0;
	public static int REMOVED = 0;
	public static int MOVED_TO = 0;
	public static int MOVED_FROM = 0;
	
	IResource getResource();
	int getFlags();
	
	IResourceDelta[] getAffectedChildren(Object o);
}
