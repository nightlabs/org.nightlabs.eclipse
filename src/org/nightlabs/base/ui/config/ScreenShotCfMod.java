package org.nightlabs.base.ui.config;

import org.nightlabs.config.ConfigModule;


public class ScreenShotCfMod extends ConfigModule {

	private static final long serialVersionUID = 1L;

	protected boolean screenShotAllowed =false;

	public ScreenShotCfMod() { }

	/**
	 * @return a boolean value if screenShot is allowed or no. 
	 */
	public boolean getScreenShotAllowed() {
		return screenShotAllowed;
	}
	/**
	 * setScreenShotAllowed.
	 * See {@link #getScreenShotAllowed()()}.
	 * 
	 * @param allowed if a screenshot option is  allowed or no.
	 */
	public void setScreenShotAllowed(boolean allowed) {
		this.screenShotAllowed = allowed;
		setChanged();
	}
}
