package org.nightlabs.base.login;

import javax.security.auth.login.LoginException;

public interface ILoginDelegate {
	void login() throws LoginException;
	void logout();
	LoginState getLoginState();
}
