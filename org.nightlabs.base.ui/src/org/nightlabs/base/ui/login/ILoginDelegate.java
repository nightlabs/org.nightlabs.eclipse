package org.nightlabs.base.ui.login;

import javax.security.auth.login.LoginException;

/**
 * Interface for providing login authentication, which can be registered via the extension-point "org.nightlabs.base.ui.login".
 * Login in applications is then available by the class {@link Login}. 
 */
public interface ILoginDelegate 
{
	/**
	 * Logs the user in.
	 * @throws LoginException if login failed.
	 */
	void login() throws LoginException;
	
	/**
	 * Logs the user out.
	 */
	void logout();
	
	/**
	 * Returns the current {@link LoginState}.
	 * @return the current {@link LoginState}.
	 */
	LoginState getLoginState();
}
