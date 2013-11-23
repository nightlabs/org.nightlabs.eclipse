package org.nightlabs.eclipse.compatibility;

public class Compatibility {
	public static final boolean isRAP = _true();
	public static final boolean isRCP = !isRAP;
	
	// avoiding dead code compiler warnings
	private static boolean _true() {
		return true;
	}
}
